package ntessema.csc575.documents;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

public class GetAllDocumentReferencesTest {

    private Indexer indexer;

    @Before
    public void init() {
        indexer = new Indexer();
    }

    @Test
    public void all_documents_are_loaded_in_the_hash_map() {
        try {
            Map<String, DocumentReference> references = indexer.getAllDocumentReferences();
            references.forEach((fileName, reference) -> {
                System.out.println(fileName + " -> " + reference.getPath().toString());
            });
        } catch(IOException ioe) {
            ioe.printStackTrace();
        } catch (URISyntaxException use) {
            use.printStackTrace();
        }
    }
}
