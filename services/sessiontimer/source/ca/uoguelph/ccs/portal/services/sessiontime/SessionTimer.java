package ca.uoguelph.ccs.portal.services.sessiontime; 

import javax.servlet.http.*;
import javax.servlet.*;
import java.io.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

/**
 * 
 * @author lalit jairath
 *
 */
public class SessionTimer extends HttpServlet {
	
	private static final Log LOG = LogFactory.getLog(SessionTimer.class);
	
	public  void doGet(HttpServletRequest request, HttpServletResponse  response) 
	throws IOException, ServletException {
		doGetOrPost(request, response);
	}
	
	public  void doPost(HttpServletRequest request, HttpServletResponse response)
	throws IOException, ServletException {
		doGetOrPost(request, response);
	}
	
	private void doGetOrPost(HttpServletRequest request, HttpServletResponse response)
	throws IOException, ServletException {
		PrintWriter out = null;
		try {
			//set headers and buffer size before accessing the Writer
			response.setContentType("text/html");
			response.setBufferSize(8192);
			out = response.getWriter();
			out.println("Session Updated - Server Time:" + new Date());
		} catch (Exception e) {
            response.resetBuffer();
			LOG.debug("Session Timer" + e.getMessage());
			throw new ServletException(e);
		} finally {
            if (out != null) {
            	out.close();
            }
        }
	}
}