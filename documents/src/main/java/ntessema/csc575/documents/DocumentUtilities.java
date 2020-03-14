package ntessema.csc575.documents;

import ntessema.csc575.commons.Utilities;
import ntessema.csc575.preprocessor.Tokenizer;
import ntessema.csc575.preprocessor.TokenizerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DocumentUtilities {

    /*
     * We don't need to instantiate this class
     * as all methods are static methods.
     */
    private DocumentUtilities() {}

    /**
     * Creates a document vector from an incoming plain document.
     * @param fileName - The name of the file to be vectorized.
     * @return a Map object representing the vector.
     */
    public static Document getDocumentFromFile(String fileName) throws IOException, URISyntaxException {

        Path path = Utilities.getInstance().getPathFromFileName(fileName);
        Tokenizer tokenizer = TokenizerFactory.createTokenizer();
        Stream<String> lines = Files.lines(path);
        String documentLines = lines.collect(Collectors.joining("\n"));
        lines.close();
        Map<String, Double> documentVector = tokenizer.tokenize(documentLines);
        Document document = new Document(
                path.getFileName().toString(),
                documentVector);
        return document;
    }
    public static Map<String, DocumentReference> getAllDocumentReferences() throws
            URISyntaxException,
            IOException {

        final String corpus = Utilities.getInstance().getCorpus();
        final String corpusDirectory = File.separator + corpus;

        File documentDirectory = Utilities.getInstance().getFile(corpusDirectory);

        Map<String, DocumentReference> documentReferences = new HashMap<>();

        if(documentDirectory != null && documentDirectory.isDirectory()) {
            for(String file : documentDirectory.list()) {

                Document document = getDocumentFromFile(file);
                if(document != null) {
                    Path path = Utilities.getInstance().getPathFromFileName(file);
                    DocumentReference reference = new DocumentReference(path);
                    documentReferences.put(file, reference);
                }
            }
        }

        return documentReferences;
    }


    /**
     * Given a path, get the document reference object
     */
    public static DocumentReference getDocumentReferenceFromFile(String fileName) throws URISyntaxException {
        Path path = Utilities.getInstance().getPathFromFileName(fileName);
        DocumentReference reference = new DocumentReference(path);
        return reference;
    }


    /**
     * Get all documents in the corpus and save them in a list.
     * @return the  list of documents in the corpus
     * @throws URISyntaxException
     */
    public static List<Document> getAllDocuments() throws
            IOException,
            URISyntaxException {

        final String corpus = Utilities.getInstance().getCorpus();
        final String corpusDirectory = File.separator + corpus;

        List<Document> documents = new LinkedList<>();
        File documentsDirectory = Utilities.getInstance().getFile(corpusDirectory);

        if(documentsDirectory != null && documentsDirectory.isDirectory()) {
            for(String file : documentsDirectory.list()) {

                Document document = DocumentUtilities.getDocumentFromFile(file);
                if(document != null) {
                    documents.add(document);
                }
            }
        }
        return documents;
    }

    /**
     * TODO: This should be moved elsewhere. The content has to
     *  come from lines 5 onwards.
     * @param path
     * @return
     * @throws IOException
     */
    public static BBCDocument getBBCDocumentFromFile(Path path) throws IOException {
        /*
         * Limit the number of characters in the synopsis of an episode.
         */
        final int SYNOPSIS_CHAR_LIMIT = 1000;
        List<String> lines = Files.readAllLines(path);

        String program;
        String title;
        String link;
        String date;
        String synopsis;

        String programPattern = "Program:";
        String titlePattern = "Title:";
        String linkPattern = "Link:";
        String datePattern = "Last Broadcast Date:";

        int numberOfLines = lines.size();
        /*
         * Documents in the corpus must strictly follow
         * a certain pattern.
         * Line 1: "Program: \\.*"
         * Line 2: "Title: \\.*"
         * Line 3: "Link: \\.*"
         *
         */
        if(numberOfLines < 4 || !lines.get(0).matches(programPattern + ".*") ||
                !lines.get(1).matches(titlePattern + ".*") ||
                !lines.get(2).matches(linkPattern + ".*") ||
                !lines.get(3).matches(datePattern + ".*")) {

            program = "Program Name Not Available";
            title = "Episode Title Not Available";
            link = "Link Not Available";
            date = "Last Broadcast Date Not Available";
            synopsis = "Synopsis Not Available";
        } else {
            program = (lines.get(0).split(programPattern).length > 1) ? lines.get(0).split(programPattern)[1] : "";
            title = (lines.get(1).split(titlePattern).length > 1) ? lines.get(1).split(titlePattern)[1] : "";
            link = (lines.get(2).split(linkPattern).length > 1) ? lines.get(2).split(linkPattern)[1] : "";
            date = (lines.get(3).split(datePattern).length > 1) ? lines.get(3).split(datePattern)[1] : "";
            StringBuilder sb = new StringBuilder();
            for(int i = 4; i < lines.size(); i++) {
                sb.append(lines.get(i));
                sb.append("\n");
            }
            /*
             * The "Show less" string is everywhere. Remove it here.
             */
            synopsis = sb.toString().replace("Show less", "");
        }
        return new BBCDocument(path.getFileName().toString(),
                program,
                title,
                link,
                date,
                /*
                 * If synopsis length exceeds limit, truncate and append "..."
                 */
                (synopsis.length() > SYNOPSIS_CHAR_LIMIT) ? synopsis.substring(0, SYNOPSIS_CHAR_LIMIT) + "..." : synopsis);
    }
}
