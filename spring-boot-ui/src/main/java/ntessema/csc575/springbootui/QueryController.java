package ntessema.csc575.springbootui;

import ntessema.csc575.documents.BBCDocument;
import ntessema.csc575.documents.DocumentReference;
import ntessema.csc575.documents.DocumentUtilities;
import ntessema.csc575.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * The Controller class in the MVC setup.
 * Forwards requests to the appropriate page.
 */
@Controller
public class QueryController {

    @Value("${spring.application.name}")
    private String applicationName;

    @Autowired
    private QueryService queryService;

    @GetMapping("/")
    public String home(Model model) {
        return "home";
    }

    @GetMapping("/search")
    public String search(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String page,
            Model model) {

        final int currentPage = (page != null && !page.equals("")) ? Integer.parseInt(page) : 1;
        final int itemsPerPage = 10;

        try {
            if(query == null || query.equals("")) {
                return "home";
            }
            Map<DocumentReference, Double> results = queryService.getResults(Query.createQueryFromString(query));
            Map<String, BBCDocument> docWithScore = new LinkedHashMap<>();
            for(Map.Entry<DocumentReference, Double> result : results.entrySet()) {
                DocumentReference documentReference = result.getKey();
                Double score = result.getValue();
                BBCDocument bbcDocument = DocumentUtilities.getBBCDocumentFromFile(documentReference.getPath());
                docWithScore.put(bbcDocument.getLink(), bbcDocument);
            }
            final int numberOfResults = docWithScore.size();
            int numberOfPages = (numberOfResults / itemsPerPage) + ((numberOfResults % itemsPerPage != 0) ? 1 : 0);
            final int from = (currentPage - 1) * itemsPerPage; //inclusive
            int to = (currentPage - 1) * itemsPerPage + itemsPerPage; //exclusive
            to = (to > numberOfResults) ? numberOfResults : to;
            Map<String, BBCDocument> currentPageResults = new LinkedHashMap<>();

            for(int i = from; i < to; i++) {
                String key = (String) docWithScore.keySet().toArray()[i];
                currentPageResults.put(key, docWithScore.get(key));
            }

            model.addAttribute("applicationName", applicationName);
            model.addAttribute("numberOfResults", numberOfResults);
            model.addAttribute("results", currentPageResults);
            model.addAttribute("numberOfPages", numberOfPages);
            model.addAttribute("page", currentPage);
            model.addAttribute("q", query);
            return "home";
        } catch (IOException ioe) {
            return "error";
        } catch(URISyntaxException use) {
            return "error";
        }
    }
}
