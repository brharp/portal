/*
 * $Id: WebCtSsoController.java,v 1.2 2006/07/17 18:24:34 brharp Exp $
 *
 * Copyright 2006 University of Guelph
 */

package ca.uoguelph.ccs.portal.webct;

import java.util.Map;
import java.util.List;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import org.springframework.web.portlet.mvc.AbstractController;
import org.springframework.web.portlet.ModelAndView;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ca.uoguelph.ccs.portal.UserInfo;

import javax.portlet.PortletSession;

/**
 * Provides auto sign-on to WebCT. This class does not function in
 * Academus 1.5 due to a bug in ActionResponse.sendRedirect. In
 * Academus 1.5, use WebCtSsoServlet for single sign-on instead.
 *
 * @see WebCtSsoServlet
 * @author $Author: brharp $
 * @version $Revision: 1.2 $
 */
public class WebCtSsoController extends AbstractController 
{
    public static final String URL = "url";

    private WebCtService webct;

    public void setWebCtService(WebCtService webct)
    {
        this.webct = webct;
    }

    /**
     * Signs the user into WebCT and redirects them to the URL
     * identified in the "url" parameter.
     *
     * @param request portlet render request
     * @param response portlet render response
     */
    public void handleActionRequestInternal(ActionRequest request, 
                                            ActionResponse response) 
        throws Exception 
    {
        Map userInfo = (Map)request.getAttribute(RenderRequest.USER_INFO);
        String userId = UserInfo.getId(userInfo);
        String timestamp = webct.getTimeStamp();
        String webctId = webct.getConsortiaId(userId, timestamp);
        String url = request.getParameter(URL);
        String ssoUrl = webct.getSingleSignonUrl(webctId, timestamp, url);
        response.sendRedirect(ssoUrl);
    }
}
