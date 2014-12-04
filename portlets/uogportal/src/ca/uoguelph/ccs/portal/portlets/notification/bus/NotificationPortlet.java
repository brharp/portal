package ca.uoguelph.ccs.portal.portlets.notification.bus;

import java.util.*;
import java.util.List;
import java.io.IOException;

import java.sql.ResultSet;
import java.sql.Timestamp;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.PortletRequest;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.PortletSecurityException;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.UnavailableException;

import javax.portlet.WindowState;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.BeansException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.web.portlet.FrameworkPortlet;

import ca.uoguelph.ccs.portal.portlets.notification.db.UofGTargettedNotificationDAO;
import ca.uoguelph.ccs.portal.portlets.notification.db.UofGUserNotificationDAO;
import ca.uoguelph.ccs.portal.portlets.notification.db.UofGUserLatestNotificationSeenDAO;
import ca.uoguelph.ccs.portal.portlets.notification.bus.UofGTargettedNotification;

import ca.uoguelph.ccs.portal.portlets.notification.bus.NotificationPortletHelper;

/*
 * 
 * @author lalit
 *
 * Targeted messaging portlet:
 * Mule runs inside Tomcat container as separate web application
 */
public class NotificationPortlet extends FrameworkPortlet
{
    private static final Log logger = LogFactory.getLog(NotificationPortlet.class);
	
    private static UofGTargettedNotificationDAO targettedNotificationDAO = NotificationPortletHelper.targettedNotificationDAO;
    private static UofGUserNotificationDAO userNotificationDAO = NotificationPortletHelper.userNotificationDAO;
    private static UofGUserLatestNotificationSeenDAO userLatestNotificationSeenDAO = NotificationPortletHelper.userLatestNotificationSeenDAO;
	
	
    protected void initFrameworkPortlet()
        throws PortletException,
               BeansException	
    {	
        logger.debug("Initialize DAO");
        if (targettedNotificationDAO == null || userNotificationDAO == null || userLatestNotificationSeenDAO == null) {
            try {
                NotificationPortletHelper.initTargetMessageDAO();
                targettedNotificationDAO = NotificationPortletHelper.targettedNotificationDAO;
                userNotificationDAO = NotificationPortletHelper.userNotificationDAO;
                userLatestNotificationSeenDAO = NotificationPortletHelper.userLatestNotificationSeenDAO;
            } catch (Exception e) {
                throw new PortletException("Could not obtain DAO object-", e);
            }
        }
    }

    public String getContextConfigLocation() {
        return "WEB-INF/context/notification-data.xml";
    }

    public void doActionService(ActionRequest actionRequest, ActionResponse actionResponse)
        throws UnavailableException, PortletSecurityException, PortletException, IOException
    {
        PortletSession psession = actionRequest.getPortletSession();
        String mail = (String) psession.getAttribute("mail"); //owner in TM
        String owner = mail;
        String userId = (String) psession.getAttribute("userId");
        //System.out.println("owner: " + owner);
        //System.out.println("user: " + userId);
		
        //front controller
        String command = actionRequest.getParameter("COMMAND");
        String window = actionRequest.getParameter("WINDOW");
        
        //System.out.println("!!!!!windowAct!!!!!:" + window);
        //System.out.println("!!!!!commandAct!!!!!:" + command);
        if (command != null) {
            actionResponse.setRenderParameter("COMMAND", command);
        }
        if (window != null) {
            actionResponse.setRenderParameter("WINDOW", window);
        }
        //TODO: alert box
        if ("Purge Deleted".equals(command)) {
            String[] delete_msg_ids = (String[]) actionRequest.getParameterValues("delete_msg_id");
            if (delete_msg_ids != null) {
                deleteMessagesForUser(userId, getMessageIds(delete_msg_ids));
            }
        }
        else if ("Mark Read".equals(command))
            {
                String[] mark_msg_ids = (String[]) actionRequest.getParameterValues("mark_msg_id");

                if (mark_msg_ids != null) {
                    //System.out.println("!!!!!mark_msg_ids!!!!!:" + mark_msg_ids[0]);
                    markReadForUser(userId, getMessageIds(mark_msg_ids));
                }
            }	
        else if ("DISPLAY".equals(command))
            {
                //mark/update opened
                String display_msg_id = (String) actionRequest.getParameter("DISPLAY_MSG_ID");
                int msg_id = Integer.parseInt(display_msg_id);
                actionResponse.setRenderParameter("DISPLAY_MSG_ID", display_msg_id);
            }
        if ("Release".equals(command)) {
            String[] release_msg_ids = (String[]) actionRequest.getParameterValues("release_msg_id");
            Timestamp markExpiry = setMarkExpiry();
            if (release_msg_ids != null) {
                releaseMessagesForOwner(owner, getMessageIds(release_msg_ids), markExpiry);
                releaseMessagesForUser(getMessageIds(release_msg_ids));
            }
        }
        actionResponse.setPortletMode(PortletMode.VIEW);
    }
	
