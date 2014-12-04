/*
 * $Id: WebCtService.java,v 1.3 2007/02/15 15:03:47 brharp Exp $
 *
 * Copyright 2006 University of Guelph
 */

package ca.uoguelph.ccs.portal.webct;

import java.util.*;
import java.net.*;
import java.io.*;
import java.security.MessageDigest;
import org.w3c.dom.Document;
import ca.uoguelph.ccs.ws.XMLHttpRequestObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class WebCtService
{
    private static final String CONSORTIA_START_TAG = "<consortiaid>";
    private static final String CONSORTIA_END_TAG = "</consortiaid>";

    private static final String XML_MAGIC = "<?xml";

    private String hostname;
    private int    port = 80;
    private String username;
    private String password;
    private String globalLearningContextId;
    private String secret;
    private String systemIntegrationApiUrl = "/webct/systemIntegrationApi.dowebct";
    private String autoSignonUrl = "/webct/public/autosignon";

    private Map consortIdCache;

    private static Log log;

    public WebCtService()
    {
        consortIdCache = new HashMap();
        log = LogFactory.getLog(this.getClass());
    }

    public void setHostname(String hostname)
    {
        this.hostname = hostname;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public void setGlobalLearningContextId(String globalLearningContextId)
    {
        this.globalLearningContextId = globalLearningContextId;
    }

    public void setSecret(String secret)
    {
        this.secret = secret;
    }

    public void setSystemIntegrationApiUrl(String systemIntegrationApiUrl)
    {
        this.systemIntegrationApiUrl = systemIntegrationApiUrl;
    }

    public void setAutoSignonUrl(String autoSignonUrl)
    {
        this.autoSignonUrl = autoSignonUrl;
    }

    public String getTimeStamp()
    {
        return Long.toString(System.currentTimeMillis() / 1000);
    }

    public String getNonce(String userId, String timestamp)
    {
        return timestamp + ":" + generateMac(userId + timestamp);
    }

    public boolean checkNonce(String userId, String nonce)
    {
        try {
            String tokens[] = nonce.split(":");
            String timestamp = tokens[0];
            String hash = tokens[1];
            try {
                long stamptime = Long.parseLong(timestamp);
                long now = System.currentTimeMillis() / 1000;
                if (now - stamptime > 600) {
                    log.debug("Nonce "+nonce+" expired.");
                    return false;
                } else {
                    String rehash = generateMac(userId + timestamp);
                    log.debug("Nonce "+nonce+" does not match "+rehash+ ".");
                    return rehash.equals(hash);
                }
            } catch (NumberFormatException nfe) {
                log.debug("Failed to parse nonce "+nonce+".");
                return false;
            }
        } catch (NullPointerException npe) {
            log.debug("Nonce is null.");
            return false;
        }
    }

    /**
     * Looks up a WebCT consortia ID given a user ID.
     */
    public String getConsortiaId(String userId, String timestamp)
        throws Exception
    {
        String consortId;

        if ((consortId = (String)consortIdCache.get(userId)) == null) {
            Map par = new HashMap();
            par.put("operation", "get");
            par.put("option", "consortia_id");
            par.put("webctid", userId);
            par.put("timestamp", timestamp);
            par.put("username", username);
            par.put("password", password);
            par.put("glcid", globalLearningContextId);
            // --- Parameters added before here are included in the MAC ------------
            par.put("auth", generateAuth(par));
            // --- Parameters added after here aren't included in the MAC ----------
            par.put("adapter", "standard");

            String postUrl = formatUrl(systemIntegrationApiUrl, par);
            XMLHttpRequestObject request = new XMLHttpRequestObject();
            request.open("POST", postUrl, false);
            request.send();
            String response = request.getResponseText();
            // Snip out the consortia ID from the response.
            if (response != null) {
                int startTagStart = response.indexOf(CONSORTIA_START_TAG);
                int endTagStart = response.indexOf(CONSORTIA_END_TAG, startTagStart);
                int startTagLength = CONSORTIA_START_TAG.length();
                consortId =  response.substring(startTagStart+startTagLength,endTagStart);
                consortIdCache.put(userId, consortId);
            }
        }

        return consortId;
    }

    public String getHomeAreaXml(String webCtId, String timestamp)
        throws Exception
    {
        Map par = new HashMap();
        par.put("action", "get");
        par.put("option", "mywebct_xml");
        par.put("webctid", webCtId);
        par.put("timestamp", timestamp);
        par.put("lc_source", "WebCT");
        par.put("lc_id", globalLearningContextId);
        // --- Parameters added before here are included in the MAC ------------
        par.put("auth", generateAuth(par));
        // --- Parameters added after here aren't included in the MAC ----------
        par.put("adapter", "standard");

        String postUrl = formatUrl(systemIntegrationApiUrl, par);
        XMLHttpRequestObject request = new XMLHttpRequestObject();
        request.open("POST", postUrl, false);
        request.send();

        // Incredibly, WebCT does not return valid XML for the home
        // area, so we must go digging in the response text for
        // "<?xml" and parse from there.
        String response = request.getResponseText();
        int xmlStart = response.indexOf(XML_MAGIC);
        return response.substring(xmlStart);
    }

    public String getSingleSignonUrl(String webCtId, String timestamp, String url)
        throws Exception
    {
        Map par = new HashMap();
        par.put("wuui", webCtId);
        par.put("timestamp", timestamp);
        par.put("url", url);
        par.put("mac", generateAuth(par));
        par.put("glcid", globalLearningContextId);

        return formatUrl(autoSignonUrl, par);
    }

    public String formatUrl(String path, Map par)
        throws Exception
    {
        StringBuffer url = new StringBuffer();
        url.append("http://");
        url.append(hostname);
        url.append(':');
        url.append(port);
        url.append(path);
        if (par != null) {
            Iterator it = par.entrySet().iterator();
            char sep = '?';
            while(it.hasNext()) {
                Map.Entry next = (Map.Entry)it.next();
                url.append(sep);
                url.append(URLEncoder.encode((String)next.getKey(), "UTF-8"));
                url.append('='); 
                url.append(URLEncoder.encode((String)next.getValue(), "UTF-8"));
                sep = '&';
            }
        }
        return url.toString();
    }

    private String convertBaToHex(byte[] data) {
        StringBuffer result = null;
        try {
            result = new StringBuffer();
            for (int i =0; i < data.length; i++) {
                int tmp = data[i];
                if (tmp < 0) tmp += 256;
                String tmpStr = (Integer.toHexString (tmp)).toUpperCase();
                result.append((tmpStr.length()==1)?"0"+tmpStr:tmpStr);
            }
        }
        catch (Exception e) {
        }
        String hex = result.toString();
        return hex;
    }

    private String calculateHash(String encrypt) {
        byte[] md5Hash = new byte[16];
        String md5HashHex = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md5Hash = md.digest(encrypt.getBytes());
            md5HashHex = convertBaToHex(md5Hash);
        } catch(Exception e) {
        }
        return md5HashHex;
    }

    private String calculateAsciiValue(String data) {
        int dataValue = 0;
        try {
            for(int i=0; i < data.length(); i++) {
                char c = data.charAt(i);
                dataValue+= (int)c;
            }
        } catch(Exception e) {
        }
        String asciiValue = "" + dataValue;
        return asciiValue;
    }

    private String generateMac(String data) {
        String browserMac = null;
        try {
            String dataValue = calculateAsciiValue(data);
            String sharedSecret = secret;
            String encrypt = dataValue + sharedSecret;
            browserMac = calculateHash(encrypt);
        } catch(Exception e) {
        }
        return browserMac;
    }

    private String generateAuth(Map par) {
        StringBuffer data = new StringBuffer();
        Iterator it = par.entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry next = (Map.Entry)it.next();
            data.append((String)par.get(next.getKey()));
        }
        return generateMac(data.toString());
    }

}
