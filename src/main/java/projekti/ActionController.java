
package projekti;

import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import projekti.domain.*;
import projekti.security.*;


@Controller
public class ActionController {
    
    @Autowired
    private MessageRepository msgRepository;
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private UserInfoRepository userInfoRepository;
    
    @Autowired
    private SkillRepository skillRepository;
    
    // Luodaan yksi virheet kerävä olio. TARKASTA VIELÄ, VOISIKO TÄMÄN LUODA AUTOMAATTISESTI
    ErrorObject actionError = new ErrorObject();
    
    @GetMapping("/index")
    public String returnHome(Model model) {
        
        if (!SecurityContextHolder.getContext().getAuthentication().getName().equals("anonymousUser")) {
            model.addAttribute("userinfo", accountRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName()));
        }
                
        System.out.println("GetMapping index: initiated");
        model.addAttribute("postedmessages", msgRepository.findAll());
        if (actionError.toString().length() > 3) {
            model.addAttribute("actionError", actionError.toString());
        }
        
        System.out.println("GetMapping index: postedmessages added");
        System.out.println("GetMapping index: " + msgRepository.findAll().toString());
        return "index";
    }
    
    @PostMapping("/postmessage")
    private String postMessage(@RequestParam String content) {

        if (content.length() < 10) {
            actionError.setError("Your post must be at least 10 characters long.");
            return "redirect:/index";
        }
        
        if (content.length() > 500) {
            actionError.setError("Your post must not be more than 500 characters long.");
            return "redirect:/index";
        }
        
        
        Message msg = new Message();
        msg.setContent(content);
        msg.setLikes(0);
        msg.setOpId(0L);
        List<Account> likers = new ArrayList<>();
        msg.setLikers(likers);
        
            
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();        
        
        msg.setUser(accountRepository.findByUsername(username));
        
        
   
        msgRepository.save(msg);
        
        System.out.println("postMessage: save passed");
        
        return "redirect:/index";
    }
    
    @GetMapping("/like/{id}")
    public String addLike(Model model, @PathVariable Long id) {
        
        System.out.println("addLike käynnistyy. Long id: " + id);
        
        // Get the user who has logged in
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        // Get message and users who have liked it
        Message msg = msgRepository.getOne(id);
        List<Account> likers = msg.getLikers();
        
        // If likers already include current user, redirect to index
        if (likers.contains(accountRepository.findByUsername(username))) {
            likers.remove(accountRepository.findByUsername(username));
            msg.setLikes(msg.getLikes() - 1);
            msgRepository.save(msg);
            return "redirect:/index";
        }        
        System.out.println("Message content: " + msg.getContent());
        System.out.println("Ohitettiin if-lauseke");
        
        // Add one like to counter and add the user who liked the post
        msg.setLikes(msg.getLikes() + 1);
        likers.add(accountRepository.findByUsername(username));
        
        System.out.println("Added one to like counter and the userlike");
        
        // save message
        msgRepository.save(msg);
        
        System.out.println("Saved message");
        
        return "redirect:/index";
    }
    
    @GetMapping("profile_view/{pathname}")
    public String profilePage(Model model, @PathVariable String pathname) {
        
        
        
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        String viewedUser = accountRepository.findByPathname(pathname).getUsername();
         
        
        
        if (username.equals(viewedUser)) {
            model.addAttribute("modify", "true");
        }
        
        Pageable topSkills = PageRequest.of(0, 3, Sort.by("endorsements", "skill").descending());
        Pageable otherSkills = PageRequest.of(1, 3, Sort.by("endorsements").descending());
        
        System.out.println(topSkills.toString());
//        System.out.println(otherSkills.toString());
        
        model.addAttribute("topSkills", skillRepository.findByUser(accountRepository.findByUsername(viewedUser),topSkills));
        model.addAttribute("otherSkills", skillRepository.findByUserOffset(accountRepository.findByUsername(viewedUser).getId()));
        model.addAttribute("userinfo", accountRepository.findByUsername(username));
        model.addAttribute("viewedProfile", accountRepository.findByPathname(pathname));
        model.addAttribute("userProfile", userInfoRepository.findByUser(accountRepository.findByPathname(pathname)));
        
        return "profile_view";
    }
    
    @PostMapping("/updateprofile")
    public String updateprofile(
            @RequestParam String description,
            @RequestParam String skill,
            @RequestParam String profileimage) {
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        String pathname = accountRepository.findByUsername(username).getPathname();
        
        actionError.setError("");
        
        if (description.length() > 4 && description.length() < 200) {
            UserInfo info = userInfoRepository.findByUser(accountRepository.findByUsername(username));
            info.setDescription(description);
            userInfoRepository.save(info);
        } else {
            actionError.addError("Your description must be between 4-200 characters. ");
        }
        
        if (skill.length() >= 1 && skill.length() < 41) {
            UserInfo info = userInfoRepository.findByUser(accountRepository.findByUsername(username));
            
            Skill newSkill = new Skill();
            newSkill.setSkill(skill);
            newSkill.setEndorsements(0);
            newSkill.setUser(accountRepository.findByUsername(username));
            skillRepository.save(newSkill);
            
//            info.getSkills().add(newSkill);
//            userInfoRepository.save(info);
        } else {
            actionError.addError("Skills must be between 1-40 characters. ");
        }
        
        
        return "redirect:/profile_view/" + pathname;
    }
    
    @GetMapping("/endorse/{id}")
    public String addEndorse(Model model, @PathVariable Long id) {
        
        System.out.println("addEndorse käynnistyy. Long id: " + id);
        
        // Get the user who has logged in
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        // Get skill and users who have liked it
        Skill skill = skillRepository.getOne(id);
        List<Account> endorsers = skill.getEndorsers();
        
        // If likers already include current user, redirect to index
        if (endorsers.contains(accountRepository.findByUsername(username))) {
            endorsers.remove(accountRepository.findByUsername(username));
            skill.setEndorsements(skill.getEndorsements() - 1);
            skillRepository.save(skill);
            return "redirect:/index";
        }        
        
        
        // Add one like to counter and add the user who liked the post
        skill.setEndorsements(skill.getEndorsements() + 1);
        endorsers.add(accountRepository.findByUsername(username));
        
        
        
        // save message
        skillRepository.save(skill);
        
        System.out.println("Saved message");
        
        return "redirect:/index";
    }
    
}
