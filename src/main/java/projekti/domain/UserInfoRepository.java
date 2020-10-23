
package projekti.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface UserInfoRepository extends JpaRepository<UserInfo, Long> {
    
    UserInfo findByUser(Account user);
    
//    Check if needed
    
//    @Query(value = "SELECT AF FROM ACCOUNT AF LEFT JOIN USER_INFO_FRIENDS UIF ON AF.ID = UIF.FRIENDS_ID LEFT JOIN USER_INFO UI ON UIF.USER_INFO_ID = UI.ID LEFT JOIN ACCOUNT A ON UI.USER_ID = A.ID WHERE A.ID = ?1", nativeQuery=true)
//    @Query(value = "SELECT * FROM ACCOUNT A WHERE A.id IN (SELECT AF.ID FROM ACCOUNT A LEFT JOIN USER_INFO UI ON A.ID = UI.USER_ID LEFT JOIN USER_INFO_FRIENDS UIF ON UI.ID = UIF.USER_INFO_ID LEFT JOIN ACCOUNT AF ON UIF.FRIENDS_ID = AF.ID WHERE A.ID = ?1)", nativeQuery=true)
//    List<Account> findFriendsByUser(Long id);
    
}
