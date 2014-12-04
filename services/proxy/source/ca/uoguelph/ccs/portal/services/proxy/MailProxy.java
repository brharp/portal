
package ca.uoguelph.ccs.portal.services.proxy;

import java.io.*;
import java.net.*;
import java.util.*;
import java.text.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.mail.*;
import javax.mail.search.*;
import org.jasig.portal.security.*;
import org.jasig.portal.security.provider.*;
import org.apache.commons.lang.StringEscapeUtils;

/**
 * Retrieve email from a remote mailbox. This servlet maintains a
 * persistent copy of mailbox data in the user session. The local copy
 * is refreshed under the following conditions:
 *
 * <ul>
 * <li> It has been too long since the last refresh. </li>
 * <li> There are not enough messages in the local copy to satisfy
 *      the request. </li>
 * </ul>
 */
public class MailProxy extends HttpServlet
{
    public static final String IPERSON = IPerson.class.getName();
    public static final String MAILHOST = "mailHost";
    public static final String MAILPROTO = "mailProto";
    public static final String START = "start";
    public static final String END = "end";
    
    static String LAST_ACCESS = MailProxy.class.getName() + ".LAST_ACCESS";
    static String HASHQ = MailProxy.class.getName() + ".HASHQ";
    static String CACHE_XML = MailProxy.class.getName() + ".CACHE_XML";

    public static final int MAX_AGE = 600000; // 10 minutes.

    private static final DateFormat rfc2822dt =
        new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z");

    protected void doGet(HttpServletRequest req,
                         HttpServletResponse res)
        throws IOException, ServletException
    {
        doPost(req, res);
    }

