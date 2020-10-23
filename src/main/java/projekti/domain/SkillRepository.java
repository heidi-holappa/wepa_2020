/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projekti.domain;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author 1040
 */
public interface SkillRepository extends JpaRepository<Skill, Long> {
    
    Skill findBySkill(String skill);
    
//    Skill findByUser(Account user, Pageable pageable);
    
    List<Skill> findByUser(Account user, Pageable pageable);
    
    List<Skill> findByUser(Account user);
    
    
// Find top 3 skills with query
    @Query(value = "SELECT * FROM Skill S WHERE S.USER_ID = ?1 AND ON_LIST = 1 ORDER BY ENDORSEMENTS DESC, SKILL DESC LIMIT 3", nativeQuery = true)
    List<Skill> findByUserTopThree(Long id);

// Find the skills that are not in top 3
    @Query(value = "SELECT * FROM Skill S WHERE S.USER_ID = ?1 AND ON_LIST = 1 ORDER BY ENDORSEMENTS DESC, SKILL DESC OFFSET 3", nativeQuery = true)
    List<Skill> findByUserOffset(Long id);
    
    @Query(value = "SELECT * FROM Skill S WHERE S.USER_ID = ?1 AND ON_LIST = 1 ORDER BY ENDORSEMENTS DESC, SKILL DESC", nativeQuery = true)
    List<Skill> findByUserActive(Long id);
    
    
    
}
