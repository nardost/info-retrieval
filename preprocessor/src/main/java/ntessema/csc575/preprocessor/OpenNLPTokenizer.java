package ntessema.csc575.preprocessor;

import opennlp.tools.stemmer.PorterStemmer;
import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.tokenize.WhitespaceTokenizer;

import java.util.HashMap;
import java.util.Map;

public class OpenNLPTokenizer implements Tokenizer {

    @Override
    public Map<String, Double> tokenize(String document) {
        Map<String, Double> documentVector = new HashMap<>();
        opennlp.tools.tokenize.Tokenizer tokenizer = WhitespaceTokenizer.INSTANCE;
        Stemmer stemmer = new PorterStemmer();
        StopWords stopWords = StopWords.getInstance();
        String [] tokens;
        String [] lines = document.split("\n");
        for(String line : lines) {
            /**
             * (1) Tokenize
             * (2) Remove stop words
             * (2) For each term in String array
             *     (3) Stem term
             *     (4) If not already in tokens, add it. Increment weight.
             *         If already in tokens, increment weight.
             */
            tokens = tokenizer.tokenize(line.toLowerCase());
            for(String token : tokens) {
                //TODO: remove if stop word.
                //https://github.com/apache/opennlp-sandbox/blob/master/summarizer/src/main/java/opennlp/summarization/preprocess/StopWords.java
                if(stopWords.isStopWord(token.toLowerCase())) {
                    continue;
                }
                String stem = stemmer.stem(token).toString();
                if(documentVector.containsKey(stem)) {
                    double f = documentVector.get(stem);
                    documentVector.replace(stem, f + 1.0);
                } else {
                    documentVector.put(stem, 1.0);
                }
            }

        }
        return documentVector;
    }
}
