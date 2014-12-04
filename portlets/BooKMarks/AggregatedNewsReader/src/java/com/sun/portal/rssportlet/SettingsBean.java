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

import java.util.Locale;
import java.util.LinkedList;
import java.util.Collections;

/**
 * This class is a bean to hold the settings for the RSS portlet.
 *
 * This bean is prepared (populated) by an <code>SettingsHandler</class>
 * object, when it is set into the handler.
 *
 * In addition to normal getters and setters,
 * this class contains a set of <code>is*Set()</code> methods. These are used
 * to determine if the value for the field has been set into this bean. This
 * information is used to determine if the value has changed, and should
 * therefore be persisted.
 */
public class SettingsBean {
    // use LinkedList, because we want to ensure a few optional List
    // operations are there
    private LinkedList feeds;
    private LinkedList titleList;
    private String selectedFeed = null;
    private String showDesc = null;
    private String windowTarget = null;
    private Integer maxAge = null;
    private Integer cacheTimeout = null;
    private Integer maxDescriptionLength = null;
    private Boolean newWindow = null;
    private Boolean disableMaxAge = null;
    private Integer maxEntries = null;
    private String selectedFeedType = null;
    private Integer maxNewsPaperEntries = null;
    private Locale locale = null;    

    /** Get show description property. */
    public String getShowDesc() {
        return showDesc;
    }
    
    /** Set show description property. */
    public void setShowDesc(String showDesc) {
        if (showDesc == null) {
            this.showDesc = "off";
        }
         this.showDesc= showDesc;
    }    

    
    /** Get the list of titles for all configured feeds. */
    public LinkedList getTitleList() {
        return titleList;
    }
    
    /** Set the list of titles for all configured feeds. */
    public void setTitleList(LinkedList titleList) {
        if (titleList == null) {
            this.titleList= new LinkedList();
        }
         this.titleList = titleList;
    }    

    /** Get the selected feed. */
    public String getSelectedFeed() {
        return selectedFeed;
    }
    
    /** Set the selected feed. */
    public void setSelectedFeed(String feed) {
        this.selectedFeed = feed;
    }

    /** Get the selected feedtype output. */
    public String getSelectedFeedType() {
        return selectedFeedType;
    }
    
    /** Set the selected feedtype output. */
    public void setSelectedFeedType(String feedType) {
        this.selectedFeedType = feedType;
    }

    /**
     * Get the cache timeout.
     *
     * The cache timeout should be a positive number of seconds defining the
     * length in time that feed entries may be cached. It may also be equal to
     * zero indicating that the feed entries should never be cached. If the
     * value is negative, the feed entries may be cached forever.
     */
    public int getCacheTimeout() {
        return cacheTimeout.intValue();
    }
    
    /** Set the cache timeout. */
    public void setCacheTimeout(int timeout) {
        this.cacheTimeout = new Integer(timeout);
    }
    
    /**
     * Get the maximum description length.
     *
     * This determines the number of characters that will be displayed for a
     * feed's description. This is to prevent the portal display from being
     * adversely affected in the case where the feed entries contain
     * a very long description.
     */
    public int getMaxDescriptionLength() {
        return maxDescriptionLength.intValue();
    }
    
    /** Set the maximum description length. */
    public void setMaxDescriptionLength(int length) {
        this.maxDescriptionLength = new Integer(length);
    }
    
    /** Should feed links be opened in a new window? */
    public boolean isNewWindow() {
        return newWindow.booleanValue();
    }
    
    /** Has the new window value been set into this bean? */
    public boolean isNewWindowSet() {
        return newWindow != null;
    }
    
    /** Set if feed links should be opened in a new window. */
    public void setNewWindow(boolean nw) {
        this.newWindow = new Boolean(nw);
    }
    
    /** Get the window target for feed links.
     *
     * This has no respective setter, and is based on the return value
     * of <code>isNewWindow()</code>. If said method returns true,
     * this method returns the string "_blank", else it returns "_top".
     */
    public String getWindowTarget() {
        String target = null;
        if (isNewWindow()) {
            target = "_blank";
        } else {
            target = "_top";
        }
        
        return target;
    }
    
