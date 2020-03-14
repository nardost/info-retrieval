package ntessema.csc575.crawler;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static ntessema.csc575.crawler.BBCRadioPrograms.PROGRAMS;

public class Crawler {

    /*
     * No need to instantiate this class.
     */
    private Crawler() {
    }


    /**
     * The crawler method.
     *
     * This version of crawler downloads all the programs
     * listed in BBCRadioPrograms.PROGRAMS
     */
    static void crawl() throws IOException {
        List<String> listOfPrograms = new ArrayList<>();
        for(Map.Entry<String, String> program : PROGRAMS.entrySet()) {
            listOfPrograms.add(PROGRAMS.get(program));
        }
        List<String> listOfEpisodes = getEpisodes(listOfPrograms);
        listOfEpisodes.forEach(System.out::println);
    }

    /**
     * The crawler method.
     *
     * This version of crawler downloads only a
     * subset of the list in BBCRadioPrograms.PROGRAMS.
     *
     * This version is written because the downloading
     * the entire archive is not feasible for this project.
     *
     * Only a select few programs will be downloaded to
     * demonstrate the application in action.
     */
    static void crawl(String[] programsSelectedForDownload) throws IOException {
        List<String> listOfSelectedPrograms = new ArrayList<>();
        for(String program : programsSelectedForDownload) {
            if(PROGRAMS.containsKey(program)) {
                listOfSelectedPrograms.add(PROGRAMS.get(program));
            }
        }
        List<String> listOfPrograms = getSelectedProgramsList(programsSelectedForDownload);
        List<String> listOfEpisodes = getEpisodes(listOfPrograms);
        listOfEpisodes.forEach(System.out::println);
    }

    /**
     * This method takes as argument the list of BBC program
     * links and returns a list of all episodes of the programs.
     *
     * The link to each program is the root url to the program.
     * A program could have multiple pages in which it lists
     * the links to the episodes.
     *
     * This method identifies how many pages, if any, a program
     * root page has and visits each page to download the episode
     * summary and link.
     */
    static List<String> getEpisodes(List<String> listOfPrograms) throws IOException {
        /*
         * List that holds all episodes of all programs.
         */
        List<String> listOfEpisodes = new ArrayList<>();

        final String cssClass = "div.programme__body h2.programme__titles a";

        /*
         * Iterate over the list of programs found.
         * Each program page could have multiple pages.
         */
        for(String programRootUrl : listOfPrograms) {

            System.out.println(String.format("%31s", "TRYING TO CONNECT TO: ") + programRootUrl);
            /*
             * Connect to the page at p.
             */
            Connection connection = Jsoup.connect(programRootUrl);

            System.out.println(String.format("%31s", "CONNECTED TO: ") + programRootUrl);
            if(connection != null) {
                /*
                 * Disable exception throwing. Many things could go
                 * wrong while crawling, but we don't want that to
                 * stop execution of the crawler.
                 */
                connection.ignoreHttpErrors(true);
                /*
                 * Proceed only if http status is OK
                 */
                int statusCode = connection.execute().statusCode();
                if (statusCode == 200) {
                    /*
                     * Get the DOM of the page at p
                     */
                    Document d = connection.get();
                    if (d != null) {
                        /*
                         * Find how many pages there are for this program
                         */
                        int totalNumberOfPages = 0;
                        /*
                         * Extract the pagination elements (<li>)
                         */
                        List<Element> paginationElements = d.select("div.programmes-page ol.pagination li.pagination__page");
                        /*
                         * If there are other pages than the current one
                         * there will be pagination and, therefore, pagination elements.
                         */
                        if(paginationElements.size() > 0) {
                            /*
                             * Get the last pagination element
                             */
                            Element lastElement = paginationElements.get(paginationElements.size() - 1).getAllElements().last();
                            /*
                             * The total number of pages is the number in the last
                             * pagination element.
                             */
                            totalNumberOfPages = Integer.parseInt(lastElement.getElementsByTag("a").html());
                            System.out.print(String.format("%31s", "PAGES: ") + totalNumberOfPages);
                        }
                        /*
                         * Ready to visit every page...
                         */
                        int currentPage = 1;
                        /*
                         * The first page is at the programRootUrl
                         * (https://www.bbc.co.uk/programmes/{programId}/episodes/player)
                         */
                        do {
                            System.out.println(String.format("%31s", "NOW IN PAGE: ") + currentPage);
                            /*
                             * all episodes in currentPage only
                             */
                            List<Element> episodes = d.select(cssClass);
                            if (episodes != null) {
                                /*
                                 * Iterate over the episodes in the currentPage
                                 */
                                for(Element e : episodes) {
                                    /*
                                     * Add the link to the discovered episodes to the
                                     * list that contains all episodes from all programs.
                                     */
                                    String episodeUrl = e.attr("href");
                                    listOfEpisodes.add(episodeUrl);
                                    /*
                                     * At this point, the URL to an episode has been
                                     * discovered. The next task is to parse the page
                                     * and get usable information, like title, and synopsis.
                                     * The extracted info is written to file and added to corpus.s
                                     */
                                    addToCorpus(episodeUrl);
                                }
                                System.out.println(String.format("%31s", "NUMBER OF EPISODES FOUND:") + episodes.size());

                            }
                            /*
                             * The next page is programRootUrl?page={pageCount}
                             * where programRootUrl = https://www.bbc.co.uk/programmes/{programId}/episodes/player
                             */
                            d = Jsoup.connect(programRootUrl + "?page=" + currentPage++).get();
                        } while(currentPage <= totalNumberOfPages);
                    }
                }
            }
        }
        System.out.println(String.format("%31s", "SIZE OF EPISODES' LIST ") + listOfEpisodes.size());
        return listOfEpisodes;
    }

