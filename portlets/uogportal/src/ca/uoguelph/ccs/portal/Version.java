package ca.uoguelph.ccs.portal;

import java.io.*;
import javax.portlet.*;
import java.util.Properties;

public class Version extends GenericPortlet
{
    private Properties version;

    public void init()
    {
        version = new Properties();
        try {
            version.load(getClass().getResourceAsStream("/version.properties"));
        } catch (IOException ioe) {}
    }
    
    protected void doView(RenderRequest request,
                          RenderResponse response)
        throws PortletException,
               java.io.IOException
    {
        request.setAttribute("revision", version.getProperty("ca.uoguelph.ccs.portal.Version.revision"));
        request.setAttribute("name", version.getProperty("ca.uoguelph.ccs.portal.Version.name"));
        request.setAttribute("date", version.getProperty("ca.uoguelph.ccs.portal.Version.date"));
        // Include the content of the view in the render response.
        getPortletContext().getRequestDispatcher("/WEB-INF/jsp/version.jsp").include(request, response);
    }
}
