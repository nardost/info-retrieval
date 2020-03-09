package ntessema.csc575.documents;

import ntessema.csc575.commons.Utilities;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(Parameterized.class)
public class DocumentTest {

    private String fileName;
    private Indexer indexer;

    public DocumentTest(String fileName) {
        this.fileName = fileName;
    }

    @Parameters
    public static Collection<String> fileNames() {
        return Stream.of(new String[] {
                "1.txt",
                "2.txt",
                "3.txt",
                "4.txt",
                "5.txt",
                "6.txt",
                "7.txt",
                "8.txt",
                "9.txt" ,
                "10.txt",
        }).collect(Collectors.toList());
    }

    @Before
    public void init() {
        indexer = new Indexer();
    }

    @Test
    public void  read_document_from_file_and_build_a_vector() {
        try {
            Path path = Utilities.getInstance().getPathFromFileName(fileName);
            Document document = indexer.getDocumentFromFile(path);
            DocumentReference reference = indexer.getDocumentReferenceFromFile(path);
            assertThat((double) document.getDocumentVector().size(), is(equalTo(reference.getLength())));
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch(URISyntaxException use) {
            use.printStackTrace();
        }
    }
}
