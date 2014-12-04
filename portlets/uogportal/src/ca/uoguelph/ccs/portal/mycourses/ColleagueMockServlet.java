/*
 * $Id: ColleagueMockServlet.java,v 1.1 2006/06/01 19:38:10 brharp Exp $
 * 
 * Copyright 2006 University of Guelph
 */

package ca.uoguelph.ccs.portal.mycourses;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * A servlet that mocks the behaviour of Colleague's course information service.
 * This servlet is only temporary (I hope!) so it hard codes the location of
 * course XML documents as C:\courses.
 * 
 * @author brharp
 * 
 */
public class ColleagueMockServlet extends HttpServlet
{
	private Log log;

	public void init()
	{
		log = LogFactory.getLog(getClass());
	}

	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException
	{
		// Get course codes from request.

		String[] codes = req.getParameterValues("course");

		// Create response document.
		DocumentBuilder documentBuilder;

		try {
			documentBuilder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();

			Document coursesDocument = documentBuilder.newDocument();
			Element coursesElement = coursesDocument.createElement("courses");
			coursesDocument.appendChild(coursesElement);

			for (int i = 0; i < codes.length; i++) {
				String code = codes[i];
				File courseXml = new File("C:\\courses\\" + code.toLowerCase()
						+ ".xml");
				log.info("Loading course " + code + " from " + courseXml);
				try {
					Document courseDocument = documentBuilder.parse(courseXml);
					Element courseElement = courseDocument.getDocumentElement();
					coursesElement.appendChild(coursesDocument.importNode(
							courseElement, true));
				} catch (Exception e) {
					log.error("Course definition not found for course " + code
							+ " at " + courseXml, e);
					throw new ServletException(e);
				}
			}
			// Send response.
			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			Transformer transformer;
			try {
				transformer = transformerFactory.newTransformer();
				transformer.transform(new DOMSource(coursesDocument),
						new StreamResult(res.getWriter()));
			} catch (Exception e) {
				log.error("Failed to serialize DOM.", e);
				throw new ServletException(e);
			}
		} catch (ParserConfigurationException pce) {
			log.error("Failed to create XML parser.", pce);
			throw new ServletException(pce);
		}

		res.flushBuffer();
	}

	static final long serialVersionUID = 6634024141326838010L;
}
