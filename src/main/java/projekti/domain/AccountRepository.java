package projekti.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AccountRepository extends JpaRepository<Account, Long> {
    Account findByUsername(String username);
    
    Account findByPathname(String pathname);
    
    List<Account> findAllByNameContainingIgnoreCase(String name);
    
}
