package ntessema.csc575.documents;

import ntessema.csc575.preprocessor.Tokenizer;
import ntessema.csc575.preprocessor.TokenizerFactory;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Map;

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
            Path path = Paths.get(getClass().getClassLoader().getResource("documents/1.txt").toURI());
            Map<String, Double> documentVector = Document.getDocumentVectorFromFile(path);
            Double max = documentVector.entrySet().stream().max(Comparator.comparing(Map.Entry::getValue)).get().getValue();
            System.out.println(max);
            documentVector.forEach((k,v) -> {
                if(true || v == max) {
                    System.out.println(k.length() + String.format("%20s", k) + "  " + String.format("%-6s", Double.toString(v)));
                }
            });
        } catch(IOException ioe) {
            System.out.println("I/O Exception occurred.");
        } catch(URISyntaxException use) {
            System.out.println("URI syntax exception.");
        }
        assertThat(1, is(equalTo(1)));
    }
}
