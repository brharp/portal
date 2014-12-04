<%@ page import="javax.portlet.*"%>
<%@ page import="java.util.*"%>
<%@ page import="java.text.*"%>
<%@ page import="java.sql.*"%>

<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="x" uri="http://java.sun.com/jstl/xml"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt"%>

<%@ page session="true"%>
<portlet:defineObjects />

<%! 
	class ColumnSorter implements Comparator {
        public int compare(Object a, Object b) {
        	int result = 0;
            Map rowMap1 = (Map)a;
            Map rowMap2 = (Map)b;
            Object release1 = rowMap1.get("release");
            Object release2 = rowMap2.get("release");
            Object read1 = rowMap1.get("mark_read");
            Object read2 = rowMap2.get("mark_read");

            /*
            if (read1 == null || read2 == null) {
            	return 0;
            }
            result = ((Comparable)read2).compareTo(read1);
			return result;            
            */
            
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
            java.util.Date defaultDate = null;
            try {
            	defaultDate = formatter.parse("2004-07-24 09:45:52.189"); //arbitrary date
            } catch(ParseException pe) {
            	
            }
            java.sql.Timestamp defaultTimestamp = new java.sql.Timestamp(defaultDate.getTime());
            
            if (read1 == null) {
            	read1 = defaultTimestamp;
            } 
            if (read2 == null) {
            	read2 = defaultTimestamp;
            }
            
            
            if (release1 == null) {
            	release1 = defaultTimestamp;
            } 
            if (release2 == null) {
            	release2 = defaultTimestamp;
            }

            result = ((Comparable)read1).compareTo(read2);
            return ((Comparable)release2).compareTo(release1);
        }
    }
%>

<%PortletSession psession = renderRequest.getPortletSession();
	String mark_release = (String)psession.getAttribute("mark_release");
	pageContext.setAttribute("mark_release", mark_release);
	
	String set_mark_release = null;
	psession.setAttribute("mark_release", set_mark_release);
	//System.out.println("mark_release :" + mark_release);
	
	List user_notificationsMap = (List) psession.getAttribute("user_notificationsMap");
	int size = 0;
	size = user_notificationsMap.size();
	String userNotificationSize = null;
	if (size != 0) {
		userNotificationSize  = String.valueOf(size);
		Collections.sort(user_notificationsMap, new ColumnSorter());
	}
	//System.out.println("jsp user size :" + userNotificationSize);
	pageContext.setAttribute("user_notificationsMap", user_notificationsMap);
	pageContext.setAttribute("userNotificationSize", userNotificationSize);
	
	List release_notificationsMap = (List) psession.getAttribute("release_notificationsMap");
	size = release_notificationsMap.size();
	String releaseNotificationSize = null;
	if (size != 0) {
		releaseNotificationSize  = String.valueOf(size);
		Collections.sort(release_notificationsMap, new ColumnSorter());
	}
				
	//System.out.println("jsp release size :" + releaseNotificationSize);
	pageContext.setAttribute("releaseNotificationSize", releaseNotificationSize);
	size = release_notificationsMap.size();
	for (int i=0; i<size; i++) {
  		Map messageMap = (Map)release_notificationsMap.get(i);	
		Iterator it = messageMap.entrySet().iterator();
        //System.out.println("messageMap: ReleaseMessage.");
		while (it.hasNext()) {
		    Map.Entry pairs = (Map.Entry)it.next();
		    //logger.debug("messageMap: ReleaseMessage.");
		    //logger.debug(pairs.getKey() + " = " + pairs.getValue());
		    //System.out.println(pairs.getKey() + " = " + pairs.getValue());
		}
	}
	pageContext.setAttribute("release_notificationsMap", release_notificationsMap);
%>

<c:choose>
	<c:when test="${empty releaseNotificationSize && empty userNotificationSize}">
		<form
			action="<portlet:renderURL><portlet:param name="COMMAND" value="REFRESH"/></portlet:renderURL>"
			method="post">
			<table class="myportico-sortable" width="100%" id="sortable1" border="1" rules="rows" cellpadding="2px" cellspacing="2px">
	 			<tr class="myportico-sortable-trhead">
	  				<td width="40%">Topic</td>
	  				<td width="25%">Sender</td>
	  				<td width="25%">Date</td>
	  				<td width="10%">&nbsp;</td>
	 			</tr>
	
				<tr>
					<td colspan="4" align="center">
					   You have no notifications! 
				  	</td>
				</tr>
			</table>	
			<!-- <input class="uportal-button" type="submit"	value="Refresh">  -->
		</form>

	</c:when>
	<c:otherwise>
	
		<c:if test="${not empty userNotificationSize}">
			<form action="<portlet:actionURL></portlet:actionURL>" method="post">
				<table class="myportico-sortable" width="100%" id="sortable2" border="1" rules="rows" cellpadding="2px" cellspacing="2px">
		 			<tr class="myportico-sortable-trhead align-left">
		  				<td width="40%">Topic</td>
		  				<td width="25%">Sender</td>
		  				<td width="25%">Date</td>
		  				<td width="10%">&nbsp;</td>
		 			</tr>
		 		<c:forEach var='item' items='${user_notificationsMap}'>
				<c:set var='messageId' value='${item["message_id"]}' />
				<c:set var='releaseDate' value='${item["release"]}' />
				<c:choose>
					<c:when test='${not empty item["mark_read"]}'>
					<tr>
						<td><a href="<portlet:actionURL>
								<portlet:param name="COMMAND" value="DISPLAY" />
								<portlet:param name="message_subject" value='${item["message_subject"]}' />
								<portlet:param name="message_from" value='${item["message_from"]}' />
								<portlet:param name="DISPLAY_MSG_ID" value='${item["message_id"]}' />
								<portlet:param name="expiry" value='${item["mark_expiry"]}' />
								<portlet:param name="read" value='${item["mark_read"]}' />
								<portlet:param name="release" value='${item["release"]}' />
							</portlet:actionURL>">${item["message_subject"]}</a>
						</td>
					  	<td>${item["message_from"]}</td>
					  	<td><fmt:formatDate value="${releaseDate}" pattern="MMM dd, yyyy hh:mm"/></td>
					  	<td><a href="<portlet:actionURL>
								<portlet:param name="COMMAND" value="Purge Deleted" />
								<portlet:param name="delete_msg_id" value='${item["message_id"]}' />
								</portlet:actionURL>"><img border="0" src="../images/icons/channel_delete_base.gif" alt="Delete this message"></a></td>
					</tr>	
					</c:when>
					<c:otherwise>
					<tr>
						<td><strong><a href="<portlet:actionURL>
								<portlet:param name="COMMAND" value="DISPLAY" />
								<portlet:param name="message_subject" value='${item["message_subject"]}' />
								<portlet:param name="message_from" value='${item["message_from"]}' />
								<portlet:param name="DISPLAY_MSG_ID" value='${item["message_id"]}' />
								<portlet:param name="expiry" value='${item["mark_expiry"]}' />
								<portlet:param name="read" value='${item["mark_read"]}' />
								<portlet:param name="release" value='${item["release"]}' />
							</portlet:actionURL>">${item["message_subject"]}</a></strong>
						</td>
					  	<td><strong>${item["message_from"]}</strong></td>
					  	<td><strong><fmt:formatDate value="${releaseDate}" pattern="MMM dd, yyyy hh:mm"/></strong></td>
					  	<td>&nbsp;</td>
					</tr>	
					
					</c:otherwise>		
					</c:choose>
				</c:forEach>
				</table>
			</form>
		</c:if>
	</c:otherwise>		
</c:choose>
