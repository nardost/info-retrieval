package ntessema.csc575.springbootui;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

/*
 * Custom error redirecting. We do not want the default "Whitelabel"
 * error page. For our search engine, any error will redirect
 * to the search home page.
 */
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String redirectErrorToHomePage(HttpServletRequest request) {
        Object httpStatus = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if(httpStatus != null) {
            Integer httpStatusCode = Integer.valueOf(httpStatus.toString());
            if (httpStatusCode == HttpStatus.NOT_FOUND.value()) {
                return "home";
            }
            if (httpStatusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                return "home";
            }
        }
        return "home";
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }
}
