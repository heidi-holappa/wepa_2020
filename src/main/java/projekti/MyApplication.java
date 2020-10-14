package projekti;

import java.util.ArrayList;
import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import org.apache.xerces.parsers.SecurityConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import projekti.domain.*;
import projekti.security.*;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MyApplication {
    
    @Autowired
    private AccountRepository accountRepository;
 
    @Autowired
    private ProductionSecurityConfiguration prodSecConf;
    
    @PostConstruct
    @Transactional
    public void init() {
 
        Account a1 = new Account();
        Account a2 = new Account();
 
        a1.setUsername("user1");
        a2.setUsername("user2");
        
        ArrayList<String> rights = new ArrayList<>();
        rights.add("USER");
 
        if (accountRepository.findByUsername(a1.getUsername()) == null) {
            a1.setSecuredPassword(prodSecConf.passwordEncoder().encode("user1Pass"));
            a1.setAuthorities(rights);
            a1.setPathname("user1");
            accountRepository.save(a1);
        }
 
        if (accountRepository.findByUsername(a2.getUsername()) == null) {
            a2.setSecuredPassword(prodSecConf.passwordEncoder().encode("user2Pass"));
            a2.setPathname("user2");
            a2.setAuthorities(rights);
            accountRepository.save(a2);
        }
 
    }
    
    public static void main(String[] args) {
        SpringApplication.run(MyApplication.class);
    }

}
