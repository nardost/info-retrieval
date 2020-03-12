package ntessema.csc575.springbootui;

import ntessema.csc575.documents.DocumentReference;
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

@Controller
public class QueryController {

    @Value("${spring.application.name}")
    private String applicationName;

    @Autowired
    private QueryService queryService;

    @GetMapping("/search")
    public String home(@RequestParam String query, Model model) {
        try {
            Map<DocumentReference, Double> results = queryService.getResults(Query.createQueryFromString(query));
            Map<String, Double> docWithScore = new LinkedHashMap<>();
            for(Map.Entry<DocumentReference, Double> result : results.entrySet()) {
                DocumentReference documentReference = result.getKey();
                Double score = result.getValue();

            }
            model.addAttribute("applicationName", applicationName);
            model.addAttribute("queryString", query);
            model.addAttribute("size", results.size());
            return "home";
        } catch (IOException ioe) {
            return "error";
        } catch(URISyntaxException use) {
            return "error";
        }
    }
}