    /**
     * @deprecated
     */
    private void deleteMessageForUser(String userId, Integer messageId) {
        //userNotificationDAO.updateMarkExpiry(userId, messageId);
    }
	
    /**
     * updates release to current time
     * @param owner email ddress
     * @param messageIds for messages to be released
     */
    private static void releaseMessagesForOwner(String owner, String messageIds, Timestamp expiry) {
        targettedNotificationDAO.updateRelease (owner, messageIds, expiry);
    }

    private static void releaseMessagesForUser(String messageIds) {
        userNotificationDAO.updateRelease (messageIds);
    }
    
    //TODO:message is marked expiry and   
    //deleted only after 7 days of expiry
    private static void deleteMessagesForUser(String userId, String messageIds) {
        userNotificationDAO.updateMarkExpiry(userId, messageIds);
    }
	
    private static void markReadForUser(String userId, String messageIds) {
        userNotificationDAO.updateMarkRead(userId, messageIds);
    }
	
    private static String getMessageIds(String[] checkedMessageIds) {
        StringBuffer sb = new StringBuffer();
        int size = checkedMessageIds.length;
		
        for (int i=0; i<size; i++) {
            if (i==size-1) {
                sb.append(checkedMessageIds[i]);
            } else {
                sb.append(checkedMessageIds[i]).append(",");
            }
        }
        String message_ids = sb.toString();
        return message_ids;
    }
	
