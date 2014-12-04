package ca.nejedly.portlet.mail;

import java.io.IOException;

import javax.portlet.GenericPortlet;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.PortletSession;
import javax.portlet.PortletPreferences;
//import javax.portlet.WindowState;

import java.util.Collection;
import java.util.Map;
import javax.portlet.PortletRequest;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import ca.nejedly.mail.imap.GotMail;
import ca.nejedly.mail.imap.GotMailException;
import ca.nejedly.mail.imap.GotMailLoginFailedException;
import ca.nejedly.mail.imap.GotMailMissingPasswordException;
import ca.nejedly.mail.imap.GotMailMissingUidException;
import ca.nejedly.mail.imap.GotMailVO;


/**
 * GotMail portlet class - provides information about the user's
 * inbox folder, e.g., number of unseen messages, total message count,
 * etc. 
 * @see ca.nejedly.mail.imap.GotMail    
 * @author  Zdenek Nejedly znejedly@uoguelph.ca
 *
 */
public class GotMailPortlet extends GenericPortlet {
	
	/* when changing these do not forget to update the view */
	public static String REQUEST_KEY_COUNT_UNSEEN = "GotMailCountUnseen";
	public static String REQUEST_KEY_COUNT_TOTAL = "GotMailCountTotal";
	public static String REQUEST_KEY_COLLECTION = "GotMailCollection";
	public static String REQUEST_KEY_ERROR_MESSAGE = "GotMailErrorMessage";
	public static String REQUEST_KEY_DETAILED_MODE_ON = "GotMailDetailedModeOn";
	public static String REQUEST_KEY_LISTING_RESTRICTED = "GotMailListingRestricted";
	
	public static String PREFS_MESSAGE_INFO_ON = "messageInfoOn";
	public static String PREFS_MESSAGE_INFO_MAX = "messageInfoMax";
	public static String PREFS_SENDER_MAX_LEN = "senderMaxLen";
	public static String PREFS_SUBJECT_MAX_LEN = "subjectMaxLen";
	public static String PREFS_USAGE_TIP_ON = "usageTipOn";
	
	public static String SESSION_KEY_DETAILED_MODE = "GotMailDetailedMode";
	
	protected String mailServerAddress;
	protected int mailServerPort;
	
	private String JSP_PATH = "/WEB-INF/jsp/";
	
	public void init(PortletConfig config) throws PortletException
	{
		
		java.util.Enumeration enum = config.getInitParameterNames();
		while (enum.hasMoreElements() ) {
			String name = (String) enum.nextElement();
			String value = (String) config.getInitParameter(name);
		}
		mailServerAddress = (String) config.getInitParameter("mailServerAddress");
		try {
			mailServerPort = Integer.parseInt(
					(String) config.getInitParameter("mailServerPort"));			
		}
		catch (Exception e) {
			System.out.println("Config par failed for port");
		}
		
		super.init(config);
	}
	
    /**
     * This method is called when the portlet is rendered in the VIEW portlet mode state.
     *
     * @see javax.portlet.GenericPortlet#doView(javax.portlet.RenderRequest, javax.portlet.RenderResponse)
     */
    public void doView(RenderRequest request, RenderResponse response)
    	throws PortletException, IOException
	{    
    	StringBuffer jspPath = new StringBuffer(JSP_PATH);
		jspPath.append("list.jsp");
		boolean retrieveCountsOnly = false;
		checkMail(request, response, retrieveCountsOnly);
		
		PortletSession session = request.getPortletSession();
		String value = (String) session.getAttribute(SESSION_KEY_DETAILED_MODE);
    	if (value!=null && value.equalsIgnoreCase("false"))
    		request.setAttribute(REQUEST_KEY_DETAILED_MODE_ON,"false");
    	else
    		request.setAttribute(REQUEST_KEY_DETAILED_MODE_ON,"true");
		  	    	
		response.setProperty("expiration-cache","0");
        PortletContext context = getPortletContext();
		PortletRequestDispatcher prd = context.getRequestDispatcher(jspPath.toString());
		response.setContentType("text/html");
		prd.include(request,response);		
    }
    
    public void doHelp(RenderRequest request, RenderResponse response)
	throws PortletException, IOException
{
    StringBuffer jspPath = new StringBuffer(JSP_PATH);
	jspPath.append("help.jsp");
	PortletContext context = getPortletContext();
	PortletRequestDispatcher prd = context.getRequestDispatcher(jspPath.toString());
	response.setContentType("text/html");
	prd.include(request,response);
}
    
