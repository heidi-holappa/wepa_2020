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
    
    @Cacheable(value = "user-byId-cache", key = "#id")
    public Account getUserById(Long id) {
        return accountRepository.getOne(id);
    }
    
    @Cacheable(value = "viewed-cache", key = "#pathname")
    public Account getViewedUserByPathname(String pathname) {
        return accountRepository.findByPathname(pathname);
    }
    
    @Cacheable("username-cache")
    public String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();    
    }
    
    @Transactional
    @Cacheable(value = "userinfo-cache", key = "#user.id")
    public UserInfo getUserInfo(Account user) {
        return userInfoRepository.findByUser(user);
    }
    
    @Cacheable(value = "userinfo_friendrequests-cache", key = "#info.id")
    public List<Account> getUserInfoFriendRequests(UserInfo info) {
        return info.getFriendRequests();
    }
    
    @Cacheable(value = "userinfo-friends-cache", key = "#info.id") 
    public List<Account> getUserInfoFriends(UserInfo info) {
        return info.getFriends();
    }
    
    @Cacheable(value = "userinfo-sentrequests-cache", key = "#info.id")
    public List<Account> getUserInfoSentRequests(UserInfo info) {
        return info.getSentRequests();
    }
    
    
    
    @Cacheable(value = "topskills-cache", key = "#id")
    public List<Skill> getTopThreeSkillsById(Long id) {
        return skillRepository.findByUserTopThree(id);
    }
    
    @Cacheable(value = "otherskills-cache", key = "#id")
    public List<Skill> getOtherSkillsById(Long id) {
        return skillRepository.findByUserOffset(id);
    }
    
    @Cacheable(value = "userfriends-cache", key = "#id")
    public List<Account> getUserFriendsById(Long id) {
        return accountRepository.findAllFriends(id);
    }
    
    @Cacheable(value = "messages-contacts-cache", key = "#id")
    public List<Message> getContactMessagesByUserId(Long id) {
        return messageRepository.findByContacts(id);
    }
    
    @Cacheable("messages-op-cache")
    public List<Message> getAllOpMessages() {
        return messageRepository.findByOriginal(0L);
    }
       
}
