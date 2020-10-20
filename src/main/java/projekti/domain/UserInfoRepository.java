
package projekti.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserInfoRepository extends JpaRepository<UserInfo, Long> {
    
    UserInfo findByUser(Account user);
    
//    Check if needed
//    List<Account> findBySentRequests(Account user);
    
}
