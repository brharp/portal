/* 
 * Copyright 2006 University of Guelph.
 */
package ca.uoguelph.ccs.atomcal;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.w3c.dom.*;

/**
 * Transforms an XML response with the configured stylesheet.
 *
 * <p>
 *
 * Assumes an XML response.
 *
 * <p>
 *
 * Request parameters are exposed to the stylesheet as parameters
 * prefixed by "param_". This prefix string may be changed by setting
 * the init parameter "parameterPrefix".
 *
 * <p>
 *
 * CGI style "environment variables" are exposed to the stylesheet as
 * parameters prefixed with "env_". This prefix string may be changed
 * by setting the init parameter "environmentPrefix".
 *
 * <p>
 *
 * All filter init parameters are passed unmodified to the stylesheet
 * as XSLT parameters.
 * 
 * @author M. Brent Harp <brharp@uoguelph.ca>
 */
public class XsltFilter implements Filter 
{
    public static final String DEFAULT_PARAMETER_PREFIX = "param_";
    public static final String DEFAULT_ENVIRONMENT_PREFIX = "env_";

    private FilterConfig filterConfig = null;
    private File   styleFile = null;
    private long   styleFileLastRead = 0;
    private String paramPrefix = DEFAULT_PARAMETER_PREFIX;
    private String envPrefix = DEFAULT_ENVIRONMENT_PREFIX;
    private String contentType = "text/xml";
    private List   pool;

    public static final boolean DEBUG = false;

    private synchronized Transformer allocate() throws Exception
    {
        // Invalidate pool when stylesheet is modified.
        if (styleFile.exists() && styleFile.lastModified() > styleFileLastRead) {
            log("Clearing stylesheet cache.");
            pool.clear();
        }
        // Return an existing transformer if possible.
        if (pool.size() > 0) {
            log("Reusing cached stylesheet.");
            return (Transformer)pool.remove(0);
        }
        else {
            log("Allocating new stylesheet.");
            TransformerFactory factory = TransformerFactory.newInstance();
            Source styleSource = new StreamSource(styleFile);
            Transformer transformer = factory.newTransformer(styleSource);
            styleFileLastRead = System.currentTimeMillis();
            return transformer;
        }
    }

    private synchronized void free(Transformer transformer)
    {
        transformer.clearParameters();
        pool.add(transformer);
    }

    public void doFilter(final ServletRequest request, 
                         final ServletResponse response, 
                         final FilterChain chain)
        throws IOException, ServletException 
    {
        // Get XML output.

        final CharArrayWriter snk = new CharArrayWriter();

        ServletResponseWrapper wrapper = 
            new HttpServletResponseWrapper((HttpServletResponse)response)
            {
                public PrintWriter getWriter() {
                    return new PrintWriter(snk);
                }
            };

        chain.doFilter(request, wrapper);

        String wrappedResponse = snk.toString();

        // Do transformation.
        Transformer xformer = null;
        try {
            xformer = allocate();
            setupEnvironment(xformer, request);
            
            // Request parameters are made available to the stylesheet as
            // param_<name>.
                
            for (Enumeration i = request.getParameterNames(); i.hasMoreElements();) {
                String paramName = (String)i.nextElement();
                String paramValue = request.getParameter(paramName);
                xformer.setParameter(paramPrefix+paramName, paramValue);
            }
            
            // The filter passes all init parameters directly to the stylesheet.
            
            for (Enumeration i = filterConfig.getInitParameterNames(); i.hasMoreElements();) {
                String paramName = (String)i.nextElement();
                String paramValue = filterConfig.getInitParameter(paramName);
                xformer.setParameter(paramName, paramValue);
            }

            response.setContentType(contentType);
            
            Source in  = new StreamSource(new StringReader(wrappedResponse));
            Result out = new StreamResult(response.getWriter());
            xformer.transform(in, out);
        }
        catch(Exception e) {
            throw new ServletException(e);
        }
        finally {
            if (xformer != null) free(xformer);
        }
    }
    
