package ntessema.csc575.documents;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RunWith(Parameterized.class)
public class RetrieverTest {

    private Retriever retriever;

    private String term;
    private Double frequency;

    public RetrieverTest(String term, Double occurrence) {
        this.term = term;
        this.frequency = occurrence;
    }

    @Parameters
    public static Collection<Object[]> queries() {
        return Stream.of(new Object[][] {
                { "Farmelo", 1.0 },
                { "Khaldun", 1.0 },
                { "Bragg", 3.0 }
        }).collect(Collectors.toList());
    }

    @Before
    public void init() {
        retriever = new Retriever();
    }

    @Test
    public void test_retriever() {
        Map<String, Double> queryVector = new HashMap<>();
        Document query = new Document("Test-Query", queryVector);
        queryVector.put(term, frequency);
        try {
            Map<DocumentReference, Double> results = retriever.retrieve(query);
            results.forEach((reference, score) -> {
                System.out.println(term + ": " + reference.getPath().getFileName().toString() + " -------> " + score);
            });
        } catch(IOException ioe) {
            ioe.printStackTrace();
        } catch (URISyntaxException use) {
            use.printStackTrace();
        }
    }
}
