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

import com.sun.portal.rssportlet.FeedHelper;

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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class builds a <code>SettingsBean</code> object.
 */
public class SettingsHandler {
    //
    // portlet preference keys
    //
    private interface PrefKeys {

        public static final String USER_FEEDS = "user_feeds";
        //no. of times new feeds have been published through this appliaction
        public static final String DEFAULT_FEEDS_LEVEL = "default_feeds_level";
        
        public static final String START_FEED = "start_feed";
        public static final String MAX_DESCRIPTION_LENGTH = "maxDescriptionLength";
        public static final String CACHE_TIMEOUT = "cacheTimeout";
        public static final String NEWWIN = "newWindow";
        public static final String MAX_AGE = "maxAge";
        public static final String MAX_ENTRIES = "maxEntries";
        public static final String DISABLE_MAX_AGE = "disableMaxAge";
    }
    
    //
    // session attribute keys
    //
    private interface SessionKeys {
        public static final String SELECTED_FEED = "selectedFeed";
        public static final String DEFAULT_FEEDS_LEVEL = "default_feeds_level";
    }

    public static final String PROXY = "/portal/webProxy?u=";
    public static final String MANDATE_FEEDS = "mandate_feeds";
    public static final String ROLE_FEEDS = "role_feeds";
    
    public static final String GUEST_FEEDS = "guest_feeds";
    public static final String STUDENT_FEEDS = "student_feeds";
    public static final String GRAD_STUDENT_FEEDS = "grad_student_feeds";
    public static final String STAFF_FEEDS = "staff_feeds";
    public static final String FACULTY_FEEDS = "faculty_feeds";

    public static final String DEFAULT_FEEDS = "default_feeds";
    public static final String SESSION_DEFAULT_FEEDS = "session_default_feeds";
    
    private SettingsBean settingsBean;
    private PortletRequest portletRequest = null;
    private PortletConfig portletConfig = null;
    
    private static String[] mandate_feeds = null;
    private static String[] role_feeds = null;
    private static List default_feeds = null;
    
    private static final Log log = LogFactory.getLog(SettingsHandler.class);
    
