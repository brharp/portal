package ca.uoguelph.ccs.portal.portlets.elmportlet;

import java.io.IOException;
import java.io.PrintStream;

import javax.portlet.GenericPortlet;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.PortletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author lalit
 *
 * Portlet for integrating software distribution site from e-academy
 */
public class ElmPortlet extends GenericPortlet
{
    protected void doView(RenderRequest request, RenderResponse response)
        throws PortletException, IOException
    {
		Map elmsUserInfo = (Map)request.getAttribute(PortletRequest.USER_INFO);
		String userName;
		String lastName;
		String organizationalStatus;

		if (null != elmsUserInfo){
			userName = (String)elmsUserInfo.get("user.login.id");
			lastName = (String)elmsUserInfo.get("user.name.last");
			organizationalStatus = (String)elmsUserInfo.get("uPortalTemplateUserName");
		}

        request.setAttribute("elmsUserInfo", elmsUserInfo);
		
        response.setContentType("text/html");
        PortletContext portletContext = getPortletContext();

        /*PortletRequestDispatcher reqDispatcher =
            portletContext.getRequestDispatcher("/hello");
        reqDispatcher.include(request, response); */

	String paramExpiry = "1";
	response.setProperty(
	    RenderResponse.EXPIRATION_CACHE, paramExpiry) ;

        PortletRequestDispatcher namedDispatcher =
            portletContext.getNamedDispatcher("ElmServlet");
        namedDispatcher.include(request, response);

    }
}
