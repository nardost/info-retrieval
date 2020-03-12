package ntessema.csc575.springbootui;

import ntessema.csc575.commons.Document;
import ntessema.csc575.indexer.Retriever;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;

@Service
public class QueryService {
    private Retriever retriever;
    public int getResults(Document query) throws IOException, URISyntaxException {
        retriever = new Retriever();
        return retriever.retrieve(query).size();
    }
}
