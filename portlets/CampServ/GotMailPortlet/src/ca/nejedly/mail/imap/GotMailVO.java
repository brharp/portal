/*
 * Created on Nov 15, 2005
 *
 */
package ca.nejedly.mail.imap;

import java.util.Vector;
import java.util.Collection;
import java.util.Iterator;
import java.util.TreeSet;
/**
 * 
 * Value Object holding the message counts and optionally also  
 * some email message information like the subject, date, from address,...  
 * @author Zdenek Nejedly znejedly@uoguelph.ca
 * 
 * @see GotMail
 *
 */
public class GotMailVO {

	public GotMailVO() {		
	}
	
	/** Optional email messages. If this field is not null then it is used to 
	 * determine the total message count or the unseen message count. If it is 
	 * null then the countUnseen or countTotal fields are used directly.
	 * @see #countUnseen 
	 * @see #countTotal  
	 * 
	 */
	protected Vector messages;
	
	/** Total number of messages in the folder. Used if the messages structure is null.
	 * 
	 */
	protected int countTotal;
	
	/** Number of unseen messages in the folder. Used if the messages structure is null.
	 * 
	 */
	protected int countUnseen;
	
	/**
	 * @return Returns the total number of messages.
	 * The internal messages structure is authoritative if non-null, 
	 * i.e., if it is not a null then the countTotal and countUnseen fields are ignored. 
	 * @see #messages
	 */
	
	// max length of specific fields
	protected int normLenSubject;
	protected int normLenFromAddress;
	
	
	
	public int getCountTotal() {
		if (messages!=null)
			countTotal = messages.size();
		return countTotal;
	}
	/**
	 * @param countTotal The total number of messages to set.
	 * Use this method if the messages structure is null. 
	 * @see #messages
	 */
	public void setCountTotal(int countTotal) {
		this.countTotal = countTotal; 
	}
	
	/**
	 * @return Returns the number of seen messages.
	 * The internal messages structure is authoritative if non-null, 
	 * i.e., if it is not a null then the countTotal and countUnseen fields are ignored. 
	 * @see #messages
	 */
	public int getCountSeen() {
		int countSeen = countTotal - countUnseen;
		if (messages!=null) {
			countSeen = 0;
			for (int i = 0; i < messages.size(); i++) {
				MessageRecord rec = (MessageRecord) messages.elementAt(i);
				if (rec!=null && rec.isSeen())
					countSeen++;
			}
		}  
		return countSeen;
	}
	/**
	 * @param countSeen 
	 * Dummy setter - does nothing.
	 */
	public void setCountSeen(int countSeen) {
	}
	
	/**
	 * @return Returns the number of unseen messages.
	 * The internal messages structure is authoritative if non-null, 
	 * i.e., if it is not a null then the countTotal and countUnseen fields are ignored. 
	 * @see #messages
	 */
	public int getCountUnseen() {
		//return getCountTotal()-getCountSeen();
		if (messages!=null) 
			return getCountTotal() - getCountSeen();
		else
			return countUnseen;
	}
	/**
	 * @param countUnseen The number of unseen messages to set.
	 * Use this method if the messages structure is null. 
	 * @see #messages
	 */
	public void setCountUnseen(int countUnseen) {
		this.countUnseen = countUnseen; 
	}
	
	
	/**
	 * @return Returns the messages. May be null.
	 * @see #messages
	 */
	public Collection getMessages() {
		return messages;
	}
	/**
	 * @param messages 
	 * dummy method;
	 */
	public void setMessages(Collection messages) {
		//this.messages = messages;
	}
	
	/**
	 * @return Returns the messages. May be null;
	 * @see #messages
	 */
	public Collection getUnseenMessages() {
		Collection unseen = new Vector();
		if (messages!=null) {
			Iterator all = messages.iterator();
			while (all.hasNext()) {
				MessageRecord rec = (MessageRecord) all.next();
				if (rec!=null) {
					if (!rec.isSeen())
						unseen.add(rec);
				}
			}
		}
		return unseen;
	}
	/**
	 * @param messages The messages to set.
	 * dummy method;
	 */
	public void setUnseenMessages(Collection messages) {
	}
	
	
	/**
	 * Adds a message to the internal buffer. 
	 * @param rec MessageRecord to be added
	 * @return current total number of messages;
	 * @see #messages 
	 */
	public int addMessageRecord(MessageRecord rec) {
		if (rec!=null) {
			if (messages==null)
				messages = new Vector();
			messages.add(rec);
		}
		return messages.size();
	}
	
	public Collection normalizeText(Collection col) {
		// go through the collection and limit the subject and from Address
		if (col!=null) {
			Iterator iterator = col.iterator();
			while (iterator.hasNext()) {
				MessageRecord rec = (MessageRecord) iterator.next();
				rec.setFromAddressNormalized(normalizeString(rec.getFromAddress(), this.normLenFromAddress));
				rec.setSubjectNormalized(normalizeString(rec.getSubject(), this.normLenSubject));
			}
		}
		return col;		
	}
	
	/**
	 * If the provided string is longer then the specified 
	 * maxLength - the length of the internally added suffix '...'
	 * then the method shortens the string to maxLength-len(suffix)
	 * and adds the suffix. 
	 * Returns an unchanged string otherwise
	 * If the specified maxLength is 0 or <0 then no modifications are done
	 *    
	 * @param source
	 * @param maxLength 
	 * @return original string limited to the maxLength length 
	 */
	public static String normalizeString(String source, int maxLength) {
		String result = null;
		String addedSuffix = "...";
		if (source!=null) {
			result = source.trim();
			int effectiveMaxLength = maxLength;
			if (addedSuffix!=null)
				effectiveMaxLength -= addedSuffix.length();
			effectiveMaxLength = Math.max(0,effectiveMaxLength);
			if (result.length()>effectiveMaxLength && maxLength>0) {
				result = result.substring(0,effectiveMaxLength);
				if (addedSuffix!=null)
					result += addedSuffix;
			}
		}
		return result;
	}
	
	public int getNormLenFromAddress() {
		return normLenFromAddress;
	}
	public void setNormLenFromAddress(int normLenFromAddress) {
		this.normLenFromAddress = normLenFromAddress;
	}
	public int getNormLenSubject() {
		return normLenSubject;
	}
	public void setNormLenSubject(int normLenSubject) {
		this.normLenSubject = normLenSubject;
	}
	
	public Collection sortCollection(Collection col) {
		MessageRecordComparator cmp = new MessageRecordComparator();
		cmp.setAscendingOrder(false);
		TreeSet sortedSet = new TreeSet(cmp);  
		if (col!=null) {
			Iterator iterator = col.iterator();
			while (iterator.hasNext()) {
				sortedSet.add(iterator.next());
			}
		} else
			return null;
		return sortedSet;		
	}
	
	/**
	 * Retuns the first 'size' number of elements   
	 * @param col
	 * @param size
	 * @return
	 */
	public Collection head(Collection col, int size) {
		Vector result = null;
		if (col!=null) {
			int count = 0;
			result = new Vector();
			Iterator iterator = col.iterator();
			while (iterator.hasNext() && count<size) {
				result.add(iterator.next());
				count++;
			}
		}
		return result;		
	}
}
