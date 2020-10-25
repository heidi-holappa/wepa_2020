package projekti;

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
