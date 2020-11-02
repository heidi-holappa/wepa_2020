// This class contains methods for the domain

package projekti.domain;

import java.io.IOException;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class DomainService {
    
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
    
       
    
    @Transactional
    public FileObject fileSaver(MultipartFile file) throws IOException {
        
        FileObject fo = new FileObject();

        fo.setName(file.getOriginalFilename());
        fo.setContentType(file.getContentType());
        fo.setContentLength(file.getSize());
        fo.setContent(file.getBytes());
        
        fo.setUser(getCurrentUser());
        fileObjectRepository.save(fo);
        
        
        return fo;
    }
    
    @Cacheable(value = "user-cache")
    public Account getCurrentUser() {
        return accountRepository.findByUsername(getCurrentUsername());
    }
    
    @Cacheable(value = "user-byId-cache", key = "#id", unless = "#result != null")
    public Account getUserById(Long id) {
        return accountRepository.getOne(id);
    }
    
    @Cacheable(value = "viewed-cache", key = "#pathname", unless = "#result != null")
    public Account getViewedUserByPathname(String pathname) {
        return accountRepository.findByPathname(pathname);
    }
    
    @Cacheable("username-cache")
    public String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();    
    }
    
    
    @Cacheable(value = "userinfo-cache", key = "#user.id", unless = "#result != null")
    public UserInfo getUserInfo(Account user) {
        return userInfoRepository.findByUser(user);
    }
    
    @Cacheable(value = "userinfo_friendrequests-cache", key = "#info.id", unless = "#result != null")
    public List<Account> getUserInfoFriendRequests(UserInfo info) {
        return info.getFriendRequests();
    }
    
    @Cacheable(value = "userinfo-friends-cache", key = "#info.id", unless = "#result != null") 
    public List<Account> getUserInfoFriends(UserInfo info) {
        return info.getFriends();
    }
    
    @Cacheable(value = "userfriends-cache", key = "#id", unless = "#result != null")
    public List<Account> getUserFriendsById(Long id) {
        return accountRepository.findAllFriends(id);
    }
    
    @Cacheable(value = "userinfo-sentrequests-cache", key = "#info.id", unless = "#result != null")
    public List<Account> getUserInfoSentRequests(UserInfo info) {
        return info.getSentRequests();
    }
    
    @Transactional
    public void acceptRequest(Account user, Account contact) {
        UserInfo userInfo = userInfoRepository.findByUser(user);
        UserInfo contactInfo = userInfoRepository.findByUser(contact);
        userInfo.getFriendRequests().remove(contact);
        userInfo.getFriends().add(contact);
        contactInfo.getFriends().add(user);
        contactInfo.getSentRequests().remove(user);
        
        userInfoRepository.save(userInfo);
        userInfoRepository.save(contactInfo);
        
    }
    
    @Transactional
    public void declineRequest(Account user, Account contact) {
        UserInfo userInfo = userInfoRepository.findByUser(user);
        UserInfo contactInfo = userInfoRepository.findByUser(contact);
        userInfo.getFriendRequests().remove(contact);
        contactInfo.getSentRequests().remove(user);
        
        userInfoRepository.save(userInfo);
        userInfoRepository.save(contactInfo);
    }
    
    
    
    @Cacheable(value = "topskills-cache", key = "#id", unless = "#result != null")
    public List<Skill> getTopThreeSkillsById(Long id) {
        return skillRepository.findByUserTopThree(id);
    }
    
    @Cacheable(value = "otherskills-cache", key = "#id", unless = "#result != null")
    public List<Skill> getOtherSkillsById(Long id) {
        return skillRepository.findByUserOffset(id);
    }
    

    
    @Cacheable(value = "messages-contacts-cache", key = "#id", unless = "#result != null")
    public List<Message> getContactMessagesByUserId(Long id) {
        System.out.println("method getContactMessagesByUserId id is: " + id);
        return messageRepository.findByContacts(id);
    }
    
    @Cacheable("messages-op-cache")
    public List<Message> getAllOpMessages() {
        return messageRepository.findByOriginal(0L);
    }
    
    // Checks that the username and pathname only contain letters, digits, underscore and dashes
    public boolean checkString(String s) {
        if (s == null) {
            return false;
        }
        
        if (s.replaceAll(" ", "").length() < s.length()) {
            return false;
        }
        
        if (s.matches(".*[!#¤%&=@£$+?].*")) {
            return false;
        }
        
        return s.matches("^(?=.*\\d*)(?=.*[a-z])(?=.*[A-Z]*)(?=.*[\\-\\_]*).{4,16}$");
        
    }
    
    public boolean checkPassword(String s) {
        
        if (s == null) {
            return false;
        }
        
        if (s.replaceAll(" ", "").length() < s.length()) {
            return false;
        }
        
        return s.matches("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]*).{8,16}$");

    }
       
}
