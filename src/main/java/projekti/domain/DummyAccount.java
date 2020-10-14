package projekti.domain;

import javax.persistence.Entity;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DummyAccount extends AbstractPersistable<Long> {
    
    @NotEmpty
    @Size(min = 4, max = 50)
    private String username;
    
    @NotEmpty
    @Size(min = 8, max = 16)
    private String password;
    
    @NotEmpty
    @Size(min = 8, max = 16)
    private String passwordtwo;
    
    @NotEmpty
    @Size(min = 4, max = 16)
    private String pathname;
    
    
    
    
}