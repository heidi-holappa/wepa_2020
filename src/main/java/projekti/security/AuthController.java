// This class contains controllers that deal with user authentication in some way
// Methods in this class do not contain logging action. 

package projekti.security;

import java.time.LocalDateTime;
import java.util.ArrayList;
import projekti.domain.*;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author Heidi Holappa
 */

@Controller
public class AuthController {
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired UserInfoRepository userInfoRepository;
    
    @Autowired
    PasswordEncoder passwordEncoder;
    
    @Autowired
    private DomainService domainService;
    
    // Create an object for errors 
    ErrorObject error = new ErrorObject();

    
    // Custom login-sivu
    @CacheEvict(value = {"user-byId-cache",
                    "viewed-cache",
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
    @GetMapping("/auth/login")
    public String login() {
        return "auth/login";
    }
    
    // Sign up view  
    // Object error contains error-messages 
    @CacheEvict(value = {"user-byId-cache",
            "viewed-cache",
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
    @GetMapping("/auth/signup")
    public String signup(@ModelAttribute Account account, Model model) {
        
        if (error.toString().length() > 1 ) {
            model.addAttribute("errors", error.toString());
        }
        error.setError("");
                
        return "auth/signup";
    }
    
    

    // POST to create user account
    @PostMapping("/auth/signup")
    public String createUsername(
            @Valid
            @ModelAttribute("account") Account account, 
            BindingResult bindingResult,
            @RequestParam String password,
            @RequestParam String passwordtwo) {
        
        // Check for BindingResult - errors
        if(bindingResult.hasErrors()) {
            return "auth/signup";
        }
        
        
        // Check that username only contains letters and digits
        if (!domainService.checkString(account.getUsername())) {
            error.setError("Username must only contain letters, digits or symbols '-' and '_'");
            return "redirect:/auth/signup?error";
        }
        
        System.out.println("Pathname: " + account.getPathname());
        // Check that pathname only contains letters and digits
        if (!domainService.checkString(account.getPathname())) {
            error.setError("Pathname must only contain letters, digits or or symbols '-' and '_'");
            return "redirect:/auth/signup?error";
        }
        
        if (!domainService.checkPassword(password)) {
            error.setError("Password must contain at least one UpperCase letter, one LowerCase letter and one digit and 8 characters.");
            return "redirect:/auth/signup?error";
        }
        
        
        // Other errors: username taken
        if (accountRepository.findByUsername(account.getUsername()) != null) {
            error.setError("Username is already in use. Choose another username.");
            System.out.println("createUserError: " + error.toString());
            return "redirect:/auth/signup?error";
        }
        
        
        
        // Other errors: path reserved 
        if (accountRepository.findByPathname(account.getPathname()) != null) {
            error.setError("Path is already in use. Choose another path.");
            System.out.println("createUserError: " + error.toString());
            return "redirect:/auth/signup?error";
        }
        
        // Other errors: passwords do not match 
        if (!password.equals(passwordtwo)) {
            error.setError("Passwords didn't match. Please try again.");
            System.out.println("createUserError: " + error.toString());
            return "redirect:/auth/signup?error";
        }
        
        // Other errors: password too short or too long
        if (password.length() < 8 || password.length() > 16) {
            error.setError("Password must be 8-16 characters long.");
            System.out.println("createUserError: " + error.toString());
            return "redirect:/auth/signup?error";
        }
        
        // Create a list of authorities 
        ArrayList<String> rights = new ArrayList<>();
        rights.add("USER");
        account.setAuthorities(rights);
        
        //Encoding the password
        account.setSecuredPassword(passwordEncoder.encode(password));
        
        //Profile-image-id to zero (no image added):
        account.setProfileImgId(0L);
        
        
        // Create the basic userInfo
        UserInfo info = new UserInfo();
        info.setDescription(account.getName() + " has not yet updated a profile description.");
        ArrayList<Account> friendRequests = new ArrayList<>();
        ArrayList<Account> friends = new ArrayList<>();
        info.setFriendRequests(friendRequests);
        info.setFriends(friends);
        info.setUser(account);
        info.setUpdateDate(LocalDateTime.now());
        userInfoRepository.save(info);

        //Save the new object
        accountRepository.save(account);
        
        
        
        return "redirect:/auth/login?createsuccess";
    }
    
    
    
    
}
