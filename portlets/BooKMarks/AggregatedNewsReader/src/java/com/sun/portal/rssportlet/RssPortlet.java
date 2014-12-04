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
 * "Portions Copyrighted 2006 Lalit Jairath, Brent Harp"
 */

package com.sun.portal.rssportlet;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.PortletSecurityException;
import javax.portlet.UnavailableException;

import java.io.IOException;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;

import java.text.Format;

import javax.portlet.PortletModeException;
import javax.portlet.PortletSession;

import com.sun.syndication.io.FeedException;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import com.sun.portal.rssportlet.FeedHelper;

import com.sun.syndication.feed.synd.*;
import com.sun.syndication.io.SyndFeedOutput;

import java.io.FileWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.SyndFeedOutput;
import org.xml.sax.InputSource;
import java.net.URL;

import com.sun.syndication.fetcher.FeedFetcher;
import com.sun.syndication.fetcher.FetcherEvent;
import com.sun.syndication.fetcher.FetcherListener;
import com.sun.syndication.fetcher.impl.FeedFetcherCache;
import com.sun.syndication.fetcher.impl.HashMapFeedInfoCache;
import com.sun.syndication.fetcher.impl.HttpURLFeedFetcher;
import com.sun.syndication.fetcher.impl.HttpClientFeedFetcher;
//import com.sun.syndication.fetcher.samples.FeedReader.FetcherEventListenerImpl;

import java.util.*;
import java.io.PrintWriter;

import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.portlet.ReadOnlyException;
import javax.portlet.ValidatorException;

import java.net.MalformedURLException;
import org.apache.commons.httpclient.HttpException;
import com.sun.syndication.fetcher.FetcherException;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.ParsingFeedException;

/**
 * This class implements the Rss portlet.
 *
 * The following is a design overview of the RSS portlet ...
 *
 * The entry points into the application are the view and edit JSPs.
 * These correspond to the portlet's view and edit modes, respectively.
 * The JSPs use the *Handler classes to prepare the *Bean classes for use
 * in the display logic. Control is passed to the JSPs in the do*() methods.
 * The only logic in this
 * class is the processing of portlet actions. This occurs for the "go"
 * form in the portlet's view mode, and the forms in the portlet's edit mode.
 *
 * The portlet's edit mode allows the user to modify a subset of the portlet
 * preferences to personalize the portlet.
 *
 */
public class RssPortlet extends GenericPortlet implements FormNames {
	
	private PortletContext portletContext;
	public static final String MAX_NEWS_PAPER_ENTRIES = "maxNewsPaperEntries";
	public static final String SELECTED_FEED_TYPE = "selectedFeedType";
	
	public void init(PortletConfig config) throws PortletException {
		super.init(config);
		portletContext = config.getPortletContext();
	}
	
	/** Include "view" JSP. */
	public void doView(RenderRequest request,RenderResponse response) throws PortletException,IOException {
		PortletSession psession = request.getPortletSession();
		String[] initFeeds = new String[] {
				"All My U of G News%http://myportico.uoguelph.ca/",
				"Campus News%http://myportico.uoguelph.ca/portal/rss/campusnews.xml",
				"CCS News%http://myportico.uoguelph.ca/portal/rss/ccsnews.xml",
				"CIP Exchange Student News%http://myportico.uoguelph.ca/portal/rss/cipexchange.xml",
				"CIP Faculty and Staff News%http://myportico.uoguelph.ca/portal/rss/cipfacstaff.xml",
				"CIP Student News%http://myportico.uoguelph.ca/portal/rss/cipstudent.xml",
				"College of Arts%http://myportico.uoguelph.ca/portal/rss/collegeofarts.xml",
				"Guelph Athletics - Football%http://myportico.uoguelph.ca/portal/rss/athletics.xml",
				"Library News%http://myportico.uoguelph.ca/portal/rss/libnews.xml",
				"OVC News%http://myportico.uoguelph.ca/portal/rss/ovcnews.xml",
				"Presidents's Blog%http://myportico.uoguelph.ca/portal/rss/presnews.xml",
				"Student Affairs Upcoming Events%http://myportico.uoguelph.ca/portal/rss/studaffairs.xml",
				"Towards the Information Ecology%http://myportico.uoguelph.ca/portal/rss/cionews.xml"
		};
		Map mapFeeds = new HashMap();
		for (int i=0; i<initFeeds.length; i++) {
			mapFeeds.put(initFeeds[i], initFeeds[i]);
		}
		psession.setAttribute("mapFeeds", mapFeeds);
		psession.setAttribute("initFeeds", initFeeds);
		include(request, response, "/WEB-INF/jsp/view.jsp");
	}
	
