package ntessema.csc575.preprocessor;

import ntessema.csc575.commons.ConfigurationManager;

/**
 * This class instantiates a specific Tokenizer based on
 * the value of the tokenizer configuration parameter.
 */
public class TokenizerFactory {

    private TokenizerFactory() {
    }

    public static Tokenizer createTokenizer() throws PreprocessorException {
        String tokenizer = ConfigurationManager.getConfiguration("tokenizer");
        switch(tokenizer) {
            case "opennlp":
                return new OpenNLPTokenizer();
            default:
                throw new PreprocessorException("Unrecognized tokenizer " + tokenizer);
        }
    }
}
