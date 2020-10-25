package projekti.domain;

import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;


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
