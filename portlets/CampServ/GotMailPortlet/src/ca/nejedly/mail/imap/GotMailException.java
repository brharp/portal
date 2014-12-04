/*
 * Created on Nov 15, 2005
 *
 */
package ca.nejedly.mail.imap;

/**
 * @author Zdenek Nejedly znejedly@uoguelph.ca
 *
 */
public class GotMailException extends java.lang.Exception {
	
	public GotMailException() {
    }
	
	public GotMailException(String msg, Throwable cause) {
        super(msg, cause);
    }
	
	public GotMailException(String msg) {
        super(msg);
    }
	
	/** 
	 *  
	 * @return all error messages with non-html line breaks
	 */
	public String getAllMessages() {
        return getAllMessages(false);
    }
    
	/** 
	 * @param html enables or disables output of the html end-of-line &lt;br/&gt;
	 * @return all error messages 
	 */
    public String getAllMessages(boolean html) {
        String txt = "";
        String crlf = "\r\n";        
        if (html) 
            crlf = "<br/>" + crlf;     
        Throwable t = this;
        while (t!=null) {
            String className = t.getClass().getName();
            String msg = t.getMessage() + " (" + className + ")";
            if (msg==null)
                msg = className;
            txt += msg + crlf;
            t = t.getCause();
        }
        return txt;
    }    
}
