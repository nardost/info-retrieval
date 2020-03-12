package ntessema.csc575.documents;

public class DocumentException extends RuntimeException {
    public DocumentException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
