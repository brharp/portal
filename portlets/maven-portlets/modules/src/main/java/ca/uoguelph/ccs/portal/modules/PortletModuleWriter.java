package ca.uoguelph.ccs.portal.modules;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.*;
import javax.portlet.*;
import org.apache.commons.lang.StringEscapeUtils;

public class PortletModuleWriter extends ModuleWriter
{
    public static final int APPLICATION_SCOPE = PortletSession.APPLICATION_SCOPE;
    public static final int PORTLET_SCOPE = PortletSession.PORTLET_SCOPE;
    static final Pattern TOKEN = Pattern.compile("\\%([a-zA-Z_]+)\\%");

    public static final String MODULE_ID = "MODULE_ID";
    public static final String MODULE_BASE = "MODULE_BASE";
    public static final String MODULE_CONTEXT = "MODULE_CONTEXT";
    public static final String MODULE_MODE = "MODULE_MODE";
    public static final String MODULE_STATE = "MODULE_STATE";
    public static final String MODULE_TITLE = "MODULE_TITLE";
    public static final String MODULE_PREF_VALUE = "MODULE_PREF_VALUE_";
    public static final String MODULE_PREF_NAME = "MODULE_PREF_NAME_";
    public static final String MODULE_ATTR_VALUE = "MODULE_ATTR_VALUE_";
    public static final String MODULE_ATTR_NAME = "MODULE_ATTR_NAME_";
    public static final String UNDEFINED = "undefined";

    PortletPreferences prefs;
    Writer out;
    String moduleId;
    ModulePrefs modulePrefs;
    Map attr;
    String base;
    String context;
    String mode;                // portlet mode
    String state;               // window state
    
    private static int id = 0;

    public PortletModuleWriter(Module module,
                               RenderRequest request, 
                               RenderResponse response)
        throws IOException
    {
        out = response.getWriter();
        prefs = request.getPreferences();

        /* Portlet API 1.0 does not provide us with the window ID, so
         * we need to create our own unique window IDs. */
        PortletSession psession = request.getPortletSession();
        synchronized (psession) {
            moduleId = (String)psession.getAttribute(MODULE_ID, PORTLET_SCOPE);
            if (moduleId == null) {
                moduleId = "m"+(id++);
                psession.setAttribute(MODULE_ID, moduleId, PORTLET_SCOPE);
            }
        }
        
        /* The module writer uses module preferences for preference
         * names, types, and default values. */
        modulePrefs = module.getPreferences();

        /* The module writer uses the user info map to replace
         * user attribute variables. */
        attr = (Map)request.getAttribute(PortletRequest.USER_INFO);
        
        /* The module writer contructs a base URI and a context URI.
         * The base URI is for resolving absolute paths relative
         * to the server root, and the context URI is for resolving
         * URIs relative to the module path. */
        try {
            //URI uri = module.getUri();
            base = module.getBaseUri().toString();
            context = module.getContextUri().toString();
        } catch(Exception e) {
            base = UNDEFINED;
            context = UNDEFINED;
        }

        /* The portlet mode and window state are exposed through the
         * MODULE_MODE, and MODULE_STATE variables. */
        mode = request.getPortletMode().toString();
        state = request.getWindowState().toString();
    }

