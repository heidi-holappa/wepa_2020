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
public class SearchObject extends AbstractPersistable<Long> {
    
    
    @ManyToOne
    private Account user;
    private String value;
    private LocalDateTime date = LocalDateTime.now();
    
}
