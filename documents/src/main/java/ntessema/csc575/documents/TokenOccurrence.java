package ntessema.csc575.documents;

import ntessema.csc575.commons.DocumentException;

public class TokenOccurrence {

    private DocumentReference documentReference;
    private double frequency;

    TokenOccurrence(DocumentReference documentReference, double frequency) throws DocumentException {
        if(documentReference == null || frequency < 0) {
            throw new DocumentException("Invalid construction value(s)");
        }
        this.documentReference = documentReference;
        this.frequency = frequency;
    }

    DocumentReference getDocumentReference() {
        return documentReference;
    }

    double getFrequency() {
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
