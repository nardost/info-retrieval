package ntessema.csc575.indexer;

import ntessema.csc575.documents.DocumentReference;
import ntessema.csc575.documents.DocumentUtilities;
import org.junit.Test;

import java.net.URISyntaxException;

public class DocumentReferenceTest {

    @Test
    public void given_a_path_method_returns_document_reference() {
        final String fName = "1.txt";

        try {

            DocumentReference reference = DocumentUtilities.getDocumentReferenceFromFile(fName);
            System.out.println("PATH: " + reference.getPath().toAbsolutePath().toString());
            System.out.println("LENGTH: " + reference.getLength());
        } catch (URISyntaxException use) {
            use.printStackTrace();
        }
    }
}
