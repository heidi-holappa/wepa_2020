package projekti.domain;


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
