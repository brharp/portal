package ca.uoguelph.ccs.portal.portlets.notification.bus;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Simple bean representing a targeted notification
 * @author ljairath
 */
public class UofGTargettedNotification implements Serializable
{
  private Integer messageId;
  private String userId;
  private String role;
  private String owner;
  private String message;
  private String messageSubject;
  private String messageFrom;
  private Timestamp expiry;
  private Timestamp release;
  private String messageImportance;

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

  public Integer getMessageId()
  {
    return messageId;
  }

  public void setMessageId(Integer messageId)
  {
    this.messageId = messageId;
  }

  public String getRole()
  {
    return role;
  }

  public void setRole(String role)
  {
    this.role= role;
  }

  public String getMessage()
  {
    return message;
  }

  public void setMessage (String message)
  {
    this.message= message;
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

  public Timestamp getExpiry()
  {
    return expiry;
  }

  public void setExpiry(Timestamp expiry)
  {
    this.expiry= expiry;
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
    return (object.getClass().equals(this.getClass()) && this.getMessageId() != null && this.getMessageId().equals(((UofGTargettedNotification) object).getMessageId()));
  }

  public String toString()
  {
    return "MessageID: " + messageId + ", Role: " + role+ ", Message: " + message;
  }
}