package ca.uoguelph.ccs.portal.portlets.tinycal;

import javax.portlet.*;
import java.util.*;
import java.net.*;
import java.io.*;
import javax.xml.parsers.*;
import com.sun.syndication.feed.synd.*;
import com.sun.syndication.io.*;
import org.w3c.dom.*;
import ca.uoguelph.ccs.syndication.feed.module.*;

public class TinyCalPortlet extends GenericPortlet
{
    public static final int APPLICATION_SCOPE = PortletSession.APPLICATION_SCOPE;
    public static final int PORTLET_SCOPE = PortletSession.PORTLET_SCOPE;

    public static final String CALENDAR_ATTRIBUTE = "ca.uoguelph.ccs.portal.portlets.tinycal.CALENDAR";
    public static final int    CALENDAR_SCOPE     = PortletSession.APPLICATION_SCOPE;
    public static final String EVENTS_ATTRIBUTE = "ca.uoguelph.ccs.portal.portlets.tinycal.EVENTS";
    public static final int    EVENTS_SCOPE     = PortletSession.APPLICATION_SCOPE;

    public void init() 
    {
    }
    
    protected void doView(RenderRequest request,
                          RenderResponse response)
        throws PortletException,
               java.io.IOException
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
            PortletSession session = request.getPortletSession();
            session.setAttribute("cal", new CalendarBean(calendar), APPLICATION_SCOPE);
            session.setAttribute("ev", new EventsBean(entries), APPLICATION_SCOPE);
            
            if ("month".equals(request.getParameter("view"))) {
                include(request, response, "/WEB-INF/jsp/month.jsp");
            } else {
                include(request, response, "/WEB-INF/jsp/view.jsp");
            }
        }
        catch(Exception e) {
            throw new PortletException(e);
        }
    }

    protected void doEdit(RenderRequest request, RenderResponse response)
        throws PortletException, IOException
    {
        List model = new LinkedList();
        List feeds = getFeedDirectory();
        PortletPreferences preferences = request.getPreferences();
        String[] selectedFeeds = preferences.getValues("feeds", null);
        List selected = Arrays.asList(selectedFeeds == null ? new String[0] : selectedFeeds);
        for(Iterator i = feeds.iterator(); i.hasNext();) {
            URL feed = (URL)i.next();
            Map entry = new HashMap();
            entry.put("feed", feed.toString());
            if (selected.contains(feed.toString())) {
                entry.put("checked", "checked");
            }
            model.add(entry);
        }
        request.getPortletSession().setAttribute("model", model, APPLICATION_SCOPE);
        
        Map form = new HashMap();
        form.put("submit", response.createActionURL().toString());
        request.getPortletSession().setAttribute("form", form, APPLICATION_SCOPE);

        include(request, response, "/WEB-INF/jsp/edit.jsp");
    }

    /** Include a page. */
    private void include(RenderRequest request, RenderResponse response, String pageName) 
        throws PortletException 
    {
        if (pageName == null || pageName.length() == 0) {
            // assert
            throw new NullPointerException("null or empty page name");
        }
        try {
            PortletRequestDispatcher dispatcher = getPortletContext().getRequestDispatcher(pageName);
            dispatcher.include(request, response);
        } catch (IOException ioe) {
            throw new PortletException(ioe);
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

    private void log(String msg)
    {
        getPortletContext().log(msg);
    }
}
