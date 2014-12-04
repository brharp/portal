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
 * "Portions Copyrighted 2006 Lalit Jairath, Brent Harp"
 */

package com.sun.portal.rssportlet;

import com.sun.portal.rssportlet.FeedHelper;
import com.sun.portal.rssportlet.FormNames;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.Iterator;
import java.util.LinkedList;

import javax.portlet.PortletConfig;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.portlet.ReadOnlyException;
import javax.portlet.ValidatorException;


/**
 * This class builds a <code>SettingsBean</code> object.
 */
public class SettingsHandler {
    //
    // portlet preference keys
    //
    private interface PrefKeys {
        public static final String FEEDS = "aggregate_feeds";
        public static final String FEED_OUTPUT_TYPE = "feedOutputType";
        public static final String MAX_DESCRIPTION_LENGTH = "maxDescriptionLength";
        public static final String SHOW_DESC = "showDesc";
        public static final String CACHE_TIMEOUT = "cacheTimeout";
        public static final String NEWWIN = "newWindow";
        public static final String MAX_AGE = "maxAge";
        public static final String MAX_ENTRIES = "maxEntries";
        public static final String MAX_NEWS_PAPER_ENTRIES = "maxNewsPaperEntries";
        public static final String SELECTED_FEED_TYPE = "selectedFeedType";
        public static final String DISABLE_MAX_AGE = "disableMaxAge";
    }
    
    //
    // session attribute keys
    //
    private interface SessionKeys {
        public static final String SELECTED_FEED = "selectedFeed";
    }
    
    private SettingsBean settingsBean;
    private PortletRequest portletRequest = null;
    private PortletConfig portletConfig = null;
    
    /**
     * Initialize the <code>RssPortletBean</code>.
     *
     * This is called after the bean has been set into this handler.
     */
    private void initSettingsBean() {
    	//LinkedList feeds = new LinkedList(Arrays.asList(getPortletPreferences().getValues(PrefKeys.FEEDS, new String[] { } )));
    	String[] aggregateFeeds = getPortletPreferences().getValues(PrefKeys.FEEDS, new String[] { } );

    	//TODO:
    	//aggregateFeeds are now retrieved from local persistent cache 
    	//if user has old links map these to new local links
    	aggregateFeeds = mapLocalPersistentFeeds(aggregateFeeds);
    	
    	String showDesc = getPortletPreferences().getValue(PrefKeys.SHOW_DESC, "off");
    	String feedOutputType = getPortletPreferences().getValue(PrefKeys.SELECTED_FEED_TYPE, "river");
    	//System.out.println("SetHandler feedOutputType***********" + feedOutputType);

    	LinkedList feeds = new LinkedList(Arrays.asList(aggregateFeeds));
        //feeds.add(0,"http://myportico.uoguelph.ca/");
        //campus news: there is at least one news feed for the application
        feeds.add(0,"http://myportico.uoguelph.ca/portal/rss/campusnews.xml");
        //feeds.add(1,"http://feeds.feedburner.com/uoguelph/president");
        
        /*
        Iterator it = feeds.listIterator();
        while (it.hasNext()) {
        	System.out.println("SetHandler feed LinkedList***********" + (String) it.next());
        }*/  
        getSettingsBean().setFeeds(feeds);
        
        int maxAge = getIntPreference(PrefKeys.MAX_AGE, 72);
        getSettingsBean().setMaxAge(maxAge);

        int cacheTimeout = getIntPreference(PrefKeys.CACHE_TIMEOUT, 3600);
        getSettingsBean().setCacheTimeout(cacheTimeout);

        int maxDescriptionLength = getIntPreference(PrefKeys.MAX_DESCRIPTION_LENGTH, 128);
        getSettingsBean().setMaxDescriptionLength(maxDescriptionLength);

        boolean newWindow = getBooleanPreference(PrefKeys.NEWWIN, true);
        getSettingsBean().setNewWindow(newWindow);

        boolean disableMaxAge = getBooleanPreference(PrefKeys.DISABLE_MAX_AGE, true);
        getSettingsBean().setDisableMaxAge(disableMaxAge);
        
        int maxEntries = getIntPreference(PrefKeys.MAX_ENTRIES, 10);
        //System.out.println("Set Handler maxEntries***********" + maxEntries);
        getSettingsBean().setMaxEntries(maxEntries);
        
        int maxNewsPaperEntries = getIntPreference(PrefKeys.MAX_NEWS_PAPER_ENTRIES, 3);
        //System.out.println("Set Handler maxNewsPaperEntries***********" + maxNewsPaperEntries);
        getSettingsBean().setMaxNewsPaperEntries(maxNewsPaperEntries);
        
        String selectedFeedType = getPortletPreferences().getValue(PrefKeys.SELECTED_FEED_TYPE, "river");
        getSettingsBean().setSelectedFeedType(selectedFeedType);
        //System.out.println(" Set hand feedType" +  selectedFeedType);
        
        getSettingsBean().setShowDesc(showDesc);
        //System.out.println(" Set hand show desc" +  showDesc);
        
        getSettingsBean().setLocale(getPortletRequest().getLocale());
        
        //set feed titles and feeds itself
        if (feeds != null && feeds.size() != 0 ) {
            setFeedTitleList(feeds);
        }
        initSelectedFeed();
    }

