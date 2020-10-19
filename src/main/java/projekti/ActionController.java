
package projekti;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import projekti.domain.*;


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
    
    @Autowired
    private FileRepository fileObjectRepository;
    
    @Autowired
    private DomainService domainService;
    
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
            actionError.setError("");
        }
        
        System.out.println("GetMapping index: postedmessages added");
        System.out.println("GetMapping index: " + msgRepository.findAll().toString());
        return "index";
    }
    
    @PostMapping("/postmessage")
    public String postMessage(@RequestParam String content) {

        if (content.length() < 10) {
            this.actionError.setError("Your post must be at least 10 characters long.");
            return "redirect:/index";
        }
        
        if (content.length() > 500) {
            this.actionError.setError("Your post must not be more than 500 characters long.");
            return "redirect:/index";
        }
        
        Message msg = new Message();
        msg.setContent(content);
        msg.setLikes(0);
        msg.setOpId(0L);
        List<Account> likers = new ArrayList<>();
        msg.setLikers(likers);
        
        // Get the user who has logged in    
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        System.out.println(username);
        
        Account user = accountRepository.findByUsername(username);
        System.out.println(user);
                
        msg.setUser(user); 
        
        System.out.println(msg);
        msgRepository.save(msg);

        
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
        
        Long id = accountRepository.findByUsername(viewedUser).getProfileImgId();
        
        if ( id != 0) {
            model.addAttribute("profilepic", id);
        }
        
        Pageable topSkills = PageRequest.of(0, 3, Sort.by("endorsements", "skill").descending());
        
        model.addAttribute("topSkills", skillRepository.findByUser(accountRepository.findByUsername(viewedUser),topSkills));
        model.addAttribute("otherSkills", skillRepository.findByUserOffset(accountRepository.findByUsername(viewedUser).getId()));
        model.addAttribute("userinfo", accountRepository.findByUsername(username));
        model.addAttribute("viewedProfile", accountRepository.findByPathname(pathname));
        model.addAttribute("userProfile", userInfoRepository.findByUser(accountRepository.findByPathname(pathname)));
        
        if (actionError.toString().length() > 3) {
            model.addAttribute("actionError", actionError.toString());
            actionError.setError("");
        }
        
        return "profile_view";
    }
    
    @PostMapping("/updatedescription")
    public String updateDescription(@RequestParam String description) {
        
        
        String username = domainService.getCurrentUsername();
        
        actionError.setError("");
        
        if (description.length() > 4 && description.length() < 200) {
            UserInfo info = userInfoRepository.findByUser(accountRepository.findByUsername(username));
            info.setDescription(description);
            info.setUpdateDate(LocalDateTime.now());
            userInfoRepository.save(info);
        } else {
            actionError.addError("Your description must be between 4-200 characters. ");
        }
        
        String pathname = accountRepository.findByUsername(username).getPathname();
        return "redirect:/updatemode/";
    }
    
    
     @PostMapping("/updateskill")
    public String updateSkill(@RequestParam String skill) {
        
        String username = domainService.getCurrentUsername();
    
        
        actionError.setError("");
        
        if (skill.length() >= 1 && skill.length() < 41) {
            UserInfo info = userInfoRepository.findByUser(accountRepository.findByUsername(username));
            
            Skill newSkill = new Skill();
            newSkill.setSkill(skill);
            newSkill.setEndorsements(0);
            newSkill.setUser(accountRepository.findByUsername(username));
            skillRepository.save(newSkill);
            info.setUpdateDate(LocalDateTime.now());
            userInfoRepository.save(info);
            
        } else {
            actionError.addError("Skills must be between 1-40 characters. ");
        }
        
        String pathname = accountRepository.findByUsername(username).getPathname();
        return "redirect:/updatemode/";
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
        
        // save 
        skillRepository.save(skill);
        
        return "redirect:/profile_view/" + skill.getUser().getPathname();
    }
    
    
    @PostMapping("/updatePicture")
    public String add(@RequestParam("file") MultipartFile file) throws IOException {
        
        // Get the user who has logged in
        String username = domainService.getCurrentUsername();
        
        // Get user's pathname
        String pathname = accountRepository.findByUsername(username).getPathname();
        
        if (!file.getContentType().contains("image/")) {
            actionError.setError("File's content-type needs to be image/*");
            return "redirect:/profile_view/" + pathname;
        }
        
        FileObject fo = domainService.fileSaver(file);

//        FileObject fo = new FileObject();
//
//        fo.setName(file.getOriginalFilename());
//        fo.setContentType(file.getContentType());
//        fo.setContentLength(file.getSize());
//        fo.setContent(file.getBytes());
//        fileObjectRepository.save(fo);
        
        UserInfo userinfo = userInfoRepository.findByUser(accountRepository.findByUsername(username));
//        userinfo.setProfilePic(fo);
        userinfo.setUpdateDate(LocalDateTime.now());

        userInfoRepository.save(userinfo);
 
        
        Account user = accountRepository.findByUsername(username);
        user.setProfileImgId(fo.getId());
        accountRepository.save(user);
        
        return "redirect:/updatemode/";
    }
    
    @Transactional
    @GetMapping(value = "/profilePic/{id}")
    public ResponseEntity<byte[]> viewFile(@PathVariable Long id) {
        FileObject fo = fileObjectRepository.getOne(id);
 
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(fo.getContentType()));
        headers.add("Content-Disposition", "attachment; filename=" + fo.getName());
        headers.setContentLength(fo.getContentLength());
 
        return new ResponseEntity<>(fo.getContent(), headers, HttpStatus.CREATED);
    }
    
    
    @GetMapping("/updatemode")
    public String updateProfile(Model model) {
        String username = domainService.getCurrentUsername();
        Account user = domainService.getCurrentUser();
        model.addAttribute("userinfo", accountRepository.findByUsername(username));
        model.addAttribute("skills", skillRepository.findByUser(user));
        model.addAttribute("userProfile", userInfoRepository.findByUser(user));
        Long id = user.getProfileImgId();
        if ( id != 0) {
            model.addAttribute("profilepic", id);
        }
        
        return "updateprofile";
    }
    
    @GetMapping("/updatedone")
    public String updateDone() {
        return "redirect:/profile_view/" + domainService.getCurrentUser().getPathname();
    }
    


    
}
