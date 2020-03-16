package ntessema.csc575.springbootui;

import ntessema.csc575.indexer.Indexer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;

import java.io.IOException;
import java.net.URISyntaxException;


/**
 * The single entry point to the application. Running this class
 * fires up the UI and inverted index construction.
 */
@SpringBootApplication
@EnableAutoConfiguration(exclude = {ErrorMvcAutoConfiguration.class})
public class SpringBootUI {
    /**
     * When the UI module first starts, it triggers the construction of
     * the inverted index.
     *
     * Since the indexer is a singleton and there is only one instance of
     * the inverted index, program startup is the only time inverted
     * index construction (and the corresponding time delay) happens.
     *
     * Please watch the top of the console output to see
     * how much time it took to construct the inverted index.
     */
    public static void main(String[] args) throws IOException, URISyntaxException {
        System.out.println("Constructing the inverted index...");
        long start = System.currentTimeMillis();
        Indexer.getInstance();
        long finish = System.currentTimeMillis();
        System.out.println("Done constructing inverted index.");
        System.out.println("Time taken to construct inverted index: " + (finish - start) + " milliseconds");
        /*
         * Start the Spring Boot UI application. This starts a
         * web application at port 8282.
         *
         * Browse to http://localhost:8282 to access the application
         */
        SpringApplication.run(SpringBootUI.class, args);
    }
}
