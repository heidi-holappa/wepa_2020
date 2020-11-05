package projekti.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Heidi Holappa
 */

public interface FileRepository extends JpaRepository<FileObject, Long> {
    
    FileObject findByName(String name);
    
    List<FileObject> findByUser(Account user);
    
}
