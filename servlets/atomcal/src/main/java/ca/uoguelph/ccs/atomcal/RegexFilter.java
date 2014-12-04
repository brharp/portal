
package ca.uoguelph.ccs.atomcal;

import java.io.*;
import java.util.Iterator;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.regex.*;

public class RegexFilter implements Filter 
{
    private FilterConfig filterConfig = null;
    private String find = new String();
    private String replace = new String();

    public void doFilter(final ServletRequest request, 
                         final ServletResponse response, 
                         final FilterChain chain)
        throws IOException, ServletException 
    {
        // Get iCalendar response.

        final CharArrayWriter snk = new CharArrayWriter();

        ServletResponseWrapper wrapper = 
            new HttpServletResponseWrapper((HttpServletResponse)response)
            {
                public PrintWriter getWriter() {
                    return new PrintWriter(snk);
                }
            };

        chain.doFilter(request, wrapper);

        // Search/replace.

        Pattern searchPattern = Pattern.compile(find);
        Matcher matcher = searchPattern.matcher(snk.toString());

        if (matcher.matches()) {
            log("Response matches.");
        } else {
            log("Response does not match.");
        }

        String result = matcher.replaceAll(replace);

        response.getWriter().write(result);
    }
    
    public void init(FilterConfig filterConfig) 
    {
        this.filterConfig = filterConfig;

        String findParam = filterConfig.getInitParameter("find");
        if (findParam != null) {
            this.find = findParam;
        }
        filterConfig.getServletContext().log("[find="+find+"]");

        String replaceParam = filterConfig.getInitParameter("replace");
        if (replaceParam != null) {
            this.replace = replaceParam;
        }
        filterConfig.getServletContext().log("[replace="+replace+"]");
    }
    
    public void destroy()
    {
        this.filterConfig = null;   
    }

    private void log(String msg)
    {
        filterConfig.getServletContext().log(msg);
    }
}


