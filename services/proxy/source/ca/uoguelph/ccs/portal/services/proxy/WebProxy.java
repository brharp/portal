
package ca.uoguelph.ccs.portal.services.proxy;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.auth.*;
import org.apache.commons.httpclient.methods.*;
import org.apache.commons.logging.*;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.net.URLCodec;
import org.jasig.portal.security.*;
import org.jasig.portal.security.provider.*;

/**
 * HTTP proxy for AJAX requests
 */
public class WebProxy extends HttpServlet
{
    private ServletConfig servletConfig;
    private Log log;
    private URLCodec urlCodec;

    public static final String URL = "u";
    public static final String SECURE = "s";
    public static final String USER_PARAM = "up";
    public static final String PASS_PARAM = "pp";
    public static final String USER_VALUE = "uv";
    public static final String PASS_VALUE = "pv";

    public void init(ServletConfig servletConfig) throws ServletException
    {
        this.servletConfig = servletConfig;
        this.log = LogFactory.getLog(this.getClass());
        this.urlCodec = new URLCodec();
    }

    protected void doGet(HttpServletRequest req,
                         HttpServletResponse resp)
        throws ServletException,
               java.io.IOException
    {
        // Get the URL to fetch.
        String url = req.getParameter(URL);

        // If isSecure==true, the URL requires some form of
        // authentication. The default will be to use a proxy ticket,
        // which requires no user credentials to be passed in the
        // query parameters, however this has yet to be implemented.
        boolean isSecure = true;
        //Boolean.valueOf(req.getParameter(SECURE)).booleanValue();
        
        // If a username and password are supplied, they will be used
        // for authentication. Passwords are assumed to be encrypted
        // with the keychain. The default form of password based
        // authentication is HTTP Digest.
        String userValue = req.getParameter(USER_VALUE);
        String passValue = req.getParameter(PASS_VALUE);

        // If a username parameter and password parameter are
        // supplied, the user credentials are passed as URL parameters
        // to the remote resource, instead of using HTTP
        // authentication.
        String userParam = req.getParameter(USER_PARAM);
        String passParam = req.getParameter(PASS_PARAM);

        // Get external resource.
        HttpClient client = new HttpClient();
        GetMethod method = new GetMethod(url);

        if (isSecure) {
            // Never cache authenticated requests. The "Cache-Control:
            // no-cache" directive is honoured by all upstream
            // proxies, so setting it here effectively prevents any
            // authenticated request from being cached.
            resp.setHeader("Cache-Control", "no-cache");

            // If authentication credentials were supplied, add them
            // to the request.
            if (userValue != null && passValue != null) {
                // Replace @username@ and @password@ tokens with
                // actual values.
                if (userValue.equals("@username@")) 
                    userValue = getUsername(req);
                if (passValue.equals("@password@"))
                    passValue = getPassword(req);
                
                // If username and password parameter names have been
                // provided, then add the user credentials as request
                // parameters.
                if (userParam != null && passParam != null) {
                    try {
                        URI uri = method.getURI();
                        StringBuffer q = new StringBuffer();
                        q.append(urlCodec.encode(userParam));
                        q.append("=");
                        q.append(urlCodec.encode(userValue));
                        q.append("&");
                        q.append(urlCodec.encode(passParam));
                        q.append("=");
                        q.append(urlCodec.encode(passValue));
                        if (uri.getQuery() != null && uri.getQuery().length() > 0) {
                            q.append("&");
                            q.append(uri.getQuery());
                        }
                        method.setQueryString(q.toString());
                    }
                    catch(Exception e) {
                        log.error("Failed to add authentication to URL ["+url+"]");
                        throw new ServletException("Bad URI.");
                    }
                }
                // If no password or username parameters have been
                // set, attempt HTTP authentication.
                else {
                    URI uri = method.getURI();
                    client.getState().setCredentials
                        (new AuthScope(uri.getHost(), uri.getPort(), "realm"),
                         new UsernamePasswordCredentials(userValue, passValue));
                    method.setDoAuthentication(true);
                }
            }
        }
        
        //log.info(method.getURI());
        
        method.setRequestHeader("If-Modified-Since", req.getHeader("Last-Modified"));
        method.setRequestHeader("If-None-Match", req.getHeader("ETag"));
            
        try {
            // Execute request method.
            client.executeMethod(method);

            if (method.getStatusCode() < 400) {
                if (log.isInfoEnabled()) {
                    log.info(method.getStatusLine());
                }
            } else {
                if (log.isErrorEnabled()) {
                    log.error(method.getStatusLine());
                }
            }

            // Set response status.
            resp.setStatus(method.getStatusCode());

            // Process headers.
            Header[] headers = method.getResponseHeaders();
            for (int i = 0; i < headers.length; i++) {
                // Set the content type from the Content-Type header.
                if (headers[i].getName().equals("Content-Type")) {
                    resp.setContentType(headers[i].getValue());
                }
                // Do not override a Cache-Control setting, which may
                // have been previously set to "no-cache" to prevent
                // caching of authenticated requests.
                else if (headers[i].getName().equals("Cache-Control")) {
                    if (!resp.containsHeader("Cache-Control")) {
                        resp.setHeader("Cache-Control", headers[i].getValue());
                    }
                }
                // Copy any other headers to the response.
                else {
                    //resp.setHeader(headers[i].getName(), headers[i].getValue());
                }
            }

            // Write response body.
            /*
            InputStream input = method.getResponseBodyAsStream();
            PrintWriter output = resp.getWriter();
            int read = 0; byte[] buf = new byte[1024];
            while((read = input.read(buf)) > -1) {
                if (log.isDebugEnabled()) log.debug(buf.toString());
                output.write(buf.toString());
            }
            */
            byte[] responseBody = method.getResponseBody();
            resp.setContentLength(responseBody.length);
            OutputStream output = resp.getOutputStream();
            //PrintWriter output = resp.getWriter();
            output.write(responseBody);
            resp.flushBuffer();
        }
        catch (Exception e) {
            log.error(e);
        }
        finally {
            method.releaseConnection();
        }
    }


    private String getUsername(HttpServletRequest req)
    {
        HttpSession httpsession;
        httpsession = req.getSession();
        if (httpsession == null) return null;

        IPerson person;
        person = (IPerson)httpsession.getAttribute(IPerson.class.getName());
        if (person == null) return null;

        ISecurityContext security;
        security = person.getSecurityContext();

        return getUsername(security);
    }

    private String getPassword(HttpServletRequest req)
    {
        HttpSession httpsession;
        httpsession = req.getSession();
        if (httpsession == null) return null;

        IPerson person;
        person = (IPerson)httpsession.getAttribute(IPerson.class.getName());
        if (person == null) return null;

        ISecurityContext security;
        security = person.getSecurityContext();

        return getPassword(security);
    }

    private String getUsername(ISecurityContext sc)
    {
        return sc.getPrincipal().getUID();
    }

    private String getPassword(ISecurityContext sc)
    {
        IOpaqueCredentials oc = sc.getOpaqueCredentials();
        if (oc instanceof NotSoOpaqueCredentials) {
            return ((NotSoOpaqueCredentials)oc).getCredentials();
        } else {
            Enumeration en = sc.getSubContexts();
            while (en.hasMoreElements()) {
                String pw = getPassword((ISecurityContext)en.nextElement());
                if (pw != null) {
                    return pw;
                }
            }
        }
        return null;
    }
}
