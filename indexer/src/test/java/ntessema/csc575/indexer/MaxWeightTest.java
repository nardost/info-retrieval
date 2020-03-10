package ntessema.csc575.indexer;

import ntessema.csc575.commons.Document;
import org.junit.Before;
import org.junit.Test;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class MaxWeightTest {

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
    public void getMaxWeight_returns_the_correct_maximum_weight_in_a_document_vector() {
        assertThat(document.getMaxWeight(), equalTo(35.7));
    }

}
