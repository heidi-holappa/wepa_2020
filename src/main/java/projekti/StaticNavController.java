// Tämä kontrolleri sisältää sivuston staattisen sisällön kontrollerit (esim. footer)


package projekti;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class StaticNavController {
    
    
    @GetMapping("/index")
    public String returnHome() {
        return "index";
    }
    
    @GetMapping("/docs/project")
    public String projectDoc() {
        return "docs/project";
    }
    
    @GetMapping("/docs/feedback")
    public String feedback() {
        return "docs/feedback";
    }
    
    @GetMapping("/docs/aboutus")
    public String aboutUs() {
        return "docs/aboutus";
    }
    
    @GetMapping("/profile_view")
    public String profileView() {
        return "profile_view";
    }
    
    @RequestMapping("/login")
    public ModelAndView login() {
        return new ModelAndView("login");
    }
    
    
}
