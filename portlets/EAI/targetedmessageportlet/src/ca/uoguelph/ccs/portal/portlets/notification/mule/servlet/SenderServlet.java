package ca.uoguelph.ccs.portal.portlets.notification.mule.servlet;

import java.text.DateFormat;
import java.util.Date;
import java.io.*;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mule.extras.client.MuleClient;
import org.mule.umo.UMOException;

/**
 * Simple servlet, which when invoked sends a string to a certain queue.
 * This String is then received when the receiver servlet is called.
 */
 public class SenderServlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
    
	private static final long serialVersionUID = -7557758560225183285L;
	private MuleClient m_client = null;
	
	
	public void init() throws ServletException {
		try {
			m_client = new MuleClient();
		} catch (UMOException e) {
			throw new ServletException(e);
		}
		super.init();
		
	}

	/** 
	 * @see javax.servlet.http.HttpServlet#HttpServlet()
	 */
	public SenderServlet() {
		super();
	}   	
	
	/**
	 * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request,response);
	}  	
	
	/**
	 * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	        DateFormat fullDateFormat =
        	    DateFormat.getDateTimeInstance(
	            DateFormat.FULL,
        	    DateFormat.FULL);

		String cdate = fullDateFormat.format(new Date(System.currentTimeMillis()));	
		PrintWriter writer = null;

		try {
        		writer = response.getWriter();
			writer.write("This is a message @" + cdate);
			m_client.send("jms://from.servlet1","This is a message @" + cdate,null);
		//} catch (UMOException e) {
		//	throw new ServletException(e);
		//}

		} catch (Exception e) {
			throw new ServletException(e);
		} finally {
			if (writer != null) writer.close();
		}
	}   	  	    
}

