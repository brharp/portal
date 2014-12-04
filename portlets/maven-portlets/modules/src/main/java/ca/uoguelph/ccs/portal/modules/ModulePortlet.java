package ca.uoguelph.ccs.portal.modules;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.portlet.*;
import javax.xml.parsers.*;
import org.apache.commons.digester.*;
import org.apache.commons.logging.*;
import org.w3c.dom.*;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.*;
import org.apache.commons.httpclient.params.*;
import ca.uoguelph.ccs.portal.modules.prefs.*;

public class ModulePortlet extends GenericPortlet
{
    public static final String URL = "url";
    public static final String ACTION = "action";
    public static final String CANCEL = "Cancel";
    public static final String PASSWD_MASK = "********";
    public static final String SELECT = "select";

    Map cache;
    Log log;

    static {
        HttpParams p = DefaultHttpParams.getDefaultParams();
        p.setIntParameter("http.connection.timeout", 5000);
    }

    public void init()
        throws PortletException
    {
        cache = new HashMap();
        log = LogFactory.getLog(ModulePortlet.class);
    }

    public void render(RenderRequest request,
                       RenderResponse response)
        throws PortletException, 
               java.io.IOException
    {
        Module module;
        String url;

        response.setContentType("text/html");

        /* The module portlet displays a module at a given URL. To
         * improve performance, the module content is normally cached,
         * unless the module explicitly says not to. */
        url = request.getPreferences().getValue(URL, null);
        synchronized(cache) {
            module = (Module)cache.get(url);
        }
        if (module == null) {
            log.info("Module not in cache.");
            try {
                module = new Module(url);
            } catch(Exception e) {
                Writer out = response.getWriter();
                if (url == null) {
                    out.write("<p>Enter the address of your own module or news feed.</p>\n");
                    out.write("<form action='"+response.createActionURL()+"'>\n");
                    out.write("<label for='url'>URL:</label><input type='text' name='url'/>\n");
                    out.write("<input type='submit' value='Submit'/>\n");
                    out.write("</form>\n");
                } else {
                    out.write("<p align=center><em>Sorry :$</em></p>");
                    out.write("<p align=center><em>This channel is temporarily unavailable.</em></p>");
                }
                out.close();
                return;
            }
            if (module.isCached()) {
                synchronized(cache) {
                    cache.put(url, module);
                    log.info("Module cached.");
                }
            }
        }

        /* Display the module title as the channel title. This doesn't
         * actually work in Academus, but it *should* work, and it
         * doesn't break anything, so I'm leaving the call in for when
         * the bug is fixed. */
        response.setTitle(module.getTitle());

        /* A module output stream writes the module
         * content, replacing special module tokens in the source. The
         * stream uses preferences from the module preferences to get
         * preference values. */
        ModuleWriter out = new PortletModuleWriter(module, request, response);

        /* Modules define their own set of preferences, with valid
         * types and default values. When replacing preference tokens
         * in the output stream, we need to use the defaults,
         * preference names, and types provided in the module
         * definition. */
        if (request.getPortletMode().equals(PortletMode.EDIT)) {
            log.info("Writing preference form.");
            out.write("<div class='preferences' style='background:#ffcc00;padding:1px;margin:1px'>\n");
            out.write("<form action='"+response.createActionURL()+"' method='POST'>\n");
            ModulePrefs prefs = module.getPreferences();
            PortletPreferences portletPrefs = request.getPreferences();
            ResourceBundle bundle = ModuleResourceBundle.getBundle(module,request.getLocale());
            Iterator i = prefs.getNames();
            if (i.hasNext()) {
                out.write("<table border=0>\n");
                out.write("<tbody>\n");
                while(i.hasNext()) {
                    String name = (String)i.next();
                    try {
                        String type = prefs.getType(name);
                        out.write("<tr><th>");
                        out.write("<label for='MODULE_PREF_VALUE_");
                        out.write(name);
                        out.write("'>");
                        out.write(bundle==null?name:bundle.getString(name));
                        out.write("</label>\n");
                        out.write("</th><td>");
                        if (SELECT.equals(type)) {
                            out.write("<select name='MODULE_PREF_VALUE_");
                            out.write(name);
                            out.write("'>\n");
                            Iterator opts = prefs.getPreference(name).getOptions().iterator();
                            while(opts.hasNext()) {
                                Option opt = (Option)opts.next();
                                String value = opt.getValue();
                                String sel = portletPrefs.getValue("MODULE_PREF_VALUE_"+name,
                                                                   prefs.getDefault(name));
                                out.write("<option value='");
                                out.write(value);
                                out.write("'");
                                if (sel.equals(value)) {
                                    out.write(" selected='selected'");
                                }
                                out.write(">");
                                out.write(opt.getName());
                                out.write("</option>\n");
                            }
                            out.write("</select>\n");
                        }
                        else {
                            out.write("<input type='"+type+"' name='MODULE_PREF_VALUE_");
                            out.write(name);
                            out.write("' value='");
                            out.write("%MODULE_PREF_VALUE_"+name+"%");
                            out.write("'/>\n");
                        }
                        out.write("<input type='hidden' name='MODULE_PREF_TYPE_");
                        out.write(name);
                        out.write("' value='");
                        out.write(type);
                        out.write("'/>");
                        out.write("</td></tr>");
                    }
                    catch(NoSuchPreferenceException nspe) {
                        log.error(nspe);
                    }
                }
                out.write("</tbody>\n");
                out.write("</table>\n");
                out.write("<input type='submit' name='action' value='Save'/>\n");
                out.write("<input type='submit' name='action' value='Cancel'/>\n");
            } else {
                out.write("<p>This module has no preferences.</p>\n");
                out.write("<input type='submit' value='OK'/>\n");
            }
            out.write("</form>\n");
            out.write("</div>\n");
        }
        /* Help Mode */
        else if (request.getPortletMode().equals(PortletMode.HELP)) {
            out.write("<h4>About this module...</h4>\n");
            out.write("<table border='0'>\n");
            out.write("<tr><td valign='top'>Title:</td><td>");
            out.write(module.getTitle());
            out.write("</td></tr>\n");
            out.write("<tr><td valign='top'>Author:</td><td>");
            out.write(module.getAuthor());
            out.write("</td></tr>\n");
            out.write("<tr><td valign='top'>Description:</td><td>");
            out.write(module.getDescription());
            out.write("</td></tr>\n");
            String help = module.getHelp();
            if (help != null) {
                out.write("<tr><td valign='top'>Help:</td><td>");
                out.write(help);
                out.write("</td></tr>\n");
            }
            out.write("</table>\n");
            out.write("<form action='");
            out.write(response.createActionURL().toString());
            out.write("' method='POST'>");
            out.write("<p align='center'>");
            out.write("<input type='submit' value='Close Help'/>");
            out.write("</p>");
            out.write("</form>");
            out.write("<!--\n");
            out.write("$RCSfile: ModulePortlet.java,v $\n");
            out.write("$Name:  $ $Revision: 1.25 $\n");
            out.write("-->\n");
            return;
        }

        /* Include an anchor named after the module for locating DOM
         * elements relative to this module. */
        out.write("<div id='%MODULE_ID%'></div>");

        /* Write module content. If the 'src' attribute of the
         * 'content' element is set, then the module source is
         * retrieved from an external URL. Otherwise, the module
         * content is included inline. Either way, tokens are replaced
         * in the module content. When fetching external content,
         * preferences and user attributes declared by the module are
         * passed to the 'src' URL as query parameters. */
        if (module.getSrc() == null) {
            out.write(module.getContent());
        } else try {
            HttpClient client = new HttpClient();
            GetMethod getContent = new GetMethod(module.getSrc());
            Vector params = new Vector();
            // Add a parameter for every module preference.
            ModulePrefs modulePrefs = module.getPreferences();
            PortletPreferences portletPrefs = request.getPreferences();
            Iterator i = modulePrefs.getNames();
            while(i.hasNext()) {
                String name = (String)i.next();
                String value = portletPrefs.getValue(name, modulePrefs.getDefault(name));
                params.add(new NameValuePair(name, value));
                log.info("Added parameter " + name + "=" + value);
            }
            // Also add a parameter for every user attribute declared
            // by the module.
            ModuleAttrs moduleAttrs = module.getAttributes();
            Map userInfo = (Map)request.getAttribute(PortletRequest.USER_INFO);
            i = moduleAttrs.getNames();
            while(i.hasNext()) {
                String name = (String)i.next();
                String alias = moduleAttrs.getAlias(name);
                String value = (userInfo != null) ? (String) userInfo.get(name) : "";
                params.add(new NameValuePair(alias, value));
                log.info("Added attribute " + alias + " (aka " + name + ")=" + value);
            }
            getContent.setQueryString((NameValuePair[])params.toArray(new NameValuePair[0]));
            log.info("Requesting " + module.getSrc());
            client.executeMethod(getContent);
            if (getContent.getStatusCode() == 200) {
                log.info(getContent.getStatusLine());
                out.write(getContent.getResponseBodyAsString());
            } else {
                log.error(getContent.getStatusLine());
                getContent.getResponseBody();
            }
            getContent.releaseConnection();
        } catch (Throwable t) {
            log.error("Failed to get module content.", t);
            out.write("<p align=center><em>Sorry :$</em></p>");
            out.write("<p align=center><em>This channel is temporarily unavailable.</em></p>");
        }
            
        /* Call module onload function. */
        out.write("<script>");
        out.write("if(window.%MODULE_ID%_onload){setTimeout(%MODULE_ID%_onload,500);}");
        out.write("</script>\n");

        out.close();
    }

