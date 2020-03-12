package ntessema.csc575.indexer;

import ntessema.csc575.documents.DocumentException;
import ntessema.csc575.documents.DocumentReference;

public class TokenOccurrence {

    private DocumentReference documentReference;
    private double frequency;

    public TokenOccurrence(DocumentReference documentReference, double frequency) throws DocumentException {
        if(documentReference == null || frequency < 0) {
            throw new DocumentException("Invalid construction value(s)");
        }
        this.documentReference = documentReference;
        this.frequency = frequency;
    }

    public DocumentReference getDocumentReference() {
        return documentReference;
    }

    public double getFrequency() {
        return frequency;
    }

    /**
     * Convert the list of occurrences into a string for viewing
     *
     * Just for testing purposes.
     */
    public String toString() {
        String fileName = documentReference.getPath().getFileName().toString();
        String name = "DOC" + fileName.replace(".txt", "");
        return name + "[" + frequency + "][" + documentReference.getLength() + "]";
    }
}