    public void doEdit(RenderRequest request, RenderResponse response)
	throws PortletException, IOException
{    
    PortletPreferences prefs = request.getPreferences();
	request.setAttribute(PREFS_MESSAGE_INFO_ON, 
				(String) prefs.getValue(PREFS_MESSAGE_INFO_ON,"true"));
	request.setAttribute(PREFS_MESSAGE_INFO_MAX, 
			(String) prefs.getValue(PREFS_MESSAGE_INFO_MAX,"10"));
	request.setAttribute(PREFS_SENDER_MAX_LEN, 
			(String) prefs.getValue(PREFS_SENDER_MAX_LEN,"30"));
	request.setAttribute(PREFS_SUBJECT_MAX_LEN, 
			(String) prefs.getValue(PREFS_SUBJECT_MAX_LEN,"30"));
	request.setAttribute(PREFS_USAGE_TIP_ON, 
		(String) prefs.getValue(PREFS_USAGE_TIP_ON,"true"));
	
	StringBuffer jspPath = new StringBuffer(JSP_PATH);
	jspPath.append("edit.jsp");
	PortletContext context = getPortletContext();
	PortletRequestDispatcher prd = context.getRequestDispatcher(jspPath.toString());
	response.setContentType("text/html");
	prd.include(request,response);
    	
} 
    
    public void processAction(ActionRequest action, ActionResponse response) 
    	throws PortletException 
	{
    	String submit = (String) action.getParameter("submit");	   
    	PortletSession session = action.getPortletSession();
    	if (submit!=null && submit.toLowerCase().startsWith("hide")) {
    		session.setAttribute(SESSION_KEY_DETAILED_MODE,"false");
    	}
    	if (submit!=null && submit.toLowerCase().startsWith("show")) {
    		session.setAttribute(SESSION_KEY_DETAILED_MODE,"true");
    	}
    	response.setPortletMode(PortletMode.VIEW);
    	response.setWindowState(javax.portlet.WindowState.NORMAL);
    	
    	String mode = (String) action.getParameter("mode");
    	if ("edit".equalsIgnoreCase(mode)) {
    		PortletPreferences prefs = action.getPreferences();    		
    		String strValue = (String) action.getParameter(PREFS_MESSAGE_INFO_ON);
    		int iValue = 0;
    		if (strValue!=null && strValue.equalsIgnoreCase("true")) 
    			prefs.setValue(PREFS_MESSAGE_INFO_ON,"true"); 
    		else
    			prefs.setValue(PREFS_MESSAGE_INFO_ON,"false");
    		
    		strValue = (String) action.getParameter(PREFS_MESSAGE_INFO_MAX);
    		if (strValue!=null) {
    			iValue = 0;    		
    			try { 
    				iValue = Integer.parseInt(strValue);
    			}
    			catch (Exception e) {	
    				System.out.println("form: conversion of " + strValue + " failed");
    				e.printStackTrace();
    			}
    			if (iValue>=1 && iValue<=100)
    				prefs.setValue(PREFS_MESSAGE_INFO_MAX,""+iValue); 
    		}
    		
    		strValue = (String) action.getParameter(PREFS_SENDER_MAX_LEN);
    		if (strValue!=null) {
    			iValue = 0;    		
    			try { 
    				iValue = Integer.parseInt(strValue);
    			}
    			catch (Exception e) {	
    				System.out.println("form: conversion of " + strValue + " failed");
    				e.printStackTrace();
    			}
    			if (iValue>=10 && iValue<=100)
    				prefs.setValue(PREFS_SENDER_MAX_LEN,""+iValue); 
    		}
    		
    		strValue = (String) action.getParameter(PREFS_SUBJECT_MAX_LEN);
    		if (strValue!=null) {
    			iValue = 0;    		
    			try { 
    				iValue = Integer.parseInt(strValue);
    			}
    			catch (Exception e) {	
    				System.out.println("form: conversion of " + strValue + " failed");
    				e.printStackTrace();
    			}
    			if (iValue>=10 && iValue<=100)
    				prefs.setValue(PREFS_SUBJECT_MAX_LEN,""+iValue); 
    		}
    		
    		strValue = (String) action.getParameter(PREFS_USAGE_TIP_ON);
    		if (strValue!=null && strValue.equalsIgnoreCase("true")) 
    			prefs.setValue(PREFS_USAGE_TIP_ON,"true"); 
    		else
    			prefs.setValue(PREFS_USAGE_TIP_ON,"false");
    		
    		
    		try {
    			System.out.println("saving preferences... ");
    			prefs.store();
    			System.out.println("Preferences saved");
    		}
    		catch (IOException e) {
    			System.out.println("Could not save the preferences");
    			e.printStackTrace();
    		}
    		catch (Exception e) {
    			System.out.println("Could not save the preferences");
    			e.printStackTrace();
    		}
    		
    	}
    	
    	
    }
    
       
    /**
     * Gets the user's uid and password, check user's mail, stores the results in the request.
     *  
     * @param request
     * @param response
     * @param retrieveCountsOnly
     * @throws GotMailException
     * @throws GotMailLoginFailedException
     * @throws GotMailMissingPasswordException
     * @throws GotMailMissingUidException
     * @throws PortletException
     * @throws IOException
     */
    public void checkMail(RenderRequest request, RenderResponse response, 
    			boolean retrieveCountsOnly) 
    			throws PortletException, IOException {
    	
    	// preferences
    	int normLenFromAddress = 40;
    	int normLenSubject = 40;
    	int headSize = 10;
    	
    	// dump preferences
    	PortletPreferences prefs = request.getPreferences();
    	String uid = null;
    	String pwd = null;
    	Map info = (Map) request.getAttribute(PortletRequest.USER_INFO);
		if (info!=null) {
        	uid = (String) info.get("username");
        	pwd = (String) info.get("password");
		}
		GotMail obj = new GotMail();
        GotMailVO vo = null;
        try {
        	vo = obj.checkMail(mailServerAddress,mailServerPort, 
        				uid,pwd, retrieveCountsOnly);
        	Collection col = vo.getUnseenMessages(); 
        	vo.setNormLenFromAddress(normLenFromAddress);
        	vo.setNormLenSubject(normLenSubject);
        	col = vo.normalizeText(col);
        	col = vo.sortCollection(col);
        	
        	if (headSize<col.size())
        		request.setAttribute(REQUEST_KEY_LISTING_RESTRICTED,""+headSize);
			col = vo.head(col,headSize);

			request.setAttribute(REQUEST_KEY_COUNT_UNSEEN, ""+vo.getCountUnseen());
            request.setAttribute(REQUEST_KEY_COUNT_TOTAL, ""+vo.getCountTotal());
            if (vo.getUnseenMessages()!=null)
            	request.setAttribute(REQUEST_KEY_COLLECTION, col);            
        }
        catch (GotMailMissingPasswordException e) {
        	request.setAttribute(REQUEST_KEY_ERROR_MESSAGE, "The mail could not be checked because the user's password " + 
        			" is unavailable. Please, contact the portal administrator.");
        }
        catch (GotMailMissingUidException e) {
        	request.setAttribute(REQUEST_KEY_ERROR_MESSAGE, "The mail could not be checked because the user's central login ID" + 
			" is unavailable. Please, contact the portal administrator.");
        }
        catch (GotMailLoginFailedException e) {
        	request.setAttribute(REQUEST_KEY_ERROR_MESSAGE, "The mail could not be checked because the password " + 
        			" verification failed. If the password was changed recently please, " + 
					" log out from the portal and log in again.");
        }
        catch (GotMailException e) {
        	request.setAttribute(REQUEST_KEY_ERROR_MESSAGE, "The mail could not be checked at this time. " +
        			"Please, try again later.");
        	e.printStackTrace();
        }
        
        
    }
    
