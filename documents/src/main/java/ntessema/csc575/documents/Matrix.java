package ntessema.csc575.documents;

public class Matrix {

    private Vector[] matrix;

    public Matrix(Vector[] m) {
        if(!isValid(m)) {
            throw new RuntimeException("Not a valid matrix. Check dimensions.");
        }
        this.matrix = m;
    }

    public Matrix(double [][] m) {
        /*
         * check validity ... overload isValid
         */
        Vector [] v = new Vector[m.length];
        for(int i = 0; i < m.length; i++) {
            v[i] = new Vector(m[i]);
        }
        this.matrix = v;
    }

    public Vector[] getRows() {
        return matrix;
    }

    public int rows() {
        return matrix.length;
    }

    public int cols() {
        return matrix[0].dimension();
    }

    public static boolean isValid(Vector[] matrix) {
        if(matrix == null || matrix.length == 0) {
            return false;
        }
        final double ROWS = matrix.length;
        final double COLS = matrix[0].dimension();
        if(matrix.length != ROWS) return false;
        for(Vector row : matrix) {
            if(row.dimension() != COLS) return false;
        }
        return true;
    }

    public Matrix multiply(Matrix m) throws RuntimeException {

        final int ROWS_A = this.rows();
        final int COLS_A = this.cols();
        final int ROWS_B = m.rows();
        final int COLS_B = m.cols();

        if(COLS_A != ROWS_B) {
            throw new RuntimeException("Matrix dimensions not right for multiplication");
        }

        Vector[] AB = new Vector[ROWS_A];

        for (int rows = 0; rows < ROWS_A; rows++) {
            /*
             * I put the next line in the inner loop and was
             * getting a wrong product for a long time.
             */
            AB[rows] = new Vector(new double[COLS_B]);
            for (int cols = 0; cols < COLS_B; cols++) {
                AB[rows].set(cols, 0);
                double accumulator = 0.0;
                for (int k = 0; k < ROWS_B; k++) {
                    accumulator += getRows()[rows].get(k) * m.getRows()[k].get(cols);
                }
                AB[rows].set(cols, accumulator);
            }
        }
        display(3);
        return new Matrix(AB);
    }

    public void  display(int decimalPlaces) {
        if(!isValid(matrix)) {
            System.out.println("Malformed matrix");
        }

        String format = "%." + decimalPlaces + "f";

        StringBuilder sb = new StringBuilder();
        for(int rows = 0; rows < matrix.length; rows++) {
            sb.append("[ ");
            for(int cols = 0; cols < matrix[rows].dimension(); cols++) {
                sb.append(String.format(format,  matrix[rows].get(cols)));
                sb.append("\t");
            }
            sb.replace(sb.length() - 1, sb.length(), "");
            sb.append(" ]\n");
        }
        System.out.println(sb.toString());
    }

    public double sumRow(int row) {
        double sum = 0.0;
        for(int j = 0; j < matrix[row].dimension(); j++) {
            sum += matrix[row].get(j);
        }
        return sum;
    }

    public double sumCol(int col) {
        double sum = 0.0;
        for(int i = 0; i < matrix.length; i++) {
            sum += matrix[i].get(col);
        }
        return sum;
    }

    public Matrix multiplyByFactor(double factor) {
        int rows = matrix.length;
        int cols = matrix[0].dimension();
        double [][] result = new double[rows][];
        for(int i = 0; i < rows; i++) {
            result[i] = new double[cols];
            for(int j = 0; j < cols; j++) {
                result[i][j] = factor * matrix[i].get(j);
            }
        }
        return new Matrix(result);
    }

    /*
     * Average of the rows of the matrix.
     */
    public Vector centroid() {
        if(matrix.length == 0) {
            throw new RuntimeException("Empty matrix.");
        }
        double [] centroid = new double[matrix[0].dimension()];
        for(int row = 0; row < matrix.length; row++) {
            for(int col = 0; col < matrix[row].dimension(); col++) {
                centroid[col] += matrix[row].get(col);
            }
        }
        centroid = new Vector(centroid).multiplyByFactor(1.0 / matrix.length);
        return new Vector(centroid);
    }

    /**
     * df = Document Frequency
     */
    public double df(int col) {
        double df = 0.0;
        for(int row = 0; row < rows(); row++) {
            if(matrix[row].get(col) > 0) {
                df += 1.0;
            }
        }
        return df;
    }

    /**
     * IDF
     */
    public double idf(int col) {
        double df = df(col);
        if(df == 0) throw new RuntimeException("Term is not in the document collection.");
        return Math.log(rows() / df) / Math.log(2.0);
    }

    /**
     * TFxIDF weighted matrix
     */
    public static Matrix tfIdf(Matrix documentTermMatrix) {
        final int COLS = documentTermMatrix.cols();
        final int ROWS = documentTermMatrix.rows();
        Vector[] rowVectors = new Vector[ROWS];
        for(int row = 0; row < ROWS; row++) {
            double [] r = new double[COLS];
            for(int col = 0; col < COLS; col++)  {
                r[col] = documentTermMatrix.getRows()[row].get(col) * documentTermMatrix.idf(col);
                rowVectors[row] = new Vector(r);
            }
        }
        return new Matrix(rowVectors);
    }
}

