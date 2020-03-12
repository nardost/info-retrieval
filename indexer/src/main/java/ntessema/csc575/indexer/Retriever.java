package ntessema.csc575.indexer;

import ntessema.csc575.documents.Document;
import ntessema.csc575.documents.DocumentReference;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

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

            /*
             * If token is not in the inverted index, skip.
             */
            if(tokenInfo == null) {
                continue;
            }
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

        /*
         * sort results by score
         */
        Map<DocumentReference, Double> sortedByScoreDescending = results
                .entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (unsorted, sorted) -> sorted, LinkedHashMap::new));

        return sortedByScoreDescending;
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


    /**
     * Quick and dirty method to test UI
     * @param term
     * @return
     */
    public String search(String term) {
        Map<String, Double> queryVector = new HashMap<>();
        Double frequency = 1.0;
        Document query = new Document("Test-Query", queryVector);
        queryVector.put(term, frequency);
        String result = null;
        try {
            Map<DocumentReference, Double> results = retrieve(query);
            StringBuilder sb = new StringBuilder();
            results.forEach((reference, score) -> {
                sb.append(term + ": " + reference.getPath().getFileName().toString() + " -------> " + score);
                sb.append("\n");
            });
            result = sb.toString();
        } catch(IOException ioe) {
            ioe.printStackTrace();
        } catch (URISyntaxException use) {
            use.printStackTrace();
        }
        return result;
    }
}
