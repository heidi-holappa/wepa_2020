package projekti;

import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import projekti.domain.*;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class RepositoryTest {
    
    // This class contains tests for selected repositories
    
    @Autowired
    private MessageRepository messageRepository;
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    PasswordEncoder passwordEncoder;
    
    
    // User account can be created. Password is encrypted.
    @Test
    public void accountRepositoryTest() {
        
        //        Create a test user
        Account user1 = new Account();
        user1.setUsername("Test1");
        user1.setPathname("user1-path");
        user1.setName("Juuso Juuseri");
        user1.setProfileImgId(0L);
        List<String> auth = new ArrayList<>();
        auth.add("USER");
        user1.setSecuredPassword(passwordEncoder.encode("P4ssword"));
        accountRepository.save(user1);
        
        // Assert that the user created exists and the password is not the string provided.
        assertTrue(accountRepository.findByUsername("Test1") != null);
        assertFalse(accountRepository.findByUsername("Test1").getSecuredPassword().equals("P4ssword"));
        
        System.out.println("AUTOMATED TESTS: Test method accountRepositoryTest from class RepositoryTests ran successfully");   
    }
    
    // Create test-message.
    @Test
    public void postMessageTest() {
        
        
        // Create a new message object
        Message msg = new Message();
        
        msg.setComments(0);
        msg.setContent("Content12345");
        ArrayList<Account> likers = new ArrayList<>();
        msg.setLikers(likers);
        msg.setLikes(0);
        msg.setComments(0);
        msg.setOpId(0L);
        
        // Get user from repository
        msg.setUser(accountRepository.findByUsername("Test1"));
        
        // Save object
        messageRepository.save(msg);
        
        // Set up boolean test to see, if object is in repository
        boolean msgSaved = false;
        
        if (messageRepository.findByContent("Content12345") != null) {
            msgSaved = true;
        }
        
        assertTrue(msgSaved);   
        
        System.out.println("AUTOMATED TESTS: Test method postMessageTest from class RepositoryTests ran successfully");
    }
}