	/** Include "edit" JSP. */
	public void doEdit(RenderRequest request,RenderResponse response) throws PortletException {
		if (request.getAttribute(FormNames.SUBMIT_CUSTOM) != null) {
			request.setAttribute(FormNames.SUBMIT_CUSTOM, null);
			include(request, response, "/WEB-INF/jsp/custom.jsp");
		} else {
			include(request, response, "/WEB-INF/jsp/edit.jsp");
		}
	}
	
	/** Include "help" JSP. */
	public void doHelp(RenderRequest request,RenderResponse response) throws PortletException {
		include(request, response, "/help.jsp");
	}
	
	/** Include a page. */
	private void include(RenderRequest request, RenderResponse response, String pageName) throws PortletException {
		response.setContentType(request.getResponseContentType());
		if (pageName == null || pageName.length() == 0) {
			// assert
			throw new NullPointerException("null or empty page name");
		}
		try {
			PortletRequestDispatcher dispatcher = portletContext.getRequestDispatcher(pageName);
			dispatcher.include(request, response);
		} catch (IOException ioe) {
			throw new PortletException(ioe);
		}
	}
	
	/** Process actions from the view mode "go" form, and the edit mode forms. */
	public void processAction(ActionRequest request, ActionResponse response) throws UnavailableException, PortletSecurityException, PortletException,IOException {
		Resources resources = new Resources("com.sun.portal.rssportlet.RssPortlet", request.getLocale());
		
		// get objects from portlet session
		// these are shared by the JSP
		String feedOutputType = request.getParameter("feedType");
		//String srcSelFeed = request.getParameter(FormNames.SOURCE_SELECT_FEED);
		String frmInpSelFeed = request.getParameter(FormNames.INPUT_SELECT_FEED);
		
		PortletSession psession = request.getPortletSession();
		//System.out.println("Edit feedType" +  feedOutputType);
		//System.out.println("Edit srcSelFeed " +  srcSelFeed );
		//System.out.println("Edit frmInpSelFeed" +  frmInpSelFeed);
		
		psession.setAttribute("feedType", feedOutputType);
		request.setAttribute("feedType", feedOutputType);
		AlertHandler ah = (AlertHandler)request.getPortletSession().getAttribute("alertHandler", PortletSession.PORTLET_SCOPE);
		
		// construct a new bean and populate with current values
		SettingsBean readBean = new SettingsBean();
		SettingsHandler handler = new SettingsHandler();
		handler.setPortletConfig(getPortletConfig());
		handler.setPortletRequest(request);
		handler.setSettingsBean(readBean);
		
		// this bean contains the changes (if any) submitted in this
		// action
		SettingsBean writeBean = new SettingsBean();
		
		if (request.getParameter(SUBMIT_GO) != null || request.getParameter(FormNames.INPUT_SELECT_FEED) != null) {
			System.out.println("processGoAction:");
			//
			// handle "go" button in view mode
			// to change the selected feed
			//
			processGoAction(request, response, readBean, writeBean);
		} else if (request.getParameter(SUBMIT_CANCEL) != null) {
			System.out.println("processCancelAction:");
			//
			// handle "cancel" button on edit page
			// return to view mode
			//
			processEditCancelAction(request, response);
		} else if (request.getParameter(SUBMIT_FINISH) != null) {
			System.out.println("processEditAction:");
			//
			// handle various functions on the
			// edit page
			//
			processEditAction(request, response, ah, resources, readBean, writeBean);
		}
		
		if (request.getParameter(SUBMIT_CUSTOM) != null ){
			request.setAttribute(SUBMIT_CUSTOM, SUBMIT_CUSTOM);
			response.setPortletMode(PortletMode.EDIT);
		} else {
			handler.persistSettingsBean(writeBean);
			//if there were any changes, persist them
		}
	}
	
