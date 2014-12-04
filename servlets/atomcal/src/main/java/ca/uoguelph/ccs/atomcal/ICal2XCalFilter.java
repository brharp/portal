
package ca.uoguelph.ccs.atomcal;

import java.io.*;
import java.util.Iterator;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import net.fortuna.ical4j.data.*;
import net.fortuna.ical4j.model.*;
import org.w3c.dom.*;

public class ICal2XCalFilter implements Filter 
{
    private FilterConfig filterConfig = null;

    public void doFilter(final ServletRequest request, 
                         final ServletResponse response, 
                         final FilterChain chain)
        throws IOException, ServletException 
    {
        String iCalendar;
        Calendar calendar;
        Document dom;

        // Get iCalendar response.

        final CharArrayWriter snk = new CharArrayWriter();

        ServletResponseWrapper wrapper = 
            new HttpServletResponseWrapper((HttpServletResponse)response)
            {
                public PrintWriter getWriter() {
                    return new PrintWriter(snk);
                }
            };

        chain.doFilter(request, wrapper);

        iCalendar = snk.toString();

        if (iCalendar == null || iCalendar.length() == 0)
            return;

        // Parse iCalendar.

        try {
            CalendarBuilder builder = new CalendarBuilder();
            calendar = builder.build(new StringReader(iCalendar));
        }
        catch(ParserException e) {
            throw new ServletException(e);
        }

        // Build xCal DOM.

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            dom = factory.newDocumentBuilder().newDocument();
        
            Element iCalendarElement = dom.createElement("iCalendar");

            Element vcalendarElement = dom.createElement("vcalendar");

            Element prodIdElement = dom.createElement("prodid");
            Text prodIdText = dom.createTextNode(calendar.getProductId().getValue());
            prodIdElement.appendChild(prodIdText);
            vcalendarElement.appendChild(prodIdElement);

            Element versionElement = dom.createElement("version");
            Text versionText = dom.createTextNode(calendar.getVersion().getValue());
            versionElement.appendChild(versionText);
            vcalendarElement.appendChild(versionElement);

            for(Iterator i = calendar.getComponents().iterator(); i.hasNext();) {
                Component component = (Component)i.next();
                Element componentElement = dom.createElement(component.getName().toLowerCase());
                for (Iterator j = component.getProperties().iterator(); j.hasNext();) {
                    Property property = (Property)j.next();
                    Element propertyElement = dom.createElement(property.getName().toLowerCase());
                    for(Iterator k = property.getParameters().iterator(); k.hasNext();) {
                        Parameter parameter = (Parameter)k.next();
                        Attr parameterAttr = dom.createAttribute(parameter.getName().toLowerCase());
                        parameterAttr.setValue(parameter.getValue());
                        ((Element)propertyElement).setAttributeNode(parameterAttr);
                    }
                    Text propertyText = dom.createTextNode(property.getValue());
                    propertyElement.appendChild(propertyText);
                    componentElement.appendChild(propertyElement);
                }
                vcalendarElement.appendChild(componentElement);
            }
        
            iCalendarElement.appendChild(vcalendarElement);
        
            dom.appendChild(iCalendarElement);
        }
        catch(Exception e) {
            throw new ServletException(e);
        }

        // Write XML response.

        try {
            response.setContentType("text/xml");
        
            Source in  = new DOMSource(dom);
            Result out = new StreamResult(response.getWriter());
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.transform(in, out);
        }
        catch(Exception e) {
            throw new ServletException(e);
        }
    }
    
    public void init(FilterConfig filterConfig) 
    {
        this.filterConfig = filterConfig;
    }
    
    public void destroy()
    {
        this.filterConfig = null;   
    }
}


