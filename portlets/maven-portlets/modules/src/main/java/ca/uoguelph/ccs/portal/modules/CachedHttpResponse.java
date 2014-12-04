package ca.uoguelph.ccs.portal.modules;

import java.io.*;
import java.util.*;
import java.text.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class CachedHttpResponse extends HttpServletResponseWrapper
{
    public int status;
    public Map headers;
    private CharArrayWriter snk;
    private String contentType;

    private DateFormat rfc1123 = new SimpleDateFormat("EEE, dd MMM yyyy kk:mm:ss z");
    private DateFormat rfc1036 = new SimpleDateFormat("EEEE, dd-MMM-yy kk:mm:ss z");
    private DateFormat asctime = new SimpleDateFormat("EEE MMM d kk:mm:ss yyyy");

    public CachedHttpResponse(HttpServletResponse base)
    {
        super(base);
        snk = new CharArrayWriter();
        headers = new HashMap();
    }
    
    public PrintWriter getWriter() {
        return new PrintWriter(snk);
    }
    
    public void setStatus(int sc) {
        status = sc;
    }
    
    public int getStatus() { return status; }
    
    public void setHeader(String name, String value) {
        headers.put(name, value);
    }
    
    public String getHeader(String name) {
        return (String)headers.get(name);
    }

    public int getIntHeader(String name) {
        try {
            return Integer.parseInt(getHeader(name));
        } catch (Exception e) {
            return -1;
        }
    }
    
    public long getDateHeader(String name) {
        String value = getHeader(name);
        if (value != null) {
            try { return rfc1123.parse(value).getTime(); }
            catch(ParseException pe1) {}
            try { return rfc1036.parse(value).getTime(); }
            catch(ParseException pe2) {}
            try { return asctime.parse(value).getTime(); }
            catch(ParseException pe3) {}
        }
        return -1;
    }

    public Map getHeaders() { return headers; }

    public String getResponseBodyAsString() {
        return snk.toString();
    }

    public void setContentType(String type)
    {
        this.contentType = type;
    }

    public String getContentType()
    {
        return contentType;
    }
}
