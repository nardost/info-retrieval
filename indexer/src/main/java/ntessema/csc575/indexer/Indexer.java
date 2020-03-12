package ntessema.csc575.indexer;

import ntessema.csc575.documents.Document;
import ntessema.csc575.documents.DocumentReference;
import ntessema.csc575.documents.DocumentUtilities;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

public class Indexer {

    /**
     * Create an inverted index.
     * Algorithm from slide # 10 & 13.
     */
    public Map<String, TokenInfo> createInvertedIndex() throws
            IOException,
            URISyntaxException {

        /*
         * Prepare an empty hash map for the inverted index.
         */
        Map<String, TokenInfo> invertedIndex = new HashMap<>();

        /*
         * Get all the documents in the corpus. No need to load the entire
         * document content. Only references are needed for indexing purpose.
         */
        Map<String, DocumentReference> documentReferences = DocumentUtilities.getAllDocumentReferences();
        final int NUMBER_OF_DOCUMENTS = documentReferences.size();
        /*
         * For each document
         */
        for(Map.Entry<String, DocumentReference> item : documentReferences.entrySet()) {
            String fileName = item.getKey();
            DocumentReference documentReference = item.getValue();
            /*
             * Get the entire document with the document-term vector.
             * We need the contents of the document to count the term occurrences.
             */
            Document document = DocumentUtilities.getDocumentFromFile(fileName);
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
        /*
         * Compute norm of documents.
         */
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
}
