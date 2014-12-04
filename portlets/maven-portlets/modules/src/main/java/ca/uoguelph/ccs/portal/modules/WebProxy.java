
package ca.uoguelph.ccs.portal.modules;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import net.sf.ehcache.*;
import org.apache.commons.logging.*;

/** 
 * The WebProxy is a caching filter that sits in front of the
 * ProxyServlet. It handles all the details of HTTP caching.
 * 
 * All responses from this proxy will have an Expires
 * header, either copied from the remote server or
 * calculated from a Last-Modified header or the current
 * time. Compliant user agents will honour this header and
 * not make repeated requests for the resource while it is
 * valid.
 * 
 * All responses from this proxy will also have
 * Last-Modified and ETag headers. When the resource
 * expires in the user agent, it will revalidate its
 * cache. A compliant user agent will send
 * If-Modified-Since or If-None-Match headers with the
 * values of Last-Modifed or ETag.
 * 
 * If the values of If-Modified-Since or If-None-Match
 * match the Last-Modified or ETag values in the cache,
 * and the locally cached copy is still valid, the
 * response will be 304 (Not Modified). If there are no
 * If-Modified-Since or If-None-Match headers, or they do
 * not match the cached values of Last-Modified or ETag,
 * and the locally cached copy is valid, the response will
 * be 200 (OK) with the cached copy of the resource.
 * 
 * If the resource is not cached locally, or the locally
 * cached copy is not valid, then the proxy will
 * revalidate its cached copy of the document. The proxy
 * will rerequest the resource, sending If-Modified-Since
 * and If-None-Match headers with the cached values of
 * Last-Modified and ETag, if available. If the response
 * from the remote server is 304 (Not Modified), the
 * expiration date of the cached copy will be updated, but
 * the cached copy will otherwise be unmodified. If the
 * response, had the cached copy been valid, would have
 * been 304, the response will be 304. Otherwise the
 * response will be 200 with the cached copy of the
 * resource. If the response from the remote server is
 * 200, the cached copy is replaced with the fresh copy
 * and the proxy responds with 200 and the new content.
 */
public class WebProxy implements Filter
{
    private FilterConfig servletConfig;
    //private Cache cache;
    private Map cache;
    private Log log;

    public static final String WEB_CACHE = "WebProxyCache";

    public void init(FilterConfig servletConfig) throws ServletException
    {
        this.servletConfig = servletConfig;
        this.log = LogFactory.getLog(WebProxy.class);
        //this.cache = CacheManager.getInstance().getCache(WEB_CACHE);
        this.cache = new HashMap();
    }

    public void destroy()
    {
    }

    protected boolean isFreshResponseRequired(HttpServletRequest request)
    {
        return false;
    }

    protected String getKey(HttpServletRequest request)
    {
        if (log.isInfoEnabled()) {
            //log.info("Caching entity with key " + request.getParameter("u"));
        }
        return request.getParameter("u");
    }

    protected boolean isError(CachedHttpResponse response)
    {
        return response.getStatus() > 399;
    }

    protected boolean isNotModified(CachedHttpResponse response)
    {
        return response.getStatus() == 304;
    }
    
    public void doFilter(ServletRequest request, 
                         ServletResponse response, 
                         FilterChain chain)
        throws ServletException,
               java.io.IOException
    {
        doFilter((HttpServletRequest)request,
                 (HttpServletResponse)response,
                 chain);
    }

