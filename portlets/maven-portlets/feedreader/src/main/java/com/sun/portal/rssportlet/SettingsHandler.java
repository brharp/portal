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

import java.io.IOException;
import java.util.Arrays;
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
        public static final String FEEDS = "feeds";
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
        LinkedList feeds = new LinkedList(Arrays.asList(getPortletPreferences().getValues(PrefKeys.FEEDS, new String[] { } )));
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
        
        initSelectedFeed();
        
        getSettingsBean().setLocale(getPortletRequest().getLocale());
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
        
        // selected feed        
        if (delta.getFeeds() != null && delta.getFeedsSize() == 0) {
            // if there are no feeds, set the selected feed to null
            getPortletSession().setAttribute(SessionKeys.SELECTED_FEED, null);                        
        } else if (delta.getSelectedFeed() != null) {
            getPortletSession().setAttribute(SessionKeys.SELECTED_FEED, delta.getSelectedFeed());
        }
        
        // feeds
        if (delta.getFeeds() != null) {
            getPortletPreferences().setValues(PrefKeys.FEEDS, (String[])delta.getFeeds().toArray(new String[0]));
            store = true;
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
}