    private String[] mapLocalPersistentFeeds(String[] aggregateFeeds) {
    	for (int i=0; i<aggregateFeeds.length; i++){
    		String feed = aggregateFeeds[i];
    	     if (feed.equalsIgnoreCase("http://www.uoguelph.ca/cip/rss/feed.cfm?pageID=25")) {
    	    	 aggregateFeeds[i] = "http://myportico.uoguelph.ca/portal/rss/cipexchange.xml"; 
             } else if (feed.equalsIgnoreCase("http://www.uoguelph.ca/cip/rss/feed.cfm?pageID=27")){
            	 aggregateFeeds[i] = "http://myportico.uoguelph.ca/portal/rss/cipfacstaff.xml";
             } else if (feed.equalsIgnoreCase("http://www.uoguelph.ca/cip/rss/feed.cfm?pageID=23")){
            	 aggregateFeeds[i] = "http://myportico.uoguelph.ca/portal/rss/cipstudent.xml";
             } else if (feed.equalsIgnoreCase("http://blogs.uoguelph.ca/ccsnews/blog/default/?flavor=atom")) {
            	 aggregateFeeds[i] = "http://myportico.uoguelph.ca/portal/rss/ccsnews.xml";
             } else if (feed.equalsIgnoreCase("http://feeds.arts.uoguelph.ca/?feed=rss2")) {
            	 aggregateFeeds[i] = "http://myportico.uoguelph.ca/portal/rss/collegeofarts.xml";
             } else if (feed.equalsIgnoreCase("http://feeds.feedburner.com/gryphons/football")) {
            	 aggregateFeeds[i]= "http://myportico.uoguelph.ca/portal/rss/athletics.xml";
             } else if (feed.equalsIgnoreCase("http://www.lib.uoguelph.ca/news/rss/dsp_viewRssFeed.cfm")) {
            	 aggregateFeeds[i] = "http://myportico.uoguelph.ca/portal/rss/libnews.xml";
             } else if (feed.equalsIgnoreCase("http://www.uoguelph.ca/mediarel/ovc/index.xml")) {
            	 aggregateFeeds[i] = "http://myportico.uoguelph.ca/portal/rss/ovcnews.xml";
             } else if (feed.equalsIgnoreCase("http://feeds.feedburner.com/uoguelph/president")) {
            	 aggregateFeeds[i] = "http://myportico.uoguelph.ca/portal/rss/presnews.xml";
             } else if (feed.equalsIgnoreCase("http://www.studentaffairs.uoguelph.ca/reg/rssfeed.cfm")) {
            	 aggregateFeeds[i] = "http://myportico.uoguelph.ca/portal/rss/studaffairs.xml";
             } else if (feed.equalsIgnoreCase("http://www.uoguelph.ca/mediarel/atom.xml")) {
            	 aggregateFeeds[i] = "http://myportico.uoguelph.ca/portal/rss/campusnews.xml";
             } else if (feed.equalsIgnoreCase("http://blogs.uoguelph.ca/cioweb/blog/default/?flavor=rss2")) {
            	 aggregateFeeds[i] = "http://myportico.uoguelph.ca/portal/rss/cionews.xml";
             }  
    	}
    	return aggregateFeeds;
    }
    /**
     * Initialize the <code>selectedFeed</code>
     * field of the <code>RssPortletBean</code>.
     */
    private void initSelectedFeed() {
        // first, try to get from session
        String selectedFeed = (String)getPortletRequest().getPortletSession().getAttribute(SessionKeys.SELECTED_FEED);
        if (selectedFeed == null || selectedFeed.trim().length() == 0) {
            // next, try to get from start feed
            selectedFeed = getSettingsBean().getStartFeed();
        }
        // might be null
        getSettingsBean().setSelectedFeed(selectedFeed);
    	//System.out.println("SetHandler initselectedFeed: "+ selectedFeed);
    }
    
