
package ca.uoguelph.ccs.portal.portlets.photo;

import javax.portlet.*;

public class FlickrPortlet extends GenericPortlet
{
    public static final String VIEW = "/WEB-INF/jsp/view.jsp";

    protected void doView(RenderRequest request,
                          RenderResponse response)
        throws PortletException,
               java.io.IOException
    {
        PortletRequestDispatcher dispatcher
            = getPortletContext().getRequestDispatcher(VIEW);
        
        dispatcher.include(request, response);
    }
}
