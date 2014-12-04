package ca.uoguelph.ccs.portal.portlets.notification.bus;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Simple bean representing latest notification for a user
 * @author ljairath@uoguelph.ca
 */
public class UofGUserLatestNotificationSeen implements Serializable
{
  private Integer messageId;
  private String userId;
  
  public String getUserId()
  {
    return userId;
  }

  public void setUserId(String userId)
  {
    this.userId= userId;
  }
  public Integer getMessageId()
  {
    return messageId;
  }

  public void setMessageId(Integer messageId)
  {
    this.messageId = messageId;
  }
  public boolean equals(Object object)
  {
    return (object.getClass().equals(this.getClass()) && this.getMessageId() != null && this.getMessageId().equals(((UofGUserNotification) object).getMessageId()));
  }

  public String toString()
  {
    return "MessageID: " + messageId + "user Id: " + userId;
  }
}