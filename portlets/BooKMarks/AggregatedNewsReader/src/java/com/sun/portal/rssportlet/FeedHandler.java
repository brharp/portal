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
 */

package com.sun.portal.rssportlet;

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Comparator;

/**
 * This class handles builds a <code>FeedBean</code>
 * object.
 */
public class FeedHandler {
    public static class EntryPubDateComparator implements Comparator {
        public int compare(Object o1, Object o2) {
            SyndEntry e1 = (SyndEntry)o1;
            SyndEntry e2 = (SyndEntry)o2;
            
            Date pub1 = e1.getPublishedDate();
            Date pub2 = e2.getPublishedDate();
            
            if (pub1 == null && pub2 == null) {
                return 0;
            }
            if (pub1 == null) {
                return 1;
            }
            if (pub2 == null) {
                return -1;
            }
            
            return pub2.compareTo(pub1);
        }
        
        public boolean equals(Object o) {
            if (o instanceof EntryPubDateComparator) {
                return true;
            }
            return false;
        }
    }
    
    private static final HTMLCleaner HTML_CLEANER = new HTMLCleaner();
        
    private SettingsBean settingsBean;
    private FeedBean feedBean;
    
    private Resources resources;
    
    
    private void initFeedBean() throws IOException, FeedException {
    	//System.out.println("****Feed Handler init feedbean****");
        initFeed();
        initFeedEntries();
        initFeedDescription();
    }
        
    /** Should the feed entry be included in the display? */
    private boolean isInTime(SyndEntry entry) {
        // if max age is disabled, everything is in time
        if (getSettingsBean().isDisableMaxAge()) {
            return true;
        }
        Date pubDate = entry.getPublishedDate();
        if (pubDate == null) {
            // assume in time if there's no pub date
            return true;
        }
        
        long now = System.currentTimeMillis();
        long pubTime = pubDate.getTime();
        long maxTime = (long)getSettingsBean().getMaxAge()*60*60*1000;
        
        if ((pubTime + maxTime) > now) {
            return true;
        }
        
        return false;
    }
    
    /**
     * Initialize the bean's feed entries
     *
     * The entries are filtered based on their published time, cleaned of all
     * HTML, and truncated to a maximum length.
     */
    private void initFeedEntries() throws IOException, FeedException {
    	//System.out.println("****Feed Handler init feedEntries****");
        List feedEntries;
        SyndFeed feed = getFeedBean().getFeed();
        if (feed == null) {
            feedEntries = Collections.EMPTY_LIST;
        } else {
            feedEntries = new ArrayList();
            List entries = feed.getEntries();
            int maxEntries = 0;            
            String selectedFeedType = getSettingsBean().getSelectedFeedType();
            //System.out.println("Feed Handler selectedFeedType" + selectedFeedType);
            //System.out.println("Feed Handler selectedFeed" + getSettingsBean().getSelectedFeed());
            
            if ("newspaper".equalsIgnoreCase(selectedFeedType) &&
            	"http://myportico.uoguelph.ca/".equals(getSettingsBean().getSelectedFeed())) {
	            	int numFeeds = getSettingsBean().getFeedsSize();
	            	maxEntries = getSettingsBean().getMaxNewsPaperEntries() * numFeeds;
            } else {
            	maxEntries = getSettingsBean().getMaxEntries();
            }
            System.out.println("Feed Handler maxEntries" + maxEntries);
            int numEntries = 0;
            for (Iterator i = entries.iterator(); numEntries < maxEntries && i.hasNext(); ) {
                SyndEntry entry = (SyndEntry)i.next();
                // test if the entry should be displayed based on its publish time
                if (isInTime(entry)) {
                    // filter tags out of description
                    SyndContent desc = entry.getDescription();
                    if (desc != null) {
                        String d = desc.getValue();
                        //System.out.println("****Feed Handler init print d****" + d);
                        d = HTML_CLEANER.clean(d);
                        int maxLength = getSettingsBean().getMaxDescriptionLength();
                        if (d.length() > maxLength) {
                            // truncate description
                            d = d.substring(0, maxLength-1);
                            // add "more" link to indicate that the description
                            // has been truncated
                            d += " <a target=\"newsriver\" href=\"" + entry.getLink() + "\">" + resources.get("more") + "</a>";
                        }
                        desc.setValue(d);
                        entry.setDescription(desc);
                    }
                    // keep track that we don't go over max entries
                    numEntries++;
                    feedEntries.add(entry);
                }
            }
        }
        //Collections.sort(feedEntries, new EntryPubDateComparator());        
        getFeedBean().setFeedEntries(feedEntries);
    }
    
    /**
     * Initialize the feed description
     *
     * The feed description is has all HTML filtered out, and is possibly
     * truncated to fit within the maximum description length.
     */
    private void initFeedDescription() throws IOException, FeedException {
    	//System.out.println("****Feed Handler init feedDescr****");
        SyndFeed feed = getFeedBean().getFeed();
        String desc = feed.getDescription();
        //System.out.println("****Feed Handler init feedDescr****" + desc);
        if (desc != null) {
            desc = HTML_CLEANER.clean(desc);
            int maxLength = getSettingsBean().getMaxDescriptionLength();
            if (desc.length() > maxLength) {
                desc = desc.substring(0, maxLength-1);
            }
        }
        
        getFeedBean().setFeedDescription(desc);
    }
    
    /**
     * Initialize the ROME feed object.
     */
    private void initFeed() throws IOException, FeedException {
        // delegate to the feed helper to get from cache
        SyndFeed feed = FeedHelper.getInstance().getFeed(getSettingsBean());
    	//System.out.println("****Feed Handler initfeed()****");
    	//SyndFeed feed = FeedHelper.getFeed(getSettingsBean());
        getFeedBean().setFeed(feed);
    }
    
        
    /** Get the Rss portlet bean. */
    public SettingsBean getSettingsBean() {
        return settingsBean;
    }
    
    /** Set the Rss portlet bean. */
    public void setSettingsBean(SettingsBean settingsBean) {
        this.settingsBean = settingsBean;
        resources = new Resources("com.sun.portal.rssportlet.FeedHandler", settingsBean.getLocale());
    }
    
    /** Get the feed bean object. */
    public FeedBean getFeedBean() {
        return feedBean;
    }

    /** 
     * Set the feed bean. 
     * 
     * This operation has the side effect of causing the feed bean to be 
     * initialized.
     */
    public void setFeedBean(FeedBean feedBean) throws IOException, FeedException {
        this.feedBean = feedBean;
        initFeedBean();
    }
}
