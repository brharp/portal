/*
 * Created on Nov 22, 2005
 *
 */
package ca.nejedly.mail.imap;

/**
 * @author Zdenek Nejedly znejedly@uoguelph.ca
 *
 * An exception thrown when the password is missing / null
 */
public class GotMailMissingPasswordException extends GotMailException {

	/**
	 * 
	 */
	public GotMailMissingPasswordException() {
		super();
	}

	/**
	 * @param msg
	 * @param cause
	 */
	public GotMailMissingPasswordException(String msg, Throwable cause) {
		super(msg, cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param msg
	 */
	public GotMailMissingPasswordException(String msg) {
		super(msg);
		// TODO Auto-generated constructor stub
	}

}