	private void processGoAction(ActionRequest request, ActionResponse response, SettingsBean readBean, SettingsBean writeBean) {
		String selectedFeed = request.getParameter(FormNames.INPUT_SELECT_FEED);
		//System.out.println("****proc selFeed" + selectedFeed);
		
		String sourceSelectedFeed = request.getParameter(FormNames.SOURCE_SELECT_FEED);
		//System.out.println("****proc sourceSelectedFeed" + sourceSelectedFeed);
		
		if (null != sourceSelectedFeed) {
			LinkedList feeds = readBean.getFeeds();
			selectedFeed = (String)feeds.get(Integer.parseInt(sourceSelectedFeed));
			System.out.println("****selFeed" + selectedFeed);
		}
		if (null != selectedFeed) {
			writeBean.setSelectedFeed(selectedFeed);
		}
	}
	
	private void processEditCancelAction(ActionRequest request, ActionResponse response) throws PortletModeException {
		response.setPortletMode(PortletMode.VIEW);
	}
	
	private void processEditAction(ActionRequest request, ActionResponse response, AlertHandler alertHandler, Resources resources, SettingsBean readBean, SettingsBean writeBean) throws PortletModeException {
		String[] checkedFeeds = request.getParameterValues(INPUT_FEEDS);
		
		LinkedList feeds = null;
		Iterator it = null;
		if (checkedFeeds != null) {
			for (int i=0; i<checkedFeeds.length; i++) {
				//System.out.println("checked feeds***********" + checkedFeeds[i]);
			}  
			feeds = new LinkedList(Arrays.asList(checkedFeeds));
			feeds.add(0,"http://myportico.uoguelph.ca/");
			feeds.add(1,"http://myportico.uoguelph.ca/portal/rss/campusnews.xml");
			/*
			it = feeds.listIterator();
			while (it.hasNext()) {
				System.out.println("RSS  edit chk Linked List***********" + (String) it.next());
			} */ 
			
		} else {//if (request.getParameter(FormNames.WINDOW_CUSTOM)!= null){
			//feeds = readBean.getFeeds();
			feeds = new LinkedList();
			feeds.add(0,"http://myportico.uoguelph.ca/");
			feeds.add(1,"http://myportico.uoguelph.ca/portal/rss/campusnews.xml");
			/*
			it = feeds.listIterator();
			while (it.hasNext()) {
				System.out.println("RSS  edit no chk Linked List***********" + (String) it.next());
			} */ 
			
		}
		if (feeds == null) {
			//
			// no feeds
			// empty the feeds list
			//
			writeBean.setFeeds(new LinkedList());
		} else {
			//
			// entries selected, reset feed list to
			// the selected entries.
			//
			writeBean.setFeeds(feeds);
			
			//
			// start feed
			// only set the start feed if the feeds list in non-null
			// (that's the only way we get here)
			// make sure the start feed is in the new feeds list
			// catch the case where the user deletes the start feed
			//
			String startFeed = request.getParameter(INPUT_START_FEED);
			if (startFeed != null && feeds.contains(startFeed)) {
				writeBean.setStartFeed(startFeed);
			}
			
			//
			// case where we delete the selected feed
			// set to the start feed
			//
			if (readBean.getSelectedFeed() != null && !feeds.contains(readBean.getSelectedFeed())) {
				String selectedFeed = writeBean.getStartFeed();
				writeBean.setSelectedFeed(selectedFeed);
			}
			
			//
			// case where selected feed is null, because feed
			// list was previously null
			// set the selected feed to the start feed
			//
			if (readBean.getFeeds().size() == 0) {
				writeBean.setSelectedFeed(writeBean.getStartFeed());
			}
		}
		
		PortletSession psession = request.getPortletSession();
		String selectedFeedType = request.getParameter(FEED_OUTPUT_TYPE);
		//System.out.println("RSS selectedFeedType" +  selectedFeedType);
		psession.setAttribute("feedType", selectedFeedType);
		writeBean.setSelectedFeedType(selectedFeedType);
		//System.out.println("RSS after writebean");
		
		if ("river".equals(selectedFeedType)) {
			String maxEntries = request.getParameter(INPUT_MAX_ENTRIES );
			System.out.println(" maxEntries" +  maxEntries);
			if (maxEntries != null && maxEntries.length() > 0) {
				//
				// test to make sure it's an int
				//
				try {
					int n = Integer.parseInt(maxEntries);
					if (n < 1) {
						alertHandler.setError(resources.get("enter_a_whole_number_greater_than_zero"));
					} else {
						writeBean.setMaxEntries(n);
					}
				} catch (NumberFormatException nfe) {
					alertHandler.setError(resources.get("enter_a_whole_number_greater_than_zero"));
				}
			}
		} else {
			String maxNewsPaperEntries = request.getParameter(INPUT_MAX_NEWS_PAPER_ENTRIES);
			System.out.println(" maxNewsPaperEntries" +  maxNewsPaperEntries);
			if (maxNewsPaperEntries != null && maxNewsPaperEntries.length() > 0) {
				//
				// test to make sure it's an int
				//
				try {
					int n = Integer.parseInt(maxNewsPaperEntries);
					if (n < 1) {
						alertHandler.setError(resources.get("enter_a_whole_number_greater_than_zero"));
					} else {
						writeBean.setMaxNewsPaperEntries(n);
					}
				} catch (NumberFormatException nfe) {
					alertHandler.setError(resources.get("enter_a_whole_number_greater_than_zero"));
				}
			}
		}
		
		String showDesc = (String) request.getParameter(FormNames.SHOW_DESC);
		if (showDesc != null) {
			psession.setAttribute(FormNames.SHOW_DESC, showDesc); 
			//System.out.println("Edit parm values show" +  showDesc);
			writeBean.setShowDesc(showDesc);
		} else {
			psession.setAttribute(FormNames.SHOW_DESC, "off"); 
			//System.out.println("Edit show" +  showDesc);
			writeBean.setShowDesc("off");
		}
		
		if (!alertHandler.isErrorExists()) {
			// if there were no errors, then go back to
			// view mode
			response.setPortletMode(PortletMode.VIEW);
		} else {
			response.setRenderParameter(FormNames.SUBMIT_CUSTOM, FormNames.SUBMIT_CUSTOM);
		}
	}
	
