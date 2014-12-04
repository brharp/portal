/*
 * Created on Apr 25, 2005
 */
package ca.uoguelph.ccs.portal.portlets.notification.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.RowMapper;

import ca.uoguelph.ccs.portal.portlets.notification.bus.UofGUserNotification;

/**
 * RowMapper for a UofGUserNotification Record
 *
 * @author rlambert
 */
public class UofGUserNotificationRowMapper implements RowMapper
{
  private static final Log log = LogFactory.getLog(UofGUserNotificationRowMapper.class);

  /**
   * Creates a UofGUserNotification from a ResultSet
   */
  public Object mapRow(ResultSet rs, int rowNum) throws SQLException
  {
    UofGUserNotification userNotification = new UofGUserNotification();
    try
    {
        userNotification.setMessageId(new Integer(rs.getInt("message_id")));
        userNotification.setUserId("ljairath");
        userNotification.setMessageFrom((String)rs.getString("message_from"));
        userNotification.setMessageSubject((String)rs.getString("message_subject"));
        userNotification.setMarkExpiry((Timestamp)rs.getTimestamp("mark_expiry"));
        System.out.println("userNotification Rowmapper:" + (Timestamp)rs.getTimestamp("mark_expiry"));
    }
    catch (SQLException e)
    {
      log.error(e);
    }
    System.out.println("userNotification Rowmapper:" + userNotification);
    return userNotification;
  }
}