    protected void doRenderService(RenderRequest request, RenderResponse response)
        throws PortletException, IOException
    {
        response.setContentType("text/html");
		
        Map userInfo = (Map) request.getAttribute(PortletRequest.USER_INFO);
        String username = (String) userInfo.get("username");
        String organizationalStatus = (String)userInfo.get("userRole");
        String mail = (String)userInfo.get("mail");
        String uogCourses = (String)userInfo.get("uogcourses");
        //String uogCourses = "CIS";
        String uogInstructor = (String)userInfo.get("uoginstructor");
        
        String owner = mail;
		
        String userId = username;
        //System.out.println("userId:" + userId);
        //System.out.println("organizationalStatus: " + organizationalStatus);
        //System.out.println("mail:" + mail);
        //System.out.println("owner:" + owner);
        //System.out.println("uogCourses: " + uogCourses);
        //System.out.println("uogInstructor: " + uogInstructor);
        
        //if (null == userId) {
        //userId = "ljairath";
        //}
        
        //if (null == mail) {
        //mail = "ljairath@uoguelph.ca";    
        //}
        //if (null == organizationalStatus) {
        //organizationalStatus = "Employee:Staff";
        //}
        
        owner = mail;
		
        PortletContext portletContext = getPortletContext();
        PortletSession psession = request.getPortletSession();
        psession.setAttribute("mail", owner); //email of owner
        psession.setAttribute("userId", userId);
        String paramExpiry = "1";
        response.setProperty(RenderResponse.EXPIRATION_CACHE, paramExpiry) ;
        String latestMessageId = null;
        
        String command = request.getParameter("COMMAND");
        String window = request.getParameter("WINDOW");
        String more = request.getParameter("MORE");
        String subject = request.getParameter("SUBJECT");
        
        psession.setAttribute("SUBJECT", subject);
        
        //System.out.println("***more Rend: " + more);
        psession.setAttribute("MORE", more);

        //command = "syndicate";
        //System.out.println("!!!!!windowView!!!!!:" + window);
        //System.out.println("!!!!!commandView!!!!!:" + command);
        
        
        if ("syndicate".equalsIgnoreCase(command)) {
            String FEED_TYPE =  "type";
            String type = request.getParameter(FEED_TYPE);
            psession.setAttribute(FEED_TYPE, type);
            //prd = portletContext.getRequestDispatcher(request.getContextPath() + "/FeedServlet");

            String messageIds =  getMessageIds(psession);
            List messageList = targettedNotificationDAO.getTargettedMessages(messageIds);
            if (messageList != null) {
                printList1Map(messageList, "Syndicate");
                //userNotificationDAO.updateMarkOpened(userId, display_msg_id);
                psession.setAttribute("messageList", messageList);
            }

            PortletRequestDispatcher prd = portletContext.getRequestDispatcher("/WEB-INF/jsp/rss.jsp");
            prd.include(request, response);
        } else if ("DISPLAY".equals(command) || "syndicate".equalsIgnoreCase(command)) {
            //mark update opened 
            String display_msg_id = (String) request.getParameter("DISPLAY_MSG_ID");
            String message_text = targettedNotificationDAO.getTargettedMessage(Integer.valueOf(display_msg_id));
            if (message_text.startsWith("text/plain")) {
            	message_text = T2HTML.convertText2HTML(message_text);
                System.out.println("message_text:" + message_text);
            }
            if (message_text != null) {
                userNotificationDAO.updateMarkOpened(userId, display_msg_id);
                psession.setAttribute("message_text", message_text);
            }
                psession.setAttribute("DISPLAY_MSG_ID", display_msg_id);
            PortletRequestDispatcher prd = portletContext.getRequestDispatcher("/WEB-INF/jsp/displayMessage.jsp");
            prd.include(request, response);
        } else if ("Mark Read".equals(command) || "Purge Deleted".equals(command)) { 
            List[] userOwnerNotificationsMap = new List[] {new ArrayList(), new ArrayList()};         
            //returns list of maps - no fresh look up
            List userNotificationsMap = userNotificationDAO.getUnExpiredNotifications(userId);
            List releasedUserNotificationsList = new ArrayList();
            if (userNotificationsMap != null && userNotificationsMap.size() != 0 ) {
                //Collections.sort(userNotificationsMap, new ColumnSorter());                
                int sizeList = userNotificationsMap.size();
                for (int i=0; i<sizeList; i++) {
                    Map listMap = (Map) userNotificationsMap.get(i);
                    Timestamp releaseUser = (Timestamp)listMap.get("release");
                    if (releaseUser != null) {
                    	releasedUserNotificationsList.add(listMap);
                    }
                }
                userOwnerNotificationsMap[0] = releasedUserNotificationsList;
                psession.setAttribute("user_notificationsMap", userOwnerNotificationsMap[0]);
            }
            List releaseNotificationsMap = (List) psession.getAttribute("release_notificationsMap");
            if (releaseNotificationsMap != null) {
                userOwnerNotificationsMap[1] = releaseNotificationsMap;   
            }
            PortletRequestDispatcher prd = null;
            if ("full".equalsIgnoreCase(window)) {
                prd = portletContext.getRequestDispatcher("/WEB-INF/jsp/notifications_maxWindowState.jsp");
            } else {
                prd = portletContext.getRequestDispatcher("/WEB-INF/jsp/notifications_normalWindowState.jsp");
            }
            prd.include(request, response);
        } else if ("Release".equals(command)) {
            List unexpiredUserOwnerNotifications =  userNotificationDAO.getUnExpiredNotifications(userId);
            //separate user owner notifications display 
            if (unexpiredUserOwnerNotifications != null && unexpiredUserOwnerNotifications.size() != 0 ) {
                //Collections.sort(unexpiredUserOwnerNotifications, new ColumnSorter());   
            }
            List[] userOwnerNotifications = setUserOwnerNotifications(unexpiredUserOwnerNotifications, owner);
            printListMap(userOwnerNotifications, "userOwnerNotifications");
			
            psession.setAttribute("user_notificationsMap", userOwnerNotifications[0]);
            psession.setAttribute("release_notificationsMap", userOwnerNotifications[1]);
            PortletRequestDispatcher prd = portletContext.getRequestDispatcher("/WEB-INF/jsp/notifications_maxWindowState.jsp");
            prd.include(request, response);
        } else { //first access/REFRESH
            latestMessageId = (String)psession.getAttribute("latestMessageId");
            //System.out.println("latestMessageId Session: " + latestMessageId);
            //if (debug) {
            int latestMessageSeen = 0; 
            //session message id is null
            if (latestMessageId == null) {
                //check database 
                latestMessageSeen = userLatestNotificationSeenDAO.getLatestNotificationSeenByUser(userId);
                latestMessageId = String.valueOf(latestMessageSeen);
                if (latestMessageSeen != 0) {
                    psession.setAttribute("latestMessageId", latestMessageId);
                }
            } else {
                latestMessageSeen = Integer.parseInt(latestMessageId);            
            }
			
            long time = -1L;
            boolean debug = logger.isDebugEnabled();
            //if (debug) {
            //logger.debug("Get new notifications");
            //System.out.println("Get new notifications");
            time = System.currentTimeMillis();
			
            List newNotification = targettedNotificationDAO.getTargettedNotificationsByRole(
                                                                                            uogInstructor, uogCourses, organizationalStatus, userId, owner, Long.valueOf(latestMessageId));
            //getUserNotifications(organizationalStatus, userId, owner, latestMessageId);
            time = System.currentTimeMillis() - time;
            //logger.debug("Retrieving new notifications for user " + userId + " took " + time + " ms.");
            //System.out.println("Retrieving new notifications for user " + userId + " took " + time + " ms.");
            if (newNotification != null) {
                //System.out.println("size of newNotification: " + newNotification.size());
            }
            List[] newNotificationList = new List[] {newNotification};
            printListMap(newNotificationList, "newNotification");
			
            logger.debug("Processing New Notifications:" + newNotification);
			
            boolean isNewNotificationInserted = false;
            int latestMessage = 0;
            if (newNotification != null) {
                int size = newNotification.size();
                for (int i=0; i<size; i++) {
                    Map mapNewNotification = (Map)newNotification.get(i);
                    //insert (release to this user) new notification
                    Timestamp expiry = (Timestamp)mapNewNotification.get("expiry");
                    //set expiry
                    if (null == expiry) {
                        mapNewNotification.put("expiry", setMarkExpiry());
                    }
                    //System.out.println(" New Noti. release: ");
                    UofGUserNotification insertUserNotification = createUofGUserNotification(mapNewNotification, userId);
                    //bulk insert?
                    userNotificationDAO.insertNotification(insertUserNotification);
                    //record latest message id
                    isNewNotificationInserted = true;
                    int messageId = ((Integer)mapNewNotification.get("message_id")).intValue();
                    latestMessage = Math.max(latestMessage, messageId);
                    //System.out.println("Latest message inserted in the user table:" + latestMessage);
                }
                if (isNewNotificationInserted) {
                    if (latestMessageSeen == 0) { //initialize the user/messageseen table
                        //first time- create the row for the user
                        userLatestNotificationSeenDAO.insertUser(userId, new Long(latestMessageSeen));
                    }
                    userLatestNotificationSeenDAO.updateLatestNotificationSeenByUser(new Long(latestMessage), userId);
                    psession.setAttribute("latestMessageId", String.valueOf(latestMessage));
                }
            }
            List unexpiredUserOwnerNotifications =  userNotificationDAO.getUnExpiredNotifications(userId);
            printList1Map(unexpiredUserOwnerNotifications, "unexpiredUserOwnerNotifications");
            if (unexpiredUserOwnerNotifications != null && unexpiredUserOwnerNotifications.size() != 0 ) {
                //Collections.sort(unexpiredUserOwnerNotifications, new ColumnSorter());
                //System.out.println("Sorted unexpiredUserOwnerNotifications----\n");
                printList1Map(unexpiredUserOwnerNotifications, "unexpiredUserOwnerNotifications");
            }
            //separate user owner notifications display 
            List[] userOwnerNotifications = setUserOwnerNotifications(unexpiredUserOwnerNotifications, owner);
            printListMap(userOwnerNotifications, "userOwnerNotifications");

            psession.setAttribute("user_notificationsMap", userOwnerNotifications[0]);
            psession.setAttribute("release_notificationsMap", userOwnerNotifications[1]);
            PortletRequestDispatcher prd = null;
            if ( "All".equalsIgnoreCase(command) || 
                 ("full".equalsIgnoreCase(window) && "Back".equalsIgnoreCase(command)) ||
                 "Refresh".equalsIgnoreCase(command) ) {
                prd = portletContext.getRequestDispatcher("/WEB-INF/jsp/notifications_maxWindowState.jsp");
            } else { 
                prd = portletContext.getRequestDispatcher("/WEB-INF/jsp/notifications_normalWindowState.jsp");
            }
            prd.include(request, response);
        }//end refresh
    }

