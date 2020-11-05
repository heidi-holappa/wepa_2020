// This class creates a customized error-view
// Found a tutorial for this from LogicBig.com, found it useful while testing
package projekti.security;

import javax.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
public class CustomErrorController implements ErrorController {

  @RequestMapping("/error")
  @ResponseBody
  public String handleError(HttpServletRequest request) {
      Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
      Exception exception = (Exception) request.getAttribute("javax.servlet.error.exception");
      return String.format(""
              + "<html>"
              + "<body>"
              + "   <h2>Error Page</h2>"
              + "   <p> This shouldn't have happened. Something did not go as planned.</p>"
              + "   <div>Status code: <b>%s</b></div>"
                  + "<div>Exception Message: <b>%s</b></div>"
              +     "<br />"
              +     "<a href='/index'>Home</a>"
              + "<body>"
              + "</html>",
              statusCode, exception==null? "N/A": exception.getMessage());
  }

  @Override
  public String getErrorPath() {
      return "/error";
  }
}
