/*
 * HttpURLFeedFetcherWithCache.java
 *
 * Created on 16 March 2006, 10:14
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.sun.portal.rssportlet;

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.fetcher.FetcherEvent;
import com.sun.syndication.fetcher.FetcherException;
import com.sun.syndication.fetcher.impl.AbstractFeedFetcher;
import com.sun.syndication.fetcher.impl.FeedFetcherCache;
import com.sun.syndication.fetcher.impl.ResponseHandler;
import com.sun.syndication.fetcher.impl.SyndFeedInfo;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.zip.GZIPInputStream;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Element;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author ismjml
 *
 *<pre>
 * if (feed url is cached in URLCACHE)
 * {
 *      fetch existing feed from FEEDCACHE.
 * }
 * else
 * {
 *      get connection response code
 *      if (304 - not modified)
 *      {
 *           fetch from FEEDCACHE
 *           re-cache url in URLCACHE
 *      }
 *      else
 *      {
 *          fetch feed afresh
 *          cache url in URLCACHE,
 *          cache feed in FEEDCACHE and
 *          cache feed info in FEEDINFOCACHE.
 *      }
 * }
 *</pre>
 */

public class SyndFeedFetcherWithCache extends AbstractFeedFetcher  {

    private static final Log logger = LogFactory.getLog(SyndFeedFetcherWithCache.class);
    private static Cache urlCache = null;
    private static Cache feedCache = null;

    private FeedFetcherCache feedInfoCache;

    public void setFeedFetcherCache(FeedFetcherCache feedFetcherCache){
        this.feedInfoCache = feedFetcherCache;
    }

    /**
     * sets url cache to be used
     */
    public void setUrlCache(Cache cache) {
        this.urlCache = cache;
    }

    /**
     * sets feed cache to be used
     */
    public void setFeedCache(Cache cache) {
        this.feedCache = cache;
    }


    /**
     * Retrieve a feed over HTTP
     *
     * @param feedUrl A non-null URL of a RSS/Atom feed to retrieve
     * @return A {@link com.sun.syndication.feed.synd.SyndFeed} object
     * @throws IllegalArgumentException if the URL is null;
     * @throws IOException if a TCP error occurs
     * @throws FeedException if the feed is not valid
     * @throws FetcherException if a HTTP error occurred
     */

    public SyndFeed retrieveFeed(URL feedUrl) throws IllegalArgumentException, IOException, FeedException, FetcherException {

        if (feedUrl == null) {
            throw new IllegalArgumentException("null is not a valid URL");
        }

        try {
            final Element urlCached = urlCache.get(feedUrl.toExternalForm());
            if ((urlCached == null) || urlCache.isExpired(urlCached)) {

                logger.debug("Retrieving Feed: " + feedUrl.toExternalForm() + " Time is " + new Date());
                /*
                SyndFeed feed = super.retrieveFeed(url);
                cache.put(new Element(url.toExternalForm(), (SyndFeedImpl)feed));
                return feed;
                 */
                final URLConnection connection = feedUrl.openConnection();
                if (feedInfoCache != null) {
                    logger.debug("*** FEED INFO CACHE NOT NULL");

                    SyndFeedInfo syndFeedInfo = feedInfoCache.getFeedInfo(feedUrl);
                    setRequestHeaders(connection, syndFeedInfo);
                    connection.connect();
                    fireEvent(FetcherEvent.EVENT_TYPE_FEED_POLLED, connection);
                    if (connection instanceof HttpURLConnection) {
                        final HttpURLConnection httpConnection = ((HttpURLConnection) connection);
                        //httpConnection.setInstanceFollowRedirects(true); // this is true by default, but can be changed on a claswide basis
                        if (syndFeedInfo == null) {
                            logger.debug("*** SYND FEED INFO IS NULL");
                            // this is a feed that hasn't been retrieved before
                            syndFeedInfo = new SyndFeedInfo();
                            retrieveAndCacheFeed(feedUrl, syndFeedInfo, httpConnection);
                        } else {
                            // check the response code
                            final int responseCode = httpConnection.getResponseCode();

                            handleErrorCodes(httpConnection.getResponseCode());

                            if (responseCode != HttpURLConnection.HTTP_NOT_MODIFIED) {
                                // the response code is not 304 NOT MODIFIED
                                // This is either because the feed server
                                // does not support condition gets
                                // or because the feed hasn't changed
                                retrieveAndCacheFeed(feedUrl, syndFeedInfo, httpConnection);
                            } else {
                                // the feed does not need retrieving
                                logger.debug("Re-Caching URL: " + feedUrl.toExternalForm());
                                urlCache.put(new Element(feedUrl.toExternalForm(), (SyndFeedImpl) syndFeedInfo.getSyndFeed() ));
                                fireEvent(FetcherEvent.EVENT_TYPE_FEED_UNCHANGED, connection);
                            }
                        }
                    } else {
                        fireEvent(FetcherEvent.EVENT_TYPE_FEED_RETRIEVED, connection);
                    }

                    return syndFeedInfo.getSyndFeed();
                } else {
                    fireEvent(FetcherEvent.EVENT_TYPE_FEED_POLLED, connection);
                    try {
                        final InputStream inputStream = feedUrl.openStream();
                        return getSyndFeedFromStream(inputStream, connection);
                    } catch (java.io.IOException e) {
                        handleErrorCodes(((HttpURLConnection)connection).getResponseCode());
                    }
                    // we will never actually get to this line
                    return null;
                }
            } else {
                logger.debug("Using Cached Feed: " + feedUrl.toExternalForm() + " Time is " + new Date());
                final Element feedCached = feedCache.get(feedUrl.toExternalForm());
                fireEvent("FEED_CACHED", feedUrl.toExternalForm());
                SyndFeed feed = null;
                if (feedCached != null) {
                    feed = (SyndFeed) feedCached.getValue();
                }
                return feed;
            }
        } catch (FetcherException e) {
            logger.error("An error retrieving url " + feedUrl.toExternalForm(), e);
            fireEvent("HTTP_ERROR", feedUrl.toExternalForm());
            return null;
        } catch (CacheException e) {
            logger.error("An error caching url " + feedUrl.toExternalForm(), e);
            throw new FetcherException(e);
        }
    }

