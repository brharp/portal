package ca.uoguelph.ccs.portal.services.sso.vpnbriefcase;

import javax.servlet.http.*;
import javax.servlet.*;

import java.io.IOException;

import org.jasig.portal.security.IPerson;
import org.jasig.portal.security.PersonManagerFactory;
import org.jasig.portal.services.Authentication;
import org.jasig.portal.services.LogService;
import org.jasig.portal.utils.ResourceLoader;

import org.jasig.portal.security.ISecurityContext;
import org.jasig.portal.security.provider.NotSoOpaqueCredentials;
import org.jasig.portal.security.IOpaqueCredentials;
import org.jasig.portal.security.IPrincipal;

/*
 * 
 * @author lalit
 * Toolbar channel to SSO to VPN Briefcase
 */
public class VPNBriefCase extends HttpServlet {
	
	public  void doGet(HttpServletRequest request, HttpServletResponse  response) 
	throws IOException, ServletException {
		doProcess(request, response);
	}
	
	public  void doPost(HttpServletRequest request, HttpServletResponse response)
	throws IOException, ServletException {
		doProcess(request, response);
	}
	
	private void doProcess(HttpServletRequest request, HttpServletResponse response)
	throws IOException, ServletException {
		String username = null;
		String password = null;
		try {
			// Get person details
			IPerson person = PersonManagerFactory.getPersonManagerInstance().getPerson(request);
			if (person != null) {
				ISecurityContext personSecurityContext = person.getSecurityContext();
				IPrincipal principal = personSecurityContext.getPrincipal();
				username = principal.getUID();
				password = getPassword(person); 
				
				request.setAttribute("username", username);
				request.setAttribute("password", password);

                //set up response to a POST request
				response.setHeader("Allow", "POST");
				
				String url="/vpnBriefCase.jsp";
				ServletContext sc = getServletContext();
				RequestDispatcher rd = sc.getRequestDispatcher(url);
				rd.forward(request, response);   
			}
			
		} catch (Exception e) {
			System.out.println("" + e.getMessage());
		}        
	}
	/**
	 * Retrieves the users cached credentials
	 * @param p the IPerson object of the person of whose credentials is being looked for
	 * @return the users cached password
	 */
	private String getPassword (IPerson p) throws Exception {
		String sPassword = null;
		try {
			ISecurityContext ic = (ISecurityContext) p.getSecurityContext();
			IOpaqueCredentials oc = ic.getOpaqueCredentials();
			if (oc instanceof NotSoOpaqueCredentials) {
				NotSoOpaqueCredentials nsoc = (NotSoOpaqueCredentials)oc;
				sPassword = nsoc.getCredentials();
			}
			
			// If still no password, loop through subcontexts to find cached credentials
			if (sPassword == null) {
				java.util.Enumeration en = ic.getSubContexts();
				while (en.hasMoreElements()) {
					ISecurityContext sctx = (ISecurityContext)en.nextElement();
					IOpaqueCredentials soc = sctx.getOpaqueCredentials();
					if (soc instanceof NotSoOpaqueCredentials) {
						NotSoOpaqueCredentials nsoc = (NotSoOpaqueCredentials)soc;
						sPassword = nsoc.getCredentials();
					}
				}
			}
		} catch (Exception e) {
			throw e;
		}
		return sPassword;
	}
}