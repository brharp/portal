/*
 * Created on Nov 24, 2005
 *
 */
package ca.nejedly.mail.imap;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;

/**
 * @author Zdenek Nejedly znejedly@uoguelph.ca
 *
 */
public class MessageRecordComparator implements Comparator, Serializable {

	protected boolean ascendingOrder = false;
	
	public MessageRecordComparator() {
		super();
	}

	/**
	 * Compares two MessageRecords based on their date
	 * null pointers come first. i.e., null < !null 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 *  
	 */
	public int compare(Object arg0, Object arg1) {
		MessageRecord rec0 = (MessageRecord) arg0;
		MessageRecord rec1 = (MessageRecord) arg1;
		if (!ascendingOrder) { // flip the two
			MessageRecord tmp = rec0;
			rec0 = rec1;
			rec1 = tmp;
		}
		if (rec0 == null || rec1==null)
			throw new NullPointerException("The records compared must not be null");		
		Date date0 = rec0.getReceivedDate();
		Date date1 = rec1.getReceivedDate();
		if (date0==null && date1==null) 
			return rec0.getMessageNumber()-rec1.getMessageNumber();
		if (date0==null)
			return -1;
		if (date1==null)
			return 1;
		if (date0.compareTo(date1)==0)
			return rec0.getMessageNumber()-rec1.getMessageNumber();
		return date0.compareTo(date1);
	}	
	
	public static void main(String args[]) {
		MessageRecordComparator comp =  new MessageRecordComparator();
		comp.setAscendingOrder(true);
		MessageRecord rec0 = new MessageRecord();
		rec0.setReceivedDate(new Date());
		try {Thread.sleep(100);	}
		catch (InterruptedException e) {;}
		MessageRecord rec1 = new MessageRecord();
		rec1.setReceivedDate(new Date());
		rec0.setReceivedDate(null);
		//System.out.println("date0:" + rec0.getReceivedDate().getTime());
		//System.out.println("date1:" + rec1.getReceivedDate().getTime());
		System.out.println("cmp: 0 < 1: " + comp.compare(rec0, rec1));
		
	}
	public boolean isAscendingOrder() {
		return ascendingOrder;
	}
	public void setAscendingOrder(boolean ascendingOrder) {
		this.ascendingOrder = ascendingOrder;
	}
}