    private static List[] setUserOwnerNotifications(List unexpiredUserOwnerNotifications, String owner) {
        List displayUnexpiredUserNotifications;
        int size = unexpiredUserOwnerNotifications.size();
        if (unexpiredUserOwnerNotifications == null || size == 0) {
            //displayUnexpiredUserNotifications = new ArrayList();
            return new List[] {new ArrayList(), new ArrayList()};
        }
        //System.out.println("displayUnexpiredUserNotifications size :" + unexpiredUserOwnerNotifications.size());
		
        List unreleasedMessages = new ArrayList();
        List releasedMessages = new ArrayList();
		
        for (int i=0; i<size; i++) {
            Map mapNewNotification = (Map)unexpiredUserOwnerNotifications.get(i);
            //insert (release to this user) new notification only if released
            Timestamp release = (Timestamp)mapNewNotification.get("release");
            String releaseOwner = (String)mapNewNotification.get("owner");
            if (owner.equalsIgnoreCase(releaseOwner) && release == null){
                //unreleased owner messages
                unreleasedMessages.add(mapNewNotification);
            } else if (release != null)  {
                releasedMessages.add(mapNewNotification);   
            }
        }

        List[] userOwnerNotifications = new List[] {releasedMessages, unreleasedMessages};
        return userOwnerNotifications;
    }

