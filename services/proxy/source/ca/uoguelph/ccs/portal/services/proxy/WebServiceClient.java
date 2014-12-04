
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
 * The web service client defines a web service end point that may be
 * called by client javascript applications. The service URL must be
 * specified in the servlet configuration.
 */
public class WebServiceClient extends HttpServlet
{
    private ServletConfig config;
    private Log log;
    private URLCodec urlCodec;
    private String wsurl;
    private String rsurl;
    
    public static final String WSURL = "service-url";
    public static final String RSURL = "results-url";
    public static final String URLENCODED ="application/x-www-form-urlencoded";

    private static final boolean DEBUG = true;

    public void init(ServletConfig servletConfig) throws ServletException
    {
        this.config = servletConfig;
        this.log = LogFactory.getLog(this.getClass());
        this.urlCodec = new URLCodec();

        if ((wsurl = config.getInitParameter(WSURL)) == null) {
            throw new ServletException(WSURL + " is null.");
        }

        rsurl = config.getInitParameter(RSURL);
    }

    protected void doGet(HttpServletRequest req,
                         HttpServletResponse resp)
        throws ServletException,
               java.io.IOException
    {
        ISecurityContext sc = getSecurityContext(req);

        if (sc == null) {
            throw new ServletException("Not authenticated.");
        }
            
        String username = getUsername(sc);
        String password = getPassword(sc);

        if (username == null || password == null) {
            throw new ServletException("Not authenticated.");
        }

        HttpClient client = new HttpClient();
        GetMethod method = new GetMethod(wsurl);

        try {
            StringBuffer qs = new StringBuffer();
            char sep = '?';
            for(Enumeration e = req.getParameterNames();e.hasMoreElements();) {
                String name = (String)e.nextElement();
                String value = req.getParameter(name);
                qs.append(sep);
                qs.append(urlCodec.encode(name));
                qs.append('=');
                if ("@username@".equals(value)) {
                    qs.append(urlCodec.encode(username));
                } else if ("@password@".equals(value)) {
                    qs.append(urlCodec.encode(password));
                } else {
                    qs.append(urlCodec.encode(value));
                }
                sep = '&';
            }
            method.setQueryString(qs.toString());
        }
        catch(EncoderException ee) {
            throw new ServletException(ee);
        }
        
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

            byte[] responseBody = method.getResponseBody();

            if (DEBUG) {
                System.out.println(new String(responseBody));
            }

            if (rsurl != null) {
                resp.setStatus(200);
                resp.setContentType("text/html");
                PrintWriter output = resp.getWriter();
                output.write("<html>");
                output.write("<head>");
                output.write("<meta http-equiv=\"Content-type\" ");
                output.write("content=\"text/html; charset=UTF-8\">");
                output.write("<meta http-equiv=\"refresh\" ");
                output.write("content=\"0; URL=");
                output.write(rsurl);
                output.write("\">");
                output.write("</head>");
                output.write("</html>");
                output.close();
            }
            else {
                // Set response status.
                resp.setStatus(method.getStatusCode());
                
                // Process headers.
                Header[] headers = method.getResponseHeaders();
                for (int i = 0; i < headers.length; i++) {
                    String name = headers[i].getName();
                    String value = headers[i].getValue();
                    if (name.equals("Content-Type")) {
                        resp.setContentType(value);
                    }
                    if (name.equals("Content-Length")) {
                        try {
                            resp.setContentLength(Integer.parseInt(value));
                        } catch(Exception e) {}
                    }
                }
                
                // Write response body.
                OutputStream output = resp.getOutputStream();
                output.write(responseBody);
                resp.flushBuffer();
            }
        }
        catch (Exception e) {
            log.error(e);
        }
        finally {
            method.releaseConnection();
        }
    }

    protected void doPost(HttpServletRequest req,
                          HttpServletResponse resp)
        throws ServletException,
               java.io.IOException
    {
        ISecurityContext sc = getSecurityContext(req);

        if (sc == null) {
            throw new ServletException("Not authenticated.");
        }
            
        String username = getUsername(sc);
        String password = getPassword(sc);

        if (username == null || password == null) {
            throw new ServletException("Not authenticated.");
        }

        HttpClient client = new HttpClient();
        PostMethod method = new PostMethod(wsurl);

        try {
            StringBuffer qs = new StringBuffer();
            String sep = "";
            for(Enumeration e = req.getParameterNames();e.hasMoreElements();) {
                String name = (String)e.nextElement();
                String value = req.getParameter(name);
                qs.append(sep);
                qs.append(urlCodec.encode(name));
                qs.append('=');
                if ("@username@".equals(value)) {
                    qs.append(urlCodec.encode(username));
                } else if ("@password@".equals(value)) {
                    qs.append(urlCodec.encode(password));
                } else {
                    qs.append(urlCodec.encode(value));
                }
                sep = "&";
            }
            if (DEBUG) {
                System.out.println("WS: Posting " + qs.toString());
            }
            RequestEntity re;
            re = new StringRequestEntity(qs.toString(), URLENCODED, "UTF-8");
            method.setRequestEntity(re);
        }
        catch(EncoderException ee) {
            throw new ServletException(ee);
        }
        
        try {
            if (rsurl != null) {
                method.setRequestHeader("Referer", rsurl);
            }

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

            byte[] responseBody = method.getResponseBody();

            if (DEBUG) {
                System.out.println(new String(responseBody));
            }

            if (rsurl != null) {
                resp.setStatus(200);
                resp.setContentType("text/html");
                PrintWriter output = resp.getWriter();
                output.write("<html>");
                output.write("<head>");
                output.write("<meta http-equiv=\"Content-type\" ");
                output.write("content=\"text/html; charset=UTF-8\">");
                output.write("<meta http-equiv=\"refres\" ");
                output.write("content=\"0; URL=");
                output.write(rsurl);
                output.write("\">");
                output.write("</head>");
                output.write("</html>");
                output.close();
            }
            else {
                // Set response status.
                resp.setStatus(method.getStatusCode());
                
                // Process headers.
                Header[] headers = method.getResponseHeaders();
                for (int i = 0; i < headers.length; i++) {
                    String name = headers[i].getName();
                    String value = headers[i].getValue();
                    if (name.equals("Content-Type")) {
                        resp.setContentType(value);
                    }
                    if (name.equals("Content-Length")) {
                        try {
                            resp.setContentLength(Integer.parseInt(value));
                        } catch(Exception e) {}
                    }
                }
                
                // Write response body.
                OutputStream output = resp.getOutputStream();
                output.write(responseBody);
                resp.flushBuffer();
            }
        }
        catch (Exception e) {
            log.error(e);
        }
        finally {
            method.releaseConnection();
        }
    }

    private ISecurityContext getSecurityContext(HttpServletRequest req)
    {
        HttpSession httpsession;
        httpsession = req.getSession();

        if (httpsession == null) {
            return null;
        }

        IPerson person;
        person = (IPerson)httpsession.getAttribute(IPerson.class.getName());

        if (person == null) {
            return null;
        }

        ISecurityContext security;
        security = person.getSecurityContext();

        return security;
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
