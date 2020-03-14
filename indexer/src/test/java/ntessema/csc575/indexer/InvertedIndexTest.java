package ntessema.csc575.indexer;

import ntessema.csc575.commons.Utilities;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

public class InvertedIndexTest {

    @Test
    public void iterate_over_the_inverted_index() {
        try {
            Map<String, TokenInfo> invertedIndex = Indexer.getInstance().getInvertedIndex();
            invertedIndex.forEach((token, tokenInfo) -> {
                System.out.println(String.format("%20s", token + "(" + tokenInfo.getIdf() + ")") + " -> " + Utilities.getInstance().listToString(tokenInfo.getOccurrence()));
            });
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (URISyntaxException use) {
            use.printStackTrace();
        }
    }
}