    /**
     * Initialize the <code>RssPortletBean</code>.
     *
     * This is called after the bean has been set into this handler.
     */
    private void initSettingsBean() {
    	PortletSession psession = getPortletRequest().getPortletSession();
    	//mandatory feeds
    	mandate_feeds = (String[])psession.getAttribute(MANDATE_FEEDS);
    	
    	//organizational feeds
    	role_feeds = (String[])psession.getAttribute(ROLE_FEEDS);
    	if (role_feeds == null) {
	        Map userInfo = (Map) portletRequest.getAttribute(PortletRequest.USER_INFO);
	        String organizationalStatus = (String)userInfo.get("userRole");
	        
	        String userRoleFeed = null;
	        if ("Student".equalsIgnoreCase(organizationalStatus)) {
	            userRoleFeed = STUDENT_FEEDS;
	        } else if ("GradStudent".equalsIgnoreCase(organizationalStatus)) {
	        	userRoleFeed = GRAD_STUDENT_FEEDS;            
	        } else if ("Employee:Staff".equalsIgnoreCase(organizationalStatus)) {
	        	userRoleFeed = STAFF_FEEDS;        
	        } else if ("Employee:Faculty".equalsIgnoreCase(organizationalStatus)) {
	        	userRoleFeed = FACULTY_FEEDS;    
	        } else if ("Guest".equalsIgnoreCase(organizationalStatus)) {
	        	userRoleFeed = GUEST_FEEDS;    
	        } else {
	        	userRoleFeed = GUEST_FEEDS;
	        }
	        role_feeds = (String[])psession.getAttribute(userRoleFeed);
	        psession.setAttribute(ROLE_FEEDS, role_feeds);
    	}

        //these are the feeds pushed at discrete time levels in the application
        //the level (horizon) of feeds that are pushed to the user and 
        //seen by the user are tracked in user pref. attribute default_feeds_level
    	default_feeds = (List)psession.getAttribute(DEFAULT_FEEDS);
    	String[] sessionDefaultFeeds = null;
    	if (null != default_feeds && default_feeds.size() != 0) {
    		int default_feeds_level = 1; //every user gets some pushed feeds 
    		String feedLevel = (String)psession.getAttribute(SessionKeys.DEFAULT_FEEDS_LEVEL);
    		if ( null != feedLevel) {
    			try {
    				default_feeds_level = Integer.parseInt( feedLevel);
    			} catch (NumberFormatException nfe) {
    				default_feeds_level = 1;
    			}
    		} else {
    			default_feeds_level = getIntPreference(PrefKeys.DEFAULT_FEEDS_LEVEL, 1);
    			//stote in session and pref.
    			psession.setAttribute(SessionKeys.DEFAULT_FEEDS_LEVEL, String.valueOf(default_feeds.size()));
    		}

    		sessionDefaultFeeds = (String[])psession.getAttribute(SESSION_DEFAULT_FEEDS);
    		if (null != sessionDefaultFeeds && sessionDefaultFeeds.length != 0) {
    			sessionDefaultFeeds = new String[] {};
    			Iterator it = default_feeds.listIterator();
    			int counter = 1;
    			int newDefaultFeeds = 0;
    			while (it.hasNext()) {
    				if (counter >= default_feeds_level) {
    					String[] defaultFeeds = (String[]) it.next();
    					for (int i=0; i<defaultFeeds.length; i++) {
    						sessionDefaultFeeds[newDefaultFeeds] =defaultFeeds[i];
    						newDefaultFeeds++;
    					}
    				}
    				counter++;
    			}
    			psession.setAttribute(SESSION_DEFAULT_FEEDS, sessionDefaultFeeds);
    		}
    	}

        //user selected feeds
        LinkedList feeds = new LinkedList(Arrays.asList(getPortletPreferences().getValues(PrefKeys.USER_FEEDS, new String[] { } )));
        feeds = stripDelimiter(feeds);
        //add mandatory+role feeds to feeds list
        if ( null != role_feeds) {
			if (log.isDebugEnabled()) {
				log.debug("SetHandler feed role_feeds.length***********" + role_feeds.length);
			}
	        for (int i=0; i<role_feeds.length; i++) {
	        	feeds.add(i, role_feeds[i]);
	        }
        }
        if ( null != mandate_feeds) {
			if (log.isDebugEnabled()) {
				log.debug("SetHandler feed mandate_feeds.length***********" + mandate_feeds.length);
			}
	        for (int i=0; i<mandate_feeds.length; i++) {
	        	feeds.add(i, mandate_feeds[i]);
	        }
        }
        
        if ( null != sessionDefaultFeeds && sessionDefaultFeeds.length != 0) {
			if (log.isDebugEnabled()) {
				log.debug("SetHandler feed mandate_feeds.length***********" + sessionDefaultFeeds.length);
			}
	        for (int i=0; i<sessionDefaultFeeds.length; i++) {
	        	feeds.add(i, sessionDefaultFeeds[i]);
	        }
        }
		if (log.isDebugEnabled()) {
	        Iterator it = feeds.listIterator();
	        while (it.hasNext()) {
	        	log.debug("SettingsHandler feed LinkedList***********" + (String) it.next());
	        }  
		}
        getSettingsBean().setFeeds(feeds);
        int maxAge = getIntPreference(PrefKeys.MAX_AGE, 72);
        getSettingsBean().setMaxAge(maxAge);
        int cacheTimeout = getIntPreference(PrefKeys.CACHE_TIMEOUT, 3600);
        getSettingsBean().setCacheTimeout(cacheTimeout);
        int maxDescriptionLength = getIntPreference(PrefKeys.MAX_DESCRIPTION_LENGTH, 512);
        getSettingsBean().setMaxDescriptionLength(maxDescriptionLength);
        boolean newWindow = getBooleanPreference(PrefKeys.NEWWIN, false);
        getSettingsBean().setNewWindow(newWindow);
        boolean disableMaxAge = getBooleanPreference(PrefKeys.DISABLE_MAX_AGE, false);
        getSettingsBean().setDisableMaxAge(disableMaxAge);
        int maxEntries = getIntPreference(PrefKeys.MAX_ENTRIES, 5);
        getSettingsBean().setMaxEntries(maxEntries);
        
        if (feeds != null && feeds.size() != 0 ) {
	        String startFeed = getPortletPreferences().getValue(PrefKeys.START_FEED, (String)feeds.get(0));
	        System.out.println("SettingsHandler PrefKeys.START_FEED ***********" + startFeed);
	        if (startFeed != null) {
	        	startFeed = stripDelimiter(startFeed);
	        }
	        getSettingsBean().setStartFeed(startFeed);
        }
        
        initSelectedFeed();
        
        getSettingsBean().setLocale(getPortletRequest().getLocale());
        //set feed titles
        if (feeds != null && feeds.size() != 0 ) {
            setFeedTitleList(feeds);
        }
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
    }
    
