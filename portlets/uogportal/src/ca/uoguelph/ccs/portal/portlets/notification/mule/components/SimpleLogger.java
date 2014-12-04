package ca.uoguelph.ccs.portal.portlets.notification.mule.components;

import java.util.Date;
import java.text.DateFormat;

import javax.mail.internet.MimeMessage;

/**
 * Very basic logger just to demonstrate the component is doing something
 * 
 * @author NL24167
 */
public class SimpleLogger {

        DateFormat fullDateFormat =
            DateFormat.getDateTimeInstance(
            DateFormat.FULL,
            DateFormat.FULL);

	String cdate = fullDateFormat.format(new Date(System.currentTimeMillis()));
	
	public void logMessage(String message) {
		//System.out.println("cdate= " + cdate);
		//System.out.println("Message '"+ message + "'passed through here @ " + cdate);

	}
	
	public void logMessage(MimeMessage message) {
		//System.out.println("Message '"+ message + "'passed through here @ " + cdate);
	}
}