    /**
     * Caches requests with an HTTP/1.1 compliant cache.
     */
    public void doFilter(HttpServletRequest request, 
                         HttpServletResponse response, 
                         FilterChain chain)
        throws ServletException,
               java.io.IOException
    {
        Http11CacheInfo ent = null;
        
        ent = (Http11CacheInfo)cache.get(getKey(request));
        //Element el = cache.get(getKey(request));
        //if (el != null) {
        //ent = (Http11CacheInfo)el.getValue();
            //}
        
        if (log.isInfoEnabled()) {
            if (ent == null) {
                log.info("Entity " + getKey(request) + " is not cached.");
            } else if (ent.isExpired()) {
                log.info("Entity " + getKey(request) + " is expired.");
            }
        }

        if (ent == null || ent.isExpired()) {
            /*
              13.4 Response Cacheability

              Unless specifically constrained [...], a caching system
              [...] MAY return [a cached response] after successful
              validation.
            */
            ServletRequestWrapper wrappedRequest = new ConditionalRequest(request,ent);
            CachedHttpResponse wrapper = new CachedHttpResponse(response);

            long requestTime = System.currentTimeMillis();

            chain.doFilter(wrappedRequest, wrapper);

            long responseTime = System.currentTimeMillis();
            
            // OR: cache.service(request, response, validator); ????
            
            if (isNotModified(wrapper)) {
                /*
                  13.5.3 Combining Headers

                  When a cache makes a validating request to a server,
                  and the server provides a 304 (Not Modified)
                  response or a 206 (Partial Content) response, [t]he
                  end-to-end headers stored in the cache entry are
                  used for the constructed response, except that
                  
                  - any stored Warning headers with warn-code 1xx (see
                  section 14.46) MUST be deleted from the cache entry
                  and the forwarded response.
                  
                  - any stored Warning headers with warn-code 2xx MUST
                  be retained in the cache entry and the forwarded
                  response.
                  
                  - any end-to-end headers provided in the 304 or 206
                  response MUST replace the corresponding headers from
                  the cache entry.
                */
                if (log.isInfoEnabled()) {
                    log.info(getKey(request) + " is not modified.");
                }
                ent.removeCacheWarnings();
                ent.setEndToEndHeaders(wrapper.getHeaders());
            }
            else if (isError(wrapper)) {
                /*
                  13.1.2 Warnings
                  
                  Whenever a cache returns a response that is neither
                  first-hand nor "fresh enough" [...], it MUST attach
                  a warning to that effect.
                */
                /*
                  13.1.5 Exceptions to the Rules and Warnings

                  In some cases, the operator of a cache MAY choose to
                  configure it to return stale responses even when not
                  requested by clients. [...] Whenever a cache returns
                  a stale response, it MUST mark it as such (using a
                  Warning header) enabling the client software to
                  alert the user that there might be a potential
                  problem. [...] (A) cache SHOULD NOT return a stale
                  response if the client explicitly requests a
                  first-hand or fresh one [...].
                */
                if (log.isErrorEnabled()) {
                    log.error("Error requesting " + getKey(request) +
                              " (" + wrapper.getStatus() + ")");
                }
                if (! isFreshResponseRequired(request)) {
                    wrapper.addHeader("Warning", "Response is stale.");
                    // send cached entity.
                }
            } else {
                ent = new Http11CacheInfo(wrapper.getHeaders(), 
                                          wrapper.getResponseBodyAsString());
                
                if (isCacheable(wrapper)) {
                    log.info("Response is cacheable");
                    ent.setRequestTime(requestTime);
                    ent.setResponseTime(responseTime);
                    ent.setAgeValue(wrapper.getIntHeader("Age"));
                    ent.setDateValue(wrapper.getDateHeader("Date"));
                    ent.setExpiresValue(wrapper.getDateHeader("Expires"));
                    ent.setLastModified(wrapper.getDateHeader("Last-Modified"));
                    log.info("Caching response to " + getKey(request));
                    //cache.put(new Element(getKey(request), ent));
                    cache.put(getKey(request), ent);
                }
            }
        }
        else {
            log.info("Returning cached entity for " + getKey(request));
        }

        /*
          13.3 Validation Model

          When a cache has a stale entry that it would like to use as
          a response to a client's request, it first has to check with
          the origin server (or possibly an intermediate cache with a
          fresh response) to see if its cached entry is still
          usable. We call this "validating" the cache entry. Since we
          do not want to have to pay the overhead of retransmitting
          the full response if the cached entry is good, and we do not
          want to pay the overhead of an extra round trip if the
          cached entry is invalid, the HTTP/1.1 protocol supports the
          use of conditional methods. [...] The server then checks
          that validator against the current validator for the entity,
          and, if they match (see section 13.3.3), it responds with a
          special status code (usually, 304 (Not Modified)) and no
          entity-body.
        */
        
        String LastModified = ent.getHeader("Last-Modified");
        String IfModifiedSince = request.getHeader("If-Modified-Since");
        String IfUnmodifiedSince = request.getHeader("If-Unmodified-Since");

        String ETag = ent.getHeader("ETag");
        String IfNoneMatch = request.getHeader("If-None-Match");
        String IfMatch = request.getHeader("If-Match");

        if (isConditional(request) &&
            (IfModifiedSince   == null || LastModified.equals(IfModifiedSince)) &&
            (IfUnmodifiedSince == null || !LastModified.equals(IfUnmodifiedSince)) &&
            (IfNoneMatch       == null || IfNoneMatch.equals(ETag)) &&
            (IfMatch           == null || !IfMatch.equals(ETag))) {
            if (log.isInfoEnabled()) {
                log.info("Sending not modifed response for " + 
                          getKey(request) + ": " + 
                          "LastModified =  " + LastModified + ", " +
                          "IfModifiedSince = " + IfModifiedSince + ", " +
                          "IfUnmodifiedSince = " + IfUnmodifiedSince + ", " +
                          "ETag = " + ETag + ", " +
                          "IfNoneMatch = " + IfNoneMatch + ", " +
                          "IfMatch = " + IfMatch);
            }
            response.setStatus(304);
        }
        /*
          [...] Otherwise, it returns a full response (including
          entity-body).
        */
        else {
            if (log.isInfoEnabled()) {
                log.info("Returning full response for " + getKey(request));
            }
            response.setStatus(200);
            Iterator i = ent.getHeaderNames();
            while(i.hasNext()) {
                String name = (String)i.next();
                response.setHeader(name, ent.getHeader(name));
            }
            response.getWriter().write(ent.getResponseBody());
        }
    }

