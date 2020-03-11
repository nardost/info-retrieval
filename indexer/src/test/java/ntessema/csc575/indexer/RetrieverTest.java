package ntessema.csc575.indexer;

import ntessema.csc575.commons.Document;
import ntessema.csc575.query.Query;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RunWith(Parameterized.class)
public class RetrieverTest {

    private Retriever retriever;

    private String queryString;

    public RetrieverTest(String query) {
        this.queryString = query;
    }

    @Parameters
    public static Collection<Object> queries() {
        return Stream.of(new Object[] {
                "CHINESE BLAMING",
                "RED CHINESE BLAMING OF ECONOMIC TROUBLES AND THE TREATY-BREAKING WITHDRAWAL OF RUSSIAN TECHNICAL ASSISTANCE FOR SERIOUS DELAYS IN ITS DEVELOPMENT PROGRAM .",
                "PROPOSALS FOR A UNIFIED EUROPE INDEPENDENT OF THE U.S .",
                "EFFORTS OF AMBASSADOR HENRY CABOT LODGE TO GET VIET NAM'S PRESIDENT DIEM TO CHANGE HIS POLICIES OF POLITICAL REPRESSION."
        }).collect(Collectors.toList());
    }

    @Before
    public void init() {
        retriever = new Retriever();
    }

    @Test
    public void test_retriever() {

        try {
            Document queryDocument = Query.createQueryFromString(queryString);
            if(queryDocument == null) {
                System.out.println("Query is null");
                return;
            }
            Map<DocumentReference, Double> results = retriever.retrieve(queryDocument);
            if(results == null) {
                System.out.println("No documents returned.");
                return;
            }
            queryDocument.getDocumentVector().forEach((term, weight) -> {
                System.out.println("Query Term: " + term + ", Weight: " + weight);
            });
            StringBuilder sb = new StringBuilder();
            StringBuilder sb2 = new StringBuilder();
            sb.append(queryString);
            sb.append(":");
            int count = 1;
            for(Map.Entry<DocumentReference, Double> docRef : results.entrySet()) {
                if(count > 20) break;
                DocumentReference reference = docRef.getKey();
                double score = docRef.getValue();
                sb.append(" ");
                sb.append(reference.getPath().getFileName().toString().replace(".txt", ""));
                sb.append("(" + String.format("%.3f", score) + ")");
                sb.append(",");
                count++;
            }
            sb.replace(sb.length() - 1, sb.length(), "");
            System.out.println(sb.toString());
        } catch(IOException ioe) {
            ioe.printStackTrace();
        } catch (URISyntaxException use) {
            use.printStackTrace();
        }
    }
}
