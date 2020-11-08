// This class includes most of the methods that involve user interaction on the application
package projekti;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import projekti.domain.*;

/**
 *
 * @author Heidi Holappa
 */


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
    
    @Autowired
    private FeedbackRepository feedbackRepository;
    
    @Autowired
    private LogObjectRepository logObjectRepository;
    
    // Creates Objects used to manage information. One for errors, one for contact filtering
    ErrorObject actionError = new ErrorObject();
    ShowObject showObject = new ShowObject();
    
    
    // This method prepares the index-view
    @GetMapping("/index")
    public String returnHome(Model model) {
        
        System.out.println("method returnHome");
        
        // Username for non-authenticated user is anonymousUser
        if (!SecurityContextHolder.getContext().getAuthentication().getName().equals("anonymousUser")) {
            Account user = domainService.getCurrentUser();
            model.addAttribute("userinfo", user);
            model.addAttribute("contactmessages", domainService.getContactMessagesByUserId(user.getId()));
            
            UserInfo userInfo = domainService.getUserInfo(user);
            model.addAttribute("userProfile", userInfo);
            boolean friendRequests = !userInfo.getFriendRequests().isEmpty();
            model.addAttribute("friendRequests", friendRequests);
            
            // Log the current action
            domainService.logAction("GET: index");
        }
        
        // Get all messages
        model.addAttribute("postedmessages", domainService.getAllOpMessages());
        
        // Button to filter what posts are shown
        model.addAttribute("show", showObject.toString());
        
        if (showObject.toString().equals("All users")) {
            model.addAttribute("showInverted", "My contacts");
        } else {
            model.addAttribute("showInverted", "All users");
        }
        
        // Show possible actionError. Showing it here, because in some cases user may be returned to /index when something goes wrong.
        if (actionError.toString().length() > 3) {
            model.addAttribute("actionError", actionError.toString());
            actionError.setError("");
        }
        
        
        
        return "index";
    }
    
    // This method handles "filtering" of posted messages (rather which query to use)
    @Secured("ROLE_USER")
    @GetMapping("/filtercontacts")
    public String filterContacts(@RequestParam String show) {
        
        System.out.println("method filterContacts");
        
        // set value for showObject
        if (show.equals("My contacts")) {
            showObject.setShow("All users");
        } else {
            showObject.setShow("My contacts");
        }
        
        // Log the current action
        domainService.logAction("GET: Filtered post-view. View set to: " + showObject.toString());
        
        return "redirect:/index";
    }
    
    // This method handles posting messages
    @CacheEvict(value = { "messages-op-cache", "messages-contacts-cache" }, allEntries = true)
    @Secured("ROLE_USER")
    @PostMapping("/postmessage")
    public String postMessage(@RequestParam String content) {
        
        System.out.println("method postMessage");

        if (content.length() < 10) {
            this.actionError.setError("Your post must be at least 10 characters long.");
            domainService.logAction("POST: posting message failed: too short");
            return "redirect:/index";
        }
        
        if (content.length() > 500) {
            this.actionError.setError("Your post must not be more than 500 characters long.");
            domainService.logAction("POST: posting message failed: too long");
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
        
        // Log the current action
        domainService.logAction("POST: posted a message");
        
        return "redirect:/index";
    }
    
    // This method handles liking posts. 
    @CacheEvict(value = { "messages-op-cache", "messages-contacts-cache" }, allEntries = true)
    @Secured("ROLE_USER")
    @GetMapping("/like/{path}/{id}")
    public String addLike(Model model, @PathVariable("id") Long id, @PathVariable("path") String path) {
        
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
            // Log the current action
            domainService.logAction("GET: removed a like from post " + id);
            if (path.equals("comment")) {
                if (msg.getOpId() == 0) {
                    return "redirect:/comment/" + msg.getId();
                } else {
                    return "redirect:/comment/" + msg.getOpId();
                }
                    
            } else {
                return "redirect:/index";
            }
        }
        
        // Add one like to counter and add the user who liked the post
        msg.setLikes(msg.getLikes() + 1);
        likers.add(accountRepository.findByUsername(username));
        
        // Log the current action
        domainService.logAction("GET: liked post " + id);

        // save message
        messageRepository.save(msg);
        
        System.out.println("Saved message");
        
            if (path.equals("comment")) {
                if (msg.getOpId() == 0) {
                    return "redirect:/comment/" + msg.getId();
                } else {
                    return "redirect:/comment/" + msg.getOpId();
                }
                    
            } else {
                return "redirect:/index";
            }
    }
    
    
    // This method prepares the comment - view
    @Secured("ROLE_USER")
    @GetMapping("/comment/{id}")
    public String comment(Model model, @PathVariable Long id) {
        
        Account user = domainService.getCurrentUser();
        UserInfo userInfo = domainService.getUserInfo(user);
        
        model.addAttribute("userinfo", user);
        model.addAttribute("message", messageRepository.getOne(id));
        model.addAttribute("comments", messageRepository.findAllByOpId(id));
        model.addAttribute("error", actionError.toString());
        actionError.setError("");
        
        // Log the current action
        domainService.logAction("GET: viewed comment " + id);
        
        return "comment";
    }
    
    
    // This method handles posting comments
    @CacheEvict(value = {"messages-op-cache", "messages-contacts-cache"}, allEntries = true)
    @Secured("ROLE_USER")
    @PostMapping("/postcomment")
    public String postComment(@RequestParam String content, @RequestParam Long messageId) {
        
        // Set error-message, if comment too short or too long
        if (content.length() < 10) {
            this.actionError.setError("Your post must be at least 10 characters long.");
            // Log the current action
            domainService.logAction("POST: posting comment failed: too short");
            return "redirect:/index";
        }
        
        if (content.length() > 500) {
            this.actionError.setError("Your post must not be more than 500 characters long.");
            // Log the current action
            domainService.logAction("POST: posting comment failed: too long");
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
        Account user = domainService.getCurrentUser();
        msg.setUser(user);
        
        messageRepository.save(msg);
        
        Message op = messageRepository.getOne(messageId);
        Integer n = op.getComments() + 1;
        op.setComments(n);
        messageRepository.save(op);
        
        // Log the current action
        domainService.logAction("POST: posted a comment");
        
        return "redirect:/comment/" + messageId;
    }
    
    
    // This method prepares the page profile_view
    @CacheEvict(value = {"userinfo-cache",
                    "viewed-cache",
                    "user-byId-cache",
                    "userinfo-sentrequests-cache", 
                    "userinfo-friends-cache", 
                    "userinfo_friendrequests-cache"
                }, allEntries = true, beforeInvocation=true)
    @Secured("ROLE_USER")
    @GetMapping("profile_view/{pathname}")
    public String profilePage(Model model, @PathVariable String pathname) {
        
        System.out.println("Method profilePage");
        
        Account viewed = domainService.getViewedUserByPathname(pathname);
        UserInfo viewedInfo = domainService.getUserInfo(viewed);
        String viewedUser = viewed.getName();
        Long id = viewed.getProfileImgId();
        
        if (!SecurityContextHolder.getContext().getAuthentication().getName().equals("anonymousUser")) {
            Account user = domainService.getCurrentUser();
            String username = user.getName();
            UserInfo userInfo = domainService.getUserInfo(user);
            model.addAttribute("userinfo", user);
            
            // Log the current action
            domainService.logAction("GET: viewed profilepath " + pathname);
            
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
            
            if (username.equals(viewedUser)) {
                model.addAttribute("modify", "true");
                System.out.println("Modify true");
            }
        }
        
        if ( id != 0) {
            model.addAttribute("profilepic", id);
        }

        // Get all information needed on the profile page
        
        model.addAttribute("viewedProfile", viewed);
        model.addAttribute("userProfile", viewedInfo);
        // These come from the domainService-class and are cached
        model.addAttribute("topSkills", domainService.getTopThreeSkillsById(viewed.getId()));
        model.addAttribute("otherSkills", domainService.getOtherSkillsById(viewed.getId()));
        model.addAttribute("contacts", domainService.getUserFriendsById(viewed.getId()));
        
        
        
        return "profile_view";
    }
    
    // This method handles updating the user description
    @CacheEvict(value = "userinfo-cache", allEntries = true)
    @Secured("ROLE_USER")
    @PostMapping("/updatedescription")
    public String updateDescription(@RequestParam String description) {
        
        
        String username = domainService.getCurrentUsername();
        Account user = domainService.getCurrentUser();
        
        // Log the current action
        domainService.logAction("POST: updated description");
        
        actionError.setError("");
        
        if (description.length() > 4 && description.length() < 200) {
            UserInfo info = domainService.getUserInfo(user);
            info.setDescription(description);
            info.setUpdateDate(LocalDateTime.now());
            userInfoRepository.save(info);
        } else {
            actionError.setError("Your description must be between 4-200 characters.");
        }
        
        String pathname = user.getPathname();
        return "redirect:/updatemode";
    }
    
    
    // This method handles adding skills
    @CacheEvict(value = { "userinfo-cache", "topskills-cache", "otherskills-cache" }, allEntries = true)
    @Secured("ROLE_USER")
    @PostMapping("/updateskill")
    public String updateSkill(@RequestParam String skill) {
        
        String username = domainService.getCurrentUsername();
        Account user = domainService.getCurrentUser();
        UserInfo info = domainService.getUserInfo(user);
        
        // Log the current action
        domainService.logAction("POST: updated skill");
        
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
            actionError.setError("Skills must be between 1-40 characters.");
        }
        
        String pathname = accountRepository.findByUsername(username).getPathname();
        return "redirect:/updatemode/";
    }
    
    // This method handles removing skills (Bonus-feature)
    @CacheEvict(value = { "userinfo-cache", "topskills-cache", "otherskills-cache" }, allEntries = true)
    @Secured("ROLE_USER")
    @PostMapping("/removeskill")
    public String removeSkill(@RequestParam Long id) {
        
        // Log the current action
        domainService.logAction("POST: removed skill from users skills");
                
        Skill skill = skillRepository.getOne(id);
        skill.setOnList(0);
        
        skillRepository.save(skill);
        
        return "redirect:/updatemode";
    }
    
    // This method handles endorsing
    @CacheEvict(value = { "userinfo-cache", "topskills-cache", "otherskills-cache" }, allEntries = true)
    @Secured("ROLE_USER")
    @GetMapping("/endorse/{id}")
    public String addEndorse(Model model, @PathVariable Long id) {
        
        System.out.println("method addEndorse");
        
        // Get the user who has logged in
        String username = domainService.getCurrentUsername();
        
        // Get skill and users who have liked it
        Skill skill = skillRepository.getOne(id);
        List<Account> endorsers = skill.getEndorsers();
        
        // If already endorsed, remove endorse
        if (endorsers.contains(accountRepository.findByUsername(username))) {
            endorsers.remove(accountRepository.findByUsername(username));
            skill.setEndorsements(skill.getEndorsements() - 1);
            skillRepository.save(skill);
            // Log the current action
            domainService.logAction("GET: removed endorse from skill" + id);
            return "redirect:/profile_view/" + skill.getUser().getPathname();
        }
        
        // Add one like to counter and add the user who liked the post
        skill.setEndorsements(skill.getEndorsements() + 1);
        endorsers.add(accountRepository.findByUsername(username));
        
        // save 
        skillRepository.save(skill);
        
        // Log the current action
        domainService.logAction("GET: endorsed skill " + id);
        
        return "redirect:/profile_view/" + skill.getUser().getPathname();
    }
    
    
    // This method adds a new profile picture
    @CacheEvict(value = {"userinfo-cache", 
                        "user-cache", 
                        "viewed-cache", 
                        "messages-op-cache", 
                        "messages-contacts-cache"
                    }, allEntries = true, beforeInvocation=true)
    @Secured("ROLE_USER")
    @PostMapping("/updatePicture")
    public String addPicure(@RequestParam("file") MultipartFile file) throws IOException {
        
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
        
        // A method in domainService handles saving the file
        FileObject fo = domainService.fileSaver(file);
        
        // Updating current users UserInfo
        UserInfo userinfo = domainService.getUserInfo(user);

        userinfo.setUpdateDate(LocalDateTime.now());

        userInfoRepository.save(userinfo);
        
        // User's account information is updated
        user.setProfileImgId(fo.getId());
        accountRepository.save(user);
        
        // Log the current action
        domainService.logAction("POST: updated profile picture");
        
        return "redirect:/updatemode/";
    }
    
    @GetMapping("/picturegallery")
    public String pictureGallery(Model model) {
        
        Account user = domainService.getCurrentUser();
        
        model.addAttribute("allpictures", domainService.getAllPictures(user));
        model.addAttribute("userinfo", user);
        
        domainService.logAction("GET: view picturegallery");
        
        return "picturegallery";
    }
    
    // This method removes the current picture from profile. 
    // Picture is still left in the repository
    @CacheEvict(value = {"userinfo-cache", 
                        "user-cache", 
                        "viewed-cache", 
                        "messages-op-cache", 
                        "messages-contacts-cache"
                    }, allEntries = true, beforeInvocation=true)
    @Secured("ROLE_USER")
    @PostMapping("/removepicture")
    public String removePic() {
        
        Account user = domainService.getCurrentUser();
        user.setProfileImgId(0L);
        accountRepository.save(user);
        
        // Log the current action
        domainService.logAction("POST: removed profile picture");
        
        return "redirect:/updatemode/";
    }
    
    
    // This method sets the selected image as profile image
    @CacheEvict(value = {"userinfo-cache", 
                        "user-cache", 
                        "viewed-cache", 
                        "messages-op-cache", 
                        "messages-contacts-cache"
                    }, allEntries = true, beforeInvocation=true)
    @Secured("ROLE_USER")
    @PostMapping("/setasprofilepic")
    public String pictureGallerySetAsProfilePic(@RequestParam Long id) {
        
        Account user = domainService.getCurrentUser();
        user.setProfileImgId(id);
        accountRepository.save(user);
        
        // Log the current action
        domainService.logAction("POST: set image id:" + id + " as profile image.");
        
        return "redirect:/updatemode";
    }
    
    // This method sets the selected image as profile image
    @CacheEvict(value = {"userinfo-cache", 
                        "user-cache", 
                        "viewed-cache", 
                        "messages-op-cache", 
                        "messages-contacts-cache"
                    }, allEntries = true, beforeInvocation=true)
    @Secured("ROLE_USER")
    // This method permanently deletes selected image
    @PostMapping("deletepic")
    public String pictureGalleryDeletePic(@RequestParam Long id) {
        
        Account user = domainService.getCurrentUser();
        
        System.out.println("users current img_id: " + user.getProfileImgId());
        System.out.println("Id of deleted picture: " + id);
        
        if (user.getProfileImgId().longValue() == id.longValue()) {
            System.out.println("If true");
            user.setProfileImgId(0L);
            accountRepository.save(user);
        }
        
        domainService.deleteImg(id);
        
        // Log the current action
        domainService.logAction("POST: permanately deleted image id " + id);
        
        
        return "redirect:/picturegallery";
    }
    
    
    // This methods returns a file for viewing. 
    @Transactional
    @Secured("ROLE_USER")
    @GetMapping(value = "/profilePic/{id}")
    public ResponseEntity<byte[]> viewFile(@PathVariable Long id) {
        
        // This method does not have a log-action, because it's not a user action
        
        FileObject fo = fileObjectRepository.getOne(id);
 
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(fo.getContentType()));
        headers.add("Content-Disposition", "attachment; filename=" + fo.getName());
        headers.setContentLength(fo.getContentLength());
 
        return new ResponseEntity<>(fo.getContent(), headers, HttpStatus.CREATED);
    }
    
    
    // This prepares the view updateprofile
    @CacheEvict(value = {"userinfo-cache", "user-cache"}, allEntries = true)
    @Secured("ROLE_USER")
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
        
        // Log the current action
        domainService.logAction("GET: updateprofile");
        
        return "updateprofile";
    }
    
    // This method returns the user to their own profile_view
    @GetMapping("/updatedone")
    @Secured("ROLE_USER")
    public String updateDone() {
        // Log the current action
        domainService.logAction("GET: return to profile_view from updateprofile");
        return "redirect:/profile_view/" + domainService.getCurrentUser().getPathname();
    }
    
    
    // This method prepares the view contacts
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
        
        // Log the current action
        domainService.logAction("GET: contacts");
        return "contacts";
    }
    
    
    // This method handles the sending contactrequests
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
    @PostMapping("/contactrequest")
    @Secured("ROLE_USER")
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
        
        // Log the current action
        domainService.logAction("GET: contact requests");
        
        return "redirect:/contacts";
    }
    
    // This method handles contact requests
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
    @PostMapping("/handlerequest")
    @Secured("ROLE_USER")
    public String handleRequest(@RequestParam String decision, @RequestParam Long contactId) {
        
        Account user = domainService.getCurrentUser();
        Account contact = domainService.getUserById(contactId);
        
        if (decision.equals("accept")) {
            domainService.acceptRequest(user, contact);
        } else if (decision.equals("decline")) {
            domainService.declineRequest(user, contact);
        } else {
            actionError.setError("You tried to answer a contact request, but something went wrong. Please contact system admin.");
        }
        
        // Log the current action
        domainService.logAction("POST: handled contact request from user id " + contactId + ". Action: " + decision);
        
        return "redirect:/contacts/";
    }
    
    
    // This method handles the termination of a friendship. Sad.
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
    @PostMapping("/terminatecontact")
    @Secured("ROLE_USER")
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
        
        // Log the current action
        domainService.logAction("POST: terminated contact from: " + pathname);
        
        return "redirect:/contacts/";
        
    }
    

    // This method handles saving the search-string
    @PostMapping("/searchpost")
    @Secured("ROLE_USER")
    public String searchByName(@RequestParam String search) {
        
        SearchObject so = new SearchObject();
        
        so.setUser(domainService.getCurrentUser());
        so.setValue(search);
        searchObjectRepository.save(so);

        // Log the current action
        domainService.logAction("POST: searchpost. Redirects to searchresults");
        
        
        return "redirect:/searchresults";
        
    }
    
    // This method returns the last search string from the current user
    @GetMapping("/searchresults")
    @Secured("ROLE_USER")
    public String searchGet(Model model) {
        
        Account user = domainService.getCurrentUser();
        UserInfo userInfo = domainService.getUserInfo(user);
        SearchObject so = searchObjectRepository.findByUserNewest(user);
        
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
        
        // Log the current action
        domainService.logAction("GET: searchresults");
        
        model.addAttribute("newest", accountRepository.findNewest(user.getId()));
        
        return "searchresults";
        
    }
    
    // This method prepares the view alldata that lists all data stored about the current user
    @GetMapping("/alldata")
    public String getAllData(Model model) {
        
        Account user = domainService.getCurrentUser();
        UserInfo userInfo = domainService.getUserInfo(user);
        
        model.addAttribute("userinfo", user);
        model.addAttribute("userProfile", userInfo);
        model.addAttribute("friends", domainService.getUserInfoFriends(userInfo));
        model.addAttribute("friendRequests", domainService.getUserInfoFriendRequests(userInfo));
        model.addAttribute("sentRequests", domainService.getUserInfoSentRequests(userInfo));
        model.addAttribute("skills", skillRepository.findByUserActive(user.getId()));
        model.addAttribute("files", fileObjectRepository.findByUser(user));
        model.addAttribute("messages", messageRepository.findByUser(user));
        model.addAttribute("search", searchObjectRepository.findByUser(user));
        model.addAttribute("feedback", feedbackRepository.findByUser(user));
        model.addAttribute("activitylog", logObjectRepository.findByUserId(user.getId()));
        
        // Log the current action
        domainService.logAction("GET: viewed all data");
        
        return "alldata";
    }
    
}
