package ntessema.csc575.query;

import ntessema.csc575.commons.Document;
import ntessema.csc575.preprocessor.Tokenizer;
import ntessema.csc575.preprocessor.TokenizerFactory;

import java.util.Map;
import java.util.UUID;

public class Query {

    /**
     * Construct a query document from the given query string.
     * @param query - the query string
     * @return the query document
     */
    public static Document createQueryFromString(String query) {

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
        Map<String, Double> documentVector = tokenizer.tokenize(query);
        Document document = new Document(queryId, documentVector);
        return document;
    }
}
