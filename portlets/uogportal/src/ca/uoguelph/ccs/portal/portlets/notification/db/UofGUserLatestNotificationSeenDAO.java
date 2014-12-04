package ca.uoguelph.ccs.portal.portlets.notification.db;

import ca.uoguelph.ccs.portal.portlets.notification.bus.UofGUserLatestNotificationSeen;

/**
 * UofGUserLatestNotificationSeen DAO
 * @author ljairath
 */
public interface UofGUserLatestNotificationSeenDAO
{
  /**
   * Returns a collection of new UofGTargettedNotifications in the system by user id.
   * @return java.util.List of UofGTargettedNotification
   */
  public int getLatestNotificationSeenByUser(String userId);
  
  public void updateLatestNotificationSeenByUser(Long messageId, String userId);
  
  public void insertUser(String userId, Long messageId);
}