    protected boolean isConditional(HttpServletRequest request)
    {
        if (request.getHeader("If-Modified-Since") != null) return true;
        if (request.getHeader("If-Unmodified-Since") != null) return true;
        if (request.getHeader("If-None-Match") != null) return true;
        if (request.getHeader("If-Match") != null) return true;
        return false;
    }

    protected boolean isCacheable(CachedHttpResponse response)
    {
        /*
          13.4 Response Cacheability
          
          [I]n some cases it might be inappropriate for a cache to
          retain an entity, or to return it in response to a
          subsequent request. This might be because absolute semantic
          transparency is deemed necessary by the service author, or
          because of security or privacy considerations. Certain
          cache-control directives are therefore provided so that the
          server can indicate that certain resource entities, or
          portions thereof, are not to be cached regardless of other
          considerations.

          Note that section 14.8 normally prevents a shared cache from
          saving and returning a response to a previous request if
          that request included an Authorization header.

          A response received with a status code of 200, 203, 206,
          300, 301 or 410 MAY be stored by a cache and used in reply
          to a subsequent request, subject to the expiration
          mechanism, unless a cache-control directive prohibits
          caching. However, a cache that does not support the Range
          and Content-Range headers MUST NOT cache 206 (Partial
          Content) responses.
          
          A response received with any other status code (e.g. status
          codes 302 and 307) MUST NOT be returned in a reply to a
          subsequent request unless there are cache-control directives
          or another header(s) that explicitly allow it. For example,
          these include the following: an Expires header (section
          14.21); a "max-age", "s-maxage", "must- revalidate",
          "proxy-revalidate", "public" or "private" cache-control
          directive (section 14.9).
         */
        String CacheControl = response.getHeader("Cache-Control");

        if (log.isInfoEnabled()) {
            log.info("Cache-Control: " + CacheControl);
        }

        if (CacheControl != null) {
            /* 14.9.1 What is Cachaeable */
            if (CacheControl.startsWith("must-revalidate")) return false;
            if (CacheControl.startsWith("public")) return true;
            if (CacheControl.startsWith("private")) return false;
            if (CacheControl.startsWith("no-cache")) return false;
            if (CacheControl.startsWith("no-store")) return false;
            if (CacheControl.startsWith("max-age")) return true;
            if (CacheControl.startsWith("s-maxage")) return true;
        }            
        
        int sc = response.getStatus();

        if (log.isInfoEnabled()) {
            log.info("Status Code: " + sc);
        }

        if (sc == 200) return true;
        if (sc == 203) return true;
        if (sc == 300) return true;
        if (sc == 301) return true;
        if (sc == 401) return true;

        return false;
    }
}