    public void processAction(ActionRequest request,
                              ActionResponse response)
        throws PortletException,
               java.io.IOException
    {
        if (CANCEL.equalsIgnoreCase(request.getParameter(ACTION))) {
            response.setPortletMode(PortletMode.VIEW);
            return;
        }
        // Process preferences. Store values and display content.
        PortletPreferences portletPrefs = request.getPreferences();
        String url = request.getParameter(URL);
        if (url != null) {
            if (isFeed(url)) {
                System.out.println("Adding news feed....");
                try {
                    java.net.URI mod = new java.net.URI
                        (
                         request.getScheme(),
                         null,  // userInfo
                         request.getServerName(),
                         request.getServerPort(),
                         request.getContextPath()+"/news.xml",
                         null,  // query
                         null   // fragment
                         );
                    System.out.println("ModulePortlet: Using module " + mod.toString());
                    portletPrefs.setValue(URL, mod.toString());
                    portletPrefs.setValue("MODULE_PREF_VALUE_feed", url);
                } catch(URISyntaxException use) {
                    log.error("Unexpected error.", use);
                }
            }
            else if (isHTML(url)) {
                System.out.println("Adding iframe channel...");
                try {
                    java.net.URI mod = new java.net.URI
                        (
                         request.getScheme(),
                         null,  // userinfo
                         request.getServerName(),
                         request.getServerPort(),
                         request.getContextPath()+"/mini.xml",
                         null,  // query
                         null   // fragment
                         );
                    portletPrefs.setValue(URL, mod.toString());
                    portletPrefs.setValue("MODULE_PREF_VALUE_address", url);
                } catch(URISyntaxException use) {
                    log.error("Unexpected error.", use);
                }
            } else {
                System.out.println("Adding module...");
                portletPrefs.setValue(URL, url);
            }
        }
        for(Enumeration i = request.getParameterNames(); i.hasMoreElements();) {
            String name = (String)i.nextElement();
            if (name != null && name.startsWith("MODULE_PREF_VALUE_")) {
                String value = request.getParameter(name);
                String type = request.getParameter("MODULE_PREF_TYPE_"+name.substring(18));
                log.info("<pref name='"+name+"' type='"+type+"' value='"+value+"'/>");
                portletPrefs.setValue(name, value);
            }
        }
        portletPrefs.store();
        response.setPortletMode(PortletMode.VIEW);
    }

