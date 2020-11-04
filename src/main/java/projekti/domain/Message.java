package projekti.domain;

import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Data
@EqualsAndHashCode(callSuper=true)
@NoArgsConstructor
@AllArgsConstructor
public class Message extends AbstractPersistable<Long> {
    
    @ManyToOne
    private Account user;
    
    private Long opId;
    
    @Lob
    private String content;
    
    @ManyToMany(cascade = {CascadeType.ALL})
    private List<Account> likers;
    
    private Integer likes;
    
    private Integer Comments;
    
    private LocalDateTime messageDate = LocalDateTime.now();
    
}
