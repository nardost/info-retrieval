package ntessema.csc575.documents;

import ntessema.csc575.commons.Utilities;
import ntessema.csc575.preprocessor.Tokenizer;
import ntessema.csc575.preprocessor.TokenizerFactory;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;

import static org.hamcrest.MatcherAssert.assertThat;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class TokenizerTest {

    private Tokenizer tokenizer;

    @Before
    public void init() {
        this.tokenizer = TokenizerFactory.createTokenizer();
    }

    @Test
    public void test_tokenizer_with_console_outputs() {
        try {
            final String fName = "1.txt";
            Path path = Utilities.getInstance().getPathFromFileName(fName);

            Document document = new Indexer().getDocumentFromFile(path);
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
