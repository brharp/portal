package ca.uoguelph.ccs.portal.services.sso.jiveforum;

import javax.servlet.http.*;
import javax.servlet.*;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.jasig.portal.security.IPerson;
import org.jasig.portal.security.PersonManagerFactory;

import org.jasig.portal.security.ISecurityContext;
import org.jasig.portal.security.provider.NotSoOpaqueCredentials;
import org.jasig.portal.security.IOpaqueCredentials;
import org.jasig.portal.security.IPrincipal;

import ca.uoguelph.ccs.portal.services.sso.util.UserCredential;
/*
 * 
 * @author lalit
 * Toolbar channel to SSO to WEBmail
 */
public class JiveForum extends HttpServlet {
	
	private static final Log LOG = LogFactory.getLog(JiveForum.class);
	
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
            IPerson person = UserCredential.getPerson(request);
            IPrincipal principal = UserCredential.getPrincipal(person);
            
            if (principal != null) {
            	username = UserCredential.getUsername(principal);
            }
			password = UserCredential.getPassword(person); 
			
			request.setAttribute("username", username);
			request.setAttribute("password", password);
			
			//set up response to a POST request
			response.setHeader("Allow", "POST");
			
			String url="/WEB-INF/jsp/jiveLogin.jsp";
			ServletContext sc = getServletContext();
			RequestDispatcher rd = sc.getRequestDispatcher(url);
			rd.forward(request, response);   
			
		} catch (Exception e) {
			LOG.debug("JiveForum Toolbar" + e.getMessage());
		}        
	}
}