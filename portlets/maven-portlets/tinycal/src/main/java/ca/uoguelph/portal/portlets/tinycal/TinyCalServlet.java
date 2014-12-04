package ca.uoguelph.ccs.portal.portlets.tinycal;

import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.net.*;
import java.io.*;
import javax.xml.parsers.*;
import com.sun.syndication.feed.synd.*;
import com.sun.syndication.io.*;
import org.w3c.dom.*;
import ca.uoguelph.ccs.syndication.feed.module.*;

public class TinyCalServlet extends HttpServlet
{
    public static final String EVENTS_ATTRIBUTE = "ca.uoguelph.ccs.portal.portlets.tinycal.EVENTS";
    //public static final int    EVENTS_SCOPE     = PortletSession.APPLICATION_SCOPE;

    public void init() 
    {
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        try {

            List feedUrls = getFeedDirectory();

            // Read all feeds.

            List feeds = new ArrayList();
            for(Iterator i = feedUrls.iterator(); i.hasNext();) {
                URL feedUrl = (URL)i.next();
                SyndFeedInput input = new SyndFeedInput();
                SyndFeed feed = input.build(new XmlReader(feedUrl));
                feeds.add(feed);
            }
            
            // Aggregate.
            
            List entries = new ArrayList();
            for(Iterator i = feeds.iterator(); i.hasNext();) {
                SyndFeed feed = (SyndFeed)i.next();
                for(Iterator j = feed.getEntries().iterator(); j.hasNext();) {
                    SyndEntry entry = (SyndEntry)j.next();
                    entry.setTitle(entry.getTitle() + " from " + feed.getTitle());
                    entries.add(entry);
                }
            }
            
            // Sort by date.
            
            Collections.sort(entries, new UGWhenComparator());
            
            // Log feed details.

            log("Number of entries: " + entries.size());

            // Display calendar.
            
            Calendar calendar = Calendar.getInstance();
            request.getSession().setAttribute("cal", new CalendarBean(calendar));
            request.getSession().setAttribute("ev", new EventsBean(entries));

            if ("month".equals(request.getParameter("view"))) {
                include(request, response, "/WEB-INF/jsp/month.jsp");
            } else {
                include(request, response, "/WEB-INF/jsp/view.jsp");
            }
        }
        catch(Exception e) {
            throw new ServletException(e);
        }
    }

    /** Include a page. */
    private void include(HttpServletRequest request, HttpServletResponse response, String pageName) 
        throws ServletException 
    {
        response.setContentType("text/html");
        if (pageName == null || pageName.length() == 0) {
            // assert
            throw new NullPointerException("null or empty page name");
        }
        try {
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(pageName);
            dispatcher.include(request, response);
        } catch (IOException ioe) {
            throw new ServletException(ioe);
        }
    }

    private synchronized List getFeedDirectory()
    {
        // Get OMPL feed list.

        List feeds = new ArrayList();
        URL opmlUrl;

        try {
            opmlUrl = new URL("http://localhost:8080/atomcal/.opml");
        }
        catch(MalformedURLException e) {
            log("Can't read OPML: malformed URL.");
            return null;
        }

        try {
            DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document opmlDocument = docBuilder.parse(opmlUrl.openStream());
            NodeList outlineNodeList = opmlDocument.getElementsByTagName("outline");
            for(int i = 0; i < outlineNodeList.getLength(); i++) {
                Element outlineElement = (Element)outlineNodeList.item(i);
                String xmlUrlAttribute = outlineElement.getAttribute("xmlUrl");
                try {
                    URL xmlUrl = new URL(xmlUrlAttribute);
                    feeds.add(xmlUrl);
                }
                catch(MalformedURLException e) {
                    log("Bad URL in OPML: " + xmlUrlAttribute + ": " + e.getMessage());
                }
            }
        }
        catch(Exception e) {
            log(e.getMessage());
        }

        log("Read " + feeds.size() + " feeds from OPML.");

        return feeds;
    }
}
