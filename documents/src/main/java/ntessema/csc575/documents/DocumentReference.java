package ntessema.csc575.documents;

import ntessema.csc575.commons.DocumentException;

import java.nio.file.Path;

/*
 * A simple class for storing a reference to a document file
 * that includes information on the length of its document
 * vector.
 */
public class DocumentReference {

    private Path path;
    private double length;

    DocumentReference(Path path, double length) throws DocumentException {
        if(length < 0 || path == null) {
            throw new DocumentException("Invalid construction value(s).");
        }
        this.path = path;
        this.length = length;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) throws DocumentException {
        if(path == null) {
            throw new DocumentException("Path variable cannot be null");
        }
        this.path = path;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) throws DocumentException {
        if(length < 0) {
            throw new DocumentException("length cannot be negative");
        }
        this.length = length;
    }
}
