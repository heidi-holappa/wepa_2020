package projekti.security;

import java.util.ArrayList;
import projekti.domain.*;

import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    PasswordEncoder passwordEncoder;
    
    ErrorObject error = new ErrorObject();

    
    @GetMapping("/auth/login")
    public String login() {
        return "/auth/login";
    }
    
    @GetMapping("/auth/signup")
    public String signup(@ModelAttribute Account account, Model model) {
        System.out.println("signup: " + error.toString());
        System.out.println("signup: " + error.toString().length());
        if (error.toString().length() > 1 ) {
            model.addAttribute("errors", error.toString());
        }
                
        return "/auth/signup";
    }
    
    @PostMapping("/auth/signup")
    public String createUsername(
            @Valid
            @ModelAttribute("account") Account account, 
            BindingResult bindingResult,
            @RequestParam String password,
            @RequestParam String passwordtwo) {
        
        if(bindingResult.hasErrors()) {
            return "/auth/signup";
        }
        
        System.out.println("Metodi createUsername käynnistyy");
        
        if (accountRepository.findByUsername(account.getUsername()) != null) {
            error.setError("Username is already in use. Choose another username.");
            System.out.println("createUserError: " + error.toString());
            return "redirect:/auth/signup";
        }
        
        if (accountRepository.findByPathname(account.getPathname()) != null) {
            error.setError("Path is already in use. Choose another path.");
            System.out.println("createUserError: " + error.toString());
            return "redirect:/auth/signup";
        }
        
        if (!password.equals(passwordtwo)) {
            error.setError("Passwords didn't match. Please try again.");
            System.out.println("createUserError: " + error.toString());
            return "redirect:/auth/signup";
        }
        
        if (password.length() < 8 || password.length() > 16) {
            error.setError("Password must be 8-16 characters long.");
            System.out.println("createUserError: " + error.toString());
            return "redirect:/auth/signup";
        }
        
        
        
        System.out.println("createUsername: If-lausekkeet ohitettu");
        
               
        System.out.println("createUsername: Tilin a tiedot");
        System.out.println("createUsername: Username: " + account.getUsername());
        System.out.println("createUsername: Password: " + password);
        System.out.println("createUsername: Pathname: " + account.getPathname());
        
        ArrayList<String> rights = new ArrayList<>();
        rights.add("USER");
        account.setAuthorities(rights);
        
        account.setSecuredPassword(passwordEncoder.encode(password));
        accountRepository.save(account);
        
        System.out.println("createUsername: Tallennus ohitettu");
        
        
        return "redirect:/auth/login";
    }
    
    
    
    
}
