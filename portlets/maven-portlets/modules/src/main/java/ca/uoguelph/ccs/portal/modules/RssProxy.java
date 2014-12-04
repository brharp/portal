
package ca.uoguelph.ccs.portal.modules;

import java.io.*;
import java.net.URL;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import com.sun.syndication.feed.synd.*;
import com.sun.syndication.io.*;
import com.sun.syndication.fetcher.*;
import com.sun.syndication.fetcher.impl.*;
import org.apache.commons.logging.*;
import org.json.*;

public class RssProxy implements Filter
{
    private FilterConfig filterConfig;
    private Log log;

    public static final String URL = "u";
    public static final String OUTPUT = "o";
    public static final String MIME_TYPE_XML="application/xml; charset=UTF-8";
    public static final String MIME_TYPE_JSON="application/json; charset=UTF-8";
    public static final String DEFAULT_FEED_TYPE = "default.feed.type";
    public static final String OUTPUT_TYPE_JSON = "json";

    private String defaultType;

    private FeedFetcherCache feedInfoCache;
    private FeedFetcher fetcher;
    private FetcherEventListenerImpl listener;

    public void init(FilterConfig filterConfig) throws ServletException
    {
        this.filterConfig = filterConfig;
        this.log = LogFactory.getLog(FeedProxy.class);
        defaultType = filterConfig.getInitParameter(DEFAULT_FEED_TYPE);
        defaultType = (defaultType!=null) ? defaultType : "rss_2.0";
    }

