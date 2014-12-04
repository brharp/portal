package ca.uoguelph.ccs.portal.modules;

import java.util.*;

/**
 * A cached HTTP 1.1 entity. This class implements the algorithms
 * suggested in RFC2616 for calculating current age and freshness
 * lifetime. To use, update the object properties with values from the
 * HTTP headers and the local clock, and call isExpired().
 */
public class Http11CacheInfo
    implements java.io.Serializable
{
    private long ageValue;
    private long dateValue;
    private long requestTime;
    private long responseTime;
    private long expiresValue;
    private long maxAgeValue;
    private long lastModified;

    private Map headers;
    private String responseBody;

    public Http11CacheInfo(Map headers, String body)
    {
        this.headers = headers;
        this.responseBody = body;
    }

    /**
     * Sets the value of the Age header received by the cache, in
     * millis.
     */
    public void setAgeValue(long ageValue)
    {
        this.ageValue = ageValue;
    }

    /**
     * Sets the value of the origin server's Date header, in millis.
     */
    public void setDateValue(long dateValue)
    {
        this.dateValue = dateValue;
    }

    /**
     * Sets the local time when the cache made the request that
     * resulted in this cached response, in millis.
     */
    public void setRequestTime(long requestTime)
    {
        this.requestTime = requestTime;
    }

    /**
     * Sets the local time when the cache received a response, in
     * millis.
     */
    public void setResponseTime(long responseTime)
    {
        this.responseTime = responseTime;
    }

    /**
     * Returns the age of the cached entity, in millis.
     */
    public long getAge()
    {
        long apparentAge = Math.max(0, responseTime - dateValue);
        long correctedReceivedAge = Math.max(apparentAge, ageValue);
        long responseDelay = responseTime - requestTime;
        long correctedInitialAge = correctedReceivedAge + responseDelay;
        long residentTime = System.currentTimeMillis() - responseTime;
        long currentAge = correctedInitialAge + residentTime;
        return currentAge;
    }

    /**
     * Sets the value of the Expires header, in millis.
     */
    public void setExpiresValue(long expiresValue)
    {
        this.expiresValue = expiresValue;
    }

    /**
     * Sets the value of the max-age directive of the Cache-Control
     * header, in millis.
     */
    public void setMaxAgeValue(long maxAgeValue)
    {
        this.maxAgeValue = maxAgeValue;
    }

    /**
     * Sets the value of the Last-Modified header, in millis.
     */
    public void setLastModified(long lastModified)
    {
        this.lastModified = lastModified;
    }

    /**
     * Returns the useful lifetime of the cached entity, in
     * millis. See RFC 2616 Section 13.2.4 Expiration Calculations.
     */
    public long getLifetime()
    {
        if (maxAgeValue > 0) {
            return maxAgeValue;
        } else if (expiresValue > 0 && dateValue > 0) {
            return expiresValue - dateValue;
        } else if (lastModified > 0) {
            return (System.currentTimeMillis() - lastModified) / 10L;
        } else { // Ummm... okay... how about 30 minutes?
            return 30L * 60L * 1000L;
        }
    }

    /**
     * Returns true if the age of the cached entity has surpassed its
     * useful lifetime, false otherwise.
     */
    public final boolean isExpired()
    {
        return getAge() > getLifetime();
    }

    /**
     * Enumerate cached headers.
     */
    public Iterator getHeaderNames()
    {
        return headers.keySet().iterator();
    }

    public String getHeader(String name)
    {
        return (String)headers.get(name);
    }

    public void setHeader(String name, String value)
    {
        if (value == null) {
            headers.remove(name);
        } else {
            headers.put(name, value);
        }
    }

    /**
     * Get cached response body.
     */
    public String getResponseBody()
    {
        return responseBody;
    }

    public void removeCacheWarnings()
    {
        setHeader("Warning", null);
    }

    public void setEndToEndHeaders(Map headers)
    {
        this.headers = headers;
    }

    public void setResponseBody(String body)
    {
        this.responseBody = body;
    }
}
