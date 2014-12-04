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

/**
 * This class provides helper constants for use within the Rss Portlet JSPs.
 *
 * There are several categories of constants:
 * <ul>
 * <li> Markup element names
 * <li> Portlet preference names
 * <li> Error parameter names
 * </ul>
 * <p>
 * If the portlet's JSPs are customized, it is important to use the
 * constant markup element names specified in this interface. If these
 * constants are not used, the portlet cannot process form submissions,
 * and Javascript in the JSPs may fail.
 * </p>
 * <p>
 * Portlet preference name constants are provided for convenience
 * as the JSPs read the preferences when rendering the portlet.
 * </p>
 * <p>
 * Error parameters are used to pass error information between the
 * portlet class and the edit JSP. Error parameters are set as
 * portlet render parameters. The existence or non-existence of
 * the error parameter is a flag indicating if the error occured.
 * The value of the error parameter may simply be "true",
 * or it may include some information about the error. See descriptions
 * of the error parameters for details.
 * </p>
 * <p>
 * <b>This API is unstable and changing.</b>
 * </p>
 */

public interface FormNames {
	
	public static final String SHOW_DESC=  "showDesc";
	
    /**
     * Markup element name of the source feed in the portlet's view mode.
     */

	public static final String SOURCE_SELECT_FEED =  "sourceSelectFeed";
	
    /**
     * Markup element name of the select feed drop down list in
     * the portlet's view mode.
     */

	public static final String INPUT_SELECT_FEED =  "selectFeed";

    /**
     * Minimum number of entries of a feed in Aggregated Feed 
     */
    public static final String NUM_ENTRY =  "numEntry";
    
    /**
     * Markup element name of the feeds check box group
     * in the portlet's edit mode.
     */
    public static final String INPUT_FEEDS =  "feeds";

    /**
     * Markup element name of the feeds check box group
     * in the portlet's edit mode.
     */
    public static final String FEED_OUTPUT_TYPE =  "feedOutputType";

    /**
     * Markup element name of the "add feed" button in the portlet's
     * edit mode.
     */
    public static final String INPUT_ADD_FEED =  "inputAddFeed";
    
    /**
     * Markup element name of the "maximum feed age" text box
     * in the portlet's edit mode
     */
    public static final String INPUT_MAX_AGE =  "inputMaxAge";
    
    /**
     * Markup element name of the "new window" check box in the
     * portlet's edit mode.
     */
    public static final String INPUT_NEWWIN =  "inputNewWindow";
    
    /**
     * Markup element name for the "maximum entries" text box in the
     * portlet's edit mode.
     **/
    public static final String INPUT_MAX_ENTRIES =  "inputMaxEntries";
    
    
    /**
     * Markup element name for the "maximum entries" text box in the
     * portlet's edit mode.
     **/
    public static final String INPUT_MAX_NEWS_PAPER_ENTRIES =  "inputMaxNewsPaperEntries";
    
    /**
     ** Markup element name for the "disable maximum feed age"
     * check box in the portlet's edit mode.
     */
    public static final String INPUT_DISABLE_MAX_AGE =  "disableMaxAge";
    
    /**
     * Markup element name for the "start feed" drop down list in the
     * portlet's edit mode.
     */
    public static final String INPUT_START_FEED =  "showAllEntries";
    
    /**
     * Markup element name for the "add new feed" submit button
     * in the portlet's edit mode.
     */
    public static final String SUBMIT_ADD =  "submitAdd";
    
    /**
     * Markup element name for the "finished" button in the
     * portlet's edit mode.
     */
    public static final String SUBMIT_FINISH =  "submitFinish";

    /** Markup element for the "cancel" button. */
    public static final String SUBMIT_CANCEL = "submitCancel";

    /** Markup element for the "custom" button. */
    public static final String SUBMIT_CUSTOM = "submitCustom";

    /** Markup element for the "custom" button. */
    public static final String WINDOW_CUSTOM = "windowCustom";
    
    /**
     * Markup element name for the "go" button in the
     * portlet's view mode.
     */
    public static final String SUBMIT_GO =  "submitGo";    
}
