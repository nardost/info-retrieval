package ntessema.csc575.commons;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
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
     */
    public Path getPathFromFileName(String fileName) throws URISyntaxException {
        final String separator = File.separator;
        final String corpusDirectory = ConfigurationManager.getConfiguration("corpusDirectory");
        String filePath = corpusDirectory + separator + fileName;
        Path path = Paths.get(getClass().getClassLoader().getResource(filePath).toURI());
        return path;
    }

    /**
     * Get File object given a path string
     */
    public File getFile(String path) {
        return new File(getClass().getClassLoader().getResource(path).getFile());
    }

    /**
     * Get the corpus location
     */
    public String getCorpusDirectory() {
        return ConfigurationManager.getConfiguration("corpusDirectory");
    }

    /**
     * Stringify a list (the toString() method must have been overridden)
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
