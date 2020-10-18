package projekti.security;

import com.google.common.io.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
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
import org.springframework.web.multipart.MultipartFile;

@Controller
public class AuthController {
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired UserInfoRepository userInfoRepository;
    
    @Autowired
    PasswordEncoder passwordEncoder;
    
    // Luodaan yksi virheet kerävä olio. TARKASTA VIELÄ, VOISIKO TÄMÄN LUODA AUTOMAATTISESTI
    ErrorObject error = new ErrorObject();

    
    // Custom login-sivu
    @GetMapping("/auth/login")
    public String login() {
        return "auth/login";
    }
    
    // Käyttäjätunnuksen luominen näkymä. 
    // Jos virhe-oliossa on virhe tallennettuna, virhe-viesti lisätään Modeliin. 
    @GetMapping("/auth/signup")
    public String signup(@ModelAttribute Account account, Model model) {
        
        if (error.toString().length() > 1 ) {
            model.addAttribute("errors", error.toString());
        }
                
        return "auth/signup";
    }
    
    // Käyttäjätunnuksen luominen POST
    @PostMapping("/auth/signup")
    public String createUsername(
            @Valid
            @ModelAttribute("account") Account account, 
            BindingResult bindingResult,
            @RequestParam String password,
            @RequestParam String passwordtwo) {
        
        // Tarkastetaan BindingResult - virheet
        if(bindingResult.hasErrors()) {
            return "auth/signup";
        }
        
        // Muut virheet: käyttäjätunnus varattu
        if (accountRepository.findByUsername(account.getUsername()) != null) {
            error.setError("Username is already in use. Choose another username.");
            System.out.println("createUserError: " + error.toString());
            return "redirect:/auth/signup";
        }
        
        // Muut virheet: polku varattu
        if (accountRepository.findByPathname(account.getPathname()) != null) {
            error.setError("Path is already in use. Choose another path.");
            System.out.println("createUserError: " + error.toString());
            return "redirect:/auth/signup";
        }
        
        // Muut virheet: salasanat eivät täsmää
        if (!password.equals(passwordtwo)) {
            error.setError("Passwords didn't match. Please try again.");
            System.out.println("createUserError: " + error.toString());
            return "redirect:/auth/signup";
        }
        
        // Muut virheet: salasana liian lyhyt tai liian pitkä
        if (password.length() < 8 || password.length() > 16) {
            error.setError("Password must be 8-16 characters long.");
            System.out.println("createUserError: " + error.toString());
            return "redirect:/auth/signup";
        }
        
        //Luodaan lista käyttäjäoikeuksista
        ArrayList<String> rights = new ArrayList<>();
        rights.add("USER");
        account.setAuthorities(rights);
        
        //Salasanan suojaus
        account.setSecuredPassword(passwordEncoder.encode(password));
        
        //Profile-image-id to zero (no image added):
        account.setProfileImgId(0L);
        
        UserInfo info = new UserInfo();
        info.setDescription(account.getName() + " is a highly accomplished professional. Their career has been described as groundbreaking, innovative and bold.");

//        Loinkin erillisen olion. Alussa ei tarvitse tallentaa mitään
//        ArrayList<Skill> skills = new ArrayList<>();
//        info.setSkills(skills);

info.setUser(account);
        info.setUpdateDate(LocalDateTime.now());
        
        userInfoRepository.save(info);

        //Tallennetaan uusi käyttäjätunnus
        accountRepository.save(account);
        
        
        
        return "redirect:/auth/login";
    }
    
    
    
    
}
