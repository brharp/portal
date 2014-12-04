package ca.uoguelph.ccs.portal.portlets.notification.bus;

import java.util.*;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import ca.uoguelph.ccs.portal.portlets.notification.db.UofGTargettedNotificationDAO;
import ca.uoguelph.ccs.portal.portlets.notification.db.UofGUserNotificationDAO;
import ca.uoguelph.ccs.portal.portlets.notification.db.UofGUserLatestNotificationSeenDAO;
import ca.uoguelph.ccs.portal.portlets.notification.bus.UofGTargettedNotification;

/*
 * 
 * @author lalit
 *
 * Helper class for targeted messaging portlet
 * 
 */
public class NotificationPortletHelper
{
	private static final Log logger = LogFactory.getLog(NotificationPortletHelper.class);
	
	public static UofGTargettedNotificationDAO targettedNotificationDAO;
	public static UofGUserNotificationDAO userNotificationDAO;
    public static UofGUserLatestNotificationSeenDAO userLatestNotificationSeenDAO;
	
	public static void initTargetMessageDAO() throws Exception {
		try {
            logger.debug("Initialize TargetMessageDAOs.");
			ClassPathResource classPathResource = new ClassPathResource("spring-config.xml");
			XmlBeanFactory beanFactory = new XmlBeanFactory(classPathResource);
			targettedNotificationDAO = (UofGTargettedNotificationDAO) beanFactory.getBean("targettedNotificationDAO");
			userNotificationDAO = (UofGUserNotificationDAO) beanFactory.getBean("userNotificationDAO");
            userLatestNotificationSeenDAO = (UofGUserLatestNotificationSeenDAO) beanFactory.getBean("userLatestNotificationSeenDAO");
		} catch (Exception e) {
			throw new Exception("Could not obtain DAO object.", e);
		}
	}
	
	public static int insertTargettedNotification (Map targetedMessage) throws Exception {
        int result = 1;
        
		if (targettedNotificationDAO == null || userNotificationDAO == null || userLatestNotificationSeenDAO == null) {
			try {
				initTargetMessageDAO();
			} catch (Exception e) {
				throw new Exception("Could not obtain DAO object.", e);
			}
		}
		if (targetedMessage != null) {
            //Map columnNameType = targettedNotificationDAO.getTargetedNotificationColumnNameType();
			//sql = "INSERT INTO ufg_targetted_notifications (message_from, message_subject, role, message, expiry) " +
			 UofGTargettedNotification targettedNotification = new UofGTargettedNotification();
             targettedNotification.setUserId((String)targetedMessage.get("user_id"));
			 targettedNotification.setMessageFrom((String)targetedMessage.get("message_from"));
			 targettedNotification.setMessageSubject((String)targetedMessage.get("message_subject"));
			 targettedNotification.setRole((String)targetedMessage.get("role"));
			 targettedNotification.setMessage((String)targetedMessage.get("message"));
			 targettedNotification.setMessageImportance((String)targetedMessage.get("message_importance"));
		}
        return result;
	}
}