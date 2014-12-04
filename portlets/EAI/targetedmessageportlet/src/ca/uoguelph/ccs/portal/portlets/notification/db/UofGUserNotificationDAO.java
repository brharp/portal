package ca.uoguelph.ccs.portal.portlets.notification.db;

import java.util.List;

import ca.uoguelph.ccs.portal.portlets.notification.bus.UofGUserNotification;

/**
 * UofGUserNotification DAO
 * @author ljairath
 */
public interface UofGUserNotificationDAO
{
  /**
   * Returns a collection of all UofGUserNotifications in the system.
   * @return java.util.Collection of UofGUserNotification
   */
  public List getUnExpiredNotifications(String userId);

  /**
   * Get a message ids List Object given the user id
   * @param user_id
   * @return List
   */
  public List getMessageIds(String user_id);

  /**
   * Insert a UofGUserNotification Object,  
   * @param UofGUserNotification
   * @return UofGUserNotification
   */
  public void insertNotification(UofGUserNotification uofGUserNotification);
  

  /**
   * update UofGUserNotification Object,  
   * @param String
   * @param String
   */
  //public void updateMarkExpiry(String userId, Integer messageId);

  public void updateMarkExpiry(String userId, String messageId);
  
  public void updateMarkRead(String userId, String messageIds);
  
  public void updateMarkOpened(String userId, String messageId);
  
  public void updateRelease(String messageIds);
}