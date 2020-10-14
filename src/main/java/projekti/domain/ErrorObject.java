
// This object collects possible errors to a String and shows them to the user. 

package projekti.domain;

public class ErrorObject {
    
    private String errormsg;
    
    public ErrorObject() {
        this.errormsg = "";
    }
    
    public ErrorObject(String firstError) {
        this.errormsg = firstError;
    }
    
    public void setError(String msg) {
        this.errormsg = msg; 
    }
    
    public void addError(String msg) {
        this.errormsg = errormsg + " / " + msg;
    }
    
    @Override
    public String toString() {
        return errormsg;
    }
    
}