    /**
     * The default implementation of this method routes the render request
     * to a set of helper methods depending on the current portlet mode the
     * portlet is currently in.
     * These methods are:
     * <ul>
     * <li><code>doView</code> for handling <code>view</code> requests
     * <li><code>doEdit</code> for handling <code>edit</code> requests
     * <li><code>doHelp</code> for handling <code>help</code> requests
     * </ul>
     * <P>
     * If the window state of this portlet is <code>minimized</code>, this
     * method does not invoke any of the portlet mode rendering methods.
     * <p>
     * For handling custom portlet modes the portlet should override this
     * method.
     *
     * @param request
     *                 the render request
     * @param response
     *                 the render response
     *
     * @exception PortletException
     *                   if the portlet cannot fulfilling the request
     * @exception  UnavailableException 	
     *                   if the portlet is unavailable to perform render at this time
     * @exception  PortletSecurityException  
     *                   if the portlet cannot fullfill this request because of security reasons
     * @exception java.io.IOException
     *                   if the streaming causes an I/O problem
     *
     * @see #doView(RenderRequest, RenderResponse)
     * @see #doEdit(RenderRequest, RenderResponse)
     * @see #doHelp(RenderRequest, RenderResponse)
     */
    /*
    protected void doDispatch (RenderRequest request,
  			  RenderResponse response) throws PortletException,java.io.IOException
    {
      WindowState state = request.getWindowState();
      
      if ( ! state.equals(WindowState.MINIMIZED)) {
        PortletMode mode = request.getPortletMode();
        
        if (mode.equals(PortletMode.VIEW)) {
        	doView (request, response);
        }
        else if (mode.equals(PortletMode.EDIT)) {
        	doEdit (request, response);
        }
        else if (mode.equals(PortletMode.HELP)) {
        	doHelp (request, response);
        }
        else {
  	throw new PortletException("unknown portlet mode: " + mode);
        }
      }
    }
*/
    
}
