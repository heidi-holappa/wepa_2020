//package projekti;
//
//import org.junit.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.mock.web.MockHttpSession;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContext;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.web.FilterChainProxy;
//import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.web.context.WebApplicationContext;
//
//@ActiveProfiles("test")
//public class Auth {  
//    
//    // This class is for trying to create user authentication for test users. At the time it is unused.
//
//    @Autowired
//    private WebApplicationContext wac;
//
//    @Autowired
//    private FilterChainProxy springSecurityFilter;
//
//    protected MockMvc mockMvc;
//    protected MockHttpSession session;
//
//    public void setup() throws Exception{
//        this.session = new MockHttpSession();
//        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac)
//                .addFilters(springSecurityFilter)
//                .build();
//    }
//
//    protected void setAuthentication(String user, String password, MockHttpSession session){
//        Authentication authentication = new UsernamePasswordAuthenticationToken(user, password);
//        SecurityContext securityContext = SecurityContextHolder.getContext();
//        securityContext.setAuthentication(authentication);
//
//        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,securityContext);
//    }
//    
//
//}
