package projekti.domain;

import java.util.List;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface MessageRepository extends JpaRepository<Message, Long> {
    
//    Message findByUser(Account user);
    
    @Transactional
    Message findByContent(String content);
    
    @Transactional
    List<Message> findAllByOpId(Long id);
    
    @Transactional
    @Query(value = "SELECT * FROM Message M WHERE M.OP_ID = ?1 ORDER BY MESSAGE_DATE DESC LIMIT 25", nativeQuery = true)
    List<Message> findByOriginal(Long id);
    
    @Transactional
    @Query(value = "SELECT DISTINCT(M.id), M.comments, M.content, M.likes, M.message_date, M.user_id, M.op_id FROM  MESSAGE M LEFT JOIN USER_INFO_FRIENDS UIF ON M.USER_ID = UIF.FRIENDS_ID LEFT JOIN USER_INFO UI ON UIF.USER_INFO_ID = UI.ID WHERE (UI.USER_ID = ?1 AND M.OP_ID = 0) OR (M.USER_ID = ?1 AND M.OP_ID = 0) ORDER BY MESSAGE_DATE DESC LIMIT 25", nativeQuery = true)
    List<Message> findByContacts(Long id);
    
    @Transactional
    List<Message> findByUser(Account user);
    
}
