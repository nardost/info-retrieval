package ntessema.csc575.indexer;

import ntessema.csc575.documents.DocumentReference;
import ntessema.csc575.documents.DocumentUtilities;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

public class GetAllDocumentReferencesTest {

    @Test @Ignore
    public void all_documents_are_loaded_in_the_hash_map() {
        try {
            Map<String, DocumentReference> references = DocumentUtilities.getAllDocumentReferences();
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
