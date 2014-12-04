/*
 */
package ca.nejedly.mail.imap;

import java.text.DateFormat;

/**
 * 
 * Message value object
 *  
 * @author  Zdenek Nejedly znejedly@uoguelph.ca
 *
 */
public class MessageRecord {

	protected String subject;
	protected String fromAddress;
	protected String subjectNormalized;
	protected String fromAddressNormalized;
	protected int messageNumber; 
	
	protected java.util.Date receivedDate;
	protected boolean seen;
	protected boolean answered;	
	
	private DateFormat dateFormat =
		DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
	
	public MessageRecord() {			
	}

	public static void main(String[] args) {
	}
	/**
	 * @return Returns the fromAddress.
	 */
	public String getFromAddress() {
		return fromAddress;
	}
	/**
	 * @param fromAddress The fromAddress to set.
	 */
	public void setFromAddress(String fromAddress) {
		this.fromAddress = fromAddress;
	}

	/**
	 * @return Returns the subject.
	 */
	public String getSubject() {
		return subject;
	}
	/**
	 * @param subject The subject to set.
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	/**
	 * @return Returns the answered.
	 */
	public boolean isAnswered() {
		return answered;
	}
	/**
	 * @param answered The answered to set.
	 */
	public void setAnswered(boolean answered) {
		this.answered = answered;
	}
	/**
	 * @return Returns the seen.
	 */
	public boolean isSeen() {
		return seen;
	}
	/**
	 * @param seen The seen to set.
	 */
	public void setSeen(boolean seen) {
		this.seen = seen;
	}
	
	/**
	 * 
	 * @return the date & time when the message was received
	 */
	public java.util.Date getReceivedDate() {
		return receivedDate;
	}
	
	/**
	 * 
	 * @param receivedDate
	 */
	public void setReceivedDate(java.util.Date receivedDate) {
		this.receivedDate = receivedDate;
	}
	
	/** 
	 * Returns a preformatted date string
	 * @return
	 */
	public String getReceivedDateString() {
		String receivedDateString = null;
		if (this.receivedDate!=null)
			receivedDateString = dateFormat.format(receivedDate);
		return receivedDateString;
	}
	
	
	/**
	 * 
	 * @param receivedDateString
	 * Dummy method - does nothing
	 */
	public void setReceivedDateString(String receivedDateString) {
	}
		

	public String getFromAddressNormalized() {
		return fromAddressNormalized;
	}
	public void setFromAddressNormalized(String fromAddressNormalized) {
		this.fromAddressNormalized = fromAddressNormalized;
	}
	public String getSubjectNormalized() {
		return subjectNormalized;
	}
	public void setSubjectNormalized(String subjectNormalized) {
		this.subjectNormalized = subjectNormalized;
	}
	public int getMessageNumber() {
		return messageNumber;
	}
	public void setMessageNumber(int messageNumber) {
		this.messageNumber = messageNumber;
	}
}
