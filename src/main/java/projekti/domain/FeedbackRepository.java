package projekti.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Heidi Holappa
 */

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    
    List<Feedback> findByUser(Account user);
    
}
