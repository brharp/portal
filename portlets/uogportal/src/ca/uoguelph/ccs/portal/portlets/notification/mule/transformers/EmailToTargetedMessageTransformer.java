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
public class EmailToTargetedMessageTransformer extends AbstractEventAwareTransformer
{
	/**
	 * logger used by this class
	 */
	protected final static transient Log logger = LogFactory.getLog(EmailToTargetedMessageTransformer.class);
	
	//start tag for text message
	private final static String textDelimiter = ":txet:";
    //start tag for html message
    private final static String htmlDelimiter = ":lmth:";
    //payloadDelimiter for plain/html text
    private final static String textHtmlDelimiter = ":txet-lmth:";
	
	public EmailToTargetedMessageTransformer()
	{
		registerSourceType(Part.class);
		registerSourceType(UMOEventContext.class);
		setReturnClass(Map.class);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mule.transformers.AbstractTransformer#doTransform(java.lang.Object)
	 */
	public Object transform(Object src, UMOEventContext context) throws TransformerException
	{
		//store message components (headers, payload, attachment) into targetMessageMap
		Map targetedMessageMap = new HashMap();
		
		//get attachment and header properties
		try {
			UMOMessage umomsg = context.getMessage();
            String  postgresNull = "null";
            /*
            if ((String)umomsg.getProperty("From") == null ) {
            	targetedMessageMap.put("message_from", postgresNull);
            } else {
                targetedMessageMap.put("message_from", (String)umomsg.getProperty("From"));
            }*/

            if ((String)umomsg.getProperty("ownerdisplayname") == null ) {
                targetedMessageMap.put("message_from", postgresNull);
            } else {
                targetedMessageMap.put("message_from", (String)umomsg.getProperty("ownerdisplayname"));
            }
            

            if ((String)umomsg.getProperty("Subject")== null ) {
                targetedMessageMap.put("message_subject", postgresNull);
            } else {
                targetedMessageMap.put("message_subject", umomsg.getProperty("Subject"));
            }
            /*
            if ((String)umomsg.getProperty("owner")== null ) {
            	targetedMessageMap.put("owner", "ljairathUOG");
            } else {
                targetedMessageMap.put("owner", umomsg.getProperty("owner"));
            }*/
            
            if ((String)umomsg.getProperty("ownermail")== null ) {
                targetedMessageMap.put("owner", "ljairath@uoguelph.ca");
            } else {
                targetedMessageMap.put("owner", umomsg.getProperty("ownermail"));
            }

            //Timestamp expiry = new Timestamp(Calendar.getInstance().getTimeInMillis());
            //targetedMessageMap.put("expiry", expiry);
            
            /*
            if ((String)umomsg.getProperty("expiry")== null ) {
                targetedMessageMap.put("expiry", postgresNull);
            } else {
                targetedMessageMap.put("expiry", umomsg.getProperty("expiry"));
            }
                String expiryText = "2006-01-31";
                DateFormat df = new SimpleDateFormat( "yyyy-MM-dd" );
                Timestamp expiry = new Timestamp( df.parse( expiryText).getTime() );
                logger.debug("eXPIRY:" + expiry);
                targetedMessageMap.put("expiry", expiry);
            */
            if ((String)umomsg.getProperty("importance")== null ) {
                targetedMessageMap.put("importance", "Normal");
            } else {
                targetedMessageMap.put("importance", umomsg.getProperty("importance"));
            }

            if ((String)umomsg.getProperty("role")== null ) {
                targetedMessageMap.put("role", "");
            } else {
                targetedMessageMap.put("role", umomsg.getProperty("role"));
            }

            //* all header properties
            Iterator it = umomsg.getPropertyNames();
            while (it.hasNext()) {
                String header = (String)it.next();
                //logger.debug("Header Name: " + header);
            }
        
            Set attachName = umomsg.getAttachmentNames();            
			it = attachName.iterator();
			while (it.hasNext()) {
				// Get name of attachment name
				String fileName = (String)it.next();
				//user list in the attachment
				logger.debug("Name of attachment" + fileName);
                if ( fileName != null && fileName.length() > 1) {
    				DataHandler dh = umomsg.getAttachment(fileName);
    				InputStream ins = dh.getInputStream();
    				targetedMessageMap.put("user", getAttachmentAsString(ins));
                } else {
                    targetedMessageMap.put("user", postgresNull);
                }
                
			}
			//get message payload in plain and html text
			Part part = (Part) src;
			if (logger.isDebugEnabled()) {
				//sanity check
				if (src instanceof Part) {
					logger.debug("Message is Part instance.");
				} else {
					logger.debug("Message is NOT Part instance.");
				}
			}
			
			//message payload is plain/text OR plan/html
            StringBuffer partSb = new StringBuffer();
			String payload = getTextHTMLPart(part, partSb);
			targetedMessageMap.put("message", payload);
			
			if (logger.isDebugEnabled()) {
				it = targetedMessageMap.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry pairs = (Map.Entry)it.next();
					logger.debug("targetedMessageMap: ******************EmailToTargetedMessage.");
					logger.debug(pairs.getKey() + " = " + pairs.getValue());
				}
			}
		} catch (Exception e) {
			throw new TransformerException(this, e);
		}
		return targetedMessageMap;
	}
	/**
     * 
	 * @param p MessagePart
	 * @return String containing both text and html messages separated by a delimiter
	 * @throws Exception
	 */
	private static String getTextHTMLPart(Part p, StringBuffer partSb) throws Exception {
		String ct = p.getContentType();
		try {
			logger.debug("CONTENT-TYPE: " + (new ContentType(ct)).toString());
		} catch (ParseException pex) {
			logger.debug("BAD CONTENT-TYPE: " + ct);
		}
		/*
		 * Using isMimeType to determine the content type avoids
		 * fetching the actual content data until we need it.
		 */
		
		if (p.isMimeType("multipart/*")) {
            logger.debug("+++This is a Multipart+++.");
            Multipart mp = (Multipart)p.getContent();
            int count = mp.getCount();
            logger.debug("Number of Message Parts : " + count);
            for (int i = 0; i < count; i++) {
                logger.debug("Part # : " + i);
                getTextHTMLPart(mp.getBodyPart(i), partSb);
            }
        } else if (p.isMimeType("text/plain")) {
			logger.debug("+++This is plain text+++.");
			//partMap.put("plain-text",(String)p.getContent());

			if(partSb == null || partSb.length() == 0) {
            	partSb.append("text/plain\n");
                partSb.append((String)p.getContent());
            }

            /*
            if(partSb == null || partSb.length() == 0) {
            	partSb.append(textDelimiter).append((String)p.getContent());
            } else {
                partSb.append(textHtmlDelimiter).append(textDelimiter).append((String)p.getContent());
            }*/
		} else if (p.isMimeType("text/html")) {
			logger.debug("+++This is html text+++.");

            //partMap.put("html-text",(String)p.getContent());
            StringBuffer htmlPartSb = new StringBuffer();
            if(partSb != null || partSb.length() != 0) {
                partSb = htmlPartSb;
            }
            partSb.append((String)p.getContent());
            /*
            if(partSb == null || partSb.length() == 0) {
                partSb.append(htmlDelimiter).append((String)p.getContent());
            } else {
                partSb.append(textHtmlDelimiter).append(htmlDelimiter).append((String)p.getContent());
            }*/
		} else if (p.isMimeType("message/rfc822")) {
			logger.debug("This is a Nested Message");
		} else {
			/*
			 * If we actually want to see the data, and it's not a
			 * MIME type we know, fetch it and check its Java type.
			 */
			Object o = p.getContent();
			if (o instanceof String) {
				logger.debug("+++This is a string+++.");
			} else if (o instanceof InputStream) {
				logger.debug("+++This is just an input stream+++.");
			} else {
				logger.debug("+++This is an unknown type+++.");
			}
		}
		return partSb.toString();
	}
	
    /**
     * 
     * @param is message as input stream
     * @return String as attachment 
     * @throws Exception
     * 
     * user ids - emails are sent as attachement and read as inputstream
     */
	private static String getAttachmentAsString(InputStream is ) throws Exception{
		//String contentType = messagePart.getContentType();
		//if (contentType.startsWith("text/")) {
		
		// If the stream is not already buffered, wrap a BufferedInputStream
		// around it.
		if ((is instanceof BufferedInputStream) == false) {
			is = new BufferedInputStream(is);
		}
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuffer buffer = new StringBuffer(32768);
		
		final String lineSeparator = System.getProperty("line.separator"); 
		String line = reader.readLine();
		while (line != null) {
			buffer.append(line);
			line = reader.readLine();
			if (line != null) {
				buffer.append(lineSeparator);
			}
		}
		return buffer.toString();
	}
}   
