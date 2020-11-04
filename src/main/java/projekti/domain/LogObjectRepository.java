package projekti.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author Heidi Holappa
 */

public interface LogObjectRepository extends JpaRepository<LogObject, Long> {
    
    @Query(value = "SELECT * FROM LOG_OBJECT LO WHERE LO.USER_ID = ?1 ORDER BY LOG_DATE ASC", nativeQuery = true)
    List<LogObject> findByUserId(Long id);
    
}
