package ntessema.csc575.indexer;

import ntessema.csc575.commons.Utilities;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * This test creates an inverted index and outputs the tokens
 * with their idf and their postings.
 */
public class InvertedIndexTest {

    @Test
    public void iterate_over_the_inverted_index() {
        try {
            Map<String, TokenInfo> invertedIndex = Indexer.getInstance().getInvertedIndex();
            StringBuilder sb = new StringBuilder();
            invertedIndex.forEach((token, tokenInfo) -> {
                sb.append(String.format("", token + "(" + tokenInfo.getIdf() + ")"));
                sb.append(" -> ");
                sb.append(Utilities.getInstance().listToString(tokenInfo.getOccurrence()));
            });
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (URISyntaxException use) {
            use.printStackTrace();
        }
    }
}
