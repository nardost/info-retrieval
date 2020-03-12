package ntessema.csc575.springbootui;

import ntessema.csc575.commons.Document;
import ntessema.csc575.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.net.URISyntaxException;

@Controller
public class QueryController {

    @Value("${spring.application.name}")
    private String applicationName;

    @Autowired
    private QueryService queryService;

    @GetMapping("/search")
    public String home(@RequestParam String query, Model model) {
        try {
            model.addAttribute("applicationName", applicationName);
            model.addAttribute("queryString", query);
            model.addAttribute("size", queryService.getResults(Query.createQueryFromString(query)));
            return "home";
        } catch (IOException ioe) {
            return "error";
        } catch(URISyntaxException use) {
            return "error";
        }
    }
}
