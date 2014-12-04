<%--
  "Copyrighted 2006 U of Guelph"
 --%>
 
<%@ page import="javax.portlet.PortletSession" %>
<%@ page import="javax.portlet.RenderRequest" %>

<%@ taglib uri="http://jakarta.apache.org/taglibs/log-1.0" prefix="log" %>

<%--
	This file is used only if the deployer school may want to push feeds that the user can't opt out
	To disable the functionality of this file just comment out the following line in view.jsp.
	<!-- <%@ include file="view_helper.jsp" %>  -->	
 --%>

<portlet:defineObjects/>
<%
	    PortletSession psession = renderRequest.getPortletSession();
		String[] mandate_feeds = new String[] {"http://feeds.feedburner.com/uoguelph/president",
				"http://blogs.uoguelph.ca/cioweb/blog/default/?flavor=atom"
				};
		
		String[][] feeds = new String[][] { 
				{
		    	    "http://www.alistapart.com/feed/rss.xml", 
		            "http://rss.cbc.ca/canadiannews.xml",
		            "http://rss.cbc.ca/lineup/sports.xml",
					"http://feeds.feedburner.com/ConfessionsOfAPioneerWoman",
					"http://wvs.topleftpixel.com/index.rdf",
					"http://feeds.gawker.com/gizmodo/excerpts.xml",
					"http://feeds.feedburner.com/GlobalCulture",
					"http://www.tbray.org/ongoing/ongoing.atom", 
					"http://www.phdcomics.com/gradfeed_justcomics.php", 
					"http://postsecret.blogspot.com/feeds/posts/default",
					"http://www.quotationspage.com/data/qotd.rss",
					"http://www.microsite.reuters.com/rss/topNews",
					"http://rickmercer.blogspot.com/feeds/posts/default",
					"http://www.gcfl.net/rss.php",
					"http://www.worldpress.org/feeds/topstories.xml"
				}, {
					"http://youtube.com/rss/global/recently_featured.rss"
				}
				
			};
		List default_feeds = new ArrayList();
		for (int i=0; i<feeds.length; i++) {
			default_feeds.add(feeds[i]);
		}
		psession.setAttribute(settingsHandler.DEFAULT_FEEDS, default_feeds, PortletSession.PORTLET_SCOPE);
		//psession.setAttribute(settingsHandler.MANDATE_FEEDS, mandate_feeds, PortletSession.PORTLET_SCOPE);
		
		String[] student_feeds = new String[] {"http://www.uoguelph.ca/portal/resources/studentcampuslife.xml"};
		String[] grad_student_feeds = new String[] {"http://www.uoguelph.ca/portal/resources/studentcampuslife.xml"};
		String[] faculty_feeds = new String[] {"http://www.uoguelph.ca/portal/resources/facultycampuslife.xml"};
		String[] staff_feeds = new String[] {"http://www.uoguelph.ca/portal/resources/staffcampuslife.xml"};
		String[] guest_feeds = new String[] {"http://www.uoguelph.ca/portal/resources/defaultcampuslife.xml"};
		
		
	    psession.setAttribute(settingsHandler.STUDENT_FEEDS, student_feeds, PortletSession.PORTLET_SCOPE);
	    psession.setAttribute(settingsHandler.GRAD_STUDENT_FEEDS, grad_student_feeds, PortletSession.PORTLET_SCOPE);
	    psession.setAttribute(settingsHandler.FACULTY_FEEDS, faculty_feeds, PortletSession.PORTLET_SCOPE);
	    psession.setAttribute(settingsHandler.STAFF_FEEDS , staff_feeds, PortletSession.PORTLET_SCOPE);
	    psession.setAttribute(settingsHandler.GUEST_FEEDS, guest_feeds, PortletSession.PORTLET_SCOPE);
 %>