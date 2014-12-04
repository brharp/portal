package ca.uoguelph.ccs.portal.portlets.hostname;

import java.io.IOException;
import java.io.Writer;

import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

public class HostnamePortlet extends GenericPortlet
{
    protected void doView(RenderRequest request, RenderResponse response)
        throws PortletException, IOException
    {
        response.setContentType("text/html");
        Writer writer = response.getWriter();
        /*writer.write("Help, I'm a portlet, and I'm trapped in a portal!");*/
        writer.write("<br />");

	try
	{
		java.net.InetAddress localMachine =
		java.net.InetAddress.getLocalHost();
		String hostname = localMachine.getHostName();

		/*coded host names for the production portal "P?"
		  coded host names for the qa portal "QA?"
		*/
		String portalHostName = null;	

		if ("happy".equals(hostname)) {
			portalHostName = "P1";
		} else if ("doc".equals(hostname)) {
			portalHostName = "P2";
		} else if ("bashful".equals(hostname)) {
			portalHostName = "P3";
		} else if ("sneezy".equals(hostname)) {
			portalHostName = "P4";
		} else if ("grumpy".equals(hostname)) {
			portalHostName = "P5";
		}

		writer.write ("Portal Server Name: " + portalHostName);
	}
	catch(java.net.UnknownHostException uhe)
	{
	//handle exception
		writer.write ("Portal Server Name: " + "Not Found.");
	}

    }
}
