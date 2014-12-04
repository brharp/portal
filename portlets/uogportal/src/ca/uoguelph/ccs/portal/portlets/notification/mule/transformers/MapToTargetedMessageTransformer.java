package ca.uoguelph.ccs.portal.portlets.notification.mule.transformers;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.io.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.sql.Timestamp;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ietf.jgss.MessageProp;
import org.mule.transformers.AbstractTransformer;
import org.mule.umo.transformer.TransformerException;

import javax.mail.Message;
import javax.mail.internet.MimeMultipart;


public class MapToTargetedMessageTransformer extends AbstractTransformer {
	
    protected final transient Log logger = LogFactory.getLog(getClass());

    public MapToTargetedMessageTransformer()
    {
        registerSourceType(Map.class);
        registerSourceType(String.class);
        registerSourceType(String.class);
        registerSourceType(String.class);
        registerSourceType(String.class);
        registerSourceType(String.class);
        registerSourceType(String.class);
        registerSourceType(Timestamp.class);
        setReturnClass(Map.class);
    }

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mule.transformers.AbstractTransformer#doTransform(java.lang.Object)
	 * message_subject, message_from, role, message, importance, expiry
	 */
	public Object doTransform(Object src) throws TransformerException
	{
		Map targetedMessage = (HashMap)src;
        
		Map targetedMessageMap = new HashMap();
		
		try {
			//users -email addresses come in attachment
			//targetedMessageMap.put("user", (String)targetedMessage.get("user"));
			targetedMessageMap.put("message_subject", (String)targetedMessage.get("message_subject"));
			targetedMessageMap.put("message_from", (String) targetedMessage.get("message_from"));
			targetedMessageMap.put("owner", (String)targetedMessage.get("owner"));
			//message(payload) contains both text/plain and text/html message
			targetedMessageMap.put("message", (String)targetedMessage.get("message"));
            //targetedMessageMap.put("expiry", (Timestamp)targetedMessage.get("expiry"));
            //if (targetedMessage.get("expiry") instanceof Timestamp) {
             //System.out.println("Expiry is instanceof Timestamp.");   
            //}
            targetedMessageMap.put("received", (Timestamp)targetedMessage.get("receive"));
            
			Iterator it = targetedMessageMap.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pairs = (Map.Entry)it.next();
				logger.debug(pairs.getKey() + " = " + pairs.getValue());
			}
			//additionally expiry, importance, roles may be added in the future through mass mail 
		} catch (Exception e) {
			throw new TransformerException(this, e);
		}
        
		return targetedMessage;
	}
}