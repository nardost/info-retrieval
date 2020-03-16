package ntessema.csc575.documents;

/**
 * A custom Java exception.
 */
public class DocumentException extends RuntimeException {
    public DocumentException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
