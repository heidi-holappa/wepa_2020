package projekti.domain;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo extends AbstractPersistable<Long> {
    
    // Käyttäjä, johon tiedot liittyvät. Jos olisi liitetty esim. Id-numeroon, voisi liittyä vahingossa väärään henkilöön.
    @OneToOne(cascade = {CascadeType.ALL})
    private Account user;
    
    // Profiilikuvan tieto. Defaultkuvaksi tulee jokin random-kuva
//    private FileObject profilepic;
    
    // Lista käyttäjän taidoista. Vai, pitäisikö olla erillinen olio? Koulutuskin voisi olla? 
//    private List<String> skills;
    
    private String description;
    
    
    
}
