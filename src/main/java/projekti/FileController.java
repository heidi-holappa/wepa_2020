package projekti;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import projekti.domain.*;

/**
 *
 * @author Heidi Holappa
 */

@Controller
public class FileController {
    
    @Autowired
    private FileRepository fileRepository;
    
    // Taken from task 04_02
    @PostMapping("/files")
    public String addFile(@RequestParam("file") MultipartFile file) throws IOException {
        FileObject fileObject = new FileObject();
        fileObject.setContentType(file.getContentType());
        fileObject.setContent(file.getBytes());
        fileObject.setName(file.getOriginalFilename());
        fileObject.setContentLength(file.getSize());
        fileRepository.save(fileObject);
 
        return "redirect:/index";
    }
    
}
