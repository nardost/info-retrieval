package ntessema.csc575.documents;

import ntessema.csc575.commons.DocumentException;

import java.util.LinkedList;
import java.util.List;

public class Corpus {

    private static List<Document> documents = new LinkedList<>();

    /**
     * Get size of corpus
     *
     * @return size of the corpus
     */
    public static long size() {
        return documents.size();
    }

    /**
     * Add a document in the corpus
     *
     * @param d document to be added to corpus
     */
    public void add(Document d) {
        if(d == null) {
            throw new DocumentException("Inserting null document is not allowed");
        }
        documents.add(d);
    }
}
