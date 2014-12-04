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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Arrays;
import java.util.Map;

import javax.portlet.PortletModeException;
import javax.portlet.PortletSession;

import com.sun.syndication.io.FeedException;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

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
       
    public void init(PortletConfig config) throws PortletException {
        super.init(config);
        portletContext = config.getPortletContext();
    }
    
    /** Include "view" JSP. */
    public void doView(RenderRequest request,RenderResponse response) throws PortletException,IOException {
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

        if (request.getParameter(SUBMIT_ADD) != null) {
            //
            // handle "add" button on edit page
            // to add a new feed
            //
            processEditAddAction(request, response, ah, resources, readBean, writeBean);
        } else if (request.getParameter(SUBMIT_GO) != null || request.getParameter(INPUT_SELECT_FEED) != null) {
            //
            // handle "go" button in view mode
            // to change the selected feed
            //
            processGoAction(request, response, readBean, writeBean);
        } else if (request.getParameter(SUBMIT_CANCEL) != null) {
            //
            // handle "cancel" button on edit page
            // return to view mode
            //
            processEditCancelAction(request, response);
        } else if (request.getParameter(SUBMIT_FINISH) != null) {
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
    
    private void processEditAddAction(ActionRequest request, ActionResponse response, AlertHandler alertHandler, Resources resources, SettingsBean readBean, SettingsBean writeBean) {
        String url = request.getParameter(INPUT_ADD_FEED);
        try {
            // see if the url exists
            // if there's no exception, then the feed exists and is valid
            FeedHelper.getInstance().getFeed(readBean, url);
            
            //add to the existing values
            LinkedList feeds = readBean.getFeeds();
            feeds.add(url);
            writeBean.setFeeds(feeds);
            
            //
            // set newly added feed as selected feed
            //
            writeBean.setSelectedFeed(url);
            
            // we stay in edit mode here
        } catch (MalformedURLException mue) {
            alertHandler.setError(resources.get("invalid_url"), mue.getMessage());
            getPortletConfig().getPortletContext().log("could not add feed", mue);
        } catch (UnknownHostException uhe) {
            alertHandler.setError(resources.get("invalid_url"), uhe.getMessage());
        } catch (FileNotFoundException fnfe) {
            alertHandler.setError(resources.get("invalid_url"), fnfe.getMessage());
            getPortletConfig().getPortletContext().log("could not add feed", fnfe);
        } catch (IllegalArgumentException iae) {
            alertHandler.setError(resources.get("invalid_url"), iae.getMessage());
            getPortletConfig().getPortletContext().log("could not add feed", iae);
        } catch (FeedException fe) {
            alertHandler.setError(resources.get("invalid_url"), fe.getMessage());
            getPortletConfig().getPortletContext().log("could not add feed", fe);
        } catch (IOException ioe) {
            alertHandler.setError(resources.get("invalid_url"), ioe.getMessage());
            getPortletConfig().getPortletContext().log("could not add feed", ioe);
        }
    }
    
    private void processGoAction(ActionRequest request, ActionResponse response, SettingsBean readBean, SettingsBean writeBean) {
        String selectedFeed = request.getParameter(INPUT_SELECT_FEED);
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
        if (checkedFeeds != null) {
            feeds = new LinkedList(Arrays.asList(checkedFeeds));
        } else if (request.getParameter(FormNames.WINDOW_CUSTOM)!= null){
            feeds = readBean.getFeeds();
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

        if (request.getParameter(FormNames.WINDOW_CUSTOM) != null)  {
            String s = request.getParameter(INPUT_MAX_AGE);
            if (s != null && s.length() > 0) {
                //
                // test to make sure it's an int
                //
                try {
                    int n = Integer.parseInt(s);
                    if (n < 1) {
                        alertHandler.setError(resources.get("enter_a_whole_number_greater_than_zero"));
                    } else {
                        writeBean.setMaxAge(n);
                    }
                } catch (NumberFormatException nfe) {
                    alertHandler.setError(resources.get("enter_a_whole_number_greater_than_zero"));
                }
            }
            
            String maxEntries = request.getParameter(INPUT_MAX_ENTRIES);
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
            
            String[] disableMaxAge = request.getParameterValues(INPUT_DISABLE_MAX_AGE);
            
            if (disableMaxAge != null && disableMaxAge.length > 0) {
                writeBean.setDisableMaxAge(true);
            } else {
                writeBean.setDisableMaxAge(false);
            }
            
            String[] newWindow = request.getParameterValues(INPUT_NEWWIN);
            
            if (newWindow != null && newWindow.length > 0) {
                writeBean.setNewWindow(true);
            } else {
                writeBean.setNewWindow(false);
            }
        }
        if (!alertHandler.isErrorExists()) {
            // if there were no errors, then go back to
            // view mode
            response.setPortletMode(PortletMode.VIEW);
        } else {
        	response.setRenderParameter(FormNames.SUBMIT_CUSTOM, FormNames.SUBMIT_CUSTOM);
        }
    }
}
