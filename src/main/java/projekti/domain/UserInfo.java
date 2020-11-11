package projekti.domain;

import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
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
public class UserInfo extends AbstractPersistable<Long> {
    
    // Käyttäjä, johon tiedot liittyvät. Jos olisi liitetty esim. Id-numeroon, voisi liittyä vahingossa väärään henkilöön.
    @OneToOne(cascade = {CascadeType.ALL})
    private Account user;
    
    private String description;
    
    private LocalDateTime updateDate;
    private LocalDateTime created = LocalDateTime.now();
    
    @ManyToMany
    private List<Account> friends;
    
    
    @ManyToMany
    private List<Account> friendRequests;
    
    @ManyToMany
    private List<Account> sentRequests;
    
}
