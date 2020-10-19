package projekti;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import projekti.domain.*;
import projekti.security.*;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;

@EnableCaching
@SpringBootApplication
public class MyApplication {
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired 
    private UserInfoRepository userInfoRepository;
 
    @Autowired
    private ProductionSecurityConfiguration prodSecConf;
    
    @Transactional
    @PostConstruct
    public void init() {
        
    }
    
    
//    public void createUsers() {
//    
//        if (accountRepository.findByUsername("user1") == null) {
//            
//            ArrayList<String> rights = new ArrayList<>();
//            rights.add("USER");
// 
//            Account a1 = new Account();
//            a1.setUsername("user1");
//            a1.setSecuredPassword(prodSecConf.passwordEncoder().encode("user1Pass"));
//            a1.setAuthorities(rights);
//            a1.setPathname("sierra-117");
//            a1.setName("John 117");
//            a1.setProfileImgId(0L);
//            
//            Account a2 = new Account();
//            a2.setUsername("user2");
//            a2.setSecuredPassword(prodSecConf.passwordEncoder().encode("user2Pass"));
//            a2.setAuthorities(rights);
//            a2.setPathname("dr-halsey");
//            a2.setName("Catherine Halsey");
//            a2.setProfileImgId(1L);
//            
//            Account a3 = new Account();
//            a3.setUsername("user3");
//            a3.setSecuredPassword(prodSecConf.passwordEncoder().encode("user3Pass"));
//            a3.setAuthorities(rights);
//            a3.setPathname("captain-lasky");
//            a3.setName("Thomas Lasky");
//            a3.setProfileImgId(1L);
//            
//            accountRepository.save(a1);
//            accountRepository.save(a2);
//            accountRepository.save(a3);
//            
//            UserInfo info1 = new UserInfo();
//            info1.setDescription(a1.getName() + " is a highly accomplished professional. Their career has been described as groundbreaking, innovative and bold.");
//            ArrayList<Account> friendRequests = new ArrayList<>();
//            ArrayList<Account> friends = new ArrayList<>();
//            info1.setFriendRequests(friendRequests);
//            info1.setFriends(friends);
//            info1.setUser(a1);
//            info1.setUpdateDate(LocalDateTime.now());
//            userInfoRepository.save(info1);
//            
//            UserInfo info2 = new UserInfo();
//            info2.setDescription(a2.getName() + " is a highly accomplished professional. Their career has been described as groundbreaking, innovative and bold.");
//            info2.setFriendRequests(friendRequests);
//            info2.setFriends(friends);
//            info2.setUser(a2);
//            info2.setUpdateDate(LocalDateTime.now());
//            userInfoRepository.save(info2);
//            
//            UserInfo info3 = new UserInfo();
//            info3.setDescription(a3.getName() + " is a highly accomplished professional. Their career has been described as groundbreaking, innovative and bold.");
//            info3.setFriendRequests(friendRequests);
//            info3.setFriends(friends);
//            info3.setUser(a3);
//            info3.setUpdateDate(LocalDateTime.now());
//            userInfoRepository.save(info3);
//            
//        
//        }
//    }
    
    
    public static void main(String[] args) {
//        SpringApplication.run(MyApplication.class);
        ApplicationContext ctx = SpringApplication.run(MyApplication.class, args);

        System.out.println("Let's inspect the beans provided by Spring Boot:");

        String[] beanNames = ctx.getBeanDefinitionNames();
        Arrays.sort(beanNames);
        for (String beanName : beanNames) {
            System.out.println(beanName);
        }

    }
    

    


}
