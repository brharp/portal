/*
 * Created on Nov 22, 2005
 *
 */
package ca.nejedly.mail.imap;

/**
 * @author Zdenek Nejedly znejedly@uoguelph.ca
 * 
 * An exception thrown when the passord is wrong  
 */
public class GotMailLoginFailedException extends GotMailException {

	public GotMailLoginFailedException() {
		super();
	}

	public GotMailLoginFailedException(String msg, Throwable cause) {
        super(msg, cause);
    }
	
	public GotMailLoginFailedException(String msg) {
        super(msg);
    }
}
