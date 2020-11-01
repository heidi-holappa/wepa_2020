package projekti;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import projekti.domain.*;

// This controller-class is for JavaScript related functionalities
// It is not used in the final production version, but these methods can be seen in action in path "/testhtml"


@Controller
public class JavaScriptController {
    
    @Autowired 
    private AccountRepository accountRepository;
    
    @Autowired
    private UserInfoRepository userInfoRepository;
    
    @Autowired
    private SkillRepository skillRepository;
    
    @Autowired
    private DomainService domainService;
    
    @ResponseBody
    @GetMapping("/getTopSkills/{id}")
    public List<Skill> getTopSkills(@PathVariable Long id) {
        return skillRepository.findByUserTopThree(id);
    }
    
    @ResponseBody
    @GetMapping("/getOtherSkills/{id}")
    public List<Skill> getOtherSkills(@PathVariable Long id) {
        return skillRepository.findByUserOffset(id);
    }
    
    @Async
    @PostMapping("/endorseJs/{id}")
    public void endorseJs(@PathVariable Long id) {
        
        System.out.println("Methd endorseJs is running");
        
         // Get the user who has logged in
        String username = domainService.getCurrentUsername();
        
        // Get skill and users who have liked it
        Skill skill = skillRepository.getOne(id);
        List<Account> endorsers = skill.getEndorsers();
        
        // If likers already include current user, redirect to index
        if (endorsers.contains(accountRepository.findByUsername(username))) {
            endorsers.remove(accountRepository.findByUsername(username));
            skill.setEndorsements(skill.getEndorsements() - 1);
            skillRepository.save(skill);
        }
        
        // Add one like to counter and add the user who liked the post
        skill.setEndorsements(skill.getEndorsements() + 1);
        endorsers.add(accountRepository.findByUsername(username));
        
        // save 
        skillRepository.save(skill);
    }
}
