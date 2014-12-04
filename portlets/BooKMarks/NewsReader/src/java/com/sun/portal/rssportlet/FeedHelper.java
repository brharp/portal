/*
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the License).  You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the license at
 * http://www.sun.com/cddl/cddl.html or
 * at portlet-repository/CDDLv1.0.txt.
 * See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL
 * Header Notice in each file and include the License file
 * at portlet-repository/CDDLv1.0.txt. 
 * If applicable, add the following below the CDDL Header,
 * with the fields enclosed by brackets [] replaced by
 * you own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * 
 * "Portions Copyrighted 2006 Lalit Jairath, Barbara Edwards, Brent Harp, Peter McCaskell"
 */

package com.sun.portal.rssportlet;

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.xml.sax.InputSource;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.*; 
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.MalformedURLException;
import com.sun.syndication.io.ParsingFeedException;
/**
 * This class manages a cache of ROME feeds.
 */
public class FeedHelper {
	private static final Log log = LogFactory.getLog(FeedHelper.class);
	//set the threshold params for HttpClient
	private static final int CONNECTION_TIMEOUT = 2000; //mSec
	private static final int SOCKET_TIMEOUT = 2000; //mSec
	
	// singleton instance
	private static FeedHelper feedHelper = new FeedHelper();

    // 
    // sync the map, not the method. 
    // if the method is sync'd, we end up spinning whenever a feed host
    // fails to respond. with the map sync'd, we still may fetch
    // feeds twice under some extreme race condition, but that is
    // very unlikely and will not cause data corruption.
    //
    private Map feeds = Collections.synchronizedMap(new HashMap());
    	
    /**
     * This class is the cached representation of a ROME feed.
     */
    private static final class FeedElement {
        private SyndFeed feed = null;
        private long cacheTime;
        private long timeout;
        
        public FeedElement(SyndFeed feed, int timeout) {
            this.feed = feed;
            this.cacheTime = System.currentTimeMillis();
            this.timeout = timeout * 1000;
        }
        
        public SyndFeed getFeed() {
            return feed;
        }
        
        public boolean isExpired() {
            // negative timeout means that the cached element never expires
            if (timeout < 0) {
                return false;
            }
            
            // otherwise, is the time cached plus the timeout still
            // less than the current time? if so, then the cache
            // has not expired
            if ((cacheTime + timeout) < System.currentTimeMillis()) {
                return true;
            }
            
            return false;
        }
    }
	
	private FeedHelper() {
		// nothing, cannot be called
	}
	
	/**
	 * Get the feed handler singleton instance.
	 */
	public static FeedHelper getInstance() {
		return feedHelper;
	}
    
    /**
     * Get the ROME SyndFeed object for the specified feed. The object may come
     * from a cache; the data in the feed may not be read at the time
     * this method is called.
     *
     * The <code>RssPortletBean</code> object is used to identify the feed
     * of interest, and the timeout value to be used when managing this
     * feed's cached value.
     *
     * @param bean an <code>RssPortletBean</code> object that describes
     * the feed of interest, and the cache timeout value for the feed.
     * @return a ROME <code>SyndFeed</code> object encapsulating the
     * feed specified by the URL.
     */
    public SyndFeed getFeed(SettingsBean bean, String selectedFeed) throws IOException, FeedException {
        SyndFeed feed = null;
        FeedElement feedElement = (FeedElement)feeds.get(selectedFeed);
        
        if (feedElement != null && !feedElement.isExpired()) {
            feed = feedElement.getFeed();
        } else {
        	GetMethod httpget = null;
        	try {
				SyndFeedInput input = new SyndFeedInput();

				HttpClient httpclient = new HttpClient();
				httpclient.getHttpConnectionManager().getParams().setConnectionTimeout(CONNECTION_TIMEOUT);
				// SO_TIMEOUT -- timeout for blocking reads
				httpclient.getHttpConnectionManager().getParams().setSoTimeout(SOCKET_TIMEOUT);
				
				httpget = new GetMethod(selectedFeed);
				
				//httpget.getParams().setParameter("http.socket.timeout", new Integer(20000));
				// Internally the parameter collections will be linked together
				// by performing the following operations: 
				// hostconfig.getParams().setDefaults(httpclient.getParams());
				// httpget.getParams().setDefaults(hostconfig.getParams());
				//httpclient.executeMethod(hostconfig, httpget);
				// Execute the method.
				
				int statusCode = httpclient.executeMethod( httpget );
				if (statusCode != HttpStatus.SC_OK) {
					log.info("Method failed: " + httpget.getStatusLine());
				}
				// Read the response body.
				InputSource src = new InputSource(httpget.getResponseBodyAsStream());
				// Deal with the response.
				// Use caution: ensure correct character encoding and is not binary data
				feed = input.build(src);
				//
				// only cache the feed if the cache timeout is not equal to 0
				// a cache timeout of 0 means "don't cache"
				//
				int timeout = bean.getCacheTimeout();
				if (timeout != 0) {
					putFeed(selectedFeed, feed, timeout);
				}
        	}
	        catch(MalformedURLException mfurlex){
				log.info("MalformedURLException: "+ mfurlex.getMessage());
				mfurlex.printStackTrace();
				throw new IOException("MalformedURLException: " + mfurlex.getMessage());
			}
			catch(HttpException httpex){
				log.info("Fatal protocol violation: " + httpex.getMessage());
				httpex.printStackTrace();
				throw new IOException("Fatal protocol violation: " + httpex.getMessage());
        	} 
			catch (IllegalArgumentException iae) {
				log.info("IllegalArgumentException: " + iae.getMessage());
				iae.printStackTrace();
				throw new IOException("IllegalArgumentException: " + iae.getMessage());
        	} 
			catch(IOException ioe){
				log.info("Fatal transport error: " + ioe.getMessage());
				ioe.printStackTrace();
				throw new IOException("Fatal transport error: " + ioe.getMessage());
			}
			catch(ParsingFeedException parsingfeedex) {
				log.info("ParsingFeedException: " + parsingfeedex.getMessage());
				parsingfeedex.printStackTrace();
				throw new FeedException("ParsingFeedException: " + parsingfeedex.getMessage());
			}
			catch(FeedException feedex){
				log.info("FeedException: "+ feedex.getMessage());
				feedex.printStackTrace();
				throw new FeedException ("FeedException: "+ feedex.getMessage());
			}
			catch (Exception ex) {
				log.info("Exception ERROR: "+ ex.getMessage());
				ex.printStackTrace();
			} finally {
				// Release the connection.
				httpget.releaseConnection();
			}
        }
        return feed;        
    }
	
	/**
	 * Get the ROME SyndFeed object for the feed specified by the 
	 * SettingsBean's selectedFeed field.
	 */
    public SyndFeed getFeed(SettingsBean bean) throws IOException, FeedException {
		return getFeed(bean, bean.getSelectedFeed());
	}
    
    /**
     * Put a ROME feed into the cache.
     * This method must be called from within a synchronzied block.
     */
    private void putFeed(String url, SyndFeed feed, int timeout) {
        FeedElement feedElement = new FeedElement(feed, timeout);
        feeds.put(url, feedElement);
    }
}
