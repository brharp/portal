package ca.uoguelph.ccs.atomcal;

import java.io.*;
import java.util.*;
import java.net.URL;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.w3c.dom.*;

public class OpmlServlet extends HttpServlet
{  
    public static final String TITLE_PROP_PREFIX = "alias.title.";
    public static final int    TITLE_PROP_PRELEN = TITLE_PROP_PREFIX.length();

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
        throws ServletException, 
               IOException
    {
        Properties props = AliasProperties.getProperties();

        String serverName = request.getServerName();
        String contextPath = request.getContextPath();

        try {
            // Build document.
            Document dom = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Element opmlElement = dom.createElement("opml");
            Element headElement = dom.createElement("head");
            Element titleElement = dom.createElement("title");
            Text titleText = dom.createTextNode("University of Guelph Calendars");
            titleElement.appendChild(titleText);
            headElement.appendChild(titleElement);
            opmlElement.appendChild(headElement);
            Element bodyElement = dom.createElement("body");
            for(Enumeration i = props.propertyNames(); i.hasMoreElements();) {
                String propName = (String)i.nextElement();
                if (propName.startsWith(TITLE_PROP_PREFIX)) {
                    String aliasName = propName.substring(TITLE_PROP_PRELEN);
                    String aliasTitle = props.getProperty(propName);
                    Element outlineElement = dom.createElement("outline");
                    Attr typeAttr = dom.createAttribute("type");
                    typeAttr.setValue("rss");
                    outlineElement.setAttributeNode(typeAttr);
                    Attr versionAttr = dom.createAttribute("version");
                    versionAttr.setValue("RSS2.0");
                    outlineElement.setAttributeNode(versionAttr);
                    Attr titleAttr = dom.createAttribute("title");
                    titleAttr.setValue(aliasTitle);
                    outlineElement.setAttributeNode(titleAttr);
                    Attr xmlUrlAttr = dom.createAttribute("xmlUrl");
                    URL link = new URL(request.getScheme(),
                                       request.getServerName(), 
                                       request.getServerPort(),
                                       request.getContextPath() +
                                       "/.rss?h=" + aliasName);
                    xmlUrlAttr.setValue(link.toString());
                    outlineElement.setAttributeNode(xmlUrlAttr);
                    bodyElement.appendChild(outlineElement);
                }
            }
            opmlElement.appendChild(bodyElement);
            dom.appendChild(opmlElement);

            // Write response.
            Source in = new DOMSource(dom);
            Result out = new StreamResult(response.getWriter());
            Transformer xform = TransformerFactory.newInstance().newTransformer();
            response.setContentType("text/xml");
            xform.transform(in, out);
        }
        catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
