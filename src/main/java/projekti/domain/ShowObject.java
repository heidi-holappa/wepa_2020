package projekti.domain;

// This class creates an object to manage, which posts are filtered to view /index


/**
 *
 * @author Heidi Holappa
 */

public class ShowObject {
    
    private String show;
    
    public ShowObject() {
        this.show = "My contacts";
    }
    
    public void setShow(String show) {
        this.show = show;
    }
    
    @Override
    public String toString() {
        return this.show;
    }
    
}