	//TODO: Alert handler (generic message) for invalid feeds
	/*
	private static void setFeedAlert (SyndFeed feed, String initFeed, String exceptionType, String ex) {
		//SyndFeed feed = new SyndFeedImpl();
        try {
            final DateFormat DATE_PARSER = new SimpleDateFormat("yyyy-MM-dd");
			String feedType = "rss_2.0";
		    feed.setFeedType(feedType);
		
		    feed.setTitle("Invalid Feed");
		    feed.setLink(initFeed);
		    feed.setDescription(exceptionType + ex);
		
		    List entries = new ArrayList();
		    SyndEntry entry;
		    SyndContent description;
		
		    entry = new SyndEntryImpl();
		    entry.setTitle("Invalid Feed Entry");
		    entry.setLink(initFeed);
		    entry.setPublishedDate(DATE_PARSER.parse("2004-06-08"));
		    description = new SyndContentImpl();
		    description.setType("text/plain");
		    description.setValue("To check the feed status please click the link: " + initFeed);
		    entry.setDescription(description);
		    entries.add(entry);
		    feed.setEntries(entries);

        }
        catch (Exception e) {
	        e.printStackTrace();
	        System.out.println("ERROR: "+ e.getMessage());
        }
	    //System.out.println("The first parameter must be the syndication format for the feed");
	    //System.out.println("  (rss_0.90, rss_0.91, rss_0.92, rss_0.93, rss_0.94, rss_1.0 rss_2.0 or atom_0.3)");
	}*/
}