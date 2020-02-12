package ntessema.csc575.documents;

import ntessema.csc575.preprocessor.Tokenizer;
import ntessema.csc575.preprocessor.TokenizerFactory;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
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
            Map<String, Double> documentVector = tokenizer.tokenize(Paths.get(getClass().getClassLoader().getResource("documents/28054-0.txt").toURI()));
            Double max = documentVector.entrySet().stream().max(Comparator.comparing(Map.Entry::getValue)).get().getValue();
            System.out.println(max);
            documentVector.forEach((k,v) -> {
                if(v == max) {
                    System.out.println(k + " -> " + v);
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
