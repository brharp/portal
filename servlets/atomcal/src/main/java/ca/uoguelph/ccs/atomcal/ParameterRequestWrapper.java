
package ca.uoguelph.ccs.atomcal;

import java.util.*;
import javax.servlet.http.*;

public class ParameterRequestWrapper extends HttpServletRequestWrapper
{
    private Map parameters;
    private Map headers;
    private String[] EMPTY = new String[0];

    public ParameterRequestWrapper(HttpServletRequest request)
    {
        super(request);
        parameters = new HashMap();
        headers = new HashMap();
    }

    public String getParameter(String name)
    {
        List values = (List)parameters.get(name);
        if (values == null) {
            return null;
        } else {
            return (String)values.get(0);
        }
    }
    
    public Map getParameterMap()
    {
        Map map = new HashMap();
        for(Iterator i = parameters.keySet().iterator(); i.hasNext();) {
            String key = (String)i.next();
            map.put(key, getParameterValues(key));
        }
        return Collections.unmodifiableMap(map);
    }

    public Enumeration getParameterNames()
    {
        return Collections.enumeration(parameters.keySet());
    }

    public String[] getParameterValues(String name)
    {
        List values = (List)parameters.get(name);
        if (values == null) {
            return null;
        } else {
            return (String[])values.toArray(EMPTY);
        }
    }

    public void addParameter(String name, String value)
    {
        List values = (List)parameters.get(name);
        if (values == null) {
            values = new ArrayList();
            parameters.put(name, values);
        }
        values.add(value);
    }

    public String getHeader(String name)
    {
        List values = (List)headers.get(name);
        if (values == null) {
            return null;
        } else {
            return (String)values.get(0);
        }
    }
    
    public Map getHeaderMap()
    {
        Map map = new HashMap();
        for(Iterator i = headers.keySet().iterator(); i.hasNext();) {
            String key = (String)i.next();
            map.put(key, getHeaderValues(key));
        }
        return Collections.unmodifiableMap(map);
    }

    public Enumeration getHeaderNames()
    {
        return Collections.enumeration(headers.keySet());
    }

    public String[] getHeaderValues(String name)
    {
        List values = (List)headers.get(name);
        if (values == null) {
            return null;
        } else {
            return (String[])values.toArray(EMPTY);
        }
    }

    public void addHeader(String name, String value)
    {
        List values = (List)headers.get(name);
        if (values == null) {
            values = new ArrayList();
            headers.put(name, values);
        }
        values.add(value);
    }

    public void setHeader(String name, String value)
    {
        List values = new ArrayList();
        values.add(value);
        headers.put(name, values);
    }            
}
