package ntessema.csc575.indexer;

import ntessema.csc575.commons.DocumentReference;
import org.junit.Before;
import org.junit.Test;

import java.net.URISyntaxException;

public class DocumentReferenceTest {

    private Indexer indexer;

    @Before
    public void init() {
        indexer = new Indexer();
    }

    @Test
    public void given_a_path_method_returns_document_reference() {
        final String fName = "1.txt";

        try {

            DocumentReference reference = indexer.getDocumentReferenceFromFile(fName);
            System.out.println("PATH: " + reference.getPath().toAbsolutePath().toString());
            System.out.println("LENGTH: " + reference.getLength());
        } catch (URISyntaxException use) {
            use.printStackTrace();
        }
    }
}
