/*
 * FeedInfoCache.java
 *
 * Created on 16 March 2006, 10:13
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.sun.portal.rssportlet;

import com.sun.syndication.fetcher.impl.FeedFetcherCache;
import com.sun.syndication.fetcher.impl.SyndFeedInfo;
import java.io.Serializable;
import java.net.URL;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Element;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author ismjml
 */
public class SyndFeedInfoCache implements FeedFetcherCache, Serializable{
    
    private static final Log logger = LogFactory.getLog(SyndFeedInfoCache.class);
    private Cache cache;
    
    /**
     * sets cache to be used
     */
    public void setCache(Cache cache) {
        this.cache = cache;
    }
    
    /**
     * @see extensions.io.FeedFetcherCache#getFeedInfo(java.net.URL)
     */
    public SyndFeedInfo getFeedInfo(URL feedUrl) {
        logger.debug("getFeedInfo");
        SyndFeedInfo sfi = null;
        try {
            final Element element = cache.get(feedUrl.toExternalForm());
            if ((element != null)) {
                sfi = (SyndFeedInfo) element.getValue();
            }
        } catch (CacheException e) {
            logger.error("getFeedInfo ERROR");
        }
        return  sfi;
    }
    
    /**
     * @see extensions.io.FeedFetcherCache#setFeedInfo(java.net.URL, extensions.io.SyndFeedInfo)
     */
    public void setFeedInfo(URL feedUrl, SyndFeedInfo syndFeedInfo) {
        logger.debug("setFeedInfo");
        cache.put(new Element(feedUrl.toExternalForm(), syndFeedInfo));
    }
    
    
}