    /* Replace tokens. This implementation takes a naive approach
     * to token replacement: it replaces each token one by one in
     * the input String.
     * <p>
     * The tokens replaced are:
     * <p>
     * %MODULE_ID%: Replaced by module's ID.
     * <p>
     * %MODULE_BASE%: The base URL from which this module was loaded.
     * <p>
     * %MODULE_CONTEXT%: The context from which this module was loaded.
     * <p>
     * %MODULE_PREF_VALUE_foo%: Replaced by the value of module preference "foo". 
     * Module preferences are stored in the portlet preferences table 
     * as "MODULE_PREF_VALUE_foo" to distinguish them from portlet 
     * preferences.
     * <p>
     * %MODULE_PREF_NAME_foo%: Replaced by "MODULE_PREF_NAME_foo". 
     * This is a little redundant, but it formalizes the mapping 
     * between token name and preference name for module preferences. 
     * <p>
     * %MODULE_ATTR_VALUE_foo%: Replaced by the value of the user attribute "foo"
     * in the user info map.
     * <p>
     * %MODULE_ATTR_NAME_foo%: Replaced by "MODULE_ATTR_NAME_foo".
     */
    public void write(String content) throws IOException
    {
        Matcher m;
        StringBuffer sb;
        
        sb = new StringBuffer();
        m = TOKEN.matcher(content);
        while(m.find()) {
            String token = m.group(1);
            /* Replace the %MODULE_ID% token with this module's ID. */
            if(MODULE_ID.equals(token)){ 
                m.appendReplacement(sb, moduleId);
            }
            /* Replace the %MODULE_BASE% token with the base URI. */
            else if(MODULE_BASE.equals(token)) {
                m.appendReplacement(sb, base);
            }
            /* Replace the %MODULE_CONTEXT% token with the context URI. */
            else if(MODULE_CONTEXT.equals(token)) {
                m.appendReplacement(sb, context);
            }
            /* Replace the %MODULE_MODE% token with the portlet mode. */
            else if (MODULE_MODE.equals(token)) {
                m.appendReplacement(sb, mode);
            }
            /* Replace the %MODULE_STATE% token with the window state. */
            else if (MODULE_STATE.equals(token)) {
                m.appendReplacement(sb, state);
            }
            /* Replace the %MODULE_TITLE% token with the element
             * containing the module title. */
            else if(MODULE_TITLE.equals(token)) {
                StringBuffer rb = new StringBuffer();
                rb.append("(document.getElementById('");
                rb.append(moduleId);
                rb.append("').parentNode.previousSibling.firstChild.firstChild)");
                m.appendReplacement(sb, rb.toString());
            }
            /* Replace %MODULE_PREF_VALUE_foo% with the value of
             * module preference "foo". Module preferences are stored
             * in the portlet preferences table as
             * "MODULE_PREF_VALUE_foo" to distinguish them from
             * portlet preferences. */
            else if(token.startsWith(MODULE_PREF_VALUE)) {
                String varName = token.substring(MODULE_PREF_VALUE.length());
                String prefVal = prefs.getValue(MODULE_PREF_VALUE+varName,
                                                modulePrefs.getDefault(varName));
                if (prefVal != null) {
                    m.appendReplacement(sb, prefVal);
                } else {
                    m.appendReplacement(sb, UNDEFINED);
                }
            }
            /* For consistency's sake, the token
             * %MODULE_PREF_NAME_foo% is replaced with
             * "MODULE_PREF_NAME_foo". This is a little redundant, but
             * it formalizes the mapping between token name and
             * preference name for module preferences. */
            else if(token.startsWith(MODULE_PREF_NAME)) {
                m.appendReplacement(sb, token);
            }
            /* Replace %MODULE_ATTR_VALUE_foo% with the value of
             * portlet user attribute "foo". */
            else if(token.startsWith(MODULE_ATTR_VALUE)) {
                String varName = token.substring(MODULE_ATTR_VALUE.length());
                String attrVal = (attr!=null) ? (String)attr.get(varName) : "";
                m.appendReplacement(sb, attrVal!=null?attrVal:"");
            }
            /* For consistency's sake, the token
             * %MODULE_ATTR_NAME_foo% is replaced with
             * "MODULE_ATTR_NAME_foo". This is a little redundant, but
             * it formalizes the mapping between token name and
             * attribute name. */
            else if(token.startsWith(MODULE_ATTR_NAME)) {
                m.appendReplacement(sb, token);
            }
        }
        m.appendTail(sb);

        out.write(sb.toString());
    }

    public void write(char[] cbuf, int off, int len) throws IOException {
        out.write(cbuf, off, len);
    }

    public void flush() throws IOException {
        out.flush();
    }

    public void close() throws IOException{
        out.close();
    }
}
