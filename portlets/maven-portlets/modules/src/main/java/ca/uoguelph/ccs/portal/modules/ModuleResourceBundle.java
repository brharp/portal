package ca.uoguelph.ccs.portal.modules;

import java.io.*;
import java.net.*;
import java.util.*;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.*;
import org.apache.commons.logging.*;

public class ModuleResourceBundle extends PropertyResourceBundle
{
    public static final String EXT_PROPERTIES = ".properties";

    private static Log log = LogFactory.getLog(ModuleResourceBundle.class);

    public ModuleResourceBundle(InputStream in) throws java.io.IOException
    {
        super(in);
    }

    public static final ResourceBundle getBundle(Module module, Locale locale)
    {
        ResourceBundle resultBundle = new BaseBundle();
        List candidateBundleNames = new LinkedList();
        
        String baseName = module.getTitle();
        String language = locale.getLanguage();
        String country = locale.getCountry();
        String variant = locale.getVariant();

        if (baseName.length() > 0)
            candidateBundleNames.add(baseName);
        if (language.length() > 0)
            candidateBundleNames.add(baseName+"_"+language);
        if (country.length() > 0)
            candidateBundleNames.add(baseName+"_"+language+"_"+country);
        if (variant.length() > 0)
            candidateBundleNames.add(baseName+"_"+language+"_"+country+"_"+variant);
        
        String contextUri = module.getContextUri().toString();
        HttpClient client = new HttpClient();
        Iterator i = candidateBundleNames.iterator();
        while(i.hasNext()) {
            String candidateName = (String)i.next();
            String candidateUri = contextUri + candidateName + EXT_PROPERTIES;
            GetMethod method = new GetMethod(candidateUri);
            try {
                log.info("Trying " + candidateUri);
                int statusCode = client.executeMethod(method);
                if (statusCode == HttpStatus.SC_OK) {
                    log.info("Loading " + candidateUri);
                    ModuleResourceBundle bundle = new ModuleResourceBundle(method.getResponseBodyAsStream());
                    bundle.setParent(resultBundle);
                    resultBundle = bundle;
                } else {
                    method.getResponseBody();
                }
            }
            catch(Exception e) {
                log.error(e);
            }
            finally {
                method.releaseConnection();
            }
        }

        if (resultBundle != null) {
            return resultBundle;
        } else {
            throw new MissingResourceException("Not found",baseName,locale.toString());
        }
    }

    public static final ResourceBundle getBundle(Module module)
    {
        return getBundle(module, Locale.getDefault());
    }

    public static ResourceBundle getBundle2(Module module, Locale locale)
    {
        try {
            log.info("Loading resource bundle from " + module.getContextUri());
            return ResourceBundle
                .getBundle(module.getTitle(), locale,
                           new URLClassLoader(new URL[] {
                               new URL(module.getContextUri().toString())}));
        }
        catch(Exception e) {
            log.error(e);
            return null;
        }
    }

    static class BaseBundle extends ResourceBundle
    {
        public Object handleGetObject(String key)
        {
            return key;
        }
        
        public Enumeration getKeys()
        {
            return new Vector().elements();
        }
    }
}
