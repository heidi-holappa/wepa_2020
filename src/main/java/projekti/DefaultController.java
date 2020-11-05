// This was the project default controller. Didn't add anything here.

package projekti;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DefaultController {
    

    @GetMapping("*")
    public String goHome() {
        return "redirect:/index";
    }
}
