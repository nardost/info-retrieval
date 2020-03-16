package ntessema.csc575.query;

import ntessema.csc575.documents.Document;
import ntessema.csc575.preprocessor.Tokenizer;
import ntessema.csc575.preprocessor.TokenizerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

/**
 * The query processor class.
 *
 * Contains a single method that converts a string query into a Document object.
 *
 * No attempt has been made to expand queries. Perhaps this is
 * the reason why the Precision value on the benchmark data set is low.
 */
public class Query {

    /**
     * Construct a query document from the given query string.
     * @param query - the query string
     * @return the query document
     */
    public static Document createQueryFromString(String query) throws UnsupportedEncodingException {

        /*
         * Clean the query string
         */
        String cleanQuery = URLDecoder.decode(query, StandardCharsets.UTF_8.toString()).toLowerCase();
        /*
         * Assign unique IDs for queries (for what it is worth)
         */
        String queryId = UUID.randomUUID().toString();

        /*
         * Get the tokenizer
         */
        Tokenizer tokenizer = TokenizerFactory.createTokenizer();

        /*
         * Tokenize the query and produce the query vector
         * represented as a hashmap with the terms as key and the
         * term frequencies as values.
         */
        Map<String, Double> documentVector = tokenizer.tokenize(cleanQuery);
        Document document = new Document(queryId, documentVector);
        return document;
    }
}
