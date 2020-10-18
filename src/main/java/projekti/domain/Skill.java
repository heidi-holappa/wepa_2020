
package projekti.domain;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Skill extends AbstractPersistable<Long> {
    
    private String skill;
    
    @ManyToOne
    private Account user;
    
    @ManyToMany(cascade = {CascadeType.ALL})
    private List<Account> endorsers;
    
    private Integer endorsements;
}
