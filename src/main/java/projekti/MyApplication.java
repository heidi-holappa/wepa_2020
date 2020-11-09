package projekti;

import java.util.Arrays;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;

@EnableCaching
@SpringBootApplication
public class MyApplication {
    
    
    public static void main(String[] args) {
//        SpringApplication.run(MyApplication.class);
//        Found this on Stack Overflow - it prints out all the beans provided at launch. Useful while debugging
        ApplicationContext ctx = SpringApplication.run(MyApplication.class, args);

        System.out.println("Let's inspect the beans provided by Spring Boot:");

        String[] beanNames = ctx.getBeanDefinitionNames();
        Arrays.sort(beanNames);
        for (String beanName : beanNames) {
            System.out.println(beanName);
        }

    }
    

    


}
