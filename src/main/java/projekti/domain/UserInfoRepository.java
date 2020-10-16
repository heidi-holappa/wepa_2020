
package projekti.domain;

import org.springframework.data.jpa.repository.JpaRepository;


public interface UserInfoRepository extends JpaRepository<UserInfo, Long> {
    
    UserInfo findByUser(Account user);
    
}
