package projekti.domain;

import org.springframework.data.jpa.repository.JpaRepository;


public interface MessageRepository extends JpaRepository<Message, Long> {
    
//    Message findByUser(Account user);
    
    Message findByContent(String content);
    
}
