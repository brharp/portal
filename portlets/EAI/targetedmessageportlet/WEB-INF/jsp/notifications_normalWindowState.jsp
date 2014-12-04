<%@ page import="javax.portlet.*"%>
<%@ page import="java.util.*"%>

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
        return ((Comparable)o2).compareTo(o1);
    }
}
%>

<%PortletSession psession = renderRequest.getPortletSession();
	List user_notificationsMap = (List) psession.getAttribute("user_notificationsMap");
	int size = 0;
	size = user_notificationsMap.size();
	String userNotificationSize = null;
	List moreUserNotification = new ArrayList();
	String more = null;

	if (size != 0) {
		userNotificationSize  = String.valueOf(size);
		Collections.sort(user_notificationsMap, new ColumnSorter());
		more = (String) psession.getAttribute("MORE");
		pageContext.setAttribute("more", more);
		System.out.println("******more*******" + more);
		if (more == null) {
			if (size <= 5) {
				moreUserNotification = user_notificationsMap;
			} else {
				for (int i=0; i<5; i++) {
					moreUserNotification.add(user_notificationsMap.get(i)); 
				}						
			}											
		} else {
			moreUserNotification = user_notificationsMap;
		}
		pageContext.setAttribute ("moreUserNotification", moreUserNotification);
	}
	System.out.println("jsp user size :" + userNotificationSize);
	pageContext
			.setAttribute("user_notificationsMap", user_notificationsMap);
	pageContext
			.setAttribute("userNotificationSize", userNotificationSize);
	
	List release_notificationsMap = (List) psession
				.getAttribute("release_notificationsMap");
	size = release_notificationsMap.size();
	String ownerNotificationSize = null;
	if (size != 0) {
		ownerNotificationSize  = String.valueOf(size);
		Collections.sort(release_notificationsMap, new ColumnSorter());
	}
				
	System.out.println("jsp owner size :" + ownerNotificationSize);
	pageContext.setAttribute("ownerNotificationSize", ownerNotificationSize);
	size = release_notificationsMap.size();
	for (int i=0; i<size; i++) {
		Map messageMap = (Map)release_notificationsMap.get(i);	
		Iterator it = messageMap.entrySet().iterator();
        System.out.println("messageMap: ReleaseMessage.");
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            //logger.debug("messageMap: ReleaseMessage.");
            //logger.debug(pairs.getKey() + " = " + pairs.getValue()); 
            System.out.println(pairs.getKey() + " = " + pairs.getValue());
        }
	}

	pageContext.setAttribute("release_notificationsMap", release_notificationsMap);
%>

<c:choose>
	<c:when test="${empty userNotificationSize}">
		<br />
            <b>No notifications.</b>
		<br />
		<br />
		<form
			action="<portlet:renderURL>
			<portlet:param name="COMMAND" value="REFRESH"/></portlet:renderURL>"
			method="post"><input class="uportal-button" type="submit"
			value="Refresh">
		</form>

	</c:when>
	<c:otherwise>
	<br />
		
	<c:set var='isMarkRead' value='true' />
	<c:forEach var='item' items='${moreUserNotification}'>
		<c:if test='${empty item["mark_read"]}'>
			<c:set var='isMarkRead' value='${item["mark_read"]}' />
		</c:if>
	</c:forEach> 		

	
	<c:choose>
	<c:when  test='${empty isMarkRead}'>	
	<form action="<portlet:actionURL></portlet:actionURL>" method="post">		
			<c:forEach var='item' items='${moreUserNotification}'>							
				<c:if test='${empty item["mark_read"]}'>
					
					<c:set var='messageId'		value='${item["message_id"]}' />
					<c:set var='messageSubject' value='${item["message_subject"]}' />
						<li>
							<a href="<portlet:actionURL>
							<portlet:param name="uP_root=" value="me" />
							<portlet:param name="COMMAND" value="DISPLAY" />
							<portlet:param name="SUBJECT" value="${messageSubject}" />
							<portlet:param name="DISPLAY_MSG_ID" value="${messageId}" />
							<portlet:param name="WINDOW" value="normal" />
								</portlet:renderURL>"><b>${messageSubject}</b>
							</a>
						</li>
				</c:if>
			</c:forEach> 
			<br />
			<c:if test="${empty more && userNotificationSize > 5 }">
				<img src="/portal/html/myportico/ccs/campusnews/images/expand.gif"</img><a href="<portlet:renderURL>
					<portlet:param name="MORE" value="more" />
					<portlet:param name="WINDOW" value="normal" />
					</portlet:renderURL>">&nbsp;&nbsp;more...
					</a>
					<br />
					<br />
			</c:if>
	<input name="WINDOW" type="hidden" value="NORMAL" />
	<input class="uportal-button" name="COMMAND" type="submit" value="All" />
		
		
		</form>
	</c:when>
	<c:otherwise>

		<br />
            No <b><i>new</i></b> notifications.
		<br />
		<br />
		<form action="<portlet:renderURL>
			<portlet:param name="COMMAND" value="ALL"/>
			<portlet:param name="WINDOW" value="normal" /></portlet:renderURL>"
			method="post">
			<input  type="submit" class="uportal-button" value="All">
		</form>

	</c:otherwise>
	</c:choose>

	</c:otherwise>
</c:choose>
<br />

