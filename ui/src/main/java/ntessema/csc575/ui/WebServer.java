/**
 * I wrote this mini web server as a class assignment for CSC435 (Dr Clark Elliott).
 * It was submitted on February 9, 2020.
 *
 * I could not re-write this project to run on a servlet engine, and I had
 * to rely on this piece of software for the user interface.
 *
 * It is a working software. However, it has not been tested thoroughly.
 * For the purposes of this project, it works very well. I haven't encountered
 * any issues.
 *
 * It will run on port 8383.
 */
package ntessema.csc575.ui;

/*-------------------------------------------------------------------------------------
 * 1. Name: Nardos Tessema
 *    Date: February 09, 2020
 *
 * 2. Java version used:
 *    Compiled with:
 *      (> javac -version)
 *      javac 1.8.0_191
 *    The program is tested and runs well on this JVM:
 *      (> java -version)
 *      java version "1.8.0_191"
 *      Java(TM) SE Runtime Environment (build 1.8.0_191-b12)
 *      Java HotSpot(TM) 64-Bit Server VM (build 25.191-b12, mixed mode)
 *
 * 3. List of Source Files:
 *    (1) MyWebServer.java
 *    (2) serverlog.txt
 *    (3) http-streams.txt
 *    (4) checklist-mywebserver.html
 *
 * 4. Command Line Compiling Instruction for Server:
 *    $javac MyWebServer.java
 *
 * 5. Command Line Running Instruction:
 *    $java MyWebServer
 *
 * 6. Execution Instruction:
 *    MyWebServer listens at port 2540 and runs like any web browser.
 *    Open any web browser (preferably Firefox) and browse to
 *    http://<server address>:2540 where server address is the ip
 *    address of the machine MyWebServer is running on.
 *
 * 7. POST and GET methods are implemented, so feel free to send form submissions
 *    with  both request methods.
 *
 * 8. Even though MyWebServer has been made aware of the most common mime types,
 *    omissions have been made for the sake of brevity.
 *
 * 9. Terminal window needs to show at least 100 characters horizontally to view the
 *    server output neat and unbroken. The console output is formatted as a table with
 *    the following header (100 characters wide). If terminal window width is less
 *    than 100 characters horizontally, the output will be broken and unintelligible.
 *
 *    ----------------------------------------------------------------------------------------------------
 *    REQUEST                                                                RESPONSE
 *    ----------------------------------------------------------------------------------------------------
 *    GET /cgi/addnums.fake-cgi?person=Nardos+Tessema&num1=45&num2=9 HTTP... HTTP/1.1 200 OK
 *
 * 10. The console log shows just the first lines of the http request and the http response. If the length of
 *    a request line is more than the fixed length allocated to it in the display (70 characters), it will be
 *    truncated, and it will be padded with "..." to show that it has been truncated (just for the console display).
 *-------------------------------------------------------------------------------------------------
 */
import ntessema.csc575.commons.Document;
import ntessema.csc575.indexer.DocumentReference;
import ntessema.csc575.indexer.Retriever;
import ntessema.csc575.query.Query;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * RFC 7231 - HTTP/1.1 Semantics and Content
 * https://tools.ietf.org/html/rfc7231
 */
enum RequestMethod { GET, HEAD, POST, PUT, DELETE, CONNECT, OPTIONS, TRACE }

class Cookie {
    String key;
    String value;

    Cookie(String key, String value) {
        this.key = key;
        this.value = value;
    }
}

class Response {
    /*
     * Status line
     */
    String protocolVersion;
    int statusCode;
    /*
     * Headers
     */
    LocalDateTime date;
    LocalDateTime expires;
    String contentType;
    long contentLength;
    String lastModified;
    String server;
    Cookie[] cookies;
    String acceptRanges;
    String body;

    Response() {
        /*
         * Currently MyWebServer uses HTTP 1.1
         */
        protocolVersion = "1.1";
        date = LocalDateTime.now();
        /*
         * Page expires after one minute
         */
        expires = date.plusMinutes(1L);
        server = "Nardos Tessema's Web Server, Version 1.0";
        cookies = new Cookie[MAX_COOKIES];
    }
    /*
     * Limit the number of cookies set in one response
     */
    private static final int MAX_COOKIES = 10;
}

public class WebServer {

    private static final int QUEUE_LENGTH = 6;
    private static final int PORT = 8383;
    private static final int MAX_THREADS = 100;

    /*
     * A list of ignored request patterns.
     * Example: don't bother about the favicon request
     */
    private static final String [] IGNORED_REQUEST_PATTERNS = new String [] {
            "GET\\s+\\/favicon\\.ico\\s+HTTP\\/1\\.[0|1]"
    };

    /*
     * Recognized request headers. Identified during my experiment or listed in RFC 2616:
     * https://www.w3.org/Protocols/rfc2616/rfc2616-sec5.html#sec5.3
     */
    private static final String [] RECOGNIZED_REQUEST_HEADERS = new String [] {
            "Accept", "Accept-Charset", "Accept-Encoding", "Accept-Language", "Authorization",
            "Connection", "Content-Length", "Content-Type", "Expect", "From",
            "Host", "If-Match", "If-Modified-Since", "If-None-Match", "If-Range",
            "If-Unmodified-Since", "Max-Forwards", "Proxy-Authorization", "Range", "Referer",
            "TE", "Upgrade-Insecure-Requests", "User-Agent"
    };

