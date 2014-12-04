/*
 * $Id: XMLHttpRequestObject.java,v 1.6 2006/09/05 16:31:09 brharp Exp $
 * 
 * Copyright 2006 University of Guelph
 */

package ca.uoguelph.ccs.ws;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author brharp
 *
 */
public class XMLHttpRequestObject implements Runnable
{
    public static final int UNINITIALIZED = 0;
    public static final int LOADING = 1;
    public static final int LOADED = 2;
    public static final int INTERACTIVE = 3;
    public static final int COMPLETE = 4;

    private String method;
    private URL url;
    private boolean async;
    private int status;
    private String statusText;
    private String responseText;
    private Document responseXML;
    private String content;
    private ReadyStateChangeListener readyStateChangeListener;
    private int readyState;

    private static final Log log = LogFactory.getLog(XMLHttpRequestObject.class);

    public XMLHttpRequestObject()
    {
        readyState = UNINITIALIZED;
    }

    public void open(String method, URL url, boolean async)
    {
        if (log.isDebugEnabled()) {
            log.debug("Opening [method="+method+"] [uri="+url+"] [async="+async+"]");
        }
        setReadyState(UNINITIALIZED);
        this.method = method;
        this.url = url;
        this.async = async;
        this.responseText = null;
        this.responseXML = null;
        this.status = 0;
    }
	
    public void open(String method, String uri, boolean async)
        throws MalformedURLException
    {
        open(method, new URL(uri), async);
    }
	
    public void send(String content)
    {
        if (log.isDebugEnabled()) {
            log.debug("Sending [content="+content+"]");
        }
        this.content = content;
        Thread runner = new Thread(this);
        runner.start();
        if (async == false) {
            try {
                runner.join();
            } catch(InterruptedException e) {
                if (log.isErrorEnabled()) {
                    log.error("Interrupted", e);
                }
            }
        }
    }

    public void run()
    {
        setReadyState(LOADING);
        HttpURLConnection conn = null;
        OutputStream output = null;
        InputStream input = null;
        try {
            conn = (HttpURLConnection)url.openConnection();
            conn.setAllowUserInteraction(false);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestMethod(method);
            conn.connect();
            if (content != null) {
                output = conn.getOutputStream();
                output.write(content.getBytes());
                output.flush();
            }
            status = conn.getResponseCode();
            statusText = conn.getResponseMessage();
            if (HttpURLConnection.HTTP_OK == status) {
                input = conn.getInputStream();
                ByteArrayOutputStream data = new ByteArrayOutputStream();
                int read = 0; byte[] buf = new byte[1024];
                while((read = input.read(buf)) > -1) {
                    if (log.isDebugEnabled()) {
                        log.debug("Read [buf="+new String(buf)+"]");
                    }
                    data.write(buf, 0, read);
                }
                setReadyState(LOADED);
                responseText = data.toString();
                if (log.isDebugEnabled()) {
                    log.debug(responseText);
                }
                responseXML = parse(data.toByteArray());
                setReadyState(COMPLETE);
            }
        }
        catch(IOException ioe) {
            ioe.printStackTrace();
            setReadyState(UNINITIALIZED);
        }
        finally {
            try {
                if (output != null) output.close();
                if (input != null) input.close();
                if (conn != null) conn.disconnect();
            } catch(Exception e) {
                if (log.isErrorEnabled()) {
                    log.error("Failed to close streams.", e);
                }
            }
        }
    }
    
    public void send()
    {
        send(null);
    }
	
    private Document parse(byte[] data)
    {
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            try {
                return documentBuilder.parse(new ByteArrayInputStream(data));
            } catch (SAXException e) {
                return null;
            } catch (IOException e) {
                return null;
            }
        } catch (ParserConfigurationException e) {
            if (log.isErrorEnabled()) {
                log.error("Parser config error: Please configure the XML parser.", e);
            }
            return null;
        }
    }

    /**
     * @return Returns the async flag.
     */
    public boolean isAsync()
    {
        return async;
    }

    /**
     * @return Returns the method.
     */
    public String getMethod()
    {
        return method;
    }

    /**
     * @return Returns the responseText.
     */
    public String getResponseText()
    {
        return responseText;
    }

    /**
     * @return Returns the responseXML.
     */
    public Document getResponseXML()
    {
        return responseXML;
    }

    /**
     * @return Returns the status.
     */
    public int getStatus()
    {
        return status;
    }

    /**
     * @return Returns the statusText.
     */
    public String getStatusText()
    {
        return statusText;
    }

    /**
     * @return Returns the url.
     */
    public URL getUrl()
    {
        return url;
    }

    public void setReadyStateChangeListener(ReadyStateChangeListener readyStateChangeListener)
    {
        this.readyStateChangeListener = readyStateChangeListener;
    }

    public int getReadyState()
    {
        return readyState;
    }

    private void setReadyState(int state)
    {
        this.readyState = state;
        if (readyStateChangeListener != null) {
            readyStateChangeListener.onReadyStateChange(this);
        }
    }
}
