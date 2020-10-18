// Tämä kontrolleri sisältää sivuston staattisen sisällön kontrollerit (esim. footer)


package projekti;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    
    @Autowired
    private AccountRepository accountRepository;
    
    @GetMapping("/docs/project")
    public String projectDoc(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        model.addAttribute("userinfo", accountRepository.findByUsername(username));
        return "docs/project";
    }
    
    @GetMapping("/docs/feedback")
    public String feedback(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        model.addAttribute("userinfo", accountRepository.findByUsername(username));
        return "docs/feedback";
    }
    
    @GetMapping("/docs/aboutus")
    public String aboutUs(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        model.addAttribute("userinfo", accountRepository.findByUsername(username));
        return "docs/aboutus";
    }
    
    @GetMapping("/profile_view")
    public String profileView(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        model.addAttribute("userinfo", accountRepository.findByUsername(username));
        return "profile_view";
    }
    
    @RequestMapping("/login")
    public ModelAndView login() {
        return new ModelAndView("login");
    }
    
    
    
}
