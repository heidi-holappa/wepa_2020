package projekti.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface AccountRepository extends JpaRepository<Account, Long> {
    Account findByUsername(String username);
    
    Account findByPathname(String pathname);
    
    List<Account> findAllByNameContainingIgnoreCase(String name);
    
    @Query(value = "SELECT * FROM ACCOUNT A WHERE A.id IN (SELECT AF.ID FROM ACCOUNT A LEFT JOIN USER_INFO UI ON A.ID = UI.USER_ID LEFT JOIN USER_INFO_FRIENDS UIF ON UI.ID = UIF.USER_INFO_ID LEFT JOIN ACCOUNT AF ON UIF.FRIENDS_ID = AF.ID WHERE A.ID = ?1)", nativeQuery=true)
    List <Account> findAllFriends(Long id);
    
    @Query(value = "SELECT * FROM ACCOUNT A LEFT JOIN USER_INFO UI ON A.ID = UI.USER_ID WHERE A.ID != ?1 ORDER BY UI.CREATED DESC LIMIT 3", nativeQuery=true)
    List<Account> findNewest(Long id);
    
}
