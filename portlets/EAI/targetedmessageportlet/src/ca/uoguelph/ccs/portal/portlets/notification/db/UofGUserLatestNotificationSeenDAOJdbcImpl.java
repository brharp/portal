package ca.uoguelph.ccs.portal.portlets.notification.db;

import java.sql.Types;
import java.sql.Timestamp;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.SqlUpdate;

import ca.uoguelph.ccs.portal.portlets.notification.db.UofGUserLatestNotificationSeenDAO;

/**
 * UofGTargettedNotificationDAO JDBC implementation 
 * @author lalit
 */
public class UofGUserLatestNotificationSeenDAOJdbcImpl implements UofGUserLatestNotificationSeenDAO
{
	private JdbcTemplate jdbcTemplate;
	
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate){
		this.jdbcTemplate = jdbcTemplate;
	}
	
	
	private static final String selectLatestMessageIdSeen = 
		"SELECT max(message_id) FROM ufg_user_latest_message WHERE user_id=?";

    private static final String updateLatestMessageIdSeen = 
        "UPDATE ufg_user_latest_message SET message_id = ? WHERE user_id= ?"; 

    private static final String inserUser = 
        "INSERT INTO ufg_user_latest_message (user_id, message_id) VALUES (?, ?)"; 

    //
    public void insertUser(String userId, Long messageId) {
        Object[] params = new Object[] {userId, messageId};
        int types[] = new int [] {Types.VARCHAR, Types.BIGINT};       
        jdbcTemplate.update(inserUser, params, types);
    }
    public int getLatestNotificationSeenByUser(String userId) {
        Object[] params = new Object[] {userId};
        int types[] = new int [] {Types.VARCHAR};       
        return jdbcTemplate.queryForInt(selectLatestMessageIdSeen, params, types);
    }
    
    public void updateLatestNotificationSeenByUser(Long messageId, String userId) {
        Object params[] = new Object[] {messageId, userId};
        int types[] = new int [] {Types.BIGINT, Types.VARCHAR};
        System.out.println(updateLatestMessageIdSeen + ": " + messageId + " " + userId);
        //jdbcTemplate.update(updateMarkDeleteNotifications, params, types);
        jdbcTemplate.update(updateLatestMessageIdSeen, params, types);
    }   

}