    private static int getLatestMessageId (List newNotification, int latestMessageSeen) {
        //get the latest message id
        int size = newNotification.size();
        int latestNewMessage = 0;
        for (int i=0; i<size; i++) {
            Map newNotificationMap = (Map)newNotification.get(i);
            latestNewMessage = ((Integer)newNotificationMap.get("message_id")).intValue();
            latestNewMessage = Math.max(latestNewMessage, latestMessageSeen);
            //System.out.println("latestNewMessage: " + latestNewMessage);
        }
        return latestNewMessage;
    }

    private static String getMessageIds(PortletSession psession) {
        List messageIds = new ArrayList();
        List notificationsMap = (List) psession.getAttribute("user_notificationsMap");
        messageIds = getMessageIds(notificationsMap, messageIds); 
        notificationsMap = (List) psession.getAttribute("release_notificationsMap");
        messageIds = getMessageIds(notificationsMap, messageIds);
        
        StringBuffer sb = new StringBuffer();
        if (messageIds != null && messageIds.size() != 0) {
            for (int i=0; i<messageIds.size(); i++) {
                sb.append((String)messageIds.get(i));
                if (i != (messageIds.size()-1)) {
                    sb.append(",");
                }
            }
        }
        //System.out.println("*******sb******\n" + sb);
        return sb.toString();
    }

