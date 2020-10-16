
package projekti.domain;

import java.util.List;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
@AllArgsConstructor
@NoArgsConstructor
public class Account extends AbstractPersistable<Long> {
    
    
    @NotEmpty
    @Size(min = 4, max = 50)
    private String username;
    
    private String securedPassword;
    
    @NotEmpty
    @Size(min = 4, max = 16)
    private String pathname;
    
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> authorities;
    
    // The id of the profile image
    private Long profileImgId;
    
    
    
    
}