    protected void doPost(HttpServletRequest req,
                          HttpServletResponse res)
        throws IOException, ServletException
    {
        HttpSession httpsession;
        httpsession = req.getSession(false);

        if (httpsession == null) {
            res.sendError(500);
            return;
        }
        
        IPerson person;
        person = (IPerson)httpsession.getAttribute(IPERSON);

        if (person == null) {
            res.sendError(500);
            return;
        }

        ISecurityContext security;
        security = person.getSecurityContext();

        String username;
        String password;
        username = getUsername(security);
        password = getPassword(security);

        if (username == null || password == null) {
            res.sendError(500);
            return;
        }

        //Enumeration en = person.getAttributeNames();
        //while(en.hasMoreElements()) {
        //String a = (String)en.nextElement();
        //log("IPerson["+a+"] = "+person.getAttribute(a));
        //}

        String host;
        host = (String)person.getAttribute(MAILHOST);

        int start = -9;
        try {
            start = Integer.parseInt(req.getParameter(START));
        } catch(NumberFormatException nfe) {
            log("Failed to parse " + req.getParameter(START));
        }

        int end = -1;
        try {
            end = Integer.parseInt(req.getParameter(END));
        } catch(NumberFormatException nfe) {
            log("Failed to parse " + req.getParameter(END));
        }

        res.setContentType("text/xml");

        // Prevent caching.
        res.setHeader("Cache-Control", "no-cache");
        res.setHeader("Pragma", "no-cache");
        res.setHeader("Expires", "-1");

        // Check for cached content.
        Date lastAccess = (Date)httpsession.getAttribute(LAST_ACCESS);
        String hashq = (String)httpsession.getAttribute(HASHQ);
        if (lastAccess != null && System.currentTimeMillis() -
            lastAccess.getTime() < MAX_AGE &&
            hashq(username,host,start,end).equals(hashq)) {
            String cacheXml = (String)httpsession.getAttribute(CACHE_XML);
            if (cacheXml != null) {
                log("Cache Hit");
                Writer out = res.getWriter();
                out.write(cacheXml);
                return;
            }
        }

        // Create empty properties
        Properties props = new Properties();
        
        // Get session
        Session session = Session.getDefaultInstance(props, null);
        
        // Get the store
        Store store = null;
        Folder folder = null;
        try {
            store = session.getStore("imap");
            store.connect(host, username, password);
            
            // Get folder
            folder = store.getFolder("INBOX");

            int exists = folder.getMessageCount();
            int recent = folder.getNewMessageCount();
            int unseen = folder.getUnreadMessageCount();

            folder.open(Folder.READ_ONLY);

            String url = folder.getURLName().toString();
            
            Writer out = new StringWriter();
            out.write("<?xml version='1.0' encoding='UTF-8'?>\n");
            out.write("<rdf:RDF");
            out.write(" xmlns:rdf='http://www.w3.org/1999/02/22-rdf-syntax-ns#'");
            out.write(" xmlns='http://www.ietf.org/rfc/rfc2060.txt#'");
            out.write(">\n");

            out.write("<Mailbox rdf:about='");
            out.write(folder.getURLName().toString());
            out.write("'>\n");
            out.write("<name>INBOX</name>\n");
            out.write("<exists>");
            out.write(Integer.toString(exists));
            out.write("</exists>\n");
            out.write("<recent>");
            out.write(Integer.toString(recent));
            out.write("</recent>\n");
            out.write("<unseen>");
            out.write(Integer.toString(unseen));
            out.write("</unseen>\n");

            out.write("<messages>\n");
            out.write("<rdf:Seq>\n");
            
            StringBuffer buf = new StringBuffer();
            
            if (exists > 0) {

                int first = Math.max(start < 0 ? exists+start+1 : start, 1);
                int last  = Math.min(end < 0 ? exists+end+1 : end, exists);
                
                Message message[] = folder.getMessages(first,last);

                FetchProfile profile = new FetchProfile();
                profile.add(FetchProfile.Item.ENVELOPE);
                folder.fetch(message, profile);

                for (int i=0, n=message.length; i<n; i++) {

                    String num = Integer
                        .toString(message[i].getMessageNumber());
                    
                    out.write("<rdf:li rdf:resource='");
                    out.write(url);
                    out.write("#");
                    out.write(num);
                    out.write("'/>\n");
                    
                    buf.append("<Message rdf:about='");
                    buf.append(url);
                    buf.append('#');
                    buf.append(num);
                    buf.append("'>\n");
                    
                    buf.append("<envelope>\n");
                    buf.append("<Envelope");
                    buf.append(" xmlns='http://www.ietf.org/rfc/rcf2822.txt#'");
                    buf.append(">\n");
                    
                    buf.append("<subject>");
                    buf.append(StringEscapeUtils.escapeHtml
                               (message[i].getSubject()));
                    buf.append("</subject>\n");
                    buf.append("<from>");
                    buf.append(StringEscapeUtils.escapeHtml
                               (message[i].getFrom()[0].toString()));
                    buf.append("</from>\n");
                    
                    // RFC2822 Date Format: 'Fri, 21 Nov 1997 09:55:06 -0600'
                    buf.append("<date>");
                    buf.append(StringEscapeUtils.escapeHtml
                               (rfc2822dt.format(message[i].getReceivedDate())));
                    buf.append("</date>\n");
                    
                    buf.append("</Envelope>\n");
                    buf.append("</envelope>\n");
                    
                    buf.append("<flags>");
                    Flags.Flag[] flags = message[i].getFlags()
                        .getSystemFlags();
                    for (int j = 0; j < flags.length; j++) {
                        if (j > 0) {
                            buf.append(' ');
                        }
                        if (flags[j] == Flags.Flag.ANSWERED) {
                            buf.append("\\Answered");
                        }
                        else if (flags[j] == Flags.Flag.DELETED) {
                            buf.append("\\Deleted");
                        }
                        else if (flags[j] == Flags.Flag.DRAFT) {
                            buf.append("\\Draft");
                        }
                        else if (flags[j] == Flags.Flag.FLAGGED) {
                            buf.append("\\Flagged");
                        }
                        else if (flags[j] == Flags.Flag.RECENT) {
                            buf.append("\\Recent");
                        }
                        else if (flags[j] == Flags.Flag.SEEN) {
                            buf.append("\\Seen");
                        }
                    }
                    buf.append("</flags>\n");
                    buf.append("</Message>\n");
                }
            }
            
            out.write("</rdf:Seq>\n");
            out.write("</messages>\n");
            out.write("</Mailbox>\n");
            out.write(buf.toString());
            out.write("</rdf:RDF>\n");

            httpsession.setAttribute(LAST_ACCESS, new Date());
            httpsession.setAttribute(HASHQ, hashq(username,host,start,end));
            httpsession.setAttribute(CACHE_XML, out.toString());

            Writer sout = res.getWriter();
            sout.write(out.toString());
        }
        catch(Exception e) {
            System.out.println("Failed to get mail for "+username+" at "
                               +host+" due to " + e);
            res.sendError(500);
        }
        finally {
            // Close connection 
            try { if (folder != null) folder.close(false); } catch(Exception f) {}
            try { if (store != null) store.close(); } catch(Exception f) {}
        }
    }

    private String hashq(String username, String host, int start, int end)
    {
        return username + "_" + host + "_" + start + "_" + end;
    }

    private String getUsername(ISecurityContext sc)
    {
        return sc.getPrincipal().getUID();
    }

    private String getPassword(ISecurityContext sc)
    {
        IOpaqueCredentials oc = sc.getOpaqueCredentials();
        if (oc instanceof NotSoOpaqueCredentials) {
            return ((NotSoOpaqueCredentials)oc).getCredentials();
        } else {
            Enumeration en = sc.getSubContexts();
            while (en.hasMoreElements()) {
                String pw = getPassword((ISecurityContext)en.nextElement());
                if (pw != null) {
                    return pw;
                }
            }
        }
        return null;
    }
}
