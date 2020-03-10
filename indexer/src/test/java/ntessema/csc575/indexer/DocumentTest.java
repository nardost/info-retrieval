package ntessema.csc575.indexer;

import ntessema.csc575.commons.Document;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.io.IOException;
import java.net.URISyntaxException;
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

    @Test @Ignore
    public void  read_document_from_file_and_build_a_vector() {
        try {
            Document document = indexer.getDocumentFromFile(fileName);
            DocumentReference reference = indexer.getDocumentReferenceFromFile(fileName);
            assertThat(document.getId(), is(equalTo(reference.getPath().getFileName().toString())));
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch(URISyntaxException use) {
            use.printStackTrace();
        }
    }
}
