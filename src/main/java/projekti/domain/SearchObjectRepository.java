package projekti.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SearchObjectRepository extends JpaRepository<SearchObject, Long> {
    
    // Find the users most recent search value
    @Query(value = "SELECT * FROM SEARCH_OBJECT SO WHERE SO.USER_ID = ?1 ORDER BY DATE DESC LIMIT 1", nativeQuery = true)
    SearchObject FindByUserNewest(Account user);
    
}