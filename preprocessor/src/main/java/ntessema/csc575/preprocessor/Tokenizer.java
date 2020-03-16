package ntessema.csc575.preprocessor;

import java.util.Map;

/**
 * An interface for Tokenizers. Used to switch between
 * different libraries parametrically.
 */
public interface Tokenizer {
    Map<String, Double> tokenize(String fileName);
}