    /**
     * Persist this handler's <code>RssPortletBean</code>, if necessary.
     */
    public void persistSettingsBean(SettingsBean delta) throws ReadOnlyException, IOException, ValidatorException {
        boolean store = false;
        LinkedList feeds = null;
        // selected feed        
        if (delta.getFeeds() != null && delta.getFeedsSize() == 0) {
            // if there are no feeds, set the selected feed to null
            getPortletSession().setAttribute(SessionKeys.SELECTED_FEED, null);                        
        } else if (delta.getSelectedFeed() != null) {
            getPortletSession().setAttribute(SessionKeys.SELECTED_FEED, delta.getSelectedFeed());
        }
        
        // feeds and start feed - remove default feeds- not stored in preferences
        if (delta.getFeeds() != null) {
        	feeds = delta.getFeeds();
        	Iterator it = null;

        	if (log.isDebugEnabled()) {
           	 it = feeds.listIterator();
	             while (it.hasNext()) {
	             	log.debug("Delta feeds before ***********" + (String) it.next());
	             }  
            }
            
            //remove mandatory+role feeds- not stored in preferences
            //retrieved from jsp file
            if (delta.getFeedsSize() > 0) {
        		if ( null != role_feeds && role_feeds.length != 0) {
            		feeds = fileterFeeds(delta, role_feeds);
        		}
        		if ( null != mandate_feeds && mandate_feeds.length != 0) {
            		feeds = fileterFeeds(delta, mandate_feeds);
        		}
            }
             
             if (log.isDebugEnabled()) {
            	 it = feeds.listIterator();
	             while (it.hasNext()) {
	             	log.debug("Delta feeds after ***********" + (String) it.next());
	             }  
             }
            getPortletPreferences().setValues(PrefKeys.USER_FEEDS, (String[])feeds.toArray(new String[0]));
            getPortletPreferences().setValue(PrefKeys.START_FEED, delta.getStartFeed());
            getPortletPreferences().setValue(PrefKeys.DEFAULT_FEEDS_LEVEL, String.valueOf(default_feeds.size()));
            store = true;
        }
        // feed titles - for all feeds
        if (delta.getFeeds() != null && delta.getFeedsSize() != 0) {
            delta.setTitleList(delta.getFeeds());
        }

        // max age
        if (delta.isMaxAgeSet()) {
            getPortletPreferences().setValue(PrefKeys.MAX_AGE, Integer.toString(delta.getMaxAge()));
            store = true;
        }
        
        // disable max age
        if (delta.isDisableMaxAgeSet()) {
            getPortletPreferences().setValue(PrefKeys.DISABLE_MAX_AGE, Boolean.toString(delta.isDisableMaxAge()));
            store = true;
        }
        
        // max entries
        if (delta.isMaxEntriesSet()) {
            getPortletPreferences().setValue(PrefKeys.MAX_ENTRIES, Integer.toString(delta.getMaxEntries()));
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
	private LinkedList fileterFeeds(SettingsBean delta, String[] feedFilter) {
		LinkedList feeds = delta.getFeeds();
		if ( null != feedFilter && feedFilter.length != 0) {
			for (int i=0; i<feedFilter.length; i++) {
				for (int j=0; j<delta.getFeedsSize(); j++) {
					if ( ((String)feeds.get(j)).equalsIgnoreCase(feedFilter[i])) {
						feeds.remove(j);
						break;
					}
				}
			}
		}
		return feeds;
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
    
    /** Set the feed titles for list of feeds. 
     * if title is missing it's set to feed URL 
     */
    private void setFeedTitleList(LinkedList feeds) {
        LinkedList titleList = new LinkedList();
        LinkedList feedList = new LinkedList();

        Iterator it = feeds.listIterator();
        while (it.hasNext()) {
            String feed = (String) it.next();
            String title = null;
            try{
                SyndFeed syndfeed = FeedHelper.getInstance().getFeed(getSettingsBean(), feed);
                if (syndfeed != null) {
                	title = syndfeed.getTitle();
                }
            } catch (FeedException fe) {
            	log.info("setFeedTitleList: could not add feed FeedException " + feed + ":" + fe.getMessage());
                getPortletConfig().getPortletContext().log("could not add feed FeedException:" + feed, fe);
            } catch (IOException ioe) {
            	 log.info("setFeedTitleList: could not add feed IOException " + feed + ":" + ioe.getMessage());
                getPortletConfig().getPortletContext().log("could not add feed IOException:" + feed, ioe);
            } catch (Exception ex) {
            	log.info("setFeedTitleList: could not add feed Exception " + feed + ":" + ex.getMessage());
                getPortletConfig().getPortletContext().log("could not add feed Exception:" + feed, ex);
            }
            if (title == null || title.length() == 0) {
            	title = feed;
            }
            titleList.add(title);
        }
        //set feed titles list
        getSettingsBean().setTitleList(titleList);
    }
    
    private LinkedList stripDelimiter(LinkedList feeds) {
    	LinkedList stripFeeds = new LinkedList();
        Iterator it = feeds.listIterator();
        while (it.hasNext()) {
        	String feed = (String) it.next();
        	String[] delimFeed = feed.split(":dlm:");
        	if (delimFeed != null && delimFeed.length != 0) {
        		System.out.println("SettingsHandler delim Feeds[:dlm:] LinkedList***********" + delimFeed[delimFeed.length-1]);
        		stripFeeds.add(delimFeed[delimFeed.length-1]);
        	}
        }
        return stripFeeds;
    }
    private String stripDelimiter(String feed) {
    	String[] delimFeed = feed.split(":dlm:");
    	if (delimFeed != null && delimFeed.length != 0) {
    		System.out.println("SettingsHandler delim Feeds[:dlm:] LinkedList***********" + delimFeed[delimFeed.length-1]);
    		return delimFeed[delimFeed.length-1];
    	}
    	return feed; //should n't get here
    }
}