    protected void retrieveAndCacheFeed(URL feedUrl, SyndFeedInfo syndFeedInfo, URLConnection connection) throws IllegalArgumentException, FeedException, FetcherException, IOException {
        logger.debug("*** retrieveAndCacheFeed");

        //if (connection instanceof HttpURLConnection) {
        //HttpURLConnection httpConnection = (HttpURLConnection)connection;
        //handleErrorCodes(httpConnection.getResponseCode());
        //}

        logger.debug("Caching URL: " + feedUrl.toExternalForm());

        resetFeedInfo(feedUrl, syndFeedInfo, (HttpURLConnection)connection);

        urlCache.put(new Element(feedUrl.toExternalForm(), (SyndFeedImpl) syndFeedInfo.getSyndFeed() ));
        feedCache.put(new Element(feedUrl.toExternalForm(), (SyndFeedImpl) syndFeedInfo.getSyndFeed() ));

        // resetting feed info in the cache
        // could be needed for some implementations
        // of FeedFetcherCache (eg, distributed HashTables)
        if (feedInfoCache != null) {
            feedInfoCache.setFeedInfo(feedUrl, syndFeedInfo);
        }
    }

    private SyndFeed getSyndFeedFromStream(InputStream inputStream, URLConnection connection) throws IOException, IllegalArgumentException, FeedException {
        BufferedInputStream is;
        final SyndFeed feed;
        if (("gzip").equalsIgnoreCase(connection.getContentEncoding())) {
            // handle gzip encoded content
            is = new BufferedInputStream(new GZIPInputStream(inputStream));
        } else {
            is = new BufferedInputStream(inputStream);
        }
        final BufferedReader reader = new BufferedReader(new InputStreamReader(is,ResponseHandler.getCharacterEncoding(connection)));

        final SyndFeedInput input = new SyndFeedInput();

        feed = input.build(reader);
        fireEvent(FetcherEvent.EVENT_TYPE_FEED_RETRIEVED, connection);
        is.close();
        return feed;
    }

    /** TAKEN directly from HttpURLFeedFetcher Rome Fetcher version 0.7
     * <p>Set appropriate HTTP headers, including conditional get and gzip encoding headers</p>
     *
     * @param connection A URLConnection
     * @param syndFeedInfo The SyndFeedInfo for the feed to be retrieved. May be null
     */
    protected void setRequestHeaders(URLConnection connection, SyndFeedInfo syndFeedInfo) {
        if (syndFeedInfo != null) {
            // set the headers to get feed only if modified
            // we support the use of both last modified and eTag headers
            if (syndFeedInfo.getLastModified() != null) {
                Object lastModified = syndFeedInfo.getLastModified();
                if (lastModified instanceof Long) {
                    connection.setIfModifiedSince(((Long)syndFeedInfo.getLastModified()).longValue());
                }
            }
            if (syndFeedInfo.getETag() != null) {
                connection.setRequestProperty("If-None-Match", syndFeedInfo.getETag());
            }

        }
        // header to retrieve feed gzipped
        connection.setRequestProperty("Accept-Encoding", "gzip");

        // set the user agent
        connection.addRequestProperty("User-Agent", getUserAgent());

        if (isUsingDeltaEncoding()) {
            connection.addRequestProperty("A-IM", "feed");
        }
    }

    /** TAKEN directly from HttpURLFeedFetcher Rome Fetcher version 0.7
     */
    protected void resetFeedInfo(URL orignalUrl, SyndFeedInfo syndFeedInfo, HttpURLConnection connection) throws IllegalArgumentException, IOException, FeedException {
        // need to always set the URL because this may have changed due to 3xx redirects
        syndFeedInfo.setUrl(connection.getURL());

        // the ID is a persistant value that should stay the same even if the URL for the
        // feed changes (eg, by 3xx redirects)
        syndFeedInfo.setId(orignalUrl.toString());

        // This will be 0 if the server doesn't support or isn't setting the last modified header
        syndFeedInfo.setLastModified(new Long(connection.getLastModified()));

        // This will be null if the server doesn't support or isn't setting the ETag header
        syndFeedInfo.setETag(connection.getHeaderField("ETag"));

        // get the contents
        InputStream inputStream = null;
        try {
            inputStream = connection.getInputStream();
            SyndFeed syndFeed = getSyndFeedFromStream(inputStream, connection);

            String imHeader = connection.getHeaderField("IM");
            if (isUsingDeltaEncoding() && (imHeader!= null && imHeader.indexOf("feed") >= 0) && (feedInfoCache != null) && connection.getResponseCode() == 226) {
                // client is setup to use http delta encoding and the server supports it and has returned a delta encoded response
                // This response only includes new items
                SyndFeedInfo cachedInfo = feedInfoCache.getFeedInfo(orignalUrl);
                if (cachedInfo != null) {
                    SyndFeed cachedFeed = cachedInfo.getSyndFeed();

                    // set the new feed to be the orginal feed plus the new items
                    syndFeed = combineFeeds(cachedFeed, syndFeed);
                }
            }

            syndFeedInfo.setSyndFeed(syndFeed);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }


}
