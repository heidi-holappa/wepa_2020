package projekti.domain;

import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;

// This class stores logged information about the user's activity on the site

/**
 *
 * @author Heidi Holappa
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class LogObject extends AbstractPersistable<Long> {
    
    @ManyToOne
    private Account user;
    
    private String userAction;
    
    private LocalDateTime logDate = LocalDateTime.now();
    
}
