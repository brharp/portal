
package ca.uoguelph.ccs.atomcal;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

/**
 * Replaces an alias with the aliased request parameters for user ID,
 * webkey, etc.
 */
public class AliasFilter implements Filter
{
    public static final String ALIAS = "h";

    private FilterConfig filterConfig;
    private String bindUser = new String();
    private String bindPass = new String();

    public void init(FilterConfig filterConfig)
    {
        this.filterConfig = filterConfig;

        String bindUserParam = getInitParameter("userId");
        if (bindUserParam != null) {
            bindUser = bindUserParam;
        }
        log("[bindUser="+bindUser+"]");

        String bindPassParam = getInitParameter("password");
        if (bindPassParam != null) {
            bindPass = bindPassParam;
        }
        log("[bindPass="+(bindPass.length() > 0)+"]");
    }

    public String getInitParameter(String name)
    {
        return filterConfig.getInitParameter(name);
    }

    public void doFilter(final ServletRequest request,
                         final ServletResponse response,
                         final FilterChain chain)
        throws IOException,
               ServletException
    {
        if (!(request instanceof HttpServletRequest)) {
            chain.doFilter(request, response);
            return;
        }

        // Retrieve tha alias parameter.

        String alias;

        alias = request.getParameter(ALIAS);

        if (alias == null) {
            chain.doFilter(request, response);
            return;
        }

        // Load public feed properties.

        Properties props = AliasProperties.getProperties();

        // Lookup alias in property file and retrieve user ID and
        // webkey.

        ParameterRequestWrapper wrequest 
            = new ParameterRequestWrapper((HttpServletRequest)request);

        String searchPrefix = "alias.param." + alias + ".";
        int searchPrefixLen = searchPrefix.length();

        for (Enumeration i = props.propertyNames(); i.hasMoreElements();) {
            String propertyName = (String)i.nextElement();
            if (propertyName.startsWith(searchPrefix)) {
                String parameterName = propertyName.substring(searchPrefixLen);
                String parameterValue = props.getProperty(propertyName);
                log("Found alias parameter: " + parameterName + "=" + parameterValue);
                wrequest.addParameter(parameterName, parameterValue);
            }
        }

        // Add basic authentication header.

        String cred = Base64.encodeString(bindUser + ":" + bindPass);
        wrequest.setHeader("Authorization", "Basic " + cred);
        
        chain.doFilter(wrequest, response);
    }

    private void log(String msg)
    {
        filterConfig.getServletContext().log(msg);
    }

    public void destroy()
    {
        filterConfig = null;
    }
}
