package ntessema.csc575.documents;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Retriever {

    public Map<DocumentReference, Double> retrieve(Document query) throws
            IOException,
            URISyntaxException {

        Map<DocumentReference, Double> results = new HashMap<>();

        Map<String, Double> queryVector = query.getDocumentVector();

        Indexer indexer = new Indexer();
        Map<String, TokenInfo> invertedIndex = indexer.createInvertedIndex();

        for(Map.Entry<String, Double> queryTerm : queryVector.entrySet()) {
            String token = queryTerm.getKey();
            double frequency = queryTerm.getValue();

            /*
             * Go inside the inverted index and retrieve TokenInfo for token
             */
            TokenInfo tokenInfo = invertedIndex.get(token);
            double idf = tokenInfo.getIdf();
            /*
             * K = number of token occurrences in query = frequency
             */
            /*
             * W = Set the weight of token in query to frequency * idf
             */
            double weight = frequency * idf;
            queryVector.put(token, weight);
            /*
             * List of token occurrences of token in the inverted index
             */
            List<TokenOccurrence> postings = tokenInfo.getOccurrence();
            for(TokenOccurrence posting : postings) {
                DocumentReference d = posting.getDocumentReference();
                /*
                 * C = Term Frequency of token in document
                 */
                double tf = posting.getFrequency();
                /*
                 * Document d was not previously retrieved.
                 */
                if(!results.containsKey(d)) {
                    results.put(d, 0.0);
                }
                /*
                 * Increment document d's score by W*I*C
                 */
                double score = results.get(d);
                results.put(d, score + weight * idf * tf);
            }

        }

        double queryLength = computeLengthOfQueryVector(queryVector);

        for(Map.Entry<DocumentReference, Double> result : results.entrySet()) {
            DocumentReference documentReference = result.getKey();
            /*
             * S = current accumulated score of document
             */
            double score = result.getValue();
            /*
             * Y = length od document
             */
            double documentLength = documentReference.getLength();
            double normalizedScore = score / (documentLength * queryLength);
            results.put(documentReference, normalizedScore);
        }

        //TODO: sort results

        return results;
    }

    /**
     * Compute the length of a query vector.
     * @param queryVector
     * @return
     */
    private double computeLengthOfQueryVector(Map<String, Double> queryVector) {
        double accumulator = 0.0;
        for(Map.Entry<String, Double> q : queryVector.entrySet()) {
            double weight = q.getValue();
            accumulator += weight * weight;
        }
        return Math.sqrt(accumulator);
    }
}
