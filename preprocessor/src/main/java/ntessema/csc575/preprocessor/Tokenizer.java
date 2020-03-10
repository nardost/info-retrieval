package ntessema.csc575.preprocessor;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Map;

public interface Tokenizer {
    Map<String, Double> tokenize(String fileName);
}
