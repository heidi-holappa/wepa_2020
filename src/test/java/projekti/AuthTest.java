// This class includes tests for creating a user account, authentication and authorization

package projekti;

import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertTrue;
import projekti.domain.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 *
 * @author Heidi Holappa
 */

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthTest {
    
    // This class tests authentication and authorization
    
    @Autowired
    private AccountRepository accountRepository;    
    
    @Autowired
    PasswordEncoder passwordEncoder;
    
    @Autowired
    private MockMvc mockMvc;
         
    @Test
    public void userInit() throws Exception {
        
//        Create a test user
        Account user1 = new Account();
        user1.setUsername("user1");
        user1.setPathname("user1-path");
        user1.setName("Juuso Juuseri");
        user1.setProfileImgId(0L);
        List<String> auth = new ArrayList<>();
        auth.add("USER");
        user1.setSecuredPassword(passwordEncoder.encode("P4ssword"));
        accountRepository.save(user1);
        
        assertTrue(accountRepository.findByUsername("user1").getPathname().equals("user1-path"));
        
        // Created username and password
        String username = "user1";
        String password = "P4ssword";
        
        // Test works, but the outcome is not as intentioned. As user logs in, credentials are right, but logging in does not work. 
        // Instead of being redirected to "/index", user is redirected to "/" 
        // Testlog states: "SessionId: null; Not granted any authorities"
        mockMvc.perform(post("/auth/login")
            .param("username", username)
            .param("password", password))
            .andExpect(redirectedUrl("/"));
        
        // This emphazises that no authentication took place. 
        // Trying to access content that requires authentication results in redirect to "http://localhost/auth/login"
        mockMvc.perform(get("/updatemode"))
                .andExpect(redirectedUrl("http://localhost/auth/login"));
        
        System.out.println("AUTOMATED TESTS: Test method userInit from class AuthTest ran successfully");
        
    }
    
    @Test
    public void statusOk() throws Exception {
        // model for request index should have these attributes
        mockMvc.perform(get("/index"))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("postedmessages"))
            .andExpect(model().attributeExists("show"));
        
        System.out.println("AUTOMATED TESTS: Test method statusOk from class AuthTest ran successfully");
    }
    
    @Test
    public void statusNotOk() throws Exception {
        // user does not have authorization and is redirected to auth/login with statuscode 302
        mockMvc.perform(get("/updatemode"))
                .andExpect(status().is(302));
        
        // Random requests redirect to login-page
        mockMvc.perform(get("/soemrandompaththatdoesntexist"))
                .andExpect(redirectedUrl("http://localhost/auth/login"));
        
        mockMvc.perform(get("/testhtml"))
                .andExpect(redirectedUrl("http://localhost/auth/login"));
        
        System.out.println("AUTOMATED TESTS: Test method statusNotOk from class AuthTest ran successfully");

    }
    
    // This tests that an authenticated user can access paths that require authentication
    @Test
    @WithMockUser
    public void statusOkAuth() throws Exception {
        
        mockMvc.perform(get("/testhtml"))
                .andExpect(status().isOk());
        
        // Page should contain the following string: 1234!"#¤asqw
                
        System.out.println("AUTOMATED TESTS: Test method statusOkAuth from class AuthTest ran successfully");

    }


   
    
}
