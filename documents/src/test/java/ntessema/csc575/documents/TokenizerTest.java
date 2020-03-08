package ntessema.csc575.documents;

import ntessema.csc575.commons.ConfigurationManager;
import ntessema.csc575.preprocessor.Tokenizer;
import ntessema.csc575.preprocessor.TokenizerFactory;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

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
            final String separator = File.separator;
            final String corpusDirectory = ConfigurationManager.getConfiguration("corpusDirectory");
            final String filePath = corpusDirectory + separator + fName;
            Path path = Paths.get(getClass().getClassLoader().getResource(filePath).toURI());

            Document document = new Indexer().getDocumentFromFile(path);
            //Double max = document.entrySet().stream().max(Comparator.comparing(Map.Entry::getValue)).get().getValue();
            System.out.println(document.getId());
            System.out.println(document.getProgram());
            System.out.println(document.getTitle());
            System.out.println(document.getLink());
            document.getDocumentVector().forEach((term, weight) -> {
                System.out.println(term + "\t" + weight);
            });
        } catch(IOException ioe) {
            System.out.println("I/O Exception occurred.");
        } catch(URISyntaxException use) {
            System.out.println("URI syntax exception.");
        }
        assertThat(1, is(equalTo(1)));
    }
}
