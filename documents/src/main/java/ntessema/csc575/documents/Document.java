package ntessema.csc575.documents;

import java.util.Comparator;
import java.util.Map;

/*
 * This can serve both as document and as query
 */
public class Document {

    /*
     * This could be the OS file name
     */
    private String id;
    /*
     * Terms and their frequencies
     */
    private Map<String, Double> documentVector;

    public Document(String id) {
        if(id == null) {
            throw new DocumentException("Null assignment not allowed during document construction.");
        }
        this.id = id;
    }

    public Document(String id, Map<String, Double> documentVector) {
        if(id == null || documentVector == null) {
            throw new DocumentException("Null assignment not allowed during document construction.");
        }
        this.id = id;
        this.documentVector = documentVector;
    }


    /**
     * Change the weight of a term in document
     *
     * @param term the term whose weight is being set.
     */
    public void setWeight(String term, double weight) {
        if(getDocumentVector().containsKey(term)) {
            getDocumentVector().put(term, weight);
        } else {
            //TODO what happens if term is not in document?
            throw new DocumentException("Term is not in document.");
        }
    }

    /**
     * Multiply document vector by a constant factor
     * i.e. multiply each term weight by the factor.
     *
     * @param factor - the multiplying factor.
     */
    public void multiply(double factor) {
        getDocumentVector().forEach((k,v) -> getDocumentVector().put(k, factor * v));
    }

    /**
     * Get the maximum weight in the document.
     *
     * @return the maximum weight.
     */
    public double getMaxWeight() {
        return getDocumentVector().entrySet().stream().max(Comparator.comparing(Map.Entry::getValue)).get().getValue();
    }

    /**
     * Dot product of two document vectors
     */
    public double dot(Document secondDocument) {
        Map<String, Double> vector1 = getDocumentVector();
        Map<String, Double> vector2 = secondDocument.getDocumentVector();
        if(vector1.size() != vector2.size()) {
            throw new DocumentException("Dimension of document vectors is not equal.");
        }
        double accumulator = 0.0;
        for(String k1 : vector1.keySet()) {
            if(vector2.containsKey(k1)) {
                accumulator += vector1.get(k1) * vector2.get(k1);
            }
        }
        return accumulator;
    }

    //TODO: more methods, like document addition...

    /**
     * Getters and Setters
     */
    public String getId() {
        return id;
    }

    public Map<String, Double> getDocumentVector() {
        return documentVector;
    }

    public void setDocumentVector(Map<String, Double> map) {
        if(map == null) {
            throw new DocumentException("Null document vector assignment is not allowed.");
        }
        documentVector = map;
    }

}
