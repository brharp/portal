package ca.uoguelph.ccs.portal.modules;

import java.util.*;
import javax.servlet.http.*;

public class ConditionalRequest extends HttpServletRequestWrapper
{
    private Http11CacheInfo entity;

    public ConditionalRequest(HttpServletRequest request,
                              Http11CacheInfo entity)
    {
        super(request);
        this.entity = entity;
    }

    public Enumeration getHeaderNames()
    {
        List headers = Arrays.asList(new String[] {"If-Modified-Since",
                                                   "If-None-Match"});
        headers.addAll(Collections.list(super.getHeaderNames()));
        return Collections.enumeration(headers);
    }

    public String getHeader(String name)
    {
        if ("If-Modified-Since".equals(name)) {
            return entity == null ? null : entity.getHeader("Last-Modified");
        } else if ("If-None-Match".equals(name)) {
            return entity == null ? null : entity.getHeader("ETag");
        } else {
            return super.getHeader(name);
        }
    }
}

