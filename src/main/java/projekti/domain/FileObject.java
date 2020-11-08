
package projekti.domain;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.springframework.data.jpa.domain.AbstractPersistable;

/**
 *
 * @author Heidi Holappa
 */

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileObject extends AbstractPersistable<Long> {
    
    private String name;
    private String contentType;
    private Long contentLength;
    
    @ManyToOne
    private Account user;
        
//    @Lob 
//    @Basic(fetch = FetchType.LAZY)
//    @Column(columnDefinition="BLOB")
    @Type(type = "org.hibernate.type.BinaryType")    
    private byte[] content;
    
}