    /*
     * The request method is case-insensitive. Do case-insensitive matching later.
     * https://www.w3.org/Protocols/rfc2616/rfc2616-sec5.html#sec5.1.1
     */
    private static final String REQUEST_METHOD_PATTERN = "GET|HEAD|POST|PUT|DELETE|CONNECT|OPTIONS|TRACE";
    /*
     * Request-URI    = "*" | absoluteURI | abs_path | authority
     * https://www.w3.org/Protocols/rfc2616/rfc2616-sec5.html#sec5.1.2
     */

    /*
     * Valid URI characters
     * https://www.ietf.org/rfc/rfc2396.txt
     */
    private final static String C = "[a-zA-Z0-9\\-_\\!~\\*\\'\\(\\)\\.\\+%]";
    /*
     * The query string pattern
     * ?name=value&name=value
     *
     * I have tried to make it robust if value is  not supplied.
     * ?name1=&name2=&name3=&name4=
     */
    private final static String Q = "(\\?("+C+")+=("+C+")*(&("+C+")+=("+C+")*)*)?";

    /*
     * Request URI pattern
     * https://www.ietf.org/rfc/rfc2396.txt
     */
    private static final String REQUEST_URI_PATTERN = "\\/(("+C+")+(\\/("+C+")+)*)*(\\/?)"+Q;

    /*
     * The CGI path, which will be given a special treatment in MyWebServer
     */
    private static final String CGI_DIRECTORY = "/cgi/";
    private static final String CGI_URI_PATTERN = CGI_DIRECTORY + ".*";

    /*
     * Only HTTP/1.0 and HTTP/1.1 are allowed. I will deal with HTTP/2.0 after this course.
     */
    private static final String PROTOCOL_VERSION_PATTERN = "HTTP\\/1\\.[0|1]";

    /*
     * Available action methods. Any number of methods can be added here followed,
     * of course, by the java method implementation.
     * Example:
     *      To add a new action called subtractnums, add the entry { "subtractnums", "subtract" }
     *      below  and implement the java method subtract() with required signature of
     *      static String subtract(Map<String, String>)
     */
    private static final Map<String, String> ACTIONS = Stream.of(new String [][] {
            /* key = name as it appears in form, value = name of java action method */
            { "search", "search" }
    }).collect(Collectors.toMap(x -> x[0], x -> x[1]));

    /*
     * Server loop control variable. Set it to false to terminate server loop
     * after completing current request/response cycle.
     */
    private static boolean serverAlive = true;
    /*
     * MyWebServer uses Thread Pools! Thread creation  and tear-up is time
     * consuming, It is, therefore, not scalable to spawn a thread for every
     * single request. Instead, get the threads from an already constructed
     * thread pool.
     */
    private static final Executor executor = Executors.newFixedThreadPool(MAX_THREADS);

    public static void main(String[] args) {
        try(ServerSocket serverSocket = new ServerSocket(PORT, QUEUE_LENGTH)) {
            System.out.println("                   +-----------------------------------------------------------+");//61
            System.out.println("                   |                 Nardos Tessema's Web Server               |");
            System.out.println("                   |                   Listening at Port " + String.format("%-21s |", PORT));
            System.out.println("                   +-----------------------------------------------------------+");
            System.out.println();
            int once = 1;
            while(serverAlive) {
                final Socket socket = serverSocket.accept();
                executor.execute(() -> serve(socket));
                if(once++ == 1) {
                    /*
                     * Print table header only once.
                     */
                    System.out.println(header());
                }
            }
        } catch(IOException ioe) {
            System.out.println("I/O error occurred while waiting for a connection from clients.");
        }
    }

