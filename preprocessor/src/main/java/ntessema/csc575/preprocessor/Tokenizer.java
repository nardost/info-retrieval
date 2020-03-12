package ntessema.csc575.preprocessor;

import java.util.Map;

public interface Tokenizer {
    Map<String, Double> tokenize(String fileName);
}
