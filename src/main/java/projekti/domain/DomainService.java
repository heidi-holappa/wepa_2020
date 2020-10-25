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
    
    @Cacheable("user-cache")
    public Account getCurrentUser() {
        return accountRepository.findByUsername(getCurrentUsername());
    }
    
    @Cacheable("user-byId-cache")
    public Account getUserById(Long id) {
        return accountRepository.getOne(id);
    }
    
    @Cacheable("viewed-cache")
    public Account getViewedUserByPathname(String pathname) {
        return accountRepository.findByPathname(pathname);
    }
    
    @Cacheable("username-cache")
    public String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();    
    }
    
    @Transactional
    @Cacheable("userinfo-cache")
    public UserInfo getUserInfo(Account user) {
        return userInfoRepository.findByUser(user);
    }
    
    @Cacheable("userinfo_friendrequests-cache")
    public List<Account> getUserInfoFriendRequests(UserInfo info) {
        return info.getFriendRequests();
    }
    
    @Cacheable("userinfo-friends-cache") 
    public List<Account> getUserInfoFriends(UserInfo info) {
        return info.getFriends();
    }
    
    @Cacheable("userinfo-sentrequests-cache")
    public List<Account> getUserInfoSentRequests(UserInfo info) {
        return info.getSentRequests();
    }
    
    
    
    @Cacheable("topskills-cache")
    public List<Skill> getTopThreeSkillsById(Long id) {
        return skillRepository.findByUserTopThree(id);
    }
    
    @Cacheable("otherskills-cache")
    public List<Skill> getOtherSkillsById(Long id) {
        return skillRepository.findByUserOffset(id);
    }
    
    @Cacheable("userfriends-cache")
    public List<Account> getUserFriendsById(Long id) {
        return accountRepository.findAllFriends(id);
    }
    
    @Cacheable("messages-contacts-cache")
    public List<Message> getContactMessagesByUserId(Long id) {
        return messageRepository.findByContacts(id);
    }
    
    @Cacheable("messages-op-cache")
    public List<Message> getAllOpMessages() {
        return messageRepository.findByOriginal(0L);
    }
       
}
