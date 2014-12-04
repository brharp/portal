/*
 * GotMail.java
 *
 * Created on November 3, 2005, 9:23 AM
 * 
 */

package ca.nejedly.mail.imap;

import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Flags;

import java.util.Properties;
import javax.mail.Address;

import java.util.Iterator;
/**
 * Checks the user's inbox and returns information about user's 
 * messages. 
 * @see GotMailVO 
 * @author  Zdenek Nejedly znejedly@uoguelph.ca
 */
public class GotMail {
    
	
    /** Creates a new instance of ImapClient */
    public GotMail() {
    }
    
    /**
     * 
     * @param mailServer domain address of the IMAP mail server 
     * @param port communication port of the IMAP server
     * @param uid user's login name
     * @param pwd user's password
     * @param retrieveCountsOnly if this parameter is true then only the number of unseen messages and the total message count is queried without opening the folder (no other message info retrieved). Full folder scan performed otherwised.  
     * @return value object with the message counts and optional message info (subject, etc) depending on the retrieveCountsOnly parameter.
     * @throws GotMailException
     */
    public GotMailVO checkMail(String mailServer, int port, 
    			String uid, String pwd, boolean retrieveCountsOnly) 
    	throws GotMailException, GotMailLoginFailedException,
				GotMailMissingUidException, GotMailMissingPasswordException {
    	Properties props = new Properties();
        boolean verbose = false;
        GotMailVO vo = new GotMailVO(); 
        boolean failOverToMessageScan = false;
        if (uid==null || uid.length()<1)
    		throw new GotMailMissingUidException("The user's login ID is missing");
        if (pwd==null || pwd.length()<1)
    		throw new GotMailMissingPasswordException("The user's password is missing");
        
        try {
        	Session session = Session.getDefaultInstance(props, null);
            Store store = session.getStore("imap");
            if (port<1)
            	store.connect(mailServer, uid, pwd);
            else
            	store.connect(mailServer, port, uid, pwd);
            String folderName = "INBOX";
            Folder folder = store.getFolder(folderName);
            if (folder == null) 
            	throw new GotMailException("The folder " + folderName + " does not exist.");
            if (retrieveCountsOnly) {
            	int count = folder.getUnreadMessageCount();
            	if (count<0)
            		failOverToMessageScan = true;
            	else
            		vo.setCountUnseen(count);
            	count = folder.getMessageCount();
            	if (count<0)
            		failOverToMessageScan = true;
            	else
            		vo.setCountTotal(count);
            }
            if (!retrieveCountsOnly || failOverToMessageScan) { 	            	
            	folder.open(Folder.READ_ONLY);
            	Message[] messages = folder.getMessages();           
            	for (int i = 0; i < messages.length; i++) {
            		MessageRecord rec = new MessageRecord();
            		Flags flags = messages[i].getFlags();
            		Flags.Flag[] sysFlags = flags.getSystemFlags();
            		for (int j = 0; j < sysFlags.length; j++) {                	
            			if (sysFlags[j] == Flags.Flag.DELETED) {                        
            			} else if (sysFlags[j] == Flags.Flag.SEEN) {
            				rec.setSeen(true);
            			} else if (sysFlags[j] == Flags.Flag.ANSWERED) {
            				rec.setAnswered(true);
            			} else if (sysFlags[j] == Flags.Flag.DRAFT) {
                        
            			} else if (sysFlags[j] == Flags.Flag.FLAGGED) {
                        
            			} else if (sysFlags[j] == Flags.Flag.RECENT) {
                        
            			} else if (sysFlags[j] == Flags.Flag.USER) {
                        
            			}
            		}
            		// get the sender's address
            		Address[] addresses = messages[i].getFrom();
            		String fromAddress = "";
            		if (addresses!=null) {
            			for (int j = 0; j < addresses.length; j++) {
                			if (addresses[j]!=null) {
                				if (j>0)
                					fromAddress +=",";
                				fromAddress += addresses[j].toString();
                			}
                		}                			 
                	}
            		rec.setReceivedDate(messages[i].getReceivedDate());
            		rec.setFromAddress(fromAddress);
            		rec.setSubject(messages[i].getSubject());
            		rec.setMessageNumber(messages[i].getMessageNumber());
            		vo.addMessageRecord(rec);
            		
            	}
            	folder.close(false);
            }
            store.close();
        }
        catch (javax.mail.AuthenticationFailedException e) {
        	throw new GotMailLoginFailedException("The password of " + uid + " is incorrect.", e);
        }
        catch (Exception e) {
            throw new GotMailException("Failed to check for new mail",e);            
        }
        return vo;
    }
    
    /**
     * For testing only 
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Properties props = new Properties();
        String mailServer = "staff.mail.uoguelph.ca";
        /*String uid = "ugzndev1";
        String pwd = "";
        */
        String uid = "znejedly";
        String pwd = "";
        
        /*if (args.length<2) {
            System.out.println("Missing uid and pwd.");
            //System.exit(111);                        
        } else {
        	uid = args[0];
            pwd = args[1];
        }
        
        boolean verbose = true;
        if (args.length>2)
            if (args[2].equalsIgnoreCase("v"))
                verbose = true; 
        */
        java.text.DecimalFormat fmt = new java.text.DecimalFormat("0.000");
        long timeStart = 0;
        try {
            GotMail obj = new GotMail();
            int loopCnt = 1;
            boolean cancelled = false;
            while (loopCnt<2 && !cancelled) {
            	timeStart = System.currentTimeMillis();
            	GotMailVO vo = obj.checkMail(mailServer,0, uid,pwd,
            			false);
            	long timeStop = System.currentTimeMillis();
            	System.out.println("" + fmt.format(((float)(timeStop - timeStart))/1000.0));
                  
            	//System.out.print ("" + loopCnt + " : ");
            	
            	if (vo!=null) {
            		vo.setNormLenFromAddress(10);
            		vo.setNormLenSubject(10);
            		System.out.println("You have " + vo.getCountUnseen() +
            			" unread message(s) and a total of "  +
            			vo.getCountTotal() + " messages in your inbox" );
            		java.util.Collection col = vo.getUnseenMessages(); 
            		if (col!=null) {
            			col = vo.normalizeText(col);
            			col = vo.sortCollection(col);
            			//col = vo.head(col,3);
            			Iterator iterator = col.iterator();
            			while (iterator.hasNext()) {
            			MessageRecord rec = (MessageRecord) iterator.next();
            			System.out.println("" + (rec.isSeen()?"S":"-") + 
            					(rec.isAnswered()?"A":"-") + " " + 
								rec.getReceivedDateString() + " " + 
								rec.getFromAddress()+
								" : " + rec.getSubject() +
								" : " + rec.getSubjectNormalized());
            			}
            		}
            	}            
            	loopCnt ++;
            }
        }
        catch (GotMailException e) {
        	long timeStop = System.currentTimeMillis();
        	System.out.println("" + fmt.format(((float)(timeStop - timeStart))/1000.0));            
            e.printStackTrace();
            
        }
    }    
}
