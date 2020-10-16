// Tämä kontrolleri sisältää sivuston staattisen sisällön kontrollerit (esim. footer)


package projekti;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import projekti.domain.*;

@Controller
public class StaticNavController {
    
    @Autowired
    private MessageRepository msgRepository;
      
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
