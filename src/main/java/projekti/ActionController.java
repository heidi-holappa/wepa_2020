
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
    private MessageRepository messageRepository;
    
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
                
        
        model.addAttribute("postedmessages", messageRepository.findByOriginal(0L));
        if (actionError.toString().length() > 3) {
            model.addAttribute("actionError", actionError.toString());
            actionError.setError("");
        }
        
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
        msg.setComments(0);
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
        messageRepository.save(msg);

        
        return "redirect:/index";
    }
    
    @GetMapping("/like/{id}")
    public String addLike(Model model, @PathVariable Long id) {
        
        System.out.println("addLike käynnistyy. Long id: " + id);
        
        // Get the user who has logged in
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        // Get message and users who have liked it
        Message msg = messageRepository.getOne(id);
        List<Account> likers = msg.getLikers();
        
        // If likers already include current user, redirect to index
        if (likers.contains(accountRepository.findByUsername(username))) {
            likers.remove(accountRepository.findByUsername(username));
            msg.setLikes(msg.getLikes() - 1);
            messageRepository.save(msg);
            return "redirect:/index";
        }        
        System.out.println("Message content: " + msg.getContent());
        System.out.println("Ohitettiin if-lauseke");
        
        // Add one like to counter and add the user who liked the post
        msg.setLikes(msg.getLikes() + 1);
        likers.add(accountRepository.findByUsername(username));
        
        System.out.println("Added one to like counter and the userlike");
        
        // save message
        messageRepository.save(msg);
        
        System.out.println("Saved message");
        
        return "redirect:/index";
    }
    
    @GetMapping("profile_view/{pathname}")
    public String profilePage(Model model, @PathVariable String pathname) {
        
        String username = domainService.getCurrentUsername();
        Account user = domainService.getCurrentUser();
        UserInfo userInfo = userInfoRepository.findByUser(user);
        
        String viewedUser = accountRepository.findByPathname(pathname).getUsername();
        Account viewed = accountRepository.findByPathname(pathname);
        
        if (username.equals(viewedUser)) {
            model.addAttribute("modify", "true");
        }
        
        Long id = viewed.getProfileImgId();
        
        if ( id != 0) {
            model.addAttribute("profilepic", id);
        }
        
        if (userInfo.getSentRequests().contains(viewed)) {
            model.addAttribute("contactrequest", "Cancel contact request");
        } else {
            model.addAttribute("contactrequest", "Send contact request");            
        }
        
        if (!userInfo.getFriendRequests().isEmpty()) {
            model.addAttribute("pending", "You have pending requests");
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
    
    @GetMapping("/contacts")
    public String contacts(Model model) {
        String username = domainService.getCurrentUsername();
        Account user = domainService.getCurrentUser();
        UserInfo userInfo = userInfoRepository.findByUser(user);
        
        if (actionError.toString().length() > 3) {
            model.addAttribute("error", actionError.toString());
            actionError.setError("");
        }
        model.addAttribute("userinfo", accountRepository.findByUsername(username));
        model.addAttribute("skills", skillRepository.findByUser(user));
        model.addAttribute("userProfile", userInfo);
        model.addAttribute("friends", userInfo.getFriends());
        model.addAttribute("friendRequests", userInfo.getFriendRequests());
        model.addAttribute("sentRequests", userInfo.getSentRequests());
        
        
        return "contacts";
    }
    
    @Transactional
    @PostMapping("/contactrequest")
    public String contactRequest(@RequestParam String pathname) {
        
        UserInfo user = userInfoRepository.findByUser(domainService.getCurrentUser());
        Account contact = accountRepository.findByPathname(pathname);
        UserInfo contactInfo = userInfoRepository.findByUser(contact);
                
        if (!user.getSentRequests().contains(contact)) {
            user.getSentRequests().add(contact);
            contactInfo.getFriendRequests().add(domainService.getCurrentUser());
        } else {
            user.getSentRequests().remove(contact);
            contactInfo.getFriendRequests().remove(domainService.getCurrentUser());
        }
        
        userInfoRepository.save(user);
        userInfoRepository.save(contactInfo);
        
        return "redirect:/contacts";
    }
    
    @PostMapping("/handlerequest")
    public String handleRequest(@RequestParam String decision, @RequestParam Long contactId) {
        
        Account user = domainService.getCurrentUser();
        UserInfo userInfo = userInfoRepository.findByUser(user);
        Account contact = accountRepository.getOne(contactId);
        UserInfo contactInfo = userInfoRepository.findByUser(contact);        
        
        if (decision.equals("accept")) {
            userInfo.getFriends().add(contact);
            userInfo.getFriendRequests().remove(contact);
            contactInfo.getFriends().add(user);
            contactInfo.getSentRequests().remove(user);
        } else if (decision.equals("decline")) {
            userInfo.getFriendRequests().remove(contact);
            contactInfo.getSentRequests().remove(user);
        } else {
            actionError.setError("You tried to answer a contact request, but something went wrong. Please contact system admin.");
        }
        
        userInfoRepository.save(userInfo);
        userInfoRepository.save(contactInfo);
        
        return "redirect:/contacts/";
    }
    
    @PostMapping("/terminatecontact")
    public String terminateContact(@RequestParam String pathname) {
        
        Account user = domainService.getCurrentUser();
        UserInfo userInfo = userInfoRepository.findByUser(user);
        Account contact = accountRepository.findByPathname(pathname);
        UserInfo contactInfo = userInfoRepository.findByUser(contact);
        
        contactInfo.getFriends().remove(user);
        userInfo.getFriends().remove(contact);
        
        userInfoRepository.save(userInfo);
        userInfoRepository.save(contactInfo);
        
        return "redirect:/contacts/";
        
    }
    
    @GetMapping("/comment/{id}")
    public String comment(Model model, @PathVariable Long id) {
        
        Account user = domainService.getCurrentUser();
        UserInfo userInfo = userInfoRepository.findByUser(user);
        
        model.addAttribute("userinfo", user);
        model.addAttribute("message", messageRepository.getOne(id));
        model.addAttribute("comments", messageRepository.findAllByOpId(id));
        model.addAttribute("error", actionError.toString());
        actionError.setError("");
        
        
        return "comment";
    }
    
    @PostMapping("/postcomment")
    public String postComment(@RequestParam String content, @RequestParam Long messageId) {
        
        if (content.length() < 10) {
            this.actionError.setError("Your post must be at least 10 characters long.");
            return "redirect:/index";
        }
        
        if (content.length() > 500) {
            this.actionError.setError("Your post must not be more than 500 characters long.");
            return "redirect:/index";
        }
        
        // Create a new reply
        Message msg = new Message();
        msg.setContent(content);
        msg.setLikes(0);
        msg.setComments(0);
        msg.setOpId(messageId);
        List<Account> likers = new ArrayList<>();
        msg.setLikers(likers);
        
        // Get the user who has logged in    
        msg.setUser(domainService.getCurrentUser());
        
        messageRepository.save(msg);
        
        Message op = messageRepository.getOne(messageId);
        Integer n = op.getComments() + 1;
        op.setComments(n);
        messageRepository.save(op);
        
        
        return "redirect:/comment/" + messageId;
    }
    
    @PostMapping("/searchpost")
    public String searchByName(@RequestParam String search) {
        
//        if (search.isEmpty()) {
//            System.out.println("searchByName if ran");
//            search = "_";
//        } 
        
        return "redirect:/searchresults?search=" + search;
               
        
    }
    
    @GetMapping("/searchresults")
    public String searchGet(Model model, @RequestParam String search) {
        
        
        Account user = domainService.getCurrentUser();
        UserInfo userInfo = userInfoRepository.findByUser(user);
        
        model.addAttribute("userinfo", user);
        if (search.equals("*")) {
            List<Account> allUsers = accountRepository.findAll();
            model.addAttribute("searchresults", allUsers);
            if (allUsers.size() == 1) {
                model.addAttribute("notification", "No other users found in the service.");
            }
        } else if (search.equals("_")) {
            List<Account> users = new ArrayList<>();
            model.addAttribute("notification", "No results.");
            model.addAttribute("searchresults", users);
        } else if (search.equals("")) {
            List<Account> users = new ArrayList<>();
            model.addAttribute("notification", "You searched for nothing and thus found nothing.");
            model.addAttribute("searchresults", users); 
        } else {
            model.addAttribute("searchresults", accountRepository.findAllByNameContainingIgnoreCase(search));
            model.addAttribute("notification", "No users found. Find all users, by typing the symbol '*' in the search field.");
            
        }   
        
        return "searchresults";
        
    }
    
    


    
}
