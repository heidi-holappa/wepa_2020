// This class contains methods for the domain

package projekti.domain;

import java.io.IOException;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class DomainService {
    
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
    
    public Account getCurrentUser() {
        return accountRepository.findByUsername(getCurrentUsername());
    }
    
    public String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();    
    }
       
}
