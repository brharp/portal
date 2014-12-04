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

import com.sun.syndication.feed.synd.SyndFeed;
import java.util.List;

/**
 * This class is a bean to hold feed-related data for RSS portlet.
 *
 * This bean is prepared (populated) by an <code>FeedHandler</class>
 * object, when it is set into the handler.
 *
 */
public class FeedBean {        
    private List feedEntries = null;
    private String feedDescription = null;
    private SyndFeed feed = null;    
    
    
    /**
     * Get the size of the feed entries.
     *
     * As opposed to calling <code>getFeedEntries().size()</code>, this is provided
     * for bean access within the display logic, as the Java 
     * <code>Collection.size()</code> method is not bean-conforming.
     *
     * This method simply returns <code>getFeedEntries().size()</code>.
     */
    public int getFeedEntriesSize() {
        return getFeedEntries().size();
    }            

    /**
     * 
     * Get the feed entries.
     * @return a List of ROME SyndFeed objects.
     */
    public List getFeedEntries() {
        return feedEntries;
    }

    /**
     * Set the feed entries.
     * @param feedEntries a List of ROME SyndFeed objects.
     */
    public void setFeedEntries(List feedEntries) {
        this.feedEntries = feedEntries;
    }

    /**
     * Get the feed description.
     */
    public String getFeedDescription() {
        return feedDescription;
    }

    /**
     * Set the feed description
     */
    public void setFeedDescription(String feedDescription) {
        this.feedDescription = feedDescription;
    }

    /**
     * Get the ROME SyndFeed object.
     */
    public SyndFeed getFeed() {
        return feed;
    }
    
    /**
     * Set the ROME SyndFeed object.
     */
    public void setFeed(SyndFeed feed) {
        this.feed = feed;
    }
}
