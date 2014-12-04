package ca.uoguelph.ccs.portal.portlets.notification.mule.servlet;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.mule.extras.client.MuleClient;
import org.mule.umo.UMOException;
import org.mule.umo.UMOMessage;

import org.jasig.portal.RDBMServices;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Servlet implementation class for Servlet: HelloServlet
 */
 public class HelloServlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {

	private static final long serialVersionUID = 2963863938299247976L;
	private MuleClient m_client = null;
	private static final Log log = LogFactory.getLog(HelloServlet.class);


	/**
	 * Constructs this servlet.
	 *  
	 * @see javax.servlet.http.HttpServlet#HttpServlet()
	 */
	public HelloServlet() {
		super();
	}   	
	
	/**
	 * Setups the mule client
	 */
	public void init() throws ServletException {
		try {
			m_client = new MuleClient();
		} catch (UMOException e) {
			throw new ServletException(e);
		}
		super.init();
		
	}
	
	/**
	 * Handles the GET HTTP requests.
	 * 
	 * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request,response);
	}  	
	
	/**
	 * Handles the POST HTTP Requests
	 * 
	 * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//BufferedWriter writer = null;
		try {
			//writer = new BufferedWriter(new OutputStreamWriter(response.getOutputStream()));
			UMOMessage message = m_client.receive("jms://to.servlet2",1000);
			HttpSession session = request.getSession();
			if (message != null) {
				String payload = message.getPayloadAsString();
				session.setAttribute("announce_payload", payload);
			} else {
				session.removeAttribute("announce_payload");
				//writer.write("--no message waiting in queue--");
			}
		} catch (Exception e) {
			throw new ServletException(e);
		} finally {
			//if (writer != null) writer.close();
		}
	}   	  
}