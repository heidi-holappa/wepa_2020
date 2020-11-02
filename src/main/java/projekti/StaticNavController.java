// Tämä kontrolleri sisältää sivuston staattisen sisällön kontrollerit (esim. footer)


package projekti;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import projekti.domain.*;

@Controller
public class StaticNavController {
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private DomainService domainService;
    
    @Autowired
    private FeedbackRepository feedbackRepository;
    
    ErrorObject fbError = new ErrorObject();
    
    
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
        
        if (fbError.toString().length() > 1) {
            model.addAttribute("notification", fbError.toString());
            fbError.setError("");
        } else {
            model.addAttribute("notification", "empty");
        }
        
        
        return "docs/feedback";
    }
    
    @PostMapping("/docs/submitfeedback")
    public String submitFeedback(@RequestParam String useful, 
                                @RequestParam String recommend, 
                                @RequestParam String openfeedback) {
        
        Feedback fb = new Feedback();
        
        
        if (openfeedback.length() < 5) {
            fbError.setError("Your feedback must be at least five (5) characters long.");
            return "redirect:/docs/feedback";
        }
        
        fb.setUser(domainService.getCurrentUser());
        fb.setRecommend(recommend);
        fb.setUseful(useful);
        fb.setOpenFeedback(openfeedback);
        feedbackRepository.save(fb);      
        
        
        return "redirect:/docs/feedback?success";
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
        String pathname = domainService.getCurrentUser().getPathname();
        return "redirect:/profile_view/" + pathname;
    }
    
    @CacheEvict(value = {"user-cache",
                        "user-byId-cache",
                        "viewed-cache",
                        "username-cache",
                        "userinfo-cache", 
                        "userinfo_friendrequests-cache",
                        "userinfo-friends-cache", 
                        "userinfo-sentrequests-cache", 
                        "topskills-cache",
                        "otherskills-cache",
                        "userfriends-cache",
                        "messages-contacts-cache",
                        "messages-op-cache"
                        }, allEntries = true, beforeInvocation=true)
    @RequestMapping("/login")
    public ModelAndView login() {
        return new ModelAndView("login");
    }
    
    // This method prepares the page for project testing
    @Secured("ROLE_USER")
    @GetMapping("/testhtml")
    public String testPage(Model model) {
        
        return "testing/testhtml";
    }
    
        
    // This method prepares the view for jsTesting
    @Secured("ROLE_USER")
    @GetMapping("/jstests")
    public String jsTestPage(Model model) {
        Account user = domainService.getCurrentUser();
        UserInfo userInfo = domainService.getUserInfo(user);
        
        model.addAttribute("useraccount", user);
        model.addAttribute("userinfo", userInfo);
        boolean friendRequests = !userInfo.getFriendRequests().isEmpty();
        model.addAttribute("friendRequests", friendRequests);
        
        return "testing/jstests";
    }
    
}