    /**
     * Persist this handler's <code>RssPortletBean</code>, if necessary.
     */
    public void persistSettingsBean(SettingsBean delta) throws ReadOnlyException, IOException, ValidatorException {
        boolean store = false;
        System.out.println("In delta****");
        // selected feed        
        if (delta.getFeeds() != null && delta.getFeedsSize() == 0) {
            // if there are no feeds, set the selected feed to null
            getPortletSession().setAttribute(SessionKeys.SELECTED_FEED, null);                        
        } else if (delta.getSelectedFeed() != null) {
            getPortletSession().setAttribute(SessionKeys.SELECTED_FEED, delta.getSelectedFeed());
        }
        
        // feeds
        LinkedList feeds = null;
        if (delta.getFeeds() != null) {
        	feeds = delta.getFeeds();
        	System.out.println("delta feeds 0 ***********" + (String)feeds.get(0));
        	int numLockedFeeds = 2;
        	for (int i=0; i<numLockedFeeds; i++) {
        		feeds.remove(0);
        	}
    		Iterator it = feeds.listIterator();
            while (it.hasNext()) {
            	System.out.println("delta feeds***********" + (String) it.next());
            }  
        	
            getPortletPreferences().setValues(PrefKeys.FEEDS, (String[])delta.getFeeds().toArray(new String[0]));
            store = true;
        }
        // feed titles
        if (delta.getFeeds() != null && delta.getFeedsSize() != 0) {
            //delta.setTitleList();
        }

        // max age
        if (delta.isMaxAgeSet()) {
            getPortletPreferences().setValue(PrefKeys.MAX_AGE, Integer.toString(delta.getMaxAge()));
            store = true;
        }
        
        // disable max age
        //if (delta.isDisableMaxAgeSet()) {
            getPortletPreferences().setValue(PrefKeys.DISABLE_MAX_AGE, "true");//Boolean.toString(delta.isDisableMaxAge()));
            store = true;
        //}
        
        // max entries
        if (delta.isMaxEntriesSet()) {
            getPortletPreferences().setValue(PrefKeys.MAX_ENTRIES, Integer.toString(delta.getMaxEntries()));
            store = true;
        }
        
        if (delta.isMaxNewsPaperEntriesSet()) {
	        getPortletPreferences().setValue(PrefKeys.MAX_NEWS_PAPER_ENTRIES, Integer.toString(delta.getMaxNewsPaperEntries()));
	        store = true;
    	}
        
        if ( null != delta.getSelectedFeedType()) {
        	getPortletPreferences().setValue(PrefKeys.SELECTED_FEED_TYPE, delta.getSelectedFeedType());
        	store = true;
        }

        if ( null != delta.getShowDesc()) {
        	getPortletPreferences().setValue(PrefKeys.SHOW_DESC, delta.getShowDesc());
        	store = true;
        }
        
        // new window
        if (delta.isNewWindowSet()) {
            getPortletPreferences().setValue(PrefKeys.NEWWIN, Boolean.toString(delta.isNewWindow()));
            store = true;
        }
        
        if (store) {
            getPortletPreferences().store();
        }
    }
    
    /** Get a portlet preference as an int. */
    private int getIntPreference(String key, int def) {
        String s = getPortletPreferences().getValue(key, null);
        int i = def;
        try {
            i = Integer.parseInt(s);
        } catch (NumberFormatException nfe) {
            i = def;
        }
        
        return i;
    }
    
    /** Get a portlet preference as a boolean. */
    private boolean getBooleanPreference(String key, boolean def) {
        Boolean b = Boolean.valueOf(getPortletPreferences().getValue(key, "false"));
        return b.booleanValue();
    }
    
    /** Get the Rss portlet bean. */
    public SettingsBean getSettingsBean() {
        return settingsBean;
    }
    
    /** Set the Rss portlet bean. */
    public void setSettingsBean(SettingsBean settingsBean) {
        this.settingsBean = settingsBean;
        initSettingsBean();
    }
    
    /** Get the portlet request. */
    private PortletRequest getPortletRequest() {
        return portletRequest;
    }
    
    /** Set the portlet request. */
    public void setPortletRequest(PortletRequest portletRequest) {
        this.portletRequest = portletRequest;
    }
    
