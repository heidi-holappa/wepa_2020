package projekti;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import org.apache.xerces.parsers.SecurityConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import projekti.domain.*;
import projekti.security.*;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class MyApplication {
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired 
    private MessageRepository msgRepository;
 
    @Autowired
    private ProductionSecurityConfiguration prodSecConf;
    
    @PostConstruct
    @Transactional
    public void init() {
// 
//        Account a1 = new Account();
//        Account a2 = new Account();
//        Account anonymousUser = new Account();
// 
//        a1.setUsername("user1");
//        a2.setUsername("user2");
//        anonymousUser.setUsername("anonymousUser");
//        
//        ArrayList<String> rights = new ArrayList<>();
//        rights.add("USER");
// 
//        if (accountRepository.findByUsername(a1.getUsername()) == null) {
//            a1.setSecuredPassword(prodSecConf.passwordEncoder().encode("user1Pass"));
//            a1.setAuthorities(rights);
//            a1.setPathname("user1");
//            a1.setName("Susi Kuulakallio");
//            a1.setProfileImgId(0L);
//      
//            accountRepository.save(a1);
//        }
//        
//        if (accountRepository.findByUsername(anonymousUser.getUsername()) == null) {
//            anonymousUser.setSecuredPassword(prodSecConf.passwordEncoder().encode("salasana"));
//            anonymousUser.setAuthorities(rights);
//            anonymousUser.setPathname("anon");
//            anonymousUser.setName("Anonymous User");
//            anonymousUser.setProfileImgId(1L);
//            
//            accountRepository.save(anonymousUser);
//        }
// 
//        if (accountRepository.findByUsername(a2.getUsername()) == null) {
//            a2.setSecuredPassword(prodSecConf.passwordEncoder().encode("user2Pass"));
//            a2.setPathname("user2");
//            a2.setName("Alex Dunphy");
//            a2.setAuthorities(rights);
//            a2.setProfileImgId(1L);
//            accountRepository.save(a2);
//        }
        
//        if (msgRepository.findAll().isEmpty()) {
//            Message msg = new Message();
//            msg.setContent("Hello Waypoint!");
//            msg.setUser(a1);
//            msg.setLikes(1);
//            msg.setOpId(0L);
//            List<Account> likers = new ArrayList<>();
//            likers.add(a2);
//            msg.setLikers(likers);
//           msgRepository.save(msg);
//        }
        
        
 
    }
    
    
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
