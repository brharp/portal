package ca.uoguelph.ccs.portal.portlets.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import java.net.HttpURLConnection;
import java.net.URL;

import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.portlet.PortletRequest;
/**
 * @author lalit
 *
 */
public class ElmServlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
	private static final long serialVersionUID = 2963863938299247976L;
		
	/**
	 * Constructs this servlet.
	 *  
	 * @see javax.servlet.http.HttpServlet#HttpServlet()
	 */
	public ElmServlet() {
		super();
	}   	
	
	/**
	 * 
	 * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request,response);
	}  	
	
	/**
	 * 
	 * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {		
		PrintWriter writer = response.getWriter();
		Map elmsUserInfo = (Map)request.getAttribute("elmsUserInfo");
		String userName;
		String lastName;
		String organizationalStatus;
		
		try{
			//retireve elms specific info
			userName = (String)elmsUserInfo.get("user.login.id");
			lastName = (String)elmsUserInfo.get("user.name.last");
			organizationalStatus = (String)elmsUserInfo.get("uPortalTemplateUserName");
			
			String click = ElmsUtils.getRedirectURL("showie", "Employee:Staff");
			//TODO:
			//click = Elm2.getRedirectURL(userName, organizationalStatus);
			//writer.write("<p>Please, click the link below: </p>"); 
			//writer.write("<a target=\"_blank\" href=\"" + click + "\">Software Distribution</a>");
			writer.write(
					"<iframe name=\"Elms\" src=\"" + click + "\" scrolling=\"auto\" frameborder=\"no\" height=\"600\" width=\"100%\"> </iframe>");
		} catch (NullPointerException npe) {
			throw new ServletException(npe);
		} catch (Exception e) {
			throw new ServletException(e);
		} finally {
			if (writer != null) writer.close();
		}
	}
}