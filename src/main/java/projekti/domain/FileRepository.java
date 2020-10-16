
package projekti.domain;

import org.springframework.data.jpa.repository.JpaRepository;


public interface FileRepository extends JpaRepository<FileObject, Long> {
    
    FileObject findByName(String name);
    
}
