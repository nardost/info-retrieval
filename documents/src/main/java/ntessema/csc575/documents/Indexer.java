package ntessema.csc575.documents;

import ntessema.csc575.commons.ConfigurationManager;
import ntessema.csc575.preprocessor.Tokenizer;
import ntessema.csc575.preprocessor.TokenizerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Indexer {

    /**
     * Get all documents in the corpus and save them in a list.
     * @return the  list of documents in the corpus
     * @throws URISyntaxException
     */
    public List<Document> getAllDocuments() throws
            IOException,
            URISyntaxException {
        final String corpusDirectory = ConfigurationManager.getConfiguration("corpusDirectory");
        final String separator = File.separator;
        List<Document> documents = new LinkedList<>();
        File documentsDirectory = new File(getClass().getClassLoader().getResource(corpusDirectory).getFile());

        if(documentsDirectory != null && documentsDirectory.isDirectory()) {
            for(String file : documentsDirectory.list()) {
                String pathToDocument = corpusDirectory + separator + file;
                Path path = Paths.get(getClass().getClassLoader().getResource(pathToDocument).toURI());
                Document document = getDocumentFromFile(path);
                if(document != null) documents.add(document);
            }
        }
        return documents;
    }

    /**
     * Creates a document vector from an incoming plain document.
     * @param path - The path of the file to be vectorized.
     * @return a Map object representing the vector.
     */
    public Document getDocumentFromFile(Path path) throws IOException {
        List<String> lines = Files.readAllLines(path);
        String program;
        String title;
        String link;
        String date;

        String programPattern = "Program:";
        String titlePattern = "Title:";
        String linkPattern = "Link:";
        String datePattern = "Date:";

        int numberOfLines = lines.size();
        /*
         * Documents in the corpus must strictly follow
         * a certain pattern.
         * Line 1: "Program: \\.*"
         * Line 2: "Title: \\.*"
         * Line 3: "Link: \\.*"
         *
         */
        if(lines.size() < 4 || !lines.get(0).matches(programPattern + ".*") ||
                        !lines.get(1).matches(titlePattern + ".*") ||
                        !lines.get(2).matches(linkPattern + ".*") ||
                        !lines.get(3).matches(datePattern + ".*")) {
            return null;
        }
        program = (lines.get(0).split(programPattern).length > 1) ? lines.get(0).split(programPattern)[1] : "unknown-program";
        title = (lines.get(1).split(titlePattern).length > 1) ? lines.get(1).split(titlePattern)[1] : "title-not-available";
        link = (lines.get(2).split(linkPattern).length > 1) ? lines.get(2).split(linkPattern)[1] : "link-not-available";
        date = (lines.get(3).split(datePattern).length > 1) ? lines.get(3).split(datePattern)[1] : "date-unknown";

        Tokenizer tokenizer = TokenizerFactory.createTokenizer();
        Map<String, Double> documentVector = tokenizer.tokenize(path);
        Document document = new Document(
                path.getFileName().toString(),
                program.replaceFirst("\\s+", ""),
                title.replaceFirst("\\s+", ""),
                link.replaceFirst("\\s+", ""),
                date.replaceFirst("\\s+", ""),
                documentVector);
        return document;
    }
}
