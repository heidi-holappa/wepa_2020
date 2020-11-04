package projekti;

import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 *
 * @author Heidi Holappa
 */

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class FluentRunTest extends org.fluentlenium.adapter.junit.FluentTest {
    
    @LocalServerPort
    private Integer port;

    @Test
    public void canLoginAndPost() {
        
        // Go to path "/index" and assert that the welcome text is found (assertTrue) and no disinformation is found (assertFalse). 
        // That last part was a joke.
        goTo("http://localhost:" + port + "/index");        
        assertTrue(pageSource().contains("Welcome to Waypoint!"));
        assertFalse(pageSource().contains("Facebook is the greatest social media platform ever!"));
        
        System.out.println("AUTOMATED TESTS: Test method canLoginAndPost from class FluentTesting ran successfully");
    }
    
}
