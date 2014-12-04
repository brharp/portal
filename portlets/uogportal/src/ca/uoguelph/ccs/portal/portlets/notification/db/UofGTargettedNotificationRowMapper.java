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

import ca.uoguelph.ccs.portal.portlets.notification.bus.UofGTargettedNotification;

/**
 * RowMapper for a UofGTargettedNotification Record
 *
 * @author rlambert
 */
public class UofGTargettedNotificationRowMapper implements RowMapper
{
  private static final Log log = LogFactory.getLog(UofGTargettedNotificationRowMapper.class);

  /**
   * Creates a UofGTargettedNotification from a ResultSet
   */
  public Object mapRow(ResultSet rs, int rowNum) throws SQLException
  {
  	UofGTargettedNotification targettedNotification = new UofGTargettedNotification();
    try
    {
        System.out.println("targettedNotification Rowmapper:" + (String)rs.getString("message_from"));
    	targettedNotification.setMessageId(new Integer(rs.getInt("message_id")));
    	targettedNotification.setMessageFrom((String)rs.getString("message_from"));
    	targettedNotification.setMessageSubject((String)rs.getString("message_subject"));
    	targettedNotification.setExpiry((Timestamp)rs.getTimestamp("expiry"));
    }
    catch (SQLException e)
    {
      log.error(e);
    }
    System.out.println("targettedNotification Rowmapper:" + targettedNotification);
    return targettedNotification;
  }
}