
package projekti;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
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
    
    @Autowired
    private SearchObjectRepository searchObjectRepository;
    
    // Creates Objects used to manage information. One for errors, one for contact filtering
    ErrorObject actionError = new ErrorObject();
    ShowObject showObject = new ShowObject();
    
    @GetMapping("/index")
    public String returnHome(Model model) {
        
        System.out.println("method returnHome");
        
        // Username for non-authenticated user is anonymousUser
        if (!SecurityContextHolder.getContext().getAuthentication().getName().equals("anonymousUser")) {
            Account user = domainService.getCurrentUser();
            model.addAttribute("userinfo", user);
            model.addAttribute("userProfile", domainService.getUserInfo(user));
            model.addAttribute("contactmessages", domainService.getContactMessagesByUserId(user.getId()));
        }
        
        // Get all messages
        model.addAttribute("postedmessages", domainService.getAllOpMessages());
        
        // Button to filter what posts are shown
        model.addAttribute("show", showObject.toString());
        
        // Show possible actionError. Showing it here, because in some cases user may be returned to /index when something goes wrong.
        if (actionError.toString().length() > 3) {
            model.addAttribute("actionError", actionError.toString());
            actionError.setError("");
        }
        
        return "index";
    }
    
    @GetMapping("/filtercontacts")
    public String filterContacts(@RequestParam String show) {
        
        System.out.println("method filterContacts");
        
        // set value for showObject
        if (show.equals("My contacts")) {
            showObject.setShow("All users");
        } else {
            showObject.setShow("My contacts");
        }
        
        return "redirect:/index";
    }
    
    @CacheEvict(value = { "messages-op-cache", "messages-contacts-cache" }, allEntries = true)
    @PostMapping("/postmessage")
    public String postMessage(@RequestParam String content) {
        
        System.out.println("method postMessage");

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
        String username = domainService.getCurrentUsername();
        Account user = domainService.getCurrentUser();
        
        msg.setUser(user);
        
        messageRepository.save(msg);
        
        return "redirect:/index";
    }
    
    @CacheEvict(value = { "messages-op-cache", "messages-contacts-cache" }, allEntries = true)
    @GetMapping("/like/{id}")
    public String addLike(Model model, @PathVariable Long id) {
        
        System.out.println("method addLike");
        
        // Get the user who has logged in
        String username = domainService.getCurrentUsername();
        
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
        
        // Add one like to counter and add the user who liked the post
        msg.setLikes(msg.getLikes() + 1);
        likers.add(accountRepository.findByUsername(username));
        
        // save message
        messageRepository.save(msg);
        
        System.out.println("Saved message");
        
        return "redirect:/index";
    }
    
    
    @CacheEvict(value = {"userinfo-cache",
                    "viewed-cache",
                    "user-byId-cache",
                    "userinfo-sentrequests-cache", 
                    "userinfo-friends-cache", 
                    "userinfo_friendrequests-cache"
                }, allEntries = true, beforeInvocation=true)
    @GetMapping("profile_view/{pathname}")
    public String profilePage(Model model, @PathVariable String pathname) {
        
        System.out.println("Method profilePage");
        
        Account user = domainService.getCurrentUser();
        String username = user.getName();
        UserInfo userInfo = domainService.getUserInfo(user);
        
        
        Account viewed = domainService.getViewedUserByPathname(pathname);
        UserInfo viewedInfo = domainService.getUserInfo(viewed);
        String viewedUser = viewed.getName();
        Long id = viewed.getProfileImgId();
        
        if (username.equals(viewedUser)) {
            model.addAttribute("modify", "true");
            System.out.println("Modify true");
        }
        
        if ( id != 0) {
            model.addAttribute("profilepic", id);
        }
        
        if (domainService.getUserInfoSentRequests(userInfo).contains(viewed)) {
            model.addAttribute("contactrequest", "Cancel contact request");
        } else {
            model.addAttribute("contactrequest", "Send contact request");            
        }
        
        if (domainService.getUserInfoFriendRequests(userInfo).contains(viewed) || domainService.getUserInfoFriends(userInfo).contains(viewed)) {
            model.addAttribute("requestreceived", "true");
        } else {
            model.addAttribute("requestreceived", "false");
        }
        
        if (!domainService.getUserInfoFriendRequests(userInfo).isEmpty()) {
            model.addAttribute("pending", "You have pending requests");
        }


        // Get all information needed on the profile page
        model.addAttribute("userinfo", user);
        model.addAttribute("viewedProfile", viewed);
        model.addAttribute("userProfile", viewedInfo);
        // These come from the domainService - class and are cached
        model.addAttribute("topSkills", domainService.getTopThreeSkillsById(viewed.getId()));
        model.addAttribute("otherSkills", domainService.getOtherSkillsById(viewed.getId()));
        model.addAttribute("contacts", domainService.getUserFriendsById(viewed.getId()));
        
        return "profile_view";
    }
    
    @CacheEvict(value = "userinfo-cache", allEntries = true)
    @PostMapping("/updatedescription")
    public String updateDescription(@RequestParam String description) {
        
        
        String username = domainService.getCurrentUsername();
        Account user = domainService.getCurrentUser();
        
        actionError.setError("");
        
        if (description.length() > 4 && description.length() < 200) {
            UserInfo info = domainService.getUserInfo(user);
            info.setDescription(description);
            info.setUpdateDate(LocalDateTime.now());
            userInfoRepository.save(info);
        } else {
            actionError.addError("Your description must be between 4-200 characters. ");
        }
        
        String pathname = user.getPathname();
        return "redirect:/updatemode";
    }
    
    
    @CacheEvict(value = { "userinfo-cache", "topskills-cache", "otherskills-cache" }, allEntries = true)
    @PostMapping("/updateskill")
    public String updateSkill(@RequestParam String skill) {
        
        String username = domainService.getCurrentUsername();
        Account user = domainService.getCurrentUser();
        UserInfo info = domainService.getUserInfo(user);
    
        
        actionError.setError("");
        
        if (skill.length() >= 1 && skill.length() < 41) {
            
            
            Skill newSkill = new Skill();
            newSkill.setSkill(skill);
            newSkill.setEndorsements(0);
            newSkill.setOnList(1);
            newSkill.setUser(user);
            skillRepository.save(newSkill);
            info.setUpdateDate(LocalDateTime.now());
            userInfoRepository.save(info);
            
        } else {
            actionError.addError("Skills must be between 1-40 characters. ");
        }
        
        String pathname = accountRepository.findByUsername(username).getPathname();
        return "redirect:/updatemode/";
    }
    
    @CacheEvict(value = { "userinfo-cache", "topskills-cache", "otherskills-cache" }, allEntries = true)
    @PostMapping("/removeskill")
    public String removeSkill(@RequestParam Long id) {
        
        Skill skill = skillRepository.getOne(id);
        skill.setOnList(0);
        
        skillRepository.save(skill);
        
        return "redirect:/updatemode";
    }
    
    @CacheEvict(value = { "userinfo-cache", "topskills-cache", "otherskills-cache" }, allEntries = true)
    @GetMapping("/endorse/{id}")
    public String addEndorse(Model model, @PathVariable Long id) {
        
        System.out.println("method addEndorse");
        
        // Get the user who has logged in
        String username = domainService.getCurrentUsername();
        
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
    
    
    @CacheEvict(value = {"userinfo-cache", "user-cache", "viewed-cache", "messages-op-cache", "messages-contacts-cache"}, allEntries = true, beforeInvocation=true)
    @PostMapping("/updatePicture")
    public String add(@RequestParam("file") MultipartFile file) throws IOException {
        
        // Get the user who has logged in
        String username = domainService.getCurrentUsername();
        Account user = domainService.getCurrentUser();
        // Get user's pathname
        String pathname = user.getPathname();
        
        
        // If ContentType is image, it can be used as a profile image. 
        if (!file.getContentType().contains("image/")) {
            actionError.setError("File's content-type needs to be image/*");
            return "redirect:/updatemode";
        }
        
        FileObject fo = domainService.fileSaver(file);
        
        UserInfo userinfo = domainService.getUserInfo(user);

        userinfo.setUpdateDate(LocalDateTime.now());

        userInfoRepository.save(userinfo);
        
        user.setProfileImgId(fo.getId());
        accountRepository.save(user);
        
        return "redirect:/updatemode/";
    }
    
    @CacheEvict(value = {"userinfo-cache", 
                        "user-cache", 
                        "viewed-cache", 
                        "messages-op-cache", 
                        "messages-contacts-cache"
                }, allEntries = true, beforeInvocation=true)
    @PostMapping("/removepicture")
    public String removePic() {
        
        Account user = domainService.getCurrentUser();
        user.setProfileImgId(0L);
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
    
    
    @CacheEvict(value = {"userinfo-cache", "user-cache"}, allEntries = true)
    @GetMapping("/updatemode")
    public String updateProfile(Model model) {
        String username = domainService.getCurrentUsername();
        Account user = domainService.getCurrentUser();
        model.addAttribute("userinfo", user);
        model.addAttribute("skills", skillRepository.findByUserActive(user.getId()));
        model.addAttribute("userProfile", domainService.getUserInfo(user));
        Long id = user.getProfileImgId();
        if ( id != 0) {
            model.addAttribute("profilepic", id);
        }
        if (actionError.toString().length() > 3) {
            model.addAttribute("actionError", actionError.toString());
            actionError.setError("");
        }
        
        return "updateprofile";
    }
    
    @GetMapping("/updatedone")
    public String updateDone() {
        return "redirect:/profile_view/" + domainService.getCurrentUser().getPathname();
    }
    
    @CacheEvict(value = {"userinfo-cache",
                    "viewed-cache",
                    "user-byId-cache",
                    "userinfo-sentrequests-cache", 
                    "userinfo-friends-cache", 
                    "userinfo_friendrequests-cache"
                }, allEntries = true, beforeInvocation=true)
    @GetMapping("/contacts")
    public String contacts(Model model) {
        String username = domainService.getCurrentUsername();
        Account user = domainService.getCurrentUser();
        UserInfo userInfo = domainService.getUserInfo(user);
        
        if (actionError.toString().length() > 3) {
            model.addAttribute("error", actionError.toString());
            actionError.setError("");
        }
        model.addAttribute("userinfo", user);
        model.addAttribute("userProfile", userInfo);
        model.addAttribute("friends", domainService.getUserInfoFriends(userInfo));
        model.addAttribute("friendRequests", domainService.getUserInfoFriendRequests(userInfo));
        model.addAttribute("sentRequests", domainService.getUserInfoSentRequests(userInfo));
        
        
        return "contacts";
    }
    
    @CacheEvict(value = {"user-cache",
                        "userinfo-cache",
                        "viewed-cache",
                        "user-byId-cache",
                        "userinfo-sentrequests-cache", 
                        "userinfo-friends-cache", 
                        "userinfo_friendrequests-cache"
                    }, allEntries = true, beforeInvocation=true)
    @Transactional
    @PostMapping("/contactrequest")
    public String contactRequest(@RequestParam String pathname) {
        
        Account user = domainService.getCurrentUser();
        UserInfo userInfo = domainService.getUserInfo(user);
        Account contact = domainService.getViewedUserByPathname(pathname);
        UserInfo contactInfo = domainService.getUserInfo(contact);
//        Account contact = accountRepository.findByPathname(pathname);
//        UserInfo contactInfo = userInfoRepository.findByUser(contact);
        
                
//        if (!user.getSentRequests().contains(contact)) {
        if (!domainService.getUserInfoSentRequests(userInfo).contains(contact)) {
            domainService.getUserInfoSentRequests(userInfo).add(contact);
            domainService.getUserInfoFriendRequests(contactInfo).add(user);
//            user.getSentRequests().add(contact);
//            contactInfo.getFriendRequests().add(domainService.getCurrentUser());
            
        } else {
            domainService.getUserInfoSentRequests(userInfo).remove(contact);
            domainService.getUserInfoFriendRequests(contactInfo).remove(user);
//            user.getSentRequests().remove(contact);
//            contactInfo.getFriendRequests().remove(domainService.getCurrentUser());
        }
        
        userInfoRepository.save(userInfo);
        userInfoRepository.save(contactInfo);
        
        return "redirect:/contacts";
    }
    
    @CacheEvict(value = {"userinfo-cache",
                        "viewed-cache",
                        "user-byId-cache",
                        "userinfo-sentrequests-cache", 
                        "userinfo-friends-cache", 
                        "userinfo_friendrequests-cache"
                    }, allEntries = true, beforeInvocation=true)
    @PostMapping("/handlerequest")
    public String handleRequest(@RequestParam String decision, @RequestParam Long contactId) {
        
        Account user = domainService.getCurrentUser();
        UserInfo userInfo = domainService.getUserInfo(user);
        Account contact = domainService.getUserById(contactId);
        UserInfo contactInfo = domainService.getUserInfo(contact);
//        Account contact = accountRepository.getOne(contactId);
//        UserInfo contactInfo = userInfoRepository.findByUser(contact);        

        
        if (decision.equals("accept")) {
            domainService.getUserInfoFriends(userInfo).add(contact);
            domainService.getUserInfoFriendRequests(userInfo).remove(contact);
            domainService.getUserInfoFriends(contactInfo).add(user);
            domainService.getUserInfoSentRequests(contactInfo).remove(user);
//            userInfo.getFriends().add(contact);
//            userInfo.getFriendRequests().remove(contact);
//            contactInfo.getFriends().add(user);
//            contactInfo.getSentRequests().remove(user);
        } else if (decision.equals("decline")) {
            domainService.getUserInfoFriendRequests(userInfo).remove(contact);
            domainService.getUserInfoSentRequests(contactInfo).remove(user);
//            userInfo.getFriendRequests().remove(contact);
//            contactInfo.getSentRequests().remove(user);
        } else {
            actionError.setError("You tried to answer a contact request, but something went wrong. Please contact system admin.");
        }
        
        userInfoRepository.save(userInfo);
        userInfoRepository.save(contactInfo);
        
        return "redirect:/contacts/";
    }
    
    @CacheEvict(value = {"userinfo-cache",
                        "viewed-cache",
                        "user-byId-cache",
                        "userinfo-sentrequests-cache", 
                        "userinfo-friends-cache", 
                        "userinfo_friendrequests-cache"
                    }, allEntries = true, beforeInvocation=true)
    @PostMapping("/terminatecontact")
    public String terminateContact(@RequestParam String pathname) {
        
        Account user = domainService.getCurrentUser();
        UserInfo userInfo = domainService.getUserInfo(user);
        Account contact = domainService.getViewedUserByPathname(pathname);
        UserInfo contactInfo = domainService.getUserInfo(contact);
//        Account contact = accountRepository.findByPathname(pathname);
//        UserInfo contactInfo = userInfoRepository.findByUser(contact);
        
        domainService.getUserInfoFriends(contactInfo).remove(user);
        domainService.getUserInfoFriends(userInfo).remove(contact);
//        contactInfo.getFriends().remove(user);
//        userInfo.getFriends().remove(contact);
        
        userInfoRepository.save(userInfo);
        userInfoRepository.save(contactInfo);
        
        return "redirect:/contacts/";
        
    }
    
    @GetMapping("/comment/{id}")
    public String comment(Model model, @PathVariable Long id) {
        
        Account user = domainService.getCurrentUser();
        UserInfo userInfo = domainService.getUserInfo(user);
        
        model.addAttribute("userinfo", user);
        model.addAttribute("message", messageRepository.getOne(id));
        model.addAttribute("comments", messageRepository.findAllByOpId(id));
        model.addAttribute("error", actionError.toString());
        actionError.setError("");
        
        
        return "comment";
    }
    
    @CacheEvict(value = {"messages-op-cache", "messages-contacts-cache"}, allEntries = true)
    @PostMapping("/postcomment")
    public String postComment(@RequestParam String content, @RequestParam Long messageId) {
        
        // Set error-message, if comment too short or too long
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
        
        SearchObject so = new SearchObject();
        
        so.setUser(domainService.getCurrentUser());
        so.setValue(search);
        searchObjectRepository.save(so);

        
        return "redirect:/searchresults";
        
    }
    
    @GetMapping("/searchresults")
    public String searchGet(Model model) {
        
        Account user = domainService.getCurrentUser();
        UserInfo userInfo = domainService.getUserInfo(user);
        SearchObject so = searchObjectRepository.FindByUserNewest(user);
        
        model.addAttribute("userinfo", user);
        
        // Some conditions for searching
        if (so.getValue().equals("*")) {
            List<Account> allUsers = accountRepository.findAll();
            model.addAttribute("searchresults", allUsers);
            if (allUsers.size() == 1) {
                model.addAttribute("notification", "No other users found in the service.");
            }
        } else if (so.getValue().equals("_")) {
            List<Account> users = new ArrayList<>();
            model.addAttribute("notification", "No results.");
            model.addAttribute("searchresults", users);
        } else if (so.getValue().equals("")) {
            List<Account> users = new ArrayList<>();
            model.addAttribute("notification", "You searched for nothing and thus found nothing.");
            model.addAttribute("searchresults", users); 
        } else {
            model.addAttribute("searchresults", accountRepository.findAllByNameContainingIgnoreCase(so.getValue()));
            model.addAttribute("notification", "No users found. Find all users, by typing the symbol '*' in the search field.");
            
        }   
        
        return "searchresults";
        
    }
    
}
