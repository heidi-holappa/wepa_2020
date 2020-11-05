// CustomUserDetailService, much like the one done during the course. 

package projekti.security;

// Import packages
import java.util.ArrayList;
import projekti.domain.*;


// Other imports 
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
 
@Service
public class CustomUserDetailsService implements UserDetailsService {
 
    @Autowired
    private AccountRepository accountRepository;
 
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByUsername(username);
        if (account == null) {
            throw new UsernameNotFoundException("No such user: " + username);
        }
        
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
            for (String authority : account.getAuthorities()) {
            authorities.add(new SimpleGrantedAuthority(authority));
        }
 
        return new org.springframework.security.core.userdetails.User(
                account.getUsername(),
                account.getSecuredPassword(),
                true,
                true,
                true,
                true,
                authorities);
    }
}
