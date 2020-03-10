package ntessema.csc575.preprocessor;

import ntessema.csc575.commons.PreprocessorException;
import ntessema.csc575.commons.ConfigurationManager;

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