    public void init(FilterConfig filterConfig) 
    {
        this.filterConfig = filterConfig;

        String stylesheetPathParam = filterConfig.getInitParameter("stylesheet");
        if (stylesheetPathParam != null) {
            String stylesheetPath = filterConfig.getServletContext()
                .getRealPath(stylesheetPathParam);
            this.styleFile = new File(stylesheetPath);
        }
        
        String paramPrefixParam = filterConfig.getInitParameter("parameterPrefix");
        if (paramPrefixParam != null) {
            this.paramPrefix = paramPrefixParam;
        }

        String envPrefixParam = filterConfig.getInitParameter("environmentPrefix");
        if (envPrefixParam != null) {
            this.envPrefix = envPrefixParam;
        }

        String contentTypeParam = filterConfig.getInitParameter("contentType");
        if (contentTypeParam != null) {
            this.contentType = contentTypeParam;
        }

        this.pool = new LinkedList();
    }
    
    public void destroy()
    {
        this.filterConfig = null;   
    }

    private void setupEnvironment(Transformer xformer, ServletRequest request)
    {
        // Pass CGI style environment variables.
        if (request.getContentLength() > -1) {
            xformer.setParameter(envPrefix+"CONTENT_LENGTH", 
                                 String.valueOf(request.getContentLength()));
        }
        if (request.getContentType() != null) {
            xformer.setParameter(envPrefix+"CONTENT_TYPE", request.getContentType());
        }
        if (request.getProtocol() != null) {
            xformer.setParameter(envPrefix+"SERVER_PROTOCOL", request.getProtocol());
        }
        if (request.getRemoteAddr() != null) {
            xformer.setParameter(envPrefix+"REMOTE_ADDR", request.getRemoteAddr());
        }
        if (request.getRemoteHost() != null) {
            xformer.setParameter(envPrefix+"REMOTE_HOST", request.getRemoteHost());
        }
        if (request.getServerName() != null) {
            xformer.setParameter(envPrefix+"SERVER_NAME", request.getServerName());
        }
        if (request.getServerPort() > -1) {
            xformer.setParameter(envPrefix+"SERVER_PORT", 
                                     String.valueOf(request.getServerPort()));
        }
        
        // The XSLT filter is HTTP aware: it will pass
        // HttpServletRequest properties as CGI style environment
        // variables prefixed by the envPrefix (which defaults to
        // "env_"
        if (request instanceof HttpServletRequest) {
            HttpServletRequest httpRequest = (HttpServletRequest)request;
            if (httpRequest.getAuthType() != null) {
                xformer.setParameter(envPrefix+"AUTH_TYPE", httpRequest.getAuthType());
            }
            if (httpRequest.getMethod() != null) {
                xformer.setParameter(envPrefix+"REQUEST_METHOD", httpRequest.getMethod());
            }
            if (httpRequest.getPathInfo() != null) {
                xformer.setParameter(envPrefix+"PATH_INFO", httpRequest.getPathInfo());
            }
            if (httpRequest.getPathTranslated() != null) {
                xformer.setParameter(envPrefix+"PATH_TRANSLATED", httpRequest.getPathTranslated());
            }
            if (httpRequest.getQueryString() != null) {
                xformer.setParameter(envPrefix+"QUERY_STRING", httpRequest.getQueryString());
            }
            if (httpRequest.getRemoteUser() != null) {
                xformer.setParameter(envPrefix+"REMOTE_USER", httpRequest.getRemoteUser());
            }
            if (httpRequest.getServletPath() != null) {
                xformer.setParameter(envPrefix+"SCRIPT_NAME", httpRequest.getServletPath());
            }
        }
    }

    private void log(String msg) 
    {
        if (DEBUG) {
            System.out.println(filterConfig.getFilterName() + ": " + msg);
        } else {
            filterConfig.getServletContext().log(msg);
        }
    }
}
