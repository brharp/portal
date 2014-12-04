/*
 * Created on Nov 22, 2005
 *
 */
package ca.nejedly.mail.imap;

/**
 * @author Zdenek Nejedly znejedly@uoguelph.ca
 *
 * An exception thrown when the uid is missing / null
 */
public class GotMailMissingUidException extends GotMailException {

	/**
	 * 
	 */
	public GotMailMissingUidException() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param msg
	 * @param cause
	 */
	public GotMailMissingUidException(String msg, Throwable cause) {
		super(msg, cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param msg
	 */
	public GotMailMissingUidException(String msg) {
		super(msg);
		// TODO Auto-generated constructor stub
	}

}
