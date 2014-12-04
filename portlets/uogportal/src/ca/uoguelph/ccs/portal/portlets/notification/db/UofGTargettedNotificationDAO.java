package ca.uoguelph.ccs.portal.portlets.notification.db;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import ca.uoguelph.ccs.portal.portlets.notification.bus.UofGTargettedNotification;

/**
 * UofGTargettedNotification DAO
 * @author ljairath
 *
 */
public interface UofGTargettedNotificationDAO
{
  /**
   * Returns a collection of new UofGTargettedNotifications in the system either by role/email.
   * @return java.util.List of UofGTargettedNotification
   */
  public List getTargettedNotificationsByRole(String instructor, String course, String organisationalStatus, String userId, String email, Long messageId);

  /**
   * Returns a collection of new UofGTargettedNotifications in the system by user id.
   * @return java.util.List of UofGTargettedNotification
   */
  public List getTargettedNotificationsByUser(String userId, String messageIds);

  
  /**
   * Get a UofGTargettedNotification Object given the id
   * @param id
   * @return UofGTargettedNotification
   */
  public String getTargettedMessage(Integer messageId);
  
  public List getTargettedMessages(String messageIds);

  /**
   * Insert a UofGTargettedNotification Object,  
   * @param uofGTargettedNotification
   * @return UofGTargettedNotification
   */
  public void insertTargettedNotification(UofGTargettedNotification uofGTargettedNotification);
  
  /**
   * 
   * @return Map of column name and type  
   */
  public Map getTargetedNotificationColumnNameType(String rowSetQuery);
  
  public void updateRelease(String owner, String messageIds, Timestamp expiry);
}