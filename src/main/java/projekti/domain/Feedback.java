// This object and it's repository handle feedback from users
// This was a bonus-feature to the project, not mandatory

package projekti.domain;

import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;

/**
 *
 * @author Heidi Holappa
 */


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Feedback extends AbstractPersistable<Long> {
    
    private String openFeedback;
    
    private String recommend;
    
    private String useful;
    
    @ManyToOne
    private Account user;
    
    private LocalDateTime messageDate = LocalDateTime.now();
    
}
