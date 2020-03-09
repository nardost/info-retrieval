package ntessema.csc575.documents;

import ntessema.csc575.commons.Utilities;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;

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
            Path path = Utilities.getInstance().getPathFromFileName(fName);
            DocumentReference reference = indexer.getDocumentReferenceFromFile(path);
            System.out.println("PATH: " + reference.getPath().toAbsolutePath().toString());
            System.out.println("LENGTH: " + reference.getLength());
        } catch(IOException ioe) {
            ioe.printStackTrace();
        } catch (URISyntaxException use) {
            use.printStackTrace();
        }
    }
}
