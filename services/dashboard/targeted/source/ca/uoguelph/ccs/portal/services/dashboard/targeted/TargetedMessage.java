package ca.uoguelph.ccs.portal.services.dashboard.targeted;

import java.sql.Timestamp;
import javax.servlet.http.*;
import javax.servlet.*;
import java.io.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.jasig.portal.security.IPerson;
import org.jasig.portal.security.PersonManagerFactory;

import org.jasig.portal.security.ISecurityContext;
import org.jasig.portal.security.provider.NotSoOpaqueCredentials;
import org.jasig.portal.security.IOpaqueCredentials;
import org.jasig.portal.security.IPrincipal;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

import org.jasig.portal.RDBMServices;

/*
 * 
 * @author lalit
 */
public class TargetedMessage extends HttpServlet {
	
    private static final Log LOG = LogFactory.getLog(TargetedMessage.class);
	
    public  void doGet(HttpServletRequest request, HttpServletResponse  response) 
	throws IOException, ServletException {
        doProcess(request, response);
    }
	
    public  void doPost(HttpServletRequest request, HttpServletResponse response)
	throws IOException, ServletException {
        doProcess(request, response);
    }
	
    private void doProcess(HttpServletRequest request, HttpServletResponse response)
	throws IOException, ServletException {
		
        String username = null;
        String organizationalStatus = null;
        String email = null;
        String uogCourses = null;
        //String uogCourses = "CIS";
        String uogInstructor = null;
        String owner = null;
		
        PrintWriter out = null;
        try {
            IPerson person = null;
            try {
                // Get person details
                person = PersonManagerFactory.getPersonManagerInstance().getPerson(request);
            } catch (Exception e) {
                if (LOG.isErrorEnabled()) {
                    LOG.error("UserCredential Person" + e.getMessage());
                }
            }        
            username = (String)person.getAttribute("username");
            if (username == null) {
                username = "guest";
            }
			
            if (!"guest".equalsIgnoreCase(username)) {
                organizationalStatus = (String)person.getAttribute("userRole");
                if (organizationalStatus == null) {
                    organizationalStatus = "other";
                }
                email = (String)person.getAttribute("mail");
                //if (email == null) {
                //	email="ljairath@uoguelph.ca";
                //}
                owner = email;
                uogCourses = (String)person.getAttribute("uogcourses");
                uogInstructor = (String)person.getAttribute("uogInstructor");
				
                if (LOG.isDebugEnabled()) {
                    LOG.debug("username: " + username);
                }
                String userId = username;
                //userId="ljairath";
				
                if (LOG.isDebugEnabled()) {
                    LOG.debug("userId:" + userId);
                    LOG.debug("organizationalStatus: " + organizationalStatus);
                    LOG.debug("mail:" + email);
                    LOG.debug("owner:" + owner);
                    LOG.debug("uogCourses: " + uogCourses);
                    LOG.debug("uogInstructor: " + uogInstructor);
                }
				
                Connection con = null;
                ResultSet rs = null;
                Statement stmt = null;
				
                long msgIdSeen = 0;
                long msgIdNew = 0;
                int newMessage = 0;
                long unread_messages = 0;
				
                Map newMessageIds = new HashMap();
                Map unreadMessageIds = new HashMap();       
				
                try {
                    con = RDBMServices.getConnection();
                    stmt = con.createStatement();
                    String msgIdSQL = "SELECT max(message_id) AS message_id FROM ufg_user_latest_message WHERE user_id=" +
                        "'" + userId + "'";
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(msgIdSQL);
                    }
					
                    rs = stmt.executeQuery(msgIdSQL);
					
                    //get the latest messageid seen by this userid
                    while(rs.next(  )) {
                        msgIdSeen = rs.getLong("message_id");
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Max message id= " + rs.getLong("message_id"));
                        }
                    }
					
                    uogInstructor = "'%" + uogInstructor + "%'";
                    String course = uogCourses;
                    uogCourses = "'%" + uogCourses + "%'";
                    organizationalStatus = "'%" + organizationalStatus + "%'";
                    owner = email;
                    email = "'%" + email + "%'";
                    userId = "'%" + userId + "%'";
                    //int msgId = messageId.intValue();
					
                    String courseSql = "";
                    if (course != null && course.length()>0) {
                        String[] parseCourse = course.split(",");
                        int lenCourse = parseCourse.length;
                        for (int i=0; i< lenCourse; i++) {
                            courseSql += " role LIKE '%" + parseCourse[i] + "%' OR ";
                        }
                    } 
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("courseSql: " + courseSql);
                    }
					
                    //get all new messages for the following profile
                    //that are latest than seen last time and store in a map
                    String sql = "SELECT owner, message_id, release"  +
                        " FROM ufg_targetted_notifications " +
                        " WHERE ( user_id LIKE " + userId  + " OR " +
                        " user_id LIKE " + email + " OR " +
                        " role LIKE " + uogInstructor + " OR " +
                        courseSql +
                        " role  LIKE " + organizationalStatus + " OR " + 
                        " owner = '" +  owner + "' ) " +
                        " AND (expiry > NOW() OR expiry IS NULL ) AND message_id > " + msgIdSeen;
			
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("sql: " + sql);
                    }
                    rs = stmt.executeQuery(sql);
					