    private boolean isHTML(String uri)
    {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(uri);
            conn = (HttpURLConnection)url.openConnection();
            String contentType = conn.getContentType();
            return contentType != null && contentType.startsWith("text/html");
        }
        catch(Exception e) {
            e.printStackTrace();
            return false;
        }
        finally {
            if (conn != null) conn.disconnect();
        }
    }

    private boolean isFeed(String uri)
    {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(uri);
            conn = (HttpURLConnection)url.openConnection();
            String contentType = conn.getContentType();
            if (contentType != null &&
                contentType.startsWith("application/rss+xml") || 
                contentType.startsWith("application/atom+xml")) {
                return true;
            } else {
                InputStream in = conn.getInputStream();
                try {
                    return isFeed(in);
                }
                catch(Exception e) {
                    e.printStackTrace();
                    return false;
                }
                finally {
                    in.close();
                }
            }
        }
        catch(Exception e) {
            e.printStackTrace();
            return false;
        }
        finally {
            if (conn != null) conn.disconnect();
        }
    }

    private boolean isFeed(InputStream in)
    {
        try {
            DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder domBuilder = domFactory.newDocumentBuilder();
            Document dom = domBuilder.parse(in);
            Element root = dom.getDocumentElement();
            String rootLocalName = root.getLocalName();
            if ("rss".equalsIgnoreCase(rootLocalName) ||
                "atom".equalsIgnoreCase(rootLocalName) ||
                "rdf".equalsIgnoreCase(rootLocalName) ||
                dom.getElementsByTagName("item").getLength() > 0) {
                return true;
            } else {
                return false;
            }
        }
        catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