    /** Get the portlet config. */
    private PortletConfig getPortletConfig() {
        return portletConfig;
    }
    
    /** Set the portlet config. */
    public void setPortletConfig(PortletConfig portletConfig) {
        this.portletConfig = portletConfig;
    }
    
    /** Get the portlet preferences. */
    private PortletPreferences getPortletPreferences() {
        return getPortletRequest().getPreferences();
    }
    
    /** Get the portlet session. */
    private PortletSession getPortletSession() {
        return getPortletRequest().getPortletSession();
    }
    
    /** Set the title to the corresponding feed. */
    private void setFeedTitleList(LinkedList feeds) {
        LinkedList titleList = new LinkedList();
        LinkedList feedList = new LinkedList();
        feedList.add("http://myportico.uoguelph.ca/");
        titleList.add("All My U of G News");
        
        Iterator it = feeds.listIterator();
        while (it.hasNext()) {
            String feed = (String) it.next();
            String title = null;
            if (feed.equalsIgnoreCase("http://myportico.uoguelph.ca/portal/rss/cipexchange.xml")) {
            	feedList.add("http://myportico.uoguelph.ca/portal/rss/cipexchange.xml");
            	titleList.add("CIP Exchange Student News");
            } else if (feed.equalsIgnoreCase("http://myportico.uoguelph.ca/portal/rss/cipfacstaff.xml")){
            	feedList.add("http://myportico.uoguelph.ca/portal/rss/cipfacstaff.xml");
            	titleList.add("CIP Faculty and Staff News");
            } else if (feed.equalsIgnoreCase("http://myportico.uoguelph.ca/portal/rss/cipstudent.xml")){
            	feedList.add("http://myportico.uoguelph.ca/portal/rss/cipstudent.xml");
            	titleList.add("CIP Student News");
            } else if (feed.equalsIgnoreCase("http://myportico.uoguelph.ca/portal/rss/ccsnews.xml")) {
            	feedList.add("http://myportico.uoguelph.ca/portal/rss/ccsnews.xml");
            	titleList.add("CCS News");
            } else if (feed.equalsIgnoreCase("http://myportico.uoguelph.ca/portal/rss/collegeofarts.xml")) {
        		feedList.add("http://myportico.uoguelph.ca/portal/rss/collegeofarts.xml");
        		titleList.add("College of Arts");
            } else if (feed.equalsIgnoreCase("http://myportico.uoguelph.ca/portal/rss/athletics.xml")) {
        		feedList.add("http://myportico.uoguelph.ca/portal/rss/athletics.xml");
        		titleList.add("Guelph Athletics - Football");
            } else if (feed.equalsIgnoreCase("http://myportico.uoguelph.ca/portal/rss/libnews.xml")) {
        		feedList.add("http://myportico.uoguelph.ca/portal/rss/libnews.xml");
        		titleList.add("Library News");
            } else if (feed.equalsIgnoreCase("http://myportico.uoguelph.ca/portal/rss/ovcnews.xml")) {
        		feedList.add("http://myportico.uoguelph.ca/portal/rss/ovcnews.xml");
        		titleList.add("OVC News");
            } else if (feed.equalsIgnoreCase("http://myportico.uoguelph.ca/portal/rss/presnews.xml")) {
        		feedList.add("http://myportico.uoguelph.ca/portal/rss/presnews.xml");
        		titleList.add("Presidents's Blog");
            } else if (feed.equalsIgnoreCase("http://myportico.uoguelph.ca/portal/rss/studaffairs.xml")) {
        		feedList.add("http://myportico.uoguelph.ca/portal/rss/studaffairs.xml");
        		titleList.add("Student Affairs Upcoming Events");
            } else if (feed.equalsIgnoreCase("http://myportico.uoguelph.ca/portal/rss/campusnews.xml")) {
            	feedList.add("http://myportico.uoguelph.ca/portal/rss/campusnews.xml");
            	titleList.add("Campus News");
            } else if (feed.equalsIgnoreCase("http://myportico.uoguelph.ca/portal/rss/cionews.xml")) {
            	feedList.add("http://myportico.uoguelph.ca/portal/rss/cionews.xml");
            	titleList.add("Towards the Information Ecology");
            }  
 
        }
        //reset feed and title lists
        if (feedList.size() != 0) {
            getSettingsBean().setFeeds(feedList);
            getSettingsBean().setTitleList(titleList);
        } else {
            getSettingsBean().setFeeds(null);
            getSettingsBean().setTitleList(null);
        }
    }
}