    public void destroy()
    {
    }

    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain)
        throws ServletException,
               java.io.IOException
    {
        try {
            doFilter((HttpServletRequest)request,
                     (HttpServletResponse)response,
                     chain);
        }
        catch(ClassCastException cce) {
            throw new ServletException(cce);
        }
    }

    public void doFilter(HttpServletRequest req,
                         HttpServletResponse resp,
                         FilterChain chain)
        throws ServletException,
               java.io.IOException
    {
        // Get the URL to fetch.
        //String urlParam = req.getParameter(URL);

        CachedHttpResponse wrapper = new CachedHttpResponse(resp);
        chain.doFilter(req, wrapper);

        if (log.isInfoEnabled()) {
            log.info("Parsing HTTP response body.");
        }

        try {
            SyndFeedInput syndFeedInput = new SyndFeedInput();
            SyndFeed feed = syndFeedInput
                .build(new StringReader(wrapper.getResponseBodyAsString()));

            if (log.isInfoEnabled()) {
                log.info("Parsed feed: " + feed.getTitle());
            }

            // Get the requested output format.
            String outputParam = req.getParameter(OUTPUT);
            String outputType = outputParam==null ? defaultType : outputParam;
            if (outputType.equals(OUTPUT_TYPE_JSON)) {
                resp.setContentType(MIME_TYPE_JSON);
                resp.getWriter().write("(");
                resp.getWriter().write(createJSONFeed(feed).toString());
                resp.getWriter().write(")");
                resp.getWriter().flush();
                log.info("Wrote " + outputType + " response.");
            } else {
                resp.setContentType(MIME_TYPE_XML);
                feed.setFeedType(outputType);
                SyndFeedOutput sink = new SyndFeedOutput();
                sink.output(feed, resp.getWriter());
                resp.getWriter().flush();
                log.info("Wrote " + outputType + " response.");
            }
        }
        catch (Exception e) {
            log.error(e);
            e.printStackTrace();
            throw new ServletException(e);
        }
    }

    private JSONObject createJSONFeed(SyndFeed feed) throws JSONException
    {
        JSONObject json = new JSONObject();
        json.put("author", feed.getAuthor());
        json.put("categories", createJSONCategories(feed.getCategories()));
        json.put("copyright", feed.getCopyright());
        json.put("description", feed.getDescription());
        json.put("entries", createJSONEntries(feed.getEntries()));
        json.put("image", createJSONImage(feed.getImage()));
        json.put("link", feed.getLink());
        json.put("publishedDate", createJSONDate(feed.getPublishedDate()));
        json.put("title", feed.getTitle());
        json.put("uri", feed.getUri());
        return json;
    }
    
    private JSONArray createJSONCategories(List list) throws JSONException
    {
        JSONArray json = new JSONArray();
        for (int i = 0; i < list.size(); i++) {
            json.put(createJSONCategory((SyndCategory)list.get(i)));
        }
        return json;
    }

    private JSONObject createJSONCategory(SyndCategory category) throws JSONException
    {
        JSONObject json = new JSONObject();
        json.put("name", category.getName());
        json.put("taxonomyUri", category.getTaxonomyUri());
        return json;
    }

    private JSONArray createJSONEntries(List list) throws JSONException
    {
        JSONArray json = new JSONArray();
        for (int i = 0; i < list.size(); i++) {
            json.put(createJSONEntry((SyndEntry)list.get(i)));
        }
        return json;
    }

    private JSONObject createJSONEntry(SyndEntry entry) throws JSONException
    {
        JSONObject json = new JSONObject();
        json.put("author", entry.getAuthor());
        json.put("categories", createJSONCategories(entry.getCategories()));
        //json.put("contents", createJSONContents(entry.getContents()));
        //json.put("description", createJSONContent(entry.getDescription()));
        json.put("enclosures", createJSONEnclosures(entry.getEnclosures()));
        json.put("link", entry.getLink());
        json.put("links", createJSONLinks(entry.getLinks()));
        json.put("publishedDate", createJSONDate(entry.getPublishedDate()));
        json.put("title", entry.getTitle());
        json.put("updatedDate", createJSONDate(entry.getUpdatedDate()));
        json.put("uri", entry.getUri());
        return json;
    }

    private JSONObject createJSONImage(SyndImage image) throws JSONException
    {
        if (image==null) return null;
        JSONObject json = new JSONObject();
        json.put("description", image.getDescription());
        json.put("link", image.getLink());
        json.put("title", image.getTitle());
        json.put("url", image.getUrl());
        return json;
    }

    private Long createJSONDate(java.util.Date date) throws JSONException
    {
        return date == null ? null : new Long(date.getTime());
    }

    private JSONArray createJSONContents(List list) throws JSONException
    {
        JSONArray json = new JSONArray();
        for (int i = 0; i < list.size(); i++) {
            json.put(createJSONContent((SyndContent)list.get(i)));
        }
        return json;
    }

    private JSONObject createJSONContent(SyndContent content) throws JSONException
    {
        if (content==null) return null;
        JSONObject json = new JSONObject();
        json.put("mode", content.getMode());
        json.put("type", content.getType());
        json.put("value", content.getValue());
        return json;
    }

    private JSONArray createJSONEnclosures(List list) throws JSONException
    {
        JSONArray json = new JSONArray();
        for (int i = 0; i < list.size(); i++) {
            json.put(createJSONEnclosure((SyndEnclosure)list.get(i)));
        }
        return json;
    }

    private JSONObject createJSONEnclosure(SyndEnclosure enclosure) throws JSONException
    {
        if (enclosure==null) return null;
        JSONObject json = new JSONObject();
        json.put("length", enclosure.getLength());
        json.put("type", enclosure.getType());
        json.put("url", enclosure.getUrl());
        return json;
    }

    private JSONArray createJSONLinks(List list) throws JSONException
    {
        JSONArray json = new JSONArray();
        for (int i = 0; i < list.size(); i++) {
            json.put(createJSONLink((SyndLink)list.get(i)));
        }
        return json;
    }

    private JSONObject createJSONLink(SyndLink link) throws JSONException
    {
        if (link==null) return null;
        JSONObject json = new JSONObject();
        json.put("href", link.getHref());
        json.put("hreflang", link.getHreflang());
        json.put("length", link.getLength());
        json.put("rel", link.getRel());
        json.put("title", link.getTitle());
        json.put("type", link.getType());
        return json;
    }

    class FetcherEventListenerImpl implements FetcherListener {
        public void fetcherEvent(FetcherEvent event) {
            String eventType = event.getEventType();
            if (FetcherEvent.EVENT_TYPE_FEED_POLLED.equals(eventType)) {
                log.info("EVENT: Feed Polled. URL = " + event.getUrlString());
            } else if (FetcherEvent.EVENT_TYPE_FEED_RETRIEVED.equals(eventType)) {
                log.info("EVENT: Feed Retrieved. URL = " + event.getUrlString());
            } else if (FetcherEvent.EVENT_TYPE_FEED_UNCHANGED.equals(eventType)) {
                log.info("EVENT: Feed Unchanged. URL = " + event.getUrlString());
            }
        }
    }
}