                    while(rs.next(  )) {
                        Timestamp release = (Timestamp)rs.getTimestamp("release");
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("release = " + release);
                        }
                        String ownerTm = rs.getString("owner");
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("ownerTm = " + ownerTm);
                        }
                        if (owner.equalsIgnoreCase(ownerTm) || null != release) {
                            String newId = String.valueOf(rs.getLong("message_id"));
                            newMessageIds.put(newId, newId);
                            newMessage++;
                        }
                    }
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("newMessage = " + newMessage);
                    }
					
                    sql = "SELECT message_id from ufg_user_notifications " +
                        " WHERE ((user_id ='" + username + "' AND release IS NOT NULL) OR " +
                        " (user_id ='" + username + "' AND owner ='" + owner + "')) AND " +
                        " mark_read IS NULL AND mark_expiry > NOW()";
					
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("sql: " + sql);
                    }
                    rs = stmt.executeQuery(sql);
                    //get unread messages for the user and store in a map
                    while(rs.next(  )) {
                        String unread_messageId = String.valueOf(rs.getLong("message_id"));
                        unreadMessageIds.put(unread_messageId, unread_messageId);
                    }
                    unread_messages = unreadMessageIds.size();
                    if (LOG.isDebugEnabled()) {
                        LOG.debug( "unread_messages: " + unread_messages);
                    }
                } catch (Exception e) {
                    if (LOG.isErrorEnabled()) {
                        LOG.error("ERROR: Sql Exception");
                    }
                    e.printStackTrace();
                } finally {
                    if (con != null) {
                        RDBMServices.releaseConnection(con);
                    }
                    if (rs!= null) {
                        rs.close(  );
                    }
                    if (stmt != null) {
                        stmt.close(  );
                    }
                }
                //set headers and buffer size before accessing the Writer
                response.setContentType("text/html");
                response.setBufferSize(8192);
                out = response.getWriter();
				
				
                //write the response
                //Random r = new Random();
                //int randInt = r.nextInt(10);
                //if (msgIdNew < msgIdSeen) {
                //	msgIdNew = msgIdSeen;   
                //}
                //out.println(msgIdNew-msgIdSeen);
				
				
                int commonIds = 0;
                for (Iterator iterator = newMessageIds.entrySet().iterator(); iterator.hasNext();) {
                    Map.Entry entry = (Map.Entry) iterator.next();
                    String keyId = (String)entry.getKey();
                    if (LOG.isDebugEnabled()) {
                        LOG.debug( "keyId: " + keyId);
                        if (unreadMessageIds.get(keyId) != null) {
                            commonIds++;
                            LOG.debug( "commonIds: " + commonIds);
                        }
                    }
                }
				
                out.println(newMessage + unread_messages - commonIds);
                if (LOG.isDebugEnabled()) {
                    LOG.debug(String.valueOf(newMessage + unread_messages - commonIds));
                }
            } else {
                out.println(0);
            }
            //LOG.debug("New Target Message:" + (msgIdNew-msgIdSeen));
        } catch (Exception e) {
            if (LOG.isErrorEnabled()) {
                LOG.error("TM Dashboard" + e.getMessage());
            }
            response.resetBuffer();
            if (out != null) { out.close(); }
            throw new ServletException(e);
        }
        out.close();
    }
}
