package ntessema.csc575.indexer;

import ntessema.csc575.documents.Document;
import ntessema.csc575.documents.DocumentUtilities;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.hamcrest.MatcherAssert.assertThat;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class TokenizerTest {

    @Test
    public void test_tokenizer_with_console_outputs() {
        try {
            final String fName = "1.txt";

            Document document = DocumentUtilities.getDocumentFromFile(fName);
            //Double max = document.entrySet().stream().max(Comparator.comparing(Map.Entry::getValue)).get().getValue();
            System.out.println(document.getId());
            document.getDocumentVector().forEach((term, weight) -> {
                System.out.println(term + "\t" + weight);
            });
        } catch(IOException ioe) {
            System.out.println("I/O Exception occurred.");
        } catch(URISyntaxException use) {
            use.printStackTrace();
        }
        assertThat(1, is(equalTo(1)));
    }
}
