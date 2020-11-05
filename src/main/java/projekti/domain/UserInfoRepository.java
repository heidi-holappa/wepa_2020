
package projekti.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author Heidi Holappa
 */

public interface UserInfoRepository extends JpaRepository<UserInfo, Long> {
    
    UserInfo findByUser(Account user);
    
}
