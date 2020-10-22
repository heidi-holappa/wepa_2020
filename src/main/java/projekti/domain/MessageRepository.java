package projekti.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface MessageRepository extends JpaRepository<Message, Long> {
    
//    Message findByUser(Account user);
    
    Message findByContent(String content);
    
    List<Message> findAllByOpId(Long id);
    
    @Query(value = "SELECT * FROM Message M WHERE M.OP_ID = ?1 ORDER BY MESSAGE_DATE DESC LIMIT 25", nativeQuery = true)
    List<Message> findByOriginal(Long id);
    
}
