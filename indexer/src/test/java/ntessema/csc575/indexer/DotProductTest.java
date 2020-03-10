package ntessema.csc575.indexer;

import ntessema.csc575.commons.Document;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;

public class DotProductTest {

    private Document document;

    @Before
    public void init() {
        document = new Document("dummy-id");
        document.setDocumentVector(Stream.of(new Object[][] {
                { "A", 12.0 },
                { "B", 25.3 },
                { "C", 4.3 },
                { "D", 5.7 },
                { "E", 6.1 },
                { "F", 11.0 },
                { "G", 15.3 },
                { "H", 14.3 },
                { "I", 35.7 },
                { "J", 16.1 }

        }).collect(Collectors.toMap(x -> (String) x[0], x -> (Double) x[1])));
    }

    @Test
    public void dot_returns_the_dot_product_of_the_current_document_vector_and_the_argument_vector() {

        Document doc2 = new Document("doc2");

        Map<String, Double> vector = Stream.of(new Object[][] {
                { "A", 1.0 },
                { "B", 2.3 },
                { "C", 1.3 },
                { "D", 0.7 },
                { "E", 0.1 },
                { "F", 1.1 },
                { "G", 1.3 },
                { "H", 1.4 },
                { "I", 3.5 },
                { "J", 6.1 }
        }).collect(Collectors.toMap(x -> (String) x[0], x -> (Double) x[1]));
        doc2.setDocumentVector(vector);

        assertThat(document.dot(doc2), is(closeTo(355.55, 0.01)));
    }
}
