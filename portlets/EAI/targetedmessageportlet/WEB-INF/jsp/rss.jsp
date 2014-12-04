<%@ page import="javax.portlet.*"%>
<%@ page import="java.util.*"%>
<%@ page import="java.io.*"%>
<%@ page import="java.sql.Timestamp"%>
<%@ page import="com.sun.syndication.feed.synd.*"%>
<%@ page import="com.sun.syndication.io.FeedException"%>
<%@ page import="com.sun.syndication.io.SyndFeedOutput"%>

<%@ page import="java.io.IOException"%>
<%@ page import="java.text.DateFormat"%>
<%@ page import="java.text.ParseException"%>
<%@ page import="java.text.SimpleDateFormat"%>

<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="x" uri="http://java.sun.com/jstl/xml"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt"%>

<%@ page session="true"%>

<portlet:defineObjects />

<%! 
class ColumnSorter implements Comparator {
        public int compare(Object a, Object b) {
            Map rowMap1 = (Map)a;
            Map rowMap2 = (Map)b;
            Object o1 = rowMap1.get("mark_expiry");
            Object o2 = rowMap2.get("mark_expiry");
            if (o1 == null || o2 == null) {
            	return 0;
            }
            return ((Comparable)o1).compareTo(o2);
        }
    }
%>
<%! 
	private List createFeedEntries(List notificationsList, List messageList) {
		String DATE_FORMATL = "yyyy-MM-dd hh:mm:ss";
		String DATE_FORMATS = "yyyy-MM-dd";//yyyy-mm-dd hh:mm:ss.fffffffff";
		
		List entries = new ArrayList();
		SyndEntry entry = null;
		SyndContent description = null;
		
		DateFormat dateParserL = new SimpleDateFormat(DATE_FORMATL);
   		DateFormat dateParserS = new SimpleDateFormat(DATE_FORMATS);
   		DateFormat dateParser = new SimpleDateFormat(DATE_FORMATS);
		int sizeList = notificationsList.size();
		int sizeMessage = messageList.size();
		try {
			for (int i=0; i<sizeList; i++) {
				Map notificationsMap = (Map)notificationsList.get(i);
				String title = (String)notificationsMap.get("message_subject");
				String owner = (String)notificationsMap.get("owner");
				String messageId = String.valueOf(notificationsMap.get("message_id"));
				System.out.println("title :" + title);
				System.out.println("messageId :" + messageId);
				System.out.println("owner :" + owner);
				entry = new SyndEntryImpl();
				entry.setTitle(title);
				entry.setAuthor(owner);
				entry.setLink("https://myportico.uoguelph.ca\n");
				Timestamp publishDate = (Timestamp)notificationsMap.get("release");
				//System.out.println("pdate :" + publishDate);
				//System.out.println("pdate :" + publishDate.getTime());
				//System.out.println("pdate :" +	new java.util.Date(publishDate.getTime()));
				//Date date = dateParserL.parse(publishDate.toString());
				//String dateString = dateParserS.format(date);
				//System.out.println(dateString);
				entry.setPublishedDate(dateParserL.parse(publishDate.toString()));
				//System.out.println(dateParserS.parse(dateString));
				//publishDate);//dateParser.parse("2004-06-08"));
				//entry.setPublishedDate(dateParser.parse(dateString));
				//dateParser.parse(publishDate.toString()));

				description = new SyndContentImpl();
				description.setType("text/plain");
				for (int j=0; j<sizeMessage; j++) {
					Map messageMap = (Map)messageList.get(i);
					String message_id = ((Integer)messageMap.get("message_id")).toString();
					System.out.println("message_id :" + message_id);
					if(messageId.equalsIgnoreCase(message_id)) {
						System.out.println("messageId :" + message_id);
						String message = (String)messageMap.get("message");
						System.out.println("message :" + message);
						description.setValue(message);
						break;
					}
				}
				entry.setDescription(description);
				entries.add(entry);
			}
		}
		catch (ParseException ex) {
			System.out.println(ex.getMessage() + ex.getStackTrace());
			// IT CANNOT HAPPEN WITH THIS SAMPLE
		}
		return entries;
	}
%>

<%
	String DEFAULT_FEED_TYPE = "default.feed.type";
	String FEED_TYPE = "type";
	String MIME_TYPE = "application/xml; charset=UTF-8";
	String COULD_NOT_GENERATE_FEED_ERROR = "Could not generate feed";	
	
	String _defaultFeedType="atom_0.3";
	try {
		System.out.println("*************process feed******\n");
		String feedType = "atom_0.3";//req.getParameter(FEED_TYPE);
		//feedType = (feedType!=null) ? feedType : _defaultFeedType;
		SyndFeed feed = new SyndFeedImpl();
		feed.setFeedType(feedType);
		feed.setTitle("Targeted Notifications\n");
		feed.setLink("https://myportico.uoguelph.ca\n");
		feed.setDescription("Targeted Notifications in MyPortico\n");
		
		PortletSession psession = renderRequest.getPortletSession();
		List messageList = (List)psession.getAttribute("messageList");
		List user_notificationsMap = (List) psession.getAttribute("user_notificationsMap");
		int size = 0;
		size = user_notificationsMap.size();
		String userNotificationSize = null;
		List entries = null;
		if (size != 0) {
			userNotificationSize  = String.valueOf(size);
			Collections.sort(user_notificationsMap, new ColumnSorter());
			entries = createFeedEntries (user_notificationsMap, messageList);
			
		}
		if (entries != null) {
			feed.setEntries(entries);
		}
		System.out.println("jsp user size :" + userNotificationSize);
	
		List release_notificationsMap = (List) psession.getAttribute("release_notificationsMap");
		size = release_notificationsMap.size();
		String ownerNotificationSize = null;
		if (size != 0) {
			ownerNotificationSize  = String.valueOf(size);
			Collections.sort(release_notificationsMap, new ColumnSorter());
			entries = createFeedEntries (release_notificationsMap, messageList);
		}
		if (entries != null) {
			feed.setEntries(entries);
		}
				
		//renderResponse.setContentType(MIME_TYPE);
		SyndFeedOutput output = new SyndFeedOutput();
		String path= renderRequest.getContextPath();
		//String path2= renderRequest.getAbsolutePath(path);
		System.out.println("*******Path*******\n" + path);
				//System.out.println("*******Path2*******\n" + path2);
		File inputFile = new File("test.txt");
	//		String redirectURL = "http://localhost:8080/" + path + "/farrago.txt";
		String redirectURL = "http://yahoo.com/";
        //response.sendRedirect(redirectURL);
        if (feed != null) {
        	System.out.println("*******feed not null*******\n" + feed.getTitle());
        }
		output.output(feed, inputFile);
		//String rss = output.outputString(feed);
		//PrintWriter out = response.getWriter();
		//out.println("<pre>");
		//output.output(feed, out);
		//out.println("</pre>");
		//		out.println(rss);
	}
	catch (IOException ioe) {
		System.out.println(ioe.getMessage() + ioe);
		//String msg = COULD_NOT_GENERATE_FEED_ERROR;
		//log(msg,ex);
		//res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,msg);
	}								
	
	catch (FeedException ex) {
		//String msg = COULD_NOT_GENERATE_FEED_ERROR;
		//log(msg,ex);
		//res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,msg);
	}								
%>

