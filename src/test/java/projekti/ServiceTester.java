package projekti;

import static org.junit.Assert.assertTrue;
import projekti.domain.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;



@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class ServiceTester {
    
    // This class tests methods in Service-classes. 

    @Autowired
    private DomainService domainService;
    
    @Autowired
    PasswordEncoder passwordEncoder;
    
    
    @Test
    public void usernameValidityTest() {
        // Should be true
        String u1 = "martti";
        String u2 = "Martti";
        String u3 = "M4rtti";
        String u4 = "m4rtti-v_rtti";
        
        assertTrue(domainService.checkString(u1));
        assertTrue(domainService.checkString(u2));
        assertTrue(domainService.checkString(u3));
        assertTrue(domainService.checkString(u4));
        
        // Should be false
        String u5 = "m rtti";
        String u6 = "Mar";
        String u7 = "M!rtti";
        String u8 = "m4rtti-vartti-laiska-startti";
        
        assertTrue(!domainService.checkString(u5));
        assertTrue(!domainService.checkString(u6));
        assertTrue(!domainService.checkString(u7));
        assertTrue(!domainService.checkString(u8));
        
        System.out.println("AUTOMATED TESTS: method usernameValidityTest from class ServiceTester ran successfully");
    }
    
    @Test
    public void passwordValidityTester() {
        // Should be true
        String p1 = "S4lasana";
        String p2 = "S@lasan4";
        String p3 = "S4l434n4";
        String p4 = "_!13-N@a";
        
        assertTrue(domainService.checkPassword(p1));
        assertTrue(domainService.checkPassword(p2));
        assertTrue(domainService.checkPassword(p3));
        assertTrue(domainService.checkPassword(p4));
        
        // Should be false
        String p5 = "S l4s4n4";
        String p6 = "Salasana";
        String p7 = "s4lasana";
        String p8 = "m4rtti-vartti-laiska-startti";
        
        assertTrue(!domainService.checkPassword(p5));
        assertTrue(!domainService.checkPassword(p6));
        assertTrue(!domainService.checkPassword(p7));
        assertTrue(!domainService.checkPassword(p8));
        
        System.out.println("AUTOMATED TESTS: method passwordValidityTest from class ServiceTester ran successfully");
    }   
}