    /*
     * The entire collection of BBC programs is too large to
     * deal with in a mini-course project. So, I will not attempt
     * to crawl the entire collection. However, if the program is
     * to be scaled up to crawl the entire collection, this
     * method (which, for now, crawls only the first pages of the
     * a to z list) can be used.
     */
    static List<String> findBBCPrograms() throws IOException {
        final String urlPrefix = "https://www.bbc.co.uk/programmes/a-z/by/";
        final String urlSuffix = "/player";
        final String cssClass = "div.programme__body h2.programme__titles a";
        /*
         * List that holds all BBC Radio programs.
         */
        List<String> listOfPrograms = new ArrayList<>();
        String url;

        /*
         * Only pages a-z are selected for crawling.
         */
        for(int i = 0x61; i <= 0x7a; i++) {
            char ch = (char) (i);
            url = urlPrefix + ch + urlSuffix;
            Document document = Jsoup.connect(url).get();
            List<Element> programmes =  document.select(cssClass);
            programmes.forEach((element) -> {
                String programLink = element.attr("href") + "/episodes/guide";
                /*
                 * The cssClass is the same for episode links
                 * add N episodes for each program
                 */
                listOfPrograms.add(programLink);
            });
        }
        listOfPrograms.forEach(System.out::println);
        return listOfPrograms;
    }

    /**
     * Return a list of BBC programs to be downloaded
     * from the selectedPrograms String array.
     *
     * Just a converter from array to list.
     */
    static List<String> getSelectedProgramsList(String[] selectedPrograms) {
        List<String> listOfPrograms = new ArrayList();
        for (String program : selectedPrograms) {
            if(PROGRAMS.containsKey(program)) {
                listOfPrograms.add(PROGRAMS.get(program));
            }
        }
        return listOfPrograms;
    }

    /**
     * Extract Program Name, Title, Synopsis,
     * Date, etc. and add a file to corpus.
     * @param episodeUrl the URL to the episode
     */
    static void addToCorpus(String episodeUrl) throws IOException {
        final String programTitleSelector = "div.br-masthead__title a";
        final String episodeTitleSelector = "div.island h1.no-margin";
        final String synopsisSelector = "div.synopsis-toggle__long";
        final String longestSynopsisSelector = "div.longest-synopsis";
        final String lastDateSelector = "span.broadcast-event__date";

        System.out.println("Connecting to " + episodeUrl);
        Connection connection = Jsoup.connect(episodeUrl);
        if(connection != null) {
            connection.ignoreHttpErrors(true);
            if(connection.execute().statusCode() == 200) {
                Document document = connection.get();
                if(document != null) {
                    final String programTitle = document.selectFirst(programTitleSelector).html();
                    final String episodeTitle = document.selectFirst(episodeTitleSelector).html();
                    Element synopsis = (document.selectFirst(synopsisSelector) != null) ?
                            document.selectFirst(synopsisSelector) :
                            document.selectFirst(longestSynopsisSelector);
                    final String programSynopsis = Jsoup.parse(synopsis.html()).text();
                    final String lastProgramDate = document.selectFirst(lastDateSelector).html();
                    StringBuilder sb = new StringBuilder();
                    sb.append("Program: " + programTitle);
                    sb.append("\n");
                    sb.append("Title: " + episodeTitle);
                    sb.append("\n");
                    sb.append("Link: " + episodeUrl);
                    sb.append("\n");
                    sb.append("Last Broadcast Date: ");
                    sb.append(lastProgramDate);
                    sb.append("\n");
                    sb.append(programSynopsis);
                    final String corporaDirectory = System.getenv("CORPORA_DIR");
                    final String corpus = "bbc";
                    final String separator = File.separator;
                    final String documentName = episodeUrl.substring(episodeUrl.lastIndexOf('/') + 1);

                    final String filePath = corporaDirectory + separator + corpus + separator + documentName;
                    File file = new File(filePath);
                    if(file.exists()) {
                        System.out.println("File " + documentName + " already exists.");
                    } else {
                        Path path = Paths.get(filePath);
                        Files.write(path, sb.toString().getBytes());
                    }
                }
            }
        }
    }
}
