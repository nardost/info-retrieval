package ntessema.csc575.indexer;

import ntessema.csc575.documents.Document;
import ntessema.csc575.documents.DocumentUtilities;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public class GetAllDocumentsTest {

    @Test
    public void get_all_documents_in_the_corpus() {
        try {
            List<Document> documents = DocumentUtilities.getAllDocuments();
            System.out.println("Number of documents: " + documents.size());
            StringBuilder sb = new StringBuilder();
            documents.forEach(document -> {
                String separator = ",";
                sb.append(document.getId());
                sb.append(separator);
                sb.append("\n");
            });
            System.out.println(sb.toString());
        } catch (URISyntaxException use) {
            use.printStackTrace();
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
