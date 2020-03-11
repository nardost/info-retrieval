package ntessema.csc575.indexer;

import ntessema.csc575.commons.BBCDocument;
import ntessema.csc575.commons.Document;
import ntessema.csc575.commons.Utilities;
import ntessema.csc575.preprocessor.Tokenizer;
import ntessema.csc575.preprocessor.TokenizerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Indexer {

    /**
     * Get all documents in the corpus and save them in a list.
     * @return the  list of documents in the corpus
     * @throws URISyntaxException
     */
    public List<Document> getAllDocuments() throws
            IOException,
            URISyntaxException {

        final String corpus = Utilities.getInstance().getCorpus();
        final String corpusDirectory = File.separator + corpus;

        List<Document> documents = new LinkedList<>();
        File documentsDirectory = Utilities.getInstance().getFile(corpusDirectory);

        if(documentsDirectory != null && documentsDirectory.isDirectory()) {
            for(String file : documentsDirectory.list()) {

                Document document = getDocumentFromFile(file);
                if(document != null) {
                    documents.add(document);
                }
            }
        }
        return documents;
    }

    public Map<String, DocumentReference> getAllDocumentReferences() throws
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
     * Creates a document vector from an incoming plain document.
     * @param fileName - The name of the file to be vectorized.
     * @return a Map object representing the vector.
     */
    public Document getDocumentFromFile(String fileName) throws IOException, URISyntaxException {

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

    /**
     * Given a path, get the document reference object
     */
    public DocumentReference getDocumentReferenceFromFile(String fileName) throws URISyntaxException {
        Path path = Utilities.getInstance().getPathFromFileName(fileName);
        DocumentReference reference = new DocumentReference(path);
        return reference;
    }

    /**
     * Create an inverted index
     */
    public Map<String, TokenInfo> createInvertedIndex() throws
            IOException,
            URISyntaxException {

        Map<String, TokenInfo> invertedIndex = new HashMap<>();

        Map<String, DocumentReference> documentReferences = getAllDocumentReferences();
        final int NUMBER_OF_DOCUMENTS = documentReferences.size();
        /*
         * For each document
         */
        for(Map.Entry<String, DocumentReference> item : documentReferences.entrySet()) {
            String fileName = item.getKey();
            DocumentReference documentReference = item.getValue();
            /*
             * Get the entire document with the document-term vector
             */
            Document document = getDocumentFromFile(fileName);
            Map<String, Double> vector = document.getDocumentVector();
            for(Map.Entry<String, Double> tokenItem : vector.entrySet()) {
                String term = tokenItem.getKey();
                //TODO: is this the term frequency?
                Double termWeight = tokenItem.getValue();

                if(!invertedIndex.containsKey(term)) {
                    invertedIndex.put(term, new TokenInfo(term));
                }
                TokenInfo tokenInfo = invertedIndex.get(term);
                TokenOccurrence tokenOccurrence = new TokenOccurrence(documentReference, termWeight);
                tokenInfo.getOccurrence().add(tokenOccurrence);
            }
        }
        /*
         * compute IDF
         */
        computeIdf(invertedIndex, NUMBER_OF_DOCUMENTS);
        computeDocumentLengths(invertedIndex);

        return invertedIndex;
    }

    /**
     * Compute idf for all tokens in the inverted index
     */
    private void computeIdf(Map<String, TokenInfo> invertedIndex, int numberOfDocuments) {
        for(Map.Entry<String, TokenInfo> token : invertedIndex.entrySet()) {
            TokenInfo tokenInfo = token.getValue();

            int documentFrequency = tokenInfo.getOccurrence().size();
            double idf = Math.log((double)numberOfDocuments / (double) documentFrequency) / Math.log(2.0);
            tokenInfo.setIdf(idf);
        }
    }

    /**
     * Compute document length
     */
    private void computeDocumentLengths(Map<String, TokenInfo> invertedIndex) {
        for(Map.Entry<String, TokenInfo> token : invertedIndex.entrySet()) {
            TokenInfo tokenInfo = token.getValue();
            double idf = tokenInfo.getIdf();
            List<TokenOccurrence> postings = tokenInfo.getOccurrence();
            for(TokenOccurrence posting : postings) {
                double tf = posting.getFrequency();
                DocumentReference d = posting.getDocumentReference();
                double length = d.getLength();
                length +=  tf * tf * idf * idf;
                posting.getDocumentReference().setLength(length);
            }
        }
        /*
         * The square root of a document reference should only be done once.
         * The iteration below comes back to a document reference as
         * it walks through the inverted index. Unless we remember
         * which document has its length already square rooted, the length
         * will be square rooted repeatedly and will converge to 1.0.
         *
         * Mark a document reference as done if the
         * square root of the length has been computed once.
         */
        List<DocumentReference> done = new ArrayList<>();
        for(Map.Entry<String, TokenInfo> token : invertedIndex.entrySet()) {
            TokenInfo tokenInfo = token.getValue();
            List<TokenOccurrence> postings = tokenInfo.getOccurrence();
            for(TokenOccurrence posting : postings) {
                DocumentReference ref = posting.getDocumentReference();
                double length = ref.getLength();
                if(done.indexOf(ref) < 0) {
                    ref.setLength(Math.sqrt(length));
                    done.add(ref);
                }
            }
        }
    }

    /**
     * TODO: This should be moved elsewhere. The content has to
     *  come from lines 5 onwards.
     * @param path
     * @return
     * @throws IOException
     */
    public BBCDocument getBBCDocumentFromFile(Path path) throws IOException {
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
        if(numberOfLines < 4 || !lines.get(0).matches(programPattern + ".*") ||
                !lines.get(1).matches(titlePattern + ".*") ||
                !lines.get(2).matches(linkPattern + ".*") ||
                !lines.get(3).matches(datePattern + ".*")) {
            //return null;
            program = "unknown-program";
            title = "title-not-available";
            link = "link-not-available";
            date = "date-unknown";
        } else {
            program = (lines.get(0).split(programPattern).length > 1) ? lines.get(0).split(programPattern)[1] : "unknown-program";
            title = (lines.get(1).split(titlePattern).length > 1) ? lines.get(1).split(titlePattern)[1] : "title-not-available";
            link = (lines.get(2).split(linkPattern).length > 1) ? lines.get(2).split(linkPattern)[1] : "link-not-available";
            date = (lines.get(3).split(datePattern).length > 1) ? lines.get(3).split(datePattern)[1] : "date-unknown";
        }
        return new BBCDocument(path.getFileName().toString(),
                program,
                title,
                link,
                date,
                title);
    }
}
