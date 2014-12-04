package ca.uoguelph.ccs.portal.portlets.notification.mule.transformers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.mule.MuleManager;
import org.mule.providers.email.MailProperties;
import org.mule.providers.email.MailUtils;
import org.mule.providers.email.MailConnector;
import org.mule.transformers.AbstractEventAwareTransformer;
import org.mule.umo.UMOEventContext;
import org.mule.umo.UMOMessage;
import org.mule.umo.transformer.TransformerException;
import org.mule.util.PropertiesHelper;
import org.mule.util.TemplateParser;
import org.mule.util.Utility;

import java.text.SimpleDateFormat;
import java.text.DateFormat;

import java.sql.Timestamp;

import java.lang.StringBuffer;
import java.util.*;
import java.io.*;

import javax.mail.*;
import javax.mail.Message.RecipientType;
import javax.mail.event.*;
import javax.mail.internet.*;
import javax.activation.*;
import javax.activation.DataHandler;

/**
 * <code>EmailToTargetedMessage</code> will convert java mail to
 * targetMessage, using the headers and message payload as contents. 
 * 
 * @author <a href="mailto:ljairath@uoguelph.ca">lalit jairath</a>
 */
public class TargetedMessageToEmailAckTransformer extends AbstractEventAwareTransformer
{
	/**
	 * logger used by this class
	 */
	protected final static transient Log logger = LogFactory.getLog(TargetedMessageToEmailAckTransformer.class);
	
	public TargetedMessageToEmailAckTransformer()
	{
		registerSourceType(Map.class);
        registerSourceType(UMOEventContext.class);
		setReturnClass(Message.class);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mule.transformers.AbstractTransformer#doTransform(java.lang.Object)
	 */
	public Object transform(Object src, UMOEventContext context) throws TransformerException
	{
        String endpointAddress = endpoint.getEndpointURI().getAddress();
		
		//get attachment and header properties
		try {
            Map targetMessageMap = (HashMap)src;
            String to = (String) targetMessageMap.get("owner");
            String from = "portaltm@uoguelph.ca";
            String subject = (String)targetMessageMap.get("message_subject");
            String cc = "ljairath@uoguelph.ca";
            if (logger.isDebugEnabled()) {
                Iterator it = targetMessageMap.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pairs = (Map.Entry)it.next();
                    logger.debug("TargetedMessageToEmailMap: TargetedMessageToEmail.");
                    logger.debug(pairs.getKey() + " = " + pairs.getValue());
                }
            }

			UMOMessage umomsg = context.getMessage();
			
            to = null;
			to = (String)umomsg.getProperty("owner");
			if (to == null) {
				logger.fatal("Address of targeted message owner is not supplied.");
			}
			
			from = (String)umomsg.getProperty("To");
			if ( from == null ) {
				from = "portaltm@uoguelph.ca";
			}
			
			subject = (String)umomsg.getProperty("Subject");
			if (subject == null) {
				subject = "Re: Your target message";
			} else {
				subject = "Re: " + subject;   
			}
			String payload = "This is the message being sent by the Targeted Message (Mule) portlet.\nYour targeted message is successfully delivered. \n" +
			"Please, log on to http://myportico.uoguelph.ca to release the message to the recipients.";
			System.out.println("To: " + to);
			System.out.println("from: " + from);
			System.out.println("subject: " + subject);
            
            umomsg.setProperty(MailProperties.TO_ADDRESSES_PROPERTY, to);
            umomsg.setProperty(MailProperties.CC_ADDRESSES_PROPERTY, "ljairath@uoguelph.ca");
            umomsg.setProperty(MailProperties.FROM_ADDRESS_PROPERTY, from);
            umomsg.setProperty(MailProperties.SUBJECT_PROPERTY, subject);
            
			Message msg = new MimeMessage((Session) endpoint.getConnector().getDispatcher(endpointAddress).getDelegateSession());
			msg.setRecipients(Message.RecipientType.TO, MailUtils.StringToInternetAddresses(to));
			// sent date
			msg.setSentDate(Calendar.getInstance().getTime());
			msg.setFrom(MailUtils.StringToInternetAddresses(from)[0]);
			msg.setRecipients(Message.RecipientType.CC, MailUtils.StringToInternetAddresses("ljairath@uoguelph.ca"));
			msg.setSubject(subject);
			msg.setText(payload);
			return msg;
		} catch (Exception e) {
			throw new TransformerException(this, e);
		}
	}
}
