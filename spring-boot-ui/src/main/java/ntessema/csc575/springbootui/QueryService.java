package ntessema.csc575.springbootui;

import ntessema.csc575.documents.Document;
import ntessema.csc575.documents.DocumentReference;
import ntessema.csc575.indexer.Retriever;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * This service class executes the retriever
 * on a query and returns the result set.
 */

@Service
public class QueryService {

    public Map<DocumentReference, Double> getResults(Document query) throws
            IOException,
            URISyntaxException {

        /*
         * Instantiate a retriever object.
         */
        Retriever retriever = new Retriever();
        /*
         * Retrieve the results of query
         */
        Map<DocumentReference, Double> results =  retriever.retrieve(query);
        /*
         * Return results
         */
        return results;
    }
}
