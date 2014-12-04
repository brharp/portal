package ca.uoguelph.ccs.portal.portlets.notification.mule.transformers;

import javax.mail.Address;
import java.util.Iterator;
import java.util.Map;

import javax.jms.BytesMessage;
import javax.jms.Message;
import javax.jms.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.config.MuleProperties;
import org.mule.impl.RequestContext;
import org.mule.providers.jms.JmsMessageUtils;
import org.mule.umo.UMOEventContext;
import org.mule.umo.transformer.TransformerException;
import org.mule.util.PropertiesHelper;

import org.mule.providers.jms.transformers.ObjectToJMSMessage;

public class StrictObjectToJmsTransformer extends ObjectToJMSMessage {
	
	private static transient Log logger = LogFactory.getLog(StrictObjectToJmsTransformer.class);
	 
    protected Message transformToMessage(Object src) throws TransformerException
    {
    	Session session = getSession();
    	
        try {
            if (session == null || getEndpoint() != null) {
                session = (Session) getEndpoint().getConnector()
                                                 .getDispatcher("transformerSession")
                                                 .getDelegateSession();
            }

            Message msg = null;
            if (src instanceof Message) {
                msg = (Message) src;
                msg.clearProperties();
            } else {
                msg = JmsMessageUtils.getMessageForObject(src, session);
            }
            // set the event properties on the Message
            UMOEventContext ctx = RequestContext.getEventContext();
            if (ctx == null) {
                logger.warn("There is no current event context");
                return msg;
            }

            Map props = ctx.getProperties();
            props = PropertiesHelper.getPropertiesWithoutPrefix(props, "JMS");
            Map.Entry entry;
            String key;
            for (Iterator iterator = props.entrySet().iterator(); iterator.hasNext();) {
                entry = (Map.Entry) iterator.next();
                key = entry.getKey().toString();
                if (MuleProperties.MULE_CORRELATION_ID_PROPERTY.equals(key)) {
                    msg.setJMSCorrelationID(entry.getValue().toString());
                }
                // this is the one thing we need to alter, we won't allow arrays here
                if (entry.getValue()!=null && !entry.getValue().getClass().isArray()) {
                	msg.setObjectProperty(encodeHeader(key), entry.getValue());	
                }
            }

            return msg;
        } catch (Exception e) {
            throw new TransformerException(this, e);
        }
    }
	
}
