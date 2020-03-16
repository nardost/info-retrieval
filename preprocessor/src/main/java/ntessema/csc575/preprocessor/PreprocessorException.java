package ntessema.csc575.preprocessor;

/**
 * A custom Java exception.
 */
public class PreprocessorException extends RuntimeException {
    public PreprocessorException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
