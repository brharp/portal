package ca.uoguelph.ccs.portal.portlets.notification.bus;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Simple bean representing a notification for user
 * @author ljairath
 */
public class UofGUserNotification implements Serializable
{
  private Integer messageId;
  private String userId;
  private String messageSubject;
  private String messageFrom;
  private Timestamp markExpiry;
  private Timestamp markOpened;
  private Timestamp release;
  private Timestamp markRead;
  private String messageImportance;
  private String role;
  private String owner;

  public String getOwner()
  {
    return owner;
  }

  public void setOwner(String owner)
  {
    this.owner= owner;
  }

  public String getUserId()
  {
    return userId;
  }

  public void setUserId(String userId)
  {
    this.userId= userId;
  }

  public String getRole()
  {
    return role;
  }

  public void setRole(String role)
  {
    this.role= role;
  }


  public Integer getMessageId()
  {
    return messageId;
  }

  public void setMessageId(Integer messageId)
  {
    this.messageId = messageId;
  }

  public String getMessageFrom()
  {
    return messageFrom;
  }

  public void setMessageFrom(String messageFrom)
  {
    this.messageFrom = messageFrom;
  }

  public String getMessageSubject()
  {
    return messageSubject;
  }

  public void setMessageSubject(String messageSubject)
  {
    this.messageSubject= messageSubject;
  }

  public Timestamp getMarkExpiry()
  {
    return markExpiry;
  }

  public void setMarkExpiry(Timestamp markExpiry)
  {
    this.markExpiry = markExpiry;
  }

  public Timestamp getMarkRead()
  {
    return markRead;
  }

  public void setMarkRead(Timestamp markRead)
  {
    this.markRead = markRead;
  }

  public Timestamp getMarkOpened()
  {
    return markOpened;
  }

  public void setMarkOpened(Timestamp markOpened)
  {
    this.markOpened= markOpened;
  }

  public Timestamp getRelease()
  {
    return release;
  }

  public void setRelease(Timestamp release)
  {
    this.release= release;
  }

  public String getMessageImportance()
  {
    return messageImportance;
  }

  public void setMessageImportance(String messageImportance)
  {
    this.messageImportance= messageImportance;
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