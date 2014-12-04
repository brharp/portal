package ca.uoguelph.ccs.portal.portlets.notification.db;

import java.sql.Types;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Iterator;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.SqlUpdate;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;

import ca.uoguelph.ccs.portal.portlets.notification.bus.UofGTargettedNotification;
import ca.uoguelph.ccs.portal.portlets.notification.db.UofGTargettedNotificationDAO;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;
import org.springframework.jdbc.support.rowset.SqlRowSet;
/**
 * UofGTargettedNotificationDAO JDBC implementation 
 * @author lalit
 */
public class UofGTargettedNotificationDAOJdbcImpl implements UofGTargettedNotificationDAO
{
	private JdbcTemplate jdbcTemplate;
	
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate){
		this.jdbcTemplate = jdbcTemplate;
	}
	
	private static String identityCall = "SELECT nextval('serial')";//postgres
	private static String query_timestamp = "SELECT NOW() + interval '30 minutes'";//postgres
	
	private static final String displayMessage = 
		"SELECT message FROM ufg_targetted_notifications WHERE message_id=?" + "::int8"; //run index query
	
	private static final String insertNotificationPayload = 
		"INSERT INTO ufg_targetted_notifications (owner, message_subject, message_from, user_id, role, message, importance, release, expiry) " +
		"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"; 
	
	private static final String rowSetQuery = "SELECT message_id from ufg_targetted_notifications LIMIT 1";
	
	//only keep new notifications - filtering by user message ids if user in role
	private static final String selectUserInRoleNewNotifications = 
		"SELECT owner, message_id, message_from, message_subject, importance, role, release, expiry " +
		" FROM ufg_targetted_notifications " +
        " WHERE ( user_id LIKE ? " + " OR " + 
        " role = 'Everyone'" + " OR " + 
        " role  LIKE ?" + " OR " + 
        " owner = ? ) " + " AND " +
        " (expiry > NOW() OR expiry IS NULL ) AND message_id > ? ";
    
    //only keep new notifications - filtering by user message ids if user in role
	private static final String selectUserNewNotifications = 
		"SELECT owner, role, message_id, message_from, message_subject, importance, release, expiry " +
		"FROM ufg_targetted_notifications " +
		"WHERE user_id like ? AND expiry > NOW() AND message_id NOT IN ";
	
    private static final String releaseMessages = 
        "UPDATE ufg_targetted_notifications SET release = NOW() WHERE owner = ? AND message_id IN ";

    
	public List getTargettedNotificationsByRole(String instructor, String course, String organisationalStatus, String userId, String email, Long messageId)
	{
        /*
		Object[] params = new Object[] {role, userId, email, messageId};
		int types[] = new int [] {Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.BIGINT};  
        System.out.println("selectUserInRoleNewNotifications:" + selectUserInRoleNewNotifications + "(" + messageId + ")");
        System.out.println("role: " + role);
        System.out.println("userId:" + userId);
        System.out.println("email: " + email);
		//return jdbcTemplate.query(selectUserInRoleNewNotifications + "(" + messageIds + ")", params, types, new UofGTargettedNotificationRowMapper());
		//return jdbcTemplate.queryForList(selectUserInRoleNewNotifications + "(" + messageIds + ")", params, types);
        */
        instructor = "'%" + instructor + "%'";
        course = "'%" + course + "%'";
        organisationalStatus = "'%" + organisationalStatus + "%'";
        userId = "'%" + email + "%'";

        int msgId = messageId.intValue();
        String sql = "SELECT owner, message_id, message_from, message_subject, importance, role, release, expiry " +
        " FROM ufg_targetted_notifications " +
        " WHERE ( user_id LIKE " + userId  + " OR " +
        " role = 'Everyone'" + " OR " + 
        " role LIKE " + instructor + " OR " +
        " role LIKE " + course + " OR " +
        " role  LIKE " + organisationalStatus + " OR " + 
        " owner = '" +  email + "' ) " +
        " AND (expiry > NOW() OR expiry IS NULL ) AND message_id > " + msgId;
        System.out.println("sql: " + sql);
        return jdbcTemplate.queryForList(sql);
        //return jdbcTemplate.queryForList(selectUserInRoleNewNotifications, params, types);
	}
	
	public List getTargettedNotificationsByUser(String userId, String messageIds)
	{
		userId = "'%" + userId + "%'";
		Object[] params = new Object[] {userId};
		int types[] = new int [] {Types.VARCHAR};  
		//System.out.println("selectUserNewNotifications:" + selectUserNewNotifications + "(" + messageIds + ")");
		return jdbcTemplate.queryForList(selectUserNewNotifications + "(" + messageIds + ")", params, types);
	}
	
    /**
     * Get Targeted message to display
     * @param String messageId message id to be displayed
     * @return String message text
     */
	public String getTargettedMessage(Integer messageId)
	{
		Object[] params = new Object[] {messageId};
		int types[] = new int [] {Types.INTEGER};       
		return (String)jdbcTemplate.queryForObject(displayMessage, params, types, String.class);
	}
	
	/**
	 * Saves the UofGTargettedNotification storage to the database.
	 */
	public void insertTargettedNotification(UofGTargettedNotification targettedNotification)
	{
        String importance = null;
        Timestamp expiry = null;
        
        if (null == targettedNotification.getMessageImportance()) {
        	importance = "Normal";
        }

        if (null == targettedNotification.getExpiry()) {
            Timestamp ts = new Timestamp(System.currentTimeMillis());
            Calendar cal = GregorianCalendar.getInstance();
            cal.setTimeInMillis(ts.getTime());
            cal.add(Calendar.MONTH, 4);
            ts.setTime(cal.getTimeInMillis());
            expiry = ts; 
            System.out.println(ts); 
        }

		Object[] params = new Object[] {
                targettedNotification.getOwner(),
				targettedNotification.getMessageSubject(),
				targettedNotification.getMessageFrom(),
				targettedNotification.getUserId(),
				targettedNotification.getRole(), 
				targettedNotification.getMessage(),
				//targettedNotification.getMessageImportance(),
                importance,
                targettedNotification.getRelease(),
                expiry };
				//(Timestamp)(jdbcTemplate.queryForObject(query_timestamp, Timestamp.class))};
		
		int types[] = {Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, 
                Types.VARCHAR, Types.TIMESTAMP, Types.TIMESTAMP};
		jdbcTemplate.update(insertNotificationPayload, params, types);
	}
	public Map getTargetedNotificationColumnNameType(String rowSetQuery) {
		SqlRowSet rowSet = jdbcTemplate.queryForRowSet(rowSetQuery);
		SqlRowSetMetaData metaData = rowSet.getMetaData();
		String[] columnNames = metaData.getColumnNames();
		int columnCount =  columnNames.length;
		Map columnNameType = new HashMap();
		for (int i=0; i<columnCount; i++) {
			columnNameType.put(columnNames[i], (String)metaData.getColumnTypeName(i)); 
		}   
		//Iterator it = columnNameType.entrySet().iterator();
		//while (it.hasNext()) {
			//Map.Entry pairs = (Map.Entry)it.next();
			//System.out.println(pairs.getKey() + "nametype = " + pairs.getValue());
		//}
		
		return columnNameType;
	}
    public void updateRelease(String owner, String messageIds, Timestamp expiry) {
        Object params[] = new Object[] {owner};
        //int types[] = new int [] {Types.VARCHAR, Types.INTEGER};
        int types[] = new int [] {Types.VARCHAR};
        //jdbcTemplate.update(updateMarkDeleteNotifications, params, types);
        System.out.println("updateRelease: " + releaseMessages);
        jdbcTemplate.update(releaseMessages + "(" + messageIds + ")", params, types);

    }   
}