    /**
     * Get the start feed.
     *
     * The start feed is the initial feed which is displayed when the user
     * logs in to the portal.
     */
    public String getStartFeed() {
        if (getFeeds().isEmpty()) {
            return null;
        }
        //valid only for aggregate feed
        //return (String)getFeeds().get(0);
        return "http://myportico.uoguelph.ca/";
    }
    
    /**
     * Set the start feed.
     *
     * This method removes the feed from the feeds list, if it exists there
     * currently, and adds it at the beginning of the list.
     */
    public void setStartFeed(String feed) {
        getFeeds().remove(feed);
        getFeeds().add(0, feed);
    }
    
    /** Get all configured feeds. */
    public LinkedList getFeeds() {
        return feeds;
    }
    
    /** Set all configured feeds. */
    public void setFeeds(LinkedList feeds) {
        if (feeds == null) {
            // make sure the getter never returns null;
            feeds = new LinkedList();
        }
        this.feeds = feeds;
    }
    
    /**
     * Get the size of the feeds set.
     *
     * As opposed to calling <code>getFeeds().size()</code>, this is provided
     * for bean access within the display logic, as the Java
     * <code>Collection.size()</code> method is not bean-conforming.
     *
     * This methos simply returns <code>getFeeds().size()</code>.
     */
    public int getFeedsSize() {
        return getFeeds().size();
    }
    
    /**
     * Get the maximum feed entry age for display.
     *
     * The display logic should not display any feed entries
     * that were published more than this number of hours
     * previous to now.
     */
    public int getMaxAge() {
        return maxAge.intValue();
    }
    
    /** Set the maximum feed entry age. */
    public void setMaxAge(int age) {
        this.maxAge = new Integer(age);
    }
    
    /**
     * Is the maximum feed entry age value set into this bean?
     */
    public boolean isMaxAgeSet() {
        return maxAge != null;
    }
    
    /**
     * Is the maximum age restriction disabled
     * If so, the display logic will ignore the maximum age setting
     * and display entries up to the number of maximum entries allowed.
     */
    public boolean isDisableMaxAge() {
        return disableMaxAge.booleanValue();
    }
    
    /** Set if the maximum age restriction is enabled. */
    public void setDisableMaxAge(boolean disableMaxAge) {
        this.disableMaxAge = new Boolean(disableMaxAge);
    }
    
    /** Is the maximum age restriction disabled flag set into this bean? */
    public boolean isDisableMaxAgeSet() {
        return disableMaxAge != null;
    }
    
    /** Get the maximum entries that will be displayed. */
    public int getMaxEntries() {
        return maxEntries.intValue();
    }
    
    /** Set the maximum entries that will be displayed. */
    public void setMaxEntries(int maxEntries) {
        this.maxEntries = new Integer(maxEntries);
    }

    /** Get the maximum newspaper type entries that will be displayed. */
    public int getMaxNewsPaperEntries() {
    	//System.out.println("Settings bean  get maxNewsPaperEntries***********" + maxNewsPaperEntries.intValue());
        return maxNewsPaperEntries.intValue();
    }
    
    /** Set the maximum newspaper type entries that will be displayed. */
    public void setMaxNewsPaperEntries(int maxNewsPaperEntries) {
    	//System.out.println("Settings bean  set maxNewsPaperEntries***********" + maxNewsPaperEntries);
        this.maxNewsPaperEntries = new Integer(maxNewsPaperEntries);
    }

    /** Is the maximum entries value set into this bean? */
    public boolean isMaxEntriesSet() {
        return maxEntries != null;
    }

    public boolean isMaxNewsPaperEntriesSet() {
        return maxNewsPaperEntries != null;
    }

    public Locale getLocale() {
        return locale;
    }
    
    public void setLocale(Locale locale) {
        this.locale = locale;
    }
}