    private static void serve(Socket s) {
        try(Socket socket = s) {
            try(
                    /*
                     * AutoCloseable resources are best used in a try-with-resources block like this so
                     * that we make absolutely sure that they are closed at the end of the block.
                     * The output stream's is auto-flush is set to true so it does not have to wait until the
                     * buffer is full to flush its contents.
                     */
                    PrintStream out = new PrintStream(socket.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                /*
                 * Read client request. Could be multi-line.
                 */
                String [] request = getRequestHeaders(in);
                /*
                 * Process request and prepare the response.
                 */
                Response response = getResponse(request);
                /*
                 * Do not respond to null response objects.
                 * This is my mechanism of handling requests
                 * that have to be ignored, like favicon requests.
                 */
                if(response != null) {
                    /*
                     * Send the response to the client.
                     */
                    out.println(render(response));
                    String responseLine  = "HTTP/" + response.protocolVersion + " " + response.statusCode + " " + RESPONSE_STATUS_MESSAGES.get(response.statusCode);
                    log(padWhiteSpaces(request[0], 70) + " " + String.format("%-30s", responseLine));
                    /*
                     * Get response status code and log it.
                     */
                } else {
                    /**
                     * Null responses are given a cold shoulder here - ignored! Example is when
                     * a favicon request is  sent by browser. There is room for more such requests
                     * but I can't think of one right now.
                     */
                }

            } catch(IOException ioe) {
                System.out.println("I/O error occurred while reading client request.");
            }
        } catch(IOException ioe) {
            System.out.println("I/O error occurred while closing socket.");
        }
    }

    /**
     * Prepare the response to the client
     */
    private static Response getResponse(String [] requestHeaders) {

        /*
         * Tokens to look for:
         * (1) requestLine
         *     The start line (Request-Line).
         *     Example: GET / HTTP/1.1
         * (2) requestBody
         *     The request body (if any). Example, in POST method...
         * (3) headers
         *     The headers the client sends. Example: User-Agent: ...
         */
        String requestLine;
        String requestBody;

        /*
         * Ignore the favicon request, and possibly others (not defined as of now).
         * If there are no http request lines (length = 0), malformed request.
         */
        if(requestHeaders.length > 0 && !ignoredRequest(requestHeaders[0])) {
            /*
             * Get the lines of the http request in an array.
             */
            String [] lines = parseHttpRequest(requestHeaders);
            /*
             * the request parser always puts the requestLine and the requestBody
             * in the first and second positions of the request lines array, respectively.
             */
            requestLine = lines[0];
            requestBody = lines[1];

            /*
             * Get the tokens for each kind of request line.
             * (1) the start line: GET / HTTP/1.1 has three tokens
             *     the method, the uri and the protocol.
             * (2) Each of the headers have two tokens, the header name and the value.
             *     e.g. User-Agent: Mozilla
             * (3) Each data value is a key-value pair.
             *     e.g. name=Nardos&password=1234 => { ("name", "Nardos"), ("password", "1234") }
             */
            String [] requestLineTokens;
            Map<String, String> headerTokens;
            Map<String, String> decodedRequestBody;

            /*
             * Dissect the request line into request method, request uri and protocol version.
             * Example. {"GET",  "/dog.txt", "HTTP/1.1"}
             */
            requestLineTokens = getRequestLineTokens(requestLine);
            /*
             * If a good request-line is sent, the three tokens, namely
             * Request Method, Request URI and Protocol Version will be there.
             * Otherwise, the request is a bad request.
             */
            if(requestLineTokens.length != 3) {
                return doBadRequest();
            }

            /*
             * If requestLine is a valid request line, Construct the response object
             * based on the request method, request uri, request body, ...
             */
            if(isValidRequestLine(requestLineTokens)) {
                /*
                 * Dissect the request headers into key-value pairs.
                 */
                headerTokens = tokenizeHeaders(Arrays.copyOfRange(lines, 2, lines.length));
                try {
                    /*
                     * Get key-value pairs from the request body.
                     */
                    decodedRequestBody = decodeQueryString(requestBody);
                } catch(UnsupportedEncodingException uee) {
                    System.out.println("Unsupported encoding exception occurred while decoding user input.");
                    /*
                     * for the sake of robustness, set the request data to empty and proceed.
                     */
                    decodedRequestBody = new HashMap<>();
                }
                /*
                 * We have a valid HTTP request at this point. Delegate response
                 * generation to the appropriate method.
                 *
                 * requestMethod cannot be null at this point because request
                 * line is valid. I don't have to check for null request method.
                 */
                RequestMethod requestMethod = requestMethod(requestLineTokens[0]);
                return doResponse(requestMethod, requestLineTokens, decodedRequestBody, headerTokens);
            } else {
                /*
                 * Three tokens were identified in the request line but
                 * they were not valid. => The request is a bad request.
                 */
                return doBadRequest();
            }
        } else if(requestHeaders.length > 0 && ignoredRequest(requestHeaders[0])) {
            /*
             * There were request lines but they are in the ignored list (favicon).
             * Return a null response so it can be ignored with a null-check later.
             */
            return null;
        }
        /* Anything that reaches to this point
         * must be a Bad Request!
         */
        return doBadRequest();
    }

    /**
     * Dissect the request.
     *
     * (1) Request-Line: the start line.
     *     e.g. GET / HTTP/1.1
     * (2) Headers: headers seent by the client.
     *     e.g. User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10.14; rv:72.0) Gecko/20100101 Firefox/72.0
     * (3) Request Body: if it exists, key1=value1&key2=value2 ...
     *     or empty string if not sent by client.
     *
     * Return requestLine in first position, requestBody in second
     * position, and headers in the rest of the array positions.
     */
    public static String [] parseHttpRequest(String [] requestHeaders) {
        /*
         * The first line is special, request line.
         */
        String requestLine = requestHeaders[0];
        /*
         * We don't know if there is message body or headers yet.
         */
        String requestBody = null;
        String [] headers = null;
        /*
         * if there is a message body in the http request, then the request lines
         * must be ≥ 3 (accounting for the start line, the CRLF line and the data line).
         */
        if(requestHeaders.length > 2) {
            /*
             * If there is request body, the penultimate line must be CRLF.
             */
            if(requestHeaders[requestHeaders.length - 2].length() == 0) {
                requestBody = requestHeaders[requestHeaders.length - 1];
            }

            if(requestBody != null) {
                headers = Arrays.copyOfRange(requestHeaders, 1, requestHeaders.length - 2);
            } else {
                headers = Arrays.copyOfRange(requestHeaders, 1, requestHeaders.length - 1);
            }
        }
        String [] tokenizedRequest;
        /*
         * headers is null only if length ≤ 2, in which case there is no requestBody.
         * Return the empty string for a non-existing request body.
         */
        if(headers == null) {
            tokenizedRequest = new String [2];
            tokenizedRequest[0] = requestLine;
            tokenizedRequest[1] = "";
        }  else {
            tokenizedRequest = new String [2 + headers.length];
            tokenizedRequest[0] = requestLine;
            tokenizedRequest[1] = (requestBody != null) ? requestBody : "";
            int i = 2;
            for(String header : headers) {
                tokenizedRequest[i++] = header;
            }
        }
        return tokenizedRequest;
    }

    /**
     * Delegate response generation to the appropriate method.
     */

    private static Response doResponse(RequestMethod requestMethod, String []requestLineTokens, Map<String, String> decodedRequestBody, Map<String, String> headerTokens) {
        if (requestMethod == RequestMethod.GET) {
            return doGet(requestLineTokens, decodedRequestBody, headerTokens);
        } else if(requestMethod == RequestMethod.POST) {
            return doPost(requestLineTokens, decodedRequestBody, headerTokens);
        } else {
            /*
             * 501 - Not implemented
             */
            Response response =  new Response();
            response.body = getDynamicServerMessage(501);
            response.contentLength = response.body.getBytes().length;
            response.contentType = "text/html";
            return  response;
        }
    }

    /**
     * Process GET requests and return a  response object.
     *
     * @param requestLineTokens The first line of  request (array of length 3).
     * @param decodedRequestBody KV values decoded into (K,V) pairs in a map.
     * @param headerTokens Headers sent by client. Also (K,V) pairs in a map.
     * @return response object ready to be rendered.
     */
    private static Response doGet(String [] requestLineTokens, Map<String, String> decodedRequestBody, Map<String, String> headerTokens) {
        String requestURI = requestLineTokens[1];
        /*
         * Check pattern of request uri
         *
         * If request is a CGI request, re-route the request to the CGI processor.
         */
        return doCGI(requestLineTokens, decodedRequestBody, headerTokens);
    }

    private static Response doPost(String [] requestLineTokens, Map<String, String> decodedRequestBody, Map<String, String> headerTokens) {
        String requestURI = requestLineTokens[1];
        if(requestURI.matches(CGI_URI_PATTERN)) {
            /*
             * Send the request to the CGI processor.
             */
            return doCGI(requestLineTokens, decodedRequestBody, headerTokens);
        } else {
            /*
             * We don't have any other CGI path than /cgi/ for the time being.
             *
             * Return a 501 - Not Implemented response.
             */
            Response response = new Response();
            response.body = getDynamicServerMessage(501);
            response.contentLength = response.body.getBytes().length;
            response.contentType = "text/html";
            return response;
        }
    }

    /**
     * Process CGI requests
     *
     * @param requestLineTokens First line of request
     * @param decodedRequestBody Query string decoded into (K,V) pairs in a map.
     * @param headerTokens Headers in HTTP request.
     * @return
     */
    private static Response doCGI(String [] requestLineTokens, Map<String, String> decodedRequestBody, Map<String, String> headerTokens) {

        /*
         * The CGI processor needs two things, the action method and the
         * and the key-value pairs (form input or query string data).
         */
        final String actionMethod = "search";
        /*
         * We need to get the key-value pairs of the values submitted in a form
         * or in a query string.
         *
         * If request method is GET, the kv pairs are in the query string (in the request uri).
         * If the method is POST, the kv pairs  are in the http request body.
         */
        Map<String, String> kvPairs;

        if(requestLineTokens[0].toUpperCase().equals("GET")) {
            String[] partsOfRequestURI = requestLineTokens[1].split("\\?");
            //actionMethod = (partsOfRequestURI.length == 2) ? partsOfRequestURI[0].substring(CGI_DIRECTORY.length()) : "";
            String queryString = (partsOfRequestURI.length == 2) ? partsOfRequestURI[1] : "";
            try {
                /*
                 * KV pairs have to be decoded from the query string.
                 */
                kvPairs = decodeQueryString(queryString);
                return doAction(actionMethod, kvPairs);
            } catch(UnsupportedEncodingException uee) {
                /*
                 * Unsupported encoding. I can't see this happening. It must be
                 * because of the HTTP request. So, send a Bad  Request response.
                 */
                uee.printStackTrace();
                return doBadRequest();
            }
        } else if(requestLineTokens[0].toUpperCase().equals("POST")) {
            //actionMethod = requestLineTokens[1].substring(CGI_DIRECTORY.length());
            /*
             * KV pairs are already supplied as the second argument to this method.
             */
            kvPairs = decodedRequestBody;
            return doAction(actionMethod, kvPairs);
        }

        /*
         * Not implemented yet. Only GET and POST for now.
         */
        Response response = new Response();
        response.body = getDynamicServerMessage(501);
        response.contentLength = response.body.getBytes().length;
        response.contentType = "text/html";
        return response;

    }

    /**
     * All actions in the web server are processed by this single method.
     * It uses the Java Reflection API to determine action methods at runtime.
     *
     * Action  methods such as addnums have to conform to one thing, namely
     * that they must all take a Map as argument and return a string (formatted HTML).
     *
     * The Map argument is the key-value pairs in a query string or submitted in a form.
     * The returned string is the formatted result HTML.
     *
     * It is possible to  add a new action which conforms to the rules mentioned above.
     * All  one has to do is include the action in the Map of available actions (ACTIONS).
     * The name of the action (as it appears in a form action) is the key and the Java
     * method name is the value.
     */
    private static Response doAction(String action, Map<String, String> kv) {
        String body;
        Response response = new Response();
        if(ACTIONS.containsKey(action)) {
            /*
             *  The action is available
             */
            String actionName = ACTIONS.get(action);
            try {
                /*
                 * Get the action method through Java reflection.
                 */
                Method method = WebServer.class.getDeclaredMethod(actionName, Map.class);
                /*
                 * The html body of the response is generated by the action method.
                 */
                body = (String) method.invoke(null, kv);
                /*
                 * status OK
                 */
                response.statusCode = 200;
            } catch(NoSuchMethodException nsme) {
                System.out.println("Internal Server Error (1)");
                body = getDynamicServerMessage(500);
                response.statusCode = 500;
            } catch(IllegalAccessException iae) {
                System.out.println("Internal Server Error (2)");
                body = getDynamicServerMessage(500);
                response.statusCode = 500;
            } catch(InvocationTargetException ite) {
                System.out.println("Internal Server Error (3)");
                body = getDynamicServerMessage(500);
                response.statusCode = 500;
            }
        } else {
            /*
             * The action is not found. 404 - Not found.
             */
            body = getDynamicServerMessage(404);
            response.statusCode = 404;
        }

        response.body = body;
        response.contentLength = response.body.getBytes().length;
        response.contentType = "text/html";
        return response;
    }

    /**
     * Bad requests will have a response status code of 400.
     * The response renderer will generate a bad request page.
     */
    private static Response doBadRequest() {
        Response response = new Response();
        response.statusCode = 400;
        response.body = getDynamicServerMessage(400);
        response.contentLength = response.body.getBytes().length;
        response.contentType = "text/html";
        return response;
    }

    /**
     * Format a response object into http response
     * headers and body strings with the proper CRLFs.
     */
    private static String render(Response response) {
        if(response == null) {
            /*
             * Ignore null responses.
             */
            return "";
        }
        String responseLine  = "HTTP/" + response.protocolVersion + " " + response.statusCode + " " + RESPONSE_STATUS_MESSAGES.get(response.statusCode);
        StringBuilder sb = new StringBuilder();
        sb.append(responseLine);
        sb.append("\r\n");
        sb.append("Server: " + response.server);
        sb.append("\r\n");
        sb.append("Content-Type: " + response.contentType + "; charset=utf-8");
        sb.append("\r\n");
        sb.append("Content-Length: " + response.contentLength);
        sb.append("\r\n");
        sb.append("Last-Modified: " + ((response.lastModified != null) ? response.lastModified : ""));
        sb.append("\r\n");
        sb.append("Date: " + response.date);
        sb.append("\r\n");
        sb.append("Expires: " + response.expires);
        sb.append("\r\n");
        for(int i = 0; i < response.cookies.length; i++) {
            if(response.cookies[i] != null) {
                sb.append("Set-Cookie: " + response.cookies[i].key + "=" + response.cookies[i].value);
                sb.append("\r\n");
            }
        }
        sb.append("Connection: Close");
        sb.append("\r\n");
        sb.append("\r\n");
        sb.append(response.body);
        return sb.toString();
    }

    /**
     * Template HTML...
     * All server generated HTML pages will have
     * a uniform look.
     *
     * Generate header HTML with title.
     */
    private static String generatePreHtml(String title) {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html>\n");
        sb.append("<html lang=\"en\">\n");
        sb.append("<head>\n");
        sb.append("\t<meta charset=\"UTF-8\">\n");
        sb.append("\t<style>\n");
        sb.append("\t\t.logo { color: #0e6655; }\n");
        sb.append("\t\t.logo h2 { color: #1f618d }\n");
        sb.append("\t\t.c, .logo { font-family: Cambria, Cochin, Georgia, Times, 'Times New Roman', serif; }\n");
        sb.append("\t\t.hilite { color: #cb4335; }\n");
        sb.append("\t\t.hilite-g { color: #229954; }\n");
        sb.append("\t\thr { border-top: 3px  #1f618d solid }\n");
        sb.append("\t\tth, td { padding-left: 5px; padding-right: 5px; text-align: left }\n");
        sb.append("\t\t.button, .text { margin-right: 1px; background-color: #ffffff; border: #1f618d 2px solid; color: #1f618d; padding: 15px 32px; text-align: center; text-decoration: none; display: inline-block; font-size: 16px;}\n");
        sb.append("\t\t.button { font-weight: bold }");
        sb.append("\t</style>\n");
        sb.append("\t<title>" + title + "</title>\n");
        sb.append("</head>\n");
        sb.append("<body>\n");
        sb.append("<div class=\"logo\">\n");
        sb.append("<h2>BBC Documentary Program Finder</h2>\n");
        sb.append("<strong>CSC-575: Intelligent Information Retrieval</strong><br>\n");
        sb.append("<span>Winter 2020</span><br>\n");
        sb.append("<em>Nardos Tessema</em><br>\n");
        sb.append("<hr>\n");
        sb.append("<form method=\"GET\" action=\"search\">");
        sb.append("<input type=\"text\" name=\"query\" size=\"50\" class=\"text\">");
        sb.append("<input type=\"submit\" value=\"SEARCH BBC RADIO\" class=\"button\">");
        sb.append("</form>");
        sb.append("<hr>\n");
        sb.append("</div>\n");
        return sb.toString();
    }

    /**
     * Generate the footer HTML.
     *
     * @return closing HTML tags
     */
    private static String generatePostHtml() {
        StringBuilder sb = new StringBuilder();
        sb.append("</body>\n");
        sb.append("</html>\n");
        return sb.toString();
    }
    /**
     * get dynamic HTML for server generated responses such as 404, 500
     */
    private static String getDynamicServerMessage(int statusCode) {
        StringBuilder sb = new StringBuilder();
        sb.append(generatePreHtml(String.valueOf(statusCode)));
        sb.append("<div class=\"c c-"  + (100 * (statusCode % 100))  + "\">\n");
        sb.append("<h1>" + statusCode + " - " + RESPONSE_STATUS_MESSAGES.get(statusCode) + "</h1>\n");
        sb.append("</div>\n");
        sb.append(generatePostHtml());
        return sb.toString();
    }


    /**
     * Read request lines. There could be multiple lines per request.
     * Challenge: How to detect when client is done sending request?
     * If Content-Length is present, get the value, and right after the first empty
     * line (CRLFCRLF) is detected, read that much number of characters with in.read()
     * and then stop reading (EOF reached).
     * If Content-Length  is not present, stop reading (EOF reached) after the first empty line is detected.
     * https://www.w3.org/Protocols/rfc2616/rfc2616-sec5.html
     *
     */
    private static String[] getRequestHeaders(BufferedReader in) {
        String line = null;
        String previousLine;
        boolean EOF = false;
        /*
         * If there is not Content-Length, stop reading after
         * the first empty line (CRLFCRLF) is detected.
         */
        int contentLength = 0;
        StringBuilder sb = new StringBuilder();
        do {
            /*
             * If previous line was CRLFCRLF, then read
             * contentLength characters from the buffer and quit (EOF).
             */
            previousLine = line;
            try {
                if(previousLine != null && previousLine.equals("")) {
                    StringBuilder strb = new StringBuilder();
                    int count = 0;
                    while(count++ < contentLength) {
                        strb.append((char) in.read());
                    }
                    line = strb.toString();
                    EOF = true;
                } else {
                    line = in.readLine();
                }
            } catch(IOException ioe) {
                System.out.println("I/O  error occurred while reading request.");
                System.exit(1);
            }
            if(line != null) {
                sb.append(line);
                sb.append("\n");
                if(line.toLowerCase().matches("content-length\\:\\s*\\d*")) {
                    contentLength = Integer.parseInt(line.split(":")[1].replaceAll("\\s", ""));
                }
            }
        } while(!EOF);
        String [] requestLines = sb.toString().split("\n");
        /*
         * Some lines could be empty lines. However, the first line is expected
         * to be a non-empty Request-Line like "GET /index.html HTTP/1.1".
         * RFC 2616 stipulates that any empty line(s) found where a Request-Line
         * is expected should be ignored in the interest of robustness.
         * https://www.w3.org/Protocols/rfc2616/rfc2616-sec4.html#sec4.1
         */
        int indexOfFirstNonEmptyLine = 0;
        while(requestLines.length > 0 && requestLines[indexOfFirstNonEmptyLine].equals("")) {
            indexOfFirstNonEmptyLine++;
        }
        return Arrays.copyOfRange(requestLines, indexOfFirstNonEmptyLine, requestLines.length);
    }


    /**
     * Validate request string
     * <METHOD> <SP> <REQUEST-URI> <SP> <HTTP-VERSION>
     */
    private static boolean  isValidRequestLine(String [] tokens) {

        if(tokens[0].toUpperCase().matches(REQUEST_METHOD_PATTERN) &&
                tokens[1].matches(REQUEST_URI_PATTERN) &&
                tokens[2].toUpperCase().matches(PROTOCOL_VERSION_PATTERN)) {
            return true;
        }
        /*
         * Malformed Request
         */
        return false;
    }

    /**
     * Separate the request line into METHOD, URI, HTTP-VERSION
     * Example:
     *    GET /one/two/index.html HTTP/1.1
     */
    private static String [] getRequestLineTokens(String requestLine) {
        return requestLine.split("\\x20+");
    }

    /**
     * Decodes the query string / request body into a map of key value pairs.
     * name1=value1&name2=value2...
     *
     * Since the HTML may be encoded, so use URLDecoder
     */
    private static Map<String, String> decodeQueryString(String requestBody) throws UnsupportedEncodingException {
        HashMap<String, String> decodedRequestBody = new HashMap<>();
        if(requestBody.length() > 0) {
            String[] params = requestBody.split("&");
            for(String param : params) {
                String [] keyValue = param.split("=", 2);
                /*
                 * Accept only US-ASCII for the keys, UTF-8 for the values.
                 * Reason: The keys will eventually be used as variable names.
                 */
                decodedRequestBody.put(URLDecoder.decode(keyValue[0], "US-ASCII"), URLDecoder.decode(keyValue[1], "US-ASCII"));
            }
        }
        return decodedRequestBody;
    }

    /**
     * Tokenize the client request headers. The header name
     * becomes the key. For example, User-Agent: Mozilla
     * becomes ("User-Agent", "Mozilla") in hash map.
     */
    private static Map<String, String> tokenizeHeaders(String [] headers) {

        /*
         * If there were no request lines, return an empty HashMap.
         * An empty request is malformed.
         */
        HashMap<String, String> tokenizedHeaders = new HashMap<>();

        /*
         * headers should match the following pattern. The list of recognized headers is
         * available at https://www.w3.org/Protocols/rfc2616/rfc2616-sec5.html#sec5.3.
         * The rest are identified during my experiment with the http protocol.
         */
        String  pattern = "(Accept|Accept-Charset|Accept-Encoding|Accept-Language|Authorization|Connection|Content-Length|Content-Type|Cookie|Expect|From|Host|If-Match|If-Modified-Since|If-None-Match|If-Range|If-Unmodified-Since|Max-Forwards|Proxy-Authorization|Range|Referer|TE|Upgrade-Insecure-Requests|User-Agent):.*";
        Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);

        for(String header : headers) {
            Matcher matcher = p.matcher(header);
            if(matcher.matches()) {
                String[] tokens = getRequestHeaderTokens(header);
                if (tokens.length > 0) {
                    tokenizedHeaders.put(tokens[0], tokens[1]);
                }
            }
        }

        return tokenizedHeaders;
    }

