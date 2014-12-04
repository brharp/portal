
/*
 * $Id: WebCtSsoServlet.java,v 1.2 2006/11/27 21:01:06 brharp Exp $
 *
 * Copyright 2006 University of Guelph
 */

package ca.uoguelph.ccs.portal.webct;

import javax.servlet.http.*;
import java.util.*;
import org.springframework.web.servlet.FrameworkServlet;

/**
 * WebCT Single Sign-on.
 *
 * <p>
 *
 * This servlet provides auto sign-on capability for WebCT. It expects
 * three parameters:
 *
 * <table>
 *   <tr><th>Name</th><th>Type</th><th>Description</th></tr>
 *
 *   <tr>
 *     <td>url</td>
 *     <td>{@link java.lang.String}</td>
 *     <td>
 *
 *       The path to a WebCT page, ie. "/webct/viewMyWebCT.dowebct".
 *
 *     </td>
 *   </tr>
 *
 *   <tr>
 *     <td>user</td>
 *     <td>{@link java.lang.String}</td>
 *     <td>
 *
 *       The user's WebCT ID. The user will be logged in to this
 *       account.
 *
 *     </td>
 *   </tr>
 *
 *   <tr>
 *     <td>nonce</td>
 *     <td>{@link java.lang.String}</td>
 *     <td>
 *
 *       A security token.
 *
 *     </td>
 *   </tr>
 * </table>
 *
 * <p>
 * 
 * The security token (nonce) is computed as (where a period (.)
 * indicates string concatenation):
 *
 * <blockquote>
 *
 *   timestamp . ":" . MD5( user . timestamp . secret )
 *
 * </blockquote>
 *
 * The servlet checks the validity of the security token with each
 * request. If the nonce is invalid or has expired, the user is 
 * directed to the WebCT login page.
 */
public class WebCtSsoServlet extends FrameworkServlet
{
    public static final String URL = "url";
    public static final String USER = "user";
    public static final String NONCE = "nonce";

    private WebCtService webct;

    public void setWebCtService(WebCtService webct)
    {
        this.webct = webct;
    }

    protected void doService(HttpServletRequest request,
                             HttpServletResponse response)
        throws Exception
    {
        String url = request.getParameter(URL);
        String userId = request.getParameter(USER);
        String nonce = request.getParameter(NONCE);

        // Security check.
        if (webct.checkNonce(userId, nonce)) {
            String timestamp = webct.getTimeStamp();
            String webctId = webct.getConsortiaId(userId, timestamp);
            String ssoUrl = webct.getSingleSignonUrl(webctId, timestamp, url);
            System.out.println("WebCT SSO: Redirecting to " + ssoUrl);
            response.sendRedirect(ssoUrl);
        } else {
            // Security check has failed, either because the nonce
            // expired or a malicious request. Redirect them to the
            // plain URL so that WebCT can handle authentication.
            String loginUrl = webct.formatUrl(url, null);
            System.out.println("WebCT SSO: Bad nonce, redirecting to " + loginUrl);
            response.sendRedirect(loginUrl);
        }
    }

    protected void initFrameworkServlet()
    {
        setWebCtService((WebCtService)getWebApplicationContext().getBean("webCtService"));
    }
}
