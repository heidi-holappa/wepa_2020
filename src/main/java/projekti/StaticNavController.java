// Tämä kontrolleri sisältää sivuston staattisen sisällön kontrollerit (esim. footer)


package projekti;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StaticNavController {
    
    
    @GetMapping
    public String projectDoc() {
        return "docs/project";
    }
    
    
    
}
