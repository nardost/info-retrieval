package ntessema.csc575.documents;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(Parameterized.class)
public class MultiplyTest {

    private Document document;
    private double factor;

    public MultiplyTest(double factor) {
        this.factor = factor;
    }

    @Parameters
    public static List<Double> factors() {
        return Arrays.asList(new Double[] {
                3.1, 1.2, 1.1, 0.0, 11.1, 10.0, 3.1, 2.0, 1.0, 4.3
        });
    }

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
    public void multiply_correctly_multiplies_each_term_weight_by_same_factor() {
        Map<String, Double> shallowCopy = new HashMap<>();
        shallowCopy.putAll(document.getDocumentVector());
        document.multiply(factor);
        document.getDocumentVector().forEach((k, v) -> {
            shallowCopy.forEach((x,y) -> {
                if(k == x) {
                    assertThat(v, equalTo(factor * y));
                }
            });
        });
    }
}
