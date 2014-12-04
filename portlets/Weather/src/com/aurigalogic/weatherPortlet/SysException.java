/*-- $Id: SysException.java,v 1.1 2006/12/22 14:40:02 ljairath Exp $ --*/
package com.aurigalogic.weatherPortlet;

/**
 * A System Exception.
 * System Exceptions are checked.
 *
 * @author Sonal Anvekar
 * @version $Revision: 1.1 $ $Date: 2006/12/22 14:40:02 $ 
 */
public class SysException extends RuntimeException {

	public SysException(String msg) {
		super(msg);	
	}

	public SysException() {
		super("");
	}

	public SysException(String msg, Throwable e) {
		super(msg, e);
	}

	public SysException(Throwable e) {
		super(e.getMessage(), e);
	}
}
