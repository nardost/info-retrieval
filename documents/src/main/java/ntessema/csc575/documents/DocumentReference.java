package ntessema.csc575.documents;

import java.nio.file.Path;

/*
 * A simple class for storing a reference to a document file
 * that includes information on the length of its document
 * vector.
 */
public class DocumentReference {

    private Path path;
    private double length;

    public DocumentReference(Path path) {
        if(path == null) {
            throw new DocumentException("Invalid construction value(s).");
        }
        this.path = path;
        this.length = 0.0;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        if(path == null) {
            throw new DocumentException("Path variable cannot be null");
        }
        this.path = path;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        if(length < 0) {
            throw new DocumentException("length cannot be negative");
        }
        this.length = length;
    }
}
