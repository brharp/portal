package ca.uoguelph.ccs.portal.modules;

import java.io.*;
import java.net.*;
import org.apache.commons.digester.*;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.*;
import org.apache.commons.httpclient.protocol.*;
import org.apache.commons.httpclient.contrib.ssl.*;
import ca.uoguelph.ccs.portal.modules.prefs.*;

public class Module
{
    private java.net.URI uri;
    private String title = "Untitled";
    private String author = "Anonymous";
    private String description = "Unavailable";
    private String content = new String();
    private String src;
    private ModulePrefs modulePrefs;
    private ModuleAttrs moduleAttrs;
    private String help;
    private boolean cached = false;

    static {
        // Install a custom protocol handler that accepts self signed
        // SSL certificates.
        Protocol easySSL = new Protocol("https",new EasySSLProtocolSocketFactory(),443);
        Protocol.registerProtocol("https",easySSL);
    }

    public Module(java.net.URI uri) throws IOException {
        if (uri == null) {
            throw new NullPointerException();
        }
        modulePrefs = new ModulePrefs();
        moduleAttrs = new ModuleAttrs();
        setUri(uri);
    }

    public Module(String uri) throws IOException, URISyntaxException
    {
        this(new java.net.URI(uri));
    }

    public void setUri(java.net.URI uri) throws IOException
    {
        HttpClient client = new HttpClient();
        GetMethod method = new GetMethod(uri.toString());
        try {
            int statusCode = client.executeMethod(method);
            if (statusCode != HttpStatus.SC_OK) {
                throw new IOException("Method failed: " + method.getStatusLine());
            }
            byte[] responseBody = method.getResponseBody();
            Digester dig = new ModuleDigester();
            dig.push(this);
            InputStream moduleInputStream = new ByteArrayInputStream(responseBody);
            dig.parse(moduleInputStream);
            moduleInputStream.close();
            this.uri = uri;
        } catch(org.xml.sax.SAXException e) {
            throw new IOException("XML Parse Error: " + e.getMessage());        
        } catch (HttpException e) {
            throw new IOException("Fatal protocol violation: " + e.getMessage());
        } catch (IOException e) {
            throw new IOException("Fatal transport error: " + e.getMessage());
        } finally {
            // Release the connection.
            method.releaseConnection();
        }
    }
    
    public java.net.URI getUri()
    {
        return uri;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getTitle()
    {
        return title;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public void addPreference(Preference preference)
    {
        modulePrefs.setPreference(preference);
    }

    public void addPreference(String name, String type, String def)
    {
        modulePrefs.setPreference(name, type, def);
    }

    public ModulePrefs getPreferences()
    {
        return modulePrefs;
    }

    public void addAttribute(String name, String alias)
    {
        moduleAttrs.setAttribute(name, alias);
    }

    public ModuleAttrs getAttributes()
    {
        return moduleAttrs;
    }

    public void setCached(String cached)
    {
        setCached("true".equalsIgnoreCase(cached));
    }

    public void setCached(boolean cached)
    {
        this.cached = cached;
    }

    public boolean isCached()
    {
        return cached;
    }

    public void setSrc(String src)
    {
        this.src = src;
    }
    
    public String getSrc()
    {
        return src;
    }

    public java.net.URI getBaseUri()
    {
        return uri.resolve("/");
    }

    public java.net.URI getContextUri()
    {
        return uri.resolve(".");
    }

    public void setHelp(String help)
    {
        this.help = help;
    }
    
    public String getHelp()
    {
        return help;
    }

    public void setAuthor(String author)
    {
        this.author = author;
    }
    
    public String getAuthor()
    {
        return author;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }
    
    public String getDescription()
    {
        return description;
    }
}
