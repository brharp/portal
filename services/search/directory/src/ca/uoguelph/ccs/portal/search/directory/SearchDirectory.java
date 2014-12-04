package ca.uoguelph.ccs.portal.search.directory;

import java.io.*;
import java.util.Iterator;
import java.util.Map;
import java.util.Enumeration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.net.URLConnection;
import java.net.MalformedURLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class SearchDirectory extends HttpServlet
{
	public static final String SEARCH_URL = "http://www.uoguelph.ca/directory/index.cfm";
	
	public static final String START_TAG = "Found";
	public static final String END_TAG = "</table>";
	
	public static final String SEARCH = "search";
	public static final String SIMPLE = "simple";
	public static final String COMPLEX = "complex";
	
	public static final String SUBMIT = "submit";
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
	throws ServletException, IOException
	{
		doGetOrPost(req, resp);
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
	throws ServletException, IOException
	{
		doGetOrPost(req, resp);
	}
	
	public void doGetOrPost(HttpServletRequest req, HttpServletResponse resp)
	throws ServletException, IOException
	{
		resp.setContentType("text/html");
		// Construct data
		/*
		 commonname=lalit&
		 orgstatus=all&
		 search=simple&
		 mail=&
		 phone=&
		 department=null&
		 showresults=yes&
		 givenname=&
		 surname=&
		 submit=search
		 */
        
		PrintWriter out = resp.getWriter();
		StringBuffer sb = new StringBuffer();
		try {
			HttpSession session = req.getSession();
			//Get the values of all request parameters
			Enumeration enum = req.getParameterNames();
			String name = null;
			String value = null;
			for (; enum.hasMoreElements(); ) {
				// Get the name of the request parameter
				name = (String)enum.nextElement();
				// Get the value of the request parameter
				value = req.getParameter(name);
				if (name != null) {
                    name = name.trim();
				}
				if (value != null) {
                    value = value.trim();
				}
				session.setAttribute(name, value);
				sb.append(URLEncoder.encode(name, "UTF-8")).append("=").append(URLEncoder.encode(value, "UTF-8")).append("&");
				System.out.println("name*****" + name);
				System.out.println("value*****" + value);
			}
			String data = "";
			if (sb != null && sb.length() != 0) {
				data = sb.toString(); 
			}
			System.out.println("data*****" + data);
			RequestDispatcher disp;
			if (COMPLEX.equalsIgnoreCase(req.getParameter(SEARCH))) {
				disp = getServletContext( ).getRequestDispatcher("/WEB-INF/jsp/ComplexSearch.jsp");
				disp.include(req, resp);
			} else { //handle simple or direct link
				disp = getServletContext( ).getRequestDispatcher("/WEB-INF/jsp/SimpleSearch.jsp");
				disp.include(req, resp);
			}
			// Send data
			URL url = new URL(SEARCH_URL);
			URLConnection conn = url.openConnection();
			conn.setDoOutput(true);
			OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
			wr.write(data);
			wr.flush();
			
			// Get the response
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			boolean scrapeFlag = false;
			StringBuffer result = new StringBuffer();
			while ((line = rd.readLine()) != null) {
				if (line.indexOf(START_TAG) != -1) {
					scrapeFlag= true;
				}
				if (scrapeFlag && line.
						indexOf(END_TAG) != -1) {
					scrapeFlag = false ;
				}
				if (scrapeFlag) {
					result.append(line);
				}
			}
			String submit = req.getParameter(SUBMIT);
			if ((result == null || result.length() == 0) && submit != null) {
				out.println("<p class=\"myportico-error-text\">No records were found.</p>\n");   
			}
			
			if (result != null && result.length() != 0) {
				out.println("<table class=\"myportico-search-results\">");
				out.println("<td colspan=2 class=\"main\">");
				out.println("<tr>");      
				out.println(result.toString());
				out.println("</table>"); // not scraped
			}
			
			out.println("<p> If your directory listing is incorrect, please contact " +
					"<a href=\"mailto:directory@uoguelph.ca\">directory@uoguelph.ca</a><br />");
			out.println("with your corrections.");
			out.println("</p>");
			
			out.println("<a href=\"javascript:window.close()\">Close</a>");
			out.println("</div> <div id=\"footer\"> <span class=\"hide\">Footer</span> <div id=\"footerlogo\">");
			out.println("<span class=\"hide\">University of Guelph</span> </div> ");
			out.println("<div id=\"institution\"> <div id=\"institutionLocation\">Guelph, Ontario, Canada</div>");
			out.println("<div id=\"institutionContact\"><div id=\"contact1\"><span id=\"contact1Label\">N1G 2W1</span>");
			out.println("</div><div id=\"contact2\"><span id=\"contact2Label\">Tel: (519) 824-4120</span></div></div></div>");
			out.println("<div id=\"legal\"><p id=\"copyright\">Copyright 2006 University of Guelph</p></div></div></div>" +
			"</body></html>");
			wr.close();
			rd.close();
		} catch (Exception e) {
			System.out.println("Exception Searching Directory:" + e.getMessage());
		}
	}
}
