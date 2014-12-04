/*
 * Created on November 3, 2005, 9:23 AM
 */

package ca.nejedly.mail.imap;

import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Flags;

import java.util.Properties;
import javax.mail.Address;

//import java.util.Iterator;
/**
 * Help test class - FOR TESTS ONLY  
 * @author  Zdenek Nejedly znejedly@uoguelph.ca
 */
public class GotMailTestRead {
    
	public static final int MAX_DATA = 100;
	public long timeData[][] = new long[MAX_DATA][3];
	public int timeDataIndex = 0;
	
    /** Creates a new instance of ImapClient */
    public GotMailTestRead() {
    }
    
    public GotMailVO checkMail(String mailServer, int port, 
    			String uid, String pwd) throws GotMailException {
    	Properties props = new Properties();
        boolean verbose = false;
        GotMailVO vo = new GotMailVO(); 
        
        long timeStart = System.currentTimeMillis();
        long timeAfterGetUnread = 0;
        long timeStop = 0;
        
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
            timeAfterGetUnread = System.currentTimeMillis();
            //System.out.print("Unread:" + folder.getUnreadMessageCount()+ " " );
            
            folder.open(Folder.READ_WRITE);
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
                rec.setFromAddress(fromAddress);
                rec.setSubject(messages[i].getSubject());
                vo.addMessageRecord(rec);
            }
            folder.close(false);
            timeStop = System.currentTimeMillis();
            store.close();
        }
        catch (Exception e) {
            throw new GotMailException("Failed to check for new mail",e);            
        }
        timeData[timeDataIndex][0] = timeStart;
        timeData[timeDataIndex][1] = timeAfterGetUnread;
        timeData[timeDataIndex][2] = timeStop;
        timeDataIndex++;
        
        /*
        java.text.DecimalFormat fmt = new java.text.DecimalFormat("0.000");
        System.out.print(" partial=" + fmt.format(((float)(timeAfterGetUnread-timeStart))/1000.0));
        System.out.print(" total=" + fmt.format(((float)(timeStop-timeStart))/1000.0));
        System.out.println(" dif=" + fmt.format(((float)(timeStop-timeAfterGetUnread))/1000.0));
        */
		return vo;
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Properties props = new Properties();
        String mailServer = "staff.mail.uoguelph.ca";
        String uid = "ugzndev1";
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
        try {
            GotMailTestRead obj = new GotMailTestRead();
            int loopCnt = 1;
            obj.timeDataIndex = 0;
            boolean cancelled = false;
            while (obj.timeDataIndex<MAX_DATA && !cancelled) {
            	GotMailVO vo = obj.checkMail(mailServer,0, uid,pwd);
            	System.out.println ("Reading " + obj.timeDataIndex + " . . . ");
            	
            	//System.out.print ("" + obj.timeDataIndex + " : ");
            	/*
            	if (vo!=null) {            		
            		System.out.println("You have " + vo.getCountUnseen() +
            			" unread message(s) and a total of "  +
            			vo.getCountTotal() + " messages in your inbox" );
            		if (vo.getCountTotal()>0) {
            		Iterator iterator = vo.getUnseenMessages().iterator();
            			while (iterator.hasNext()) {
            			MessageRecord rec = (MessageRecord) iterator.next();
            			System.out.println("" + (rec.isSeen()?"S":"-") + 
            					(rec.isAnswered()?"A":"-") + " " + rec.getFromAddress()+
								" : " + rec.getSubject());
            			}
            		}
            	}
            	*/
            	try {
            		Thread.sleep(3000);            		            		
            	}
            	catch (InterruptedException e) {;}
            }
            // dump the data
            java.text.DecimalFormat fmt = new java.text.DecimalFormat("0.000");
            for (int i = 0; i < obj.timeDataIndex; i++) {
            	System.out.print("" + i + ", ");
            	System.out.print(" partial=" + fmt.format( ((float)(obj.timeData[i][1] - obj.timeData[i][0]))/1000.0));
            	
                System.out.print(" total=" + fmt.format(((float)(obj.timeData[i][2] - obj.timeData[i][0]))/1000.0));
                System.out.println(" dif=" + fmt.format(((float)(obj.timeData[i][2] - obj.timeData[i][1]))/1000.0));
            }
            System.out.println("Times");
            for (int i = 0; i < obj.timeDataIndex; i++) {
            	System.out.println(obj.timeData[i][0]+","+obj.timeData[i][1]+","+obj.timeData[i][2]);            	
            }
            
            
            
        }
        catch (GotMailException e) {
            e.printStackTrace();            
        }
    }
    
}
