package ca.uoguelph.ccs.portal.portlets.notification.mule.transformers;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.io.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.sql.Timestamp;


import org.ietf.jgss.MessageProp;
import org.mule.transformers.AbstractTransformer;
import org.mule.umo.transformer.TransformerException;

import javax.mail.Message;
import javax.mail.internet.MimeMultipart;


public class StringToTargetedMessageTransformer extends AbstractTransformer {
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mule.transformers.AbstractTransformer#doTransform(java.lang.Object)
	 * message_subject, message_from, role, message, importance, expiry
	 */
	public Object doTransform(Object src) throws TransformerException
	{
        String payload = (String)src;
        Map map = new HashMap();
        Map targetedMessageMap = new HashMap();
        StringBuffer sbEmail = new StringBuffer();

		try {
			String[] message = payload.split("!@#");
			int len = message.length;
			for (int i=0; i<len-1; i++) {
				String[] messagePart = message[i].trim().split("=",2);
                
                //System.out.println("messagePart[0]" +  messagePart[0]);
                //System.out.println("messagePart[1]" +  messagePart[1]);
                
                if (null == messagePart || messagePart.length == 0 || "null".equalsIgnoreCase(messagePart[0])) {
                 continue;   
                }
				String key = messagePart[0];
				String value = messagePart[1];
				if (key.startsWith("user")) {
					if (sbEmail.length() != 0) {
						sbEmail.append(":");   
					}
					sbEmail.append(value.trim());
				} else if (key.startsWith("from")) {
					targetedMessageMap.put("message_from", value);
				} else if (key.startsWith("subject")) {
					targetedMessageMap.put("message_subject", value);
				} else if (key.startsWith("importance")) {
					targetedMessageMap.put("importance", value);
				} else if (key.startsWith("payload")) {
					targetedMessageMap.put("message", value);
				} else if (key.startsWith("recipientCount")) {
					targetedMessageMap.put("recipientCount", value);                
				} else if (key.startsWith("expiry")) {
					SimpleDateFormat sdfInput = new SimpleDateFormat( "MM/dd/yy" );
					SimpleDateFormat sdfOutput = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
					Date date = null;
					try {
						date = sdfInput.parse(value);
                        targetedMessageMap.put("expiry", Timestamp.valueOf(sdfOutput.format( date )));
					} catch (ParseException pe) {
						//System.out.println("Expiry parse date exception: " + pe.getMessage());
					}
					//System.out.println( sdfOutput.format( date ) );
                    
					//targetedMessageMap.put("expiry", value);
				} else if (key.startsWith("from2")) {
					
				} else {
					map.put(key, value);
				}
			}
			if (sbEmail.length() != 0 && sbEmail != null) {
				targetedMessageMap.put("user", sbEmail.toString());
			}
			/*
			 Iterator it = map.entrySet().iterator();
			 while (it.hasNext()) {
			 Map.Entry pairs = (Map.Entry)it.next();
			 //System.out.println(pairs.getKey() + " = " + pairs.getValue());
			 }
			 */
			
			String roles =(String)map.get("roles");
			if (null != roles) {        
				StringBuffer sbRole = new StringBuffer();
				sbRole.append(roles);
				roles = setRoles(roles, sbRole, map);
                targetedMessageMap.put("role", roles);
				//System.out.println("TEST=" + roles);
			}
		} catch (Exception e) {
			throw new TransformerException(this, e);
		}
		return targetedMessageMap;
	}
	
	public static String setRoles (String roles, StringBuffer sbRole, Map map) {
		String[] rolePart = roles.split(",");
		int len = rolePart.length;
		for (int i=0; i<len; i++) {
			roles = (String)map.get("roles." + rolePart[i]);
			if (null != roles) {
				sbRole.append("#").append(roles);
				setRoles(roles, sbRole, map);
			}
		}
		return sbRole.toString();
	}    
}