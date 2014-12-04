package ca.uoguelph.ccs.portal.portlets.notification.db;

import java.sql.Types;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.SqlUpdate;

import ca.uoguelph.ccs.portal.portlets.notification.bus.UofGUserNotification;
import ca.uoguelph.ccs.portal.portlets.notification.db.UofGUserNotificationDAO;

/**
 * UofGUserNotificationDAO JDBC implementation using Spring JdbcTemplate.
 * @author rlambert
 */
public class UofGUserNotificationDAOJdbcImpl implements UofGUserNotificationDAO
{
    private JdbcTemplate jdbcTemplate;
    
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }
    
	private final static String identityCall = "SELECT nextval('serial')";//postgres
	private final static String tableName = "ufg_user_notifications";
	
	//private static final String updateMarkDeleteNotifications = 
		//"UPDATE " + tableName + " SET mark_expiry = NOW() WHERE user_id= ? AND message_id=?";

    private static final String updateMarkOpened =
    //"UPDATE " + tableName + " SET mark_opened = NOW() WHERE user_id= ? AND message_id=?" + "::int8";
        "UPDATE " + tableName + " SET mark_opened = NOW() WHERE user_id= ? AND message_id=?";
    
    private static final String updateMarkDeleteNotifications = 
        "UPDATE " + tableName + " SET mark_expiry = NOW() WHERE user_id= ? AND message_id IN ";

    private static final String updateMarkRead = 
        "UPDATE " + tableName + " SET mark_read = NOW() WHERE user_id= ? AND message_id IN ";
    
	private static final String displayUnExpiredNotifications = 
		"SELECT owner, role, user_id, message_id, message_from, message_subject, importance, mark_opened, mark_read, mark_expiry, release " +
		"FROM ufg_user_notifications " +
		"WHERE ( mark_expiry > NOW() OR mark_expiry IS NULL) AND user_id = ?";
	
	// insert only ne messages- filter messages already in user's notifications table
	private static final String selectUserMessagesById = 
		"SELECT message_id FROM ufg_user_notifications WHERE user_id = ?";
	
	private static final String insertPayload = 
		"INSERT INTO ufg_user_notifications " +
		"(owner, role, user_id, message_id, message_from, message_subject, importance, release, mark_expiry) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
	
    private static final String updateReleaseForMessages = 
        "UPDATE ufg_user_notifications SET release = NOW() WHERE message_id IN ";

	public List getUnExpiredNotifications(String userId) {
        //System.out.println("displayUnExpiredNotifications " + "For userId = " + userId + displayUnExpiredNotifications);
		Object params[] = new Object[] {userId};
		int types[] = new int [] {Types.VARCHAR};       
        //rowmapper bug? 
		//return jdbcTemplate.query(displayUnExpiredNotifications, params, types, new UofGUserNotificationRowMapper());
        return jdbcTemplate.queryForList(displayUnExpiredNotifications, params, types);
	}
	public List getMessageIds(String userId)
	{
		Object params[] = new Object[] {userId};
		int types[] = new int [] {Types.VARCHAR};       
		return jdbcTemplate.queryForList(selectUserMessagesById, params, types);
	}
	
	/**
	 * Saves the UofGUserNotification storage to the database.
	 */
	public void insertNotification(UofGUserNotification userNotification)
	{
        Object[] params = new Object[] {
                userNotification.getOwner(),
                userNotification.getRole(),
                userNotification.getUserId(),
                userNotification.getMessageId(),
                userNotification.getMessageFrom(),
                userNotification.getMessageSubject(),
                userNotification.getMessageImportance(),
                (Timestamp)(userNotification.getRelease()),
                (Timestamp)(userNotification.getMarkExpiry())};
        int types[] = {Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.INTEGER, Types.VARCHAR, Types.VARCHAR, 
                Types.VARCHAR, Types.TIMESTAMP, Types.TIMESTAMP};
        //System.out.println("insertPayload:" + insertPayload);
        jdbcTemplate.update(insertPayload, params, types);
	}
	
	//public void updateMarkExpiry(String userId, Integer messageId) {
    public void updateMarkExpiry(String userId, String messageIds) {
        Object params[] = new Object[] {userId};
        //int types[] = new int [] {Types.VARCHAR, Types.INTEGER};
        int types[] = new int [] {Types.VARCHAR};
		//jdbcTemplate.update(updateMarkDeleteNotifications, params, types);
        jdbcTemplate.update(updateMarkDeleteNotifications + "(" + messageIds + ")", params, types);
	}

    public void updateMarkOpened(String userId, String messageId) {
        Object params[] = new Object[] {userId, messageId};
        int types[] = new int [] {Types.VARCHAR, Types.VARCHAR};
        jdbcTemplate.update(updateMarkOpened, params, types);
    }

    public void updateMarkRead(String userId, String messageIds) {
        //System.out.println("updateMark:" + updateMarkRead + "(" + messageIds + ")");
        Object params[] = new Object[] {userId};
        //int types[] = new int [] {Types.VARCHAR, Types.INTEGER};
        int types[] = new int [] {Types.VARCHAR};
        //jdbcTemplate.update(updateMarkDeleteNotifications, params, types);
        jdbcTemplate.update(updateMarkRead + "(" + messageIds + ")", params, types);
    }
    
    public void updateRelease(String messageIds) {
    	//System.out.println("updateUserRelease:" + updateReleaseForMessages + "(" + messageIds + ")");
        jdbcTemplate.update(updateReleaseForMessages + "(" + messageIds + ")");
    }   
}