    private static List getMessageIds(List notificationsMap, List messageIds) {
        if (notificationsMap != null && notificationsMap.size() != 0) {
            for (int i=0; i<notificationsMap.size(); i++) {
                Map map = (Map)notificationsMap.get(i);
                messageIds.add( (String.valueOf(map.get("message_id"))) + "::int8");
            }
        }
        return messageIds;
    }
    /*
     * 
     * @param owner
     * @param userNotificationsMap
     * @return
     */
    private static Timestamp setMarkExpiry() {
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.add(Calendar.MONTH, 4);
        ts.setTime(cal.getTimeInMillis());
        //System.out.println("mark expiry: " + ts); 
        return ts;
    }
    private static UofGUserNotification createUofGUserNotification(Map newNotification, String userId) {
        //message must have been released to get inserted in user tables
        UofGUserNotification insertUserNotification = new UofGUserNotification();
        insertUserNotification.setUserId(userId);
        insertUserNotification.setRole((String)newNotification.get("role"));
        insertUserNotification.setOwner((String)newNotification.get("owner"));
        insertUserNotification.setMessageId((Integer)newNotification.get("message_id"));
        insertUserNotification.setMessageSubject((String)newNotification.get("message_subject"));
        insertUserNotification.setMessageFrom((String)newNotification.get("message_from"));
        Timestamp release = (Timestamp)newNotification.get("release");
        insertUserNotification.setRelease(release);
        insertUserNotification.setMarkExpiry((Timestamp)newNotification.get("expiry"));
        insertUserNotification.setMessageImportance((String)newNotification.get("importance"));
        //System.out.println("release timestamp: " + (Timestamp)insertUserNotification.getRelease());
        return insertUserNotification;
    }
	
    class ColumnSorter implements Comparator {
        public int compare(Object a, Object b) {
            Map rowMap1 = (Map)a;
            Map rowMap2 = (Map)b;
            Object o1 = rowMap1.get("mark_expiry");
            Object o2 = rowMap2.get("mark_expiry");
            if (o1 == null || o2 == null) {
            	return 0;
            }
            return ((Comparable)o1).compareTo(o2);
        }
    }

    private static void printListMap(List[] userOwnerNotificationsMap, String mapName) {
        //System.out.println(mapName + ": " + userOwnerNotificationsMap.length);
        for (int i=0; i<userOwnerNotificationsMap.length; i++) {
            if (userOwnerNotificationsMap[i] != null) {
                //System.out.println(mapName + ": " + i + "size = " + userOwnerNotificationsMap[i].size());
                int size = userOwnerNotificationsMap[i].size();
                for (int j=0; j<size; j++) {
                    Map messageMap = (Map)userOwnerNotificationsMap[i].get(j);
                    Iterator it = messageMap.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry pairs = (Map.Entry)it.next();
                        //System.out.println(pairs.getKey() + " = " + pairs.getValue());
                    }
                }
            }
        }
    }
    
    private static void printList1Map(List userOwnerNotificationsMap, String mapName) {
        if (userOwnerNotificationsMap!= null && userOwnerNotificationsMap.size() != 0) {
            //System.out.println(mapName + ": " + "size = " + userOwnerNotificationsMap.size());
            int size = userOwnerNotificationsMap.size();
            for (int j=0; j<size; j++) {
                Map messageMap = (Map)userOwnerNotificationsMap.get(j);
                Iterator it = messageMap.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pairs = (Map.Entry)it.next();
                    //System.out.println(pairs.getKey() + " = " + pairs.getValue());
                }
            }
        }
    }

    private static void printMap(Map messageMap, String mapName) {
        Iterator it = messageMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            //System.out.println(pairs.getKey() + " = " + pairs.getValue());
        }
    }
	
    private static void printList(List messageList, String listName) {
        //System.out.println("Message List name: " + listName);
        int size = messageList.size();
        for (int i=0; i<size; i++) {
            //System.out.println("Message List value: " + messageList.get(i));
        }
    }
}
