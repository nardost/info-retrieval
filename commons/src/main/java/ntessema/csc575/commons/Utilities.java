package ntessema.csc575.commons;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

public class Utilities {

    private static Utilities INSTANCE = null;

    private Utilities() {
    }

    public static Utilities getInstance() {
        if(INSTANCE == null) {
            synchronized (Utilities.class) {
                if(INSTANCE == null) {
                    INSTANCE = new Utilities();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Get the Path object associated with a document in the corpus directory
     * Make file access machine independent.
     * Declare and environment variable CORPORA_DIR
     * and set it to the directory in which the various corpora live.
     * For example, in my mac osx machine, the various corpora I am using
     * live in /Users/nardos/Documents/corpora/. Therefore, the env variable
     * will be set as $export CORPORA_DIR=/Users/nardos/Documents/corpora
     *
           /Users/nardos/Documents/corpora/
                ├── documents
                │   ├── 1.txt
                │   ├── 10.txt
                │   ├── 2.txt
                │   ├── 3.txt
                │   ├── 4.txt
                │   ├── 5.txt
                │   ├── 6.txt
                │   ├── 7.txt
                │   ├── 8.txt
                │   └── 9.txt
                ├── test
                │   ├── 1.txt
                │   ├── 2.txt
                │   └── 3.txt
                └── time
                    ├── 1.txt
                    ├── 10.txt
                    ├── 100.txt
                    ├── 101.txt
                    ├── 102.txt
                    ├── 103.txt
     *
     * Given a fileName, this method returns the Path corresponding to
     * the file in the corpus directory.
     */
    public Path getPathFromFileName(String fileName) {
        final String separator = File.separator;
        final String corpus = ConfigurationManager.getConfiguration("corpus");
        final String corporaDirEnvVar = ConfigurationManager.getConfiguration("corporaDirEnvVar");
        final String CORPORA_DIR = System.getenv(corporaDirEnvVar);
        String filePath = CORPORA_DIR + separator + corpus + separator + fileName;
        File file = new File(filePath);
        Path path = file.toPath();
        return path;
    }

    /**
     * Get File object given a path string
     */
    public File getFile(String path) {
        final String CORPORA_DIR = System.getenv("CORPORA_DIR");
        String fullPath = CORPORA_DIR + File.separator + path;
        return new File(fullPath);
    }

    /**
     * Get the corpus currently being used (time, bbc, etc.)
     */
    public String getCorpus() {
        return ConfigurationManager.getConfiguration("corpus");
    }

    /**
     * Stringify a list (the toString() method must have been overridden).
     * This method is used in my tests to print linked lists contents.
     */
    public <T> String listToString(List<T> list) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for(T item : list) {
            sb.append(" ");
            sb.append(item.toString());
            sb.append(",");
        }
        sb.replace(sb.length() - 1, sb.length(), " ");
        sb.append("}");
        return sb.toString();
    }
}
