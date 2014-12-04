
package ca.uoguelph.ccs.portal.modules;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.mail.*;
import javax.mail.search.*;
import org.apache.commons.lang.StringEscapeUtils;

/**
 * Retrieve email from a remote mailbox.
 * <P>
 * Parameters:
 * <TABLE>
 * <TR><TD>h</TD><TD>Mail server hostname.</TD></TR>
 * <TR><TD>p</TD><TD>Protocol (imap)</TD></TR>
 * <TR><TD>o</TD><TD>Output (rss)</TD></TR>
 * </TABLE>
 */
public class MailProxy extends HttpServlet
{
    public static final String HOST = "h";
    public static final String PROTOCOL = "p";
    public static final String OUTPUT = "o";
    public static final String USERNAME = "u";
    public static final String PASSWORD = "a";

    private ServletConfig servletConfig;

    public void init(ServletConfig servletConfig) throws ServletException
    {
        this.servletConfig = servletConfig;
    }

    protected void doGet(HttpServletRequest req,
                         HttpServletResponse res)
        throws IOException
    {
        String host;
        String protocol;
        String output;
        String username;
        String password;

        host = req.getParameter(HOST);
        protocol = req.getParameter(PROTOCOL);
        output = req.getParameter(OUTPUT);
        username = req.getParameter(USERNAME);
        password = KeyChain.getInstance().decrypt(req.getParameter(PASSWORD));

        res.setContentType("text/xml");
        Writer out = res.getWriter();
        out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        out.write("<rdf:RDF\n");
        out.write(" xmlns=\"http://purl.org/rss/1.0/\"\n");
        out.write(" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n");
        out.write(" xmlns:dc=\"http://purl.org/dc/elements/1.1/\"\n");
        out.write(">\n");
        
        // Create empty properties
        Properties props = new Properties();
        
        // Get session
        Session session = Session.getDefaultInstance(props, null);
        
        // Get the store
        Store store = null;
        Folder folder = null;
        try {
            store = session.getStore(protocol);
            store.connect(host, username, password);
            
            // Get folder
            folder = store.getFolder("INBOX");
            folder.open(Folder.READ_ONLY);
            
            // Get directory
            SearchTerm recent = new FlagTerm(new Flags(Flags.Flag.RECENT), true);
            Message message[] = folder.search(recent);
            //Message message[] = folder.getMessages();
            
            for (int i=0, n=Math.min(message.length,10); i<n; i++) {
                out.write("<item>\n");
                out.write("<title>");
                out.write(StringEscapeUtils.escapeHtml(message[i].getSubject()));
                out.write("</title>\n");
                out.write("<dc:creator>");
                out.write(StringEscapeUtils.escapeHtml(message[i].getFrom()[0].toString()));
                out.write("</dc:creator>\n");
                out.write("</item>\n");
            }
        }
        catch(Exception e) {
            log("Failed to get mail for "+username+" at "+host+".",e);
        }
        finally {
            // Close connection 
            try { if (folder != null) folder.close(false); } catch(Exception f) {}
            try { if (store != null) store.close(); } catch(Exception f) {}
        }
        
        out.write("</rdf:RDF>\n");
    }
}