    /**
     * Separate header lines into Header-Name, Header-Value.
     * Example:
     *    Content-Length: 57
     *    User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10.14; rv:72.0) Gecko/20100101 Firefox/72.0
     */
    private static String [] getRequestHeaderTokens(String header) {
        if(header.length() > 0) {
            return header.split(":", 2);
        }
        return new String [] {};
    }

    /**
     * If a request is in the list of ignored request
     * patterns return true. Return false otherwise.
     */
    private static boolean ignoredRequest(String request) {
        for(String ignoredPattern : IGNORED_REQUEST_PATTERNS) {
            Pattern pattern = Pattern.compile(ignoredPattern, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(request);
            if(matcher.matches()) {
                return true;
            }
        }
        return false;
    }

    /**
     * get request method enums from strings
     * @param method the request method string.
     */
    private static RequestMethod requestMethod(String method) {
        if(method.toUpperCase().equals("GET")) return RequestMethod.GET;
        if(method.toUpperCase().equals("HEAD")) return RequestMethod.HEAD;
        if(method.toUpperCase().equals("POST")) return RequestMethod.POST;
        if(method.toUpperCase().equals("PUT")) return RequestMethod.PUT;
        if(method.toUpperCase().equals("DELETE")) return RequestMethod.DELETE;
        if(method.toUpperCase().equals("CONNECT")) return RequestMethod.CONNECT;
        if(method.toUpperCase().equals("OPTION)")) return RequestMethod.OPTIONS;
        if(method.toUpperCase().equals("TRACE")) return RequestMethod.TRACE;
        return null;
    }

    /**
     * Response statuses. Only a few will be used in MyWebServer. The entire list is obtained from
     * https://developer.mozilla.org/en-US/docs/Web/HTTP/Status
     */
    private static final Map<Integer, String> RESPONSE_STATUS_MESSAGES = Stream.of(new Object[][] {
            /*
             * Successful responses
             */
            { 200, "OK" },
            { 201, "Created" },
            /*
             * Redirection messages
             */
            { 301, "Moved Permanently" },
            { 304, "Not Modified" },
            { 307, "Temporary Redirect" },
            { 308, "Permanent Redirect" },
            /*
             * Client error responses
             */
            { 400, "Bad Request" },
            { 401, "Unauthorized" },
            { 403, "Forbidden" },
            { 404, "Not Found" },
            { 405, "Method Not Allowed" },
            { 408, "Request Timeout" },
            { 411, "Length Required" },
            { 413, "Payload Too Large" },
            { 414, "URI Too Long" },
            { 415, "Unsupported Media Type" },
            /*
             * Server error responses
             */
            { 500, "Internal Server Error" },
            { 501, "Not Implemented" },
            { 503, "Service Unavailable" },
            { 505, "HTTP Version Not Supported" },
            { 507, "Insufficient Storage" },
            { 511, "Network Authentication Required" },
    }).collect(Collectors.toMap(x -> (Integer) x[0], x -> (String) x[1]));

    /**
     * Get the file extension
     *
     * @param path The path of the file including the file name.
     */
    private static String getFileExtension(String path) {
        return path.substring(1 + path.lastIndexOf('.'));
    }

    /**
     * The logging method. However we decide to log server
     * activity, this is the single point to implement it.
     * @param log the message to be logged.
     */
    private static void log(String log) {
        System.out.println(log);
    }

    /**
     * Table head
     */
    private static String header() {
        StringBuilder sb = new StringBuilder();
        sb.append("----------------------------------------------------------------------------------------------------\n");
        sb.append("REQUEST                                                                RESPONSE                     \n");
        sb.append("----------------------------------------------------------------------------------------------------");
        return sb.toString();
    }

    /**
     *  Pad whitespaces to make fixed length
     */
    private static String padWhiteSpaces(String input, int length) {
        if(input.length() < length) {
            StringBuilder sb = new StringBuilder(input);
            for(int i = 0; i < length - input.length(); i++) {
                sb.append(" ");
            }
            return sb.toString();
        } else {
            return input.substring(0, length - 3) + "...";
        }
    }

    /**
     * Mime Types that will be recognized by MyWebServer.
     * (I have tried to include most files I am familiar with.)
     *
     * Most of them obtained (and formatted with script) from source below:
     * https://developer.mozilla.org/en-US/docs/Web/HTTP/Basics_of_HTTP/MIME_types/Complete_list_of_MIME_types
     */
    private static final Map<String, String> MIME_TYPES = Stream.of(new String[][] {
            { "java", "text/plain" }, /* "text/x-java-source" */
            { "log", "text/plain" },
            { "css", "text/css" },
            { "html", "text/html" },
            { "htm", "text/html" },
            { "aac", "audio/aac" },
            { "abw", "application/x-abiword" },
            { "arc", "application/x-freearc" },
            { "avi", "video/x-msvideo" },
            { "azw", "application/vnd.amazon.ebook" },
            { "bin", "application/octet-stream" },
            { "bmp", "image/bmp" },
            { "bz", "application/x-bzip" },
            { "bz2", "application/x-bzip2" },
            { "csh", "application/x-csh" },
            { "csv", "text/csv" },
            { "doc", "application/msword" },
            { "docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document" },
            { "eot", "application/vnd.ms-fontobject" },
            { "epub", "application/epub+zip" },
            { "gz", "application/gzip" },
            { "gif", "image/gif" },
            { "ico", "image/vnd.microsoft.icon" },
            { "ics", "text/calendar" },
            { "jar", "application/java-archive" },
            { "class", "application/java-byte-code" },
            { "jpeg", "image/jpeg" },
            { "jpg", "image/jpeg" },
            { "js", "text/javascript" },
            { "json", "application/json" },
            { "jsonld", "application/ld+json" },
            { "mid", "audio/midi audio/x-midi" },
            { "midi", "audio/midi audio/x-midi" },
            { "mp3", "MP3 audio", "audio/mpeg" },
            { "mpeg", "MPEG Video", "video/mpeg" },
            { "png", "image/png" },
            { "pdf", "application/pdf" },
            { "php", "application/php" },
            { "ppt", "application/vnd.ms-powerpoint" },
            { "pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation" },
            { "rar", "application/vnd.rar" },
            { "rtf", "application/rtf" },
            { "sh", "application/x-sh" },
            { "svg", "image/svg+xml" },
            { "swf", "application/x-shockwave-flash" },
            { "tar", "application/x-tar" },
            { "tif", "image/tiff" },
            { "tiff", "image/tiff" },
            { "ts", "video/mp2t" },
            { "ttf", "font/ttf" },
            { "txt", "text/plain" },
            { "wav", "audio/wav" },
            { "xhtml", "application/xhtml+xml" },
            { "xls", "application/vnd.ms-excel" },
            { "xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" },
            { "xml", "application/xml" },
            { "zip", "application/zip" },
            { "7z", "application/x-7z-compressed" }
    }).collect(Collectors.toMap(x -> x[0], x -> x[1]));

    /**
     * Action method. All action methods should take a map as
     * an argument and return a String (formatted HTML).
     *
     * This particular action method expects the following form inputs:
     *      (1) query - expected to be a string
     */
    private static String search(Map<String, String> kv) {
        /*
         * The HTML that will be generated by this action method
         */
        String innerHtml;

        /*
         * The variables this action expects to be supplied with
         */
        String query;

        if(kv.isEmpty()) {
            /*
             * No arguments passed by client.
             */
            innerHtml = "";
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("<div>\n");
            sb.append("<table>\n");
            sb.append("\t<tr><td colspan=\"2\">");

            sb.append("</td></tr>");
            query = kv.get("query");
            sb.append("\t<tr><td colspan=\"2\">SEARCH RESULTS FOR<br>" + query + "</td></tr>\n");

            Retriever retriever = new Retriever();

            try {
                Document queryDocument = Query.createQueryFromString(query);
                Map<DocumentReference, Double> results = retriever.retrieve(queryDocument);
                if(results == null) {
                    System.out.println("No documents returned.");
                    //TODO redirect
                }
                queryDocument.getDocumentVector().forEach((term, weight) -> {
                    System.out.println("Query Term: " + term + ", Weight: " + weight);
                });
                System.out.println(results.size());
                for(Map.Entry<DocumentReference, Double> docRef : results.entrySet()) {
                    DocumentReference reference = docRef.getKey();
                    double score = docRef.getValue();
                    sb.append("<tr><td>");
                    sb.append(reference.getPath().getFileName().toString().replace(".txt", ""));
                    sb.append("</td><td>");
                    sb.append(String.format("%.3f", score));
                    sb.append("</td></tr>");
                }

            } catch (IOException ioe) {
                //TODO redirect to error page
            } catch (URISyntaxException use) {
                //TODO redirect to error page
            }
            sb.append("</table>\n");
            sb.append("</div>\n");
            innerHtml = sb.toString();
        }
        StringBuilder sb = new StringBuilder();
        sb.append(generatePreHtml("Add Numbers"));
        sb.append(innerHtml);
        sb.append(generatePostHtml());
        return  sb.toString();
    }

}
