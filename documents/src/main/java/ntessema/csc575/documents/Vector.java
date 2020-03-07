package ntessema.csc575.documents;

enum SimilarityMeasure { DOT_PRODUCT, COSINE }

public class Vector {

    private double [] vector;

    public Vector(double [] v) {
        if(v == null) {
            throw new RuntimeException("Null array cannot be used to initialize a vector");
        }
        this.vector = v;
    }

    public int dimension() {
        return vector.length;
    }
    public double get(int i) {
        if(i < 0 || i > dimension()) {
            throw new RuntimeException("Out of bound exception.");
        }
        return vector[i];
    }
    public void set(int i, double v) {
        if(i < 0 || i > dimension()) {
            throw new RuntimeException("Out of bound exception.");
        }
        vector[i] = v;
    }
    public double[] multiplyByFactor(double factor) {
        double[] result = new double[vector.length];
        for(int i = 0; i < vector.length; i++) {
            result[i] = factor * vector[i];
        }
        return result;
    }
    public double sum() {
        double sum = 0.0;
        for(int i = 0; i < vector.length; i++) {
            sum += vector[i];
        }
        return sum;
    }
    public double norm() {
        double normSquared = 0.0;
        for(int i = 0; i < dimension(); i++) {
            normSquared += vector[i] * vector[i];
        }
        return Math.sqrt(normSquared);
    }
    public void display(int decimalPlaces) {
        String format = "%." + decimalPlaces + "f";
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for(int i = 0; i < vector.length; i++) {
            sb.append(" ");
            sb.append(String.format(format, vector[i]));
            sb.append(",");
        }
        sb.replace(sb.length() - 1, sb.length(), " ");
        sb.append("]");
        System.out.println(sb.toString());
    }

    /**
     * Dot Product of two vectors
     */
    public double dotProduct(Vector b) {
        if(vector.length != b.dimension()) {
            throw new RuntimeException("Incompatible dimensions.");
        }
        double dotProduct = 0.0;

        for(int i = 0; i < vector.length; i++) {
            dotProduct += vector[i] * b.get(i);
        }
        return dotProduct;
    }

    public double similarity(Vector b, SimilarityMeasure similarityMeasure) {
        switch(similarityMeasure) {
            case DOT_PRODUCT: return dotSimilarity(b);
            default: return cosineSimilarity(b);
        }
    }

    public double dotSimilarity(Vector b) {
        return dotProduct(b);
    }

    public double cosineSimilarity(Vector b) {
        return dotProduct(b) / (norm() * b.norm());
    }
}
