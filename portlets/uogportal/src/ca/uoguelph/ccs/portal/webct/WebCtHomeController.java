/*
 * $Id: WebCtHomeController.java,v 1.2 2006/07/17 18:24:34 brharp Exp $
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
import javax.portlet.PortletURL;
import javax.portlet.WindowState;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.portlet.mvc.AbstractController;
import org.springframework.web.portlet.ModelAndView;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ca.uoguelph.ccs.portal.UserInfo;

import javax.portlet.PortletSession;

import org.w3c.dom.Node;

/**
 *
 * @author $Author: brharp $
 * @version $Revision: 1.2 $
 */
public class WebCtHomeController extends AbstractController 
{
    public static final String URL = "url";

    private WebCtService webct;

    /**
     * Sets the webCtService.
     * 
     * @param webct the WebCtService to set.
     */
    public void setWebCtService(WebCtService webct)
    {
        this.webct = webct;
    }

    /**
     * Renders the WebCT view.
     *
     * <p>
     *
     * Model:
     *
     * <table>
     *   <tr><th>Key</th><th>Type</th><th>Description</th></tr>
     *
     *   <tr>
     *     <td>homeAreaXml</td>
     *     <td>{@link java.lang.String}</td>
     *     <td>
     *       The "home area" XML returned by WebCT.
     *     </td>
     *   </tr>
     *
     *   <tr>
     *     <td>user</td>
     *     <td>{@link java.lang.String}</td>
     *     <td>
     *       The user's ID. Must be passed to the SSO servlet
     *       when attempting auto signon to WebCT.
     *     </td>
     *   </tr>
     *
     *   <tr>
     *     <td>nonce</td>
     *     <td>{@link java.lang.String}</td>
     *     <td>
     *       A security token. Must be passed to the SSO servlet
     *       when attempting auto signon to WebCT. 
     *     </td>
     *   </tr>
     * </table>
     *
     * <p>
     *
     * The home area XML string will be parsed and rendered by the
     * view. See the home view for details on the XML format. Any
     * WebCT links in the home area must be passed to the autosignon
     * servlet.
     *
     * <p>
     *
     * The user ID and nonce values are used by the WebCT autosignon
     * servlet. These values are a hack: they would not be necessary
     * if not for a bug in Academus. Ideally, we would transform any
     * WebCT links to invoke the {@link WebCtSsoController}, which
     * could use the portlet session to validate the request. However,
     * since ActionResponse.sendRedirect() is broken in Academus 1.5
     * we can not use this approach. Instead, auto signon URLs are
     * passed to a seperate servlet. Since the SSO servlet is outside
     * of the portal context, we must pass a security token to
     * validate the link originated with the portal. Hence the user ID
     * and nonce must be passed along with the URL when invoking the
     * SSO servlet.
     *
     * @see WebCtSsoServlet
     * @see WebCtSsoController
     * @param request portlet render request
     * @param response portlet render response
     * @return a model and view
     */
    public ModelAndView handleRenderRequestInternal(RenderRequest request, 
                                                    RenderResponse response) 
        throws Exception 
    {
        Map userInfo = (Map)request.getAttribute(RenderRequest.USER_INFO);
        String userId = UserInfo.getId(userInfo);
        String timestamp = webct.getTimeStamp();
        String webCtId = webct.getConsortiaId(userId, timestamp);
        String homeAreaXml = webct.getHomeAreaXml(webCtId, timestamp);
        String nonce = webct.getNonce(userId, timestamp);
        Map model = new java.util.HashMap();
        model.put("homeAreaXml", homeAreaXml);
        model.put("user", userId);
        model.put("nonce", nonce);
        return new ModelAndView("webct/home", model);
    }
}
