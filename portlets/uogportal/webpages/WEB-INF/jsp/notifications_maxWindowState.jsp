<%@ page import="javax.portlet.*"%>
<%@ page import="java.util.*"%>
<%@ page import="java.text.*"%>


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
				if (size != 0) {
					userNotificationSize  = String.valueOf(size);
					Collections.sort(user_notificationsMap, new ColumnSorter());
				}
		//System.out.println("jsp user size :" + userNotificationSize);
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
				
		//System.out.println("jsp owner size :" + ownerNotificationSize);
			pageContext.setAttribute("ownerNotificationSize", ownerNotificationSize);
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
		pageContext
				.setAttribute("release_notificationsMap", release_notificationsMap);

	DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
	String releaseDate = dateFormat.format(date);
        pageContext.setAttribute("releaseDate", releaseDate);
								
	%>

<c:choose>
	<c:when test="${empty ownerNotificationSize && empty userNotificationSize}">
		<br />
            <b>No notifications.</b>
	<br />
		<br />
		<form
			action="<portlet:renderURL><portlet:param name="COMMAND" value="REFRESH"/></portlet:renderURL>"
			method="post"><input class="uportal-button" type="submit"
			value="Refresh">
</form>

	</c:when>
	<c:otherwise>


		<c:if test="${not empty ownerNotificationSize}">
				<form action="<portlet:actionURL><portlet:param name="WINDOW" value="full" /></portlet:actionURL>" method="post">
			<div>
			<table class="myportico-sortable" id="tmTable1" cellpadding="0" cellspacing="0" border="0" width="98%">
					<tr class="myportico-sortable-trhead">
						<td width="20%" >Release</td>
						<td width="35%">Message</td>
						<td width="20%">From</td>
						<td width="5%">Importance</td>
						<td width="20%">Date</td>
					</tr>
					<c:forEach var='item' items='${release_notificationsMap}'>
						
						<c:set var='messageId' value='${item["message_id"]}' />
						<c:set var='messageSubject' value='${item["message_subject"]}' />
						<tr>
								<td class="myportico-sortable-formElement" ><input
									align="middle" type="checkbox" name="release_msg_id"
									value="${messageId}" /></td>

							<td class="myportico-sortable-tdnormal"><a href="<portlet:renderURL>
				<portlet:param name="COMMAND" value="DISPLAY" />
				<portlet:param name="DISPLAY_MSG_ID" value="${messageId}" />
				<portlet:param name="SUBJECT" value="${messageSubject}" />
				<portlet:param name="WINDOW" value="full" />
			</portlet:renderURL>">${messageSubject}
							</a></td>
							<td class="myportico-sortable-tdnormal">${item["message_from"]}
							</td>
							<td class="myportico-sortable-tdnormal">${item["importance"]}</td>
							<td class="myportico-sortable-tdlast">${releaseDate}</td>
						</tr>
											
					</c:forEach>
			</table>
			</div>

			<br />
			<br />

			<input class="uportal-button" name="COMMAND" type="submit"
				value="Release" />
			<input class="uportal-button" name="COMMAND" type="submit"
				value="Refresh" />
				
</form>
<br />


</c:if>


<br />

<c:if test="${not empty userNotificationSize}">
		<form action="<portlet:actionURL><portlet:param name="WINDOW" value="full" /></portlet:actionURL>" method="post">
<div>
<table class="myportico-sortable" id="tmTable2" cellpadding="0" cellspacing="0" border="0" width="98%">
		<tr class="myportico-sortable-trhead">
			<td width="13%" >Mark Read</td>
			<td width="7%" >Delete</td>
			<td width="35%">Message</td>
			<td width="20%">From</td>
			<td width="5%">Importance</td>
			<td width="20%" >Date</td>
		</tr>
		<c:forEach var='item' items='${user_notificationsMap}'>			
			<c:set var='messageId' value='${item["message_id"]}' />
			<c:set var='messageSubject' value='${item["message_subject"]}' />
			<c:set var='releaseDate' value='${item["release"]}' />
			<tr>

				<c:if test='${empty item["mark_read"]}'>
					<td class="myportico-sortable-formElement"><input
						align="middle" type="checkbox" name="mark_msg_id"
						value="${messageId}" /></td>
					<td class="myportico-sortable-formElement" ><input
						align="middle" type="checkbox" DISABLED name="delete_msg_id"
						value="${messageId}" /></td>

				</c:if>

				<c:if test='${!empty item["mark_read"]}'>
					<td class="myportico-sortable-formElement"><input
						align="middle" type="checkbox" DISABLED name="mark_msg_id"
						value="${messageId}" /></td>
					<td class="myportico-sortable-formElement"><input
						align="middle" type="checkbox" name="delete_msg_id"
						value="${messageId}" /></td>
				</c:if>
				<td class="myportico-sortable-tdnormal"><a href="<portlet:renderURL>
				<portlet:param name="COMMAND" value="DISPLAY" />
				<portlet:param name="uP_root" value="me" />
				<portlet:param name="DISPLAY_MSG_ID" value="${messageId}" />
				<portlet:param name="SUBJECT" value="${messageSubject}" />
				<portlet:param name="WINDOW" value="full" />
			</portlet:renderURL>">${messageSubject}
				</a></td>
				<td class="myportico-sortable-tdnormal">${item["message_from"]}
				</td>
				<td class="myportico-sortable-tdnormal">${item["importance"]}</td>
				<td class="myportico-sortable-tdlast"><fmt:formatDate value="${releaseDate}" pattern="dd/MM/yyyy" /></td>
			</tr>
		</c:forEach>
</table>
</div>

<br />
<br />

<input class="uportal-button" name="COMMAND" type="submit"
	value="Mark Read" />

<input class="uportal-button" name="COMMAND" type="submit"
	value="Purge Deleted" />

<input class="uportal-button" name="COMMAND" type="submit"
	value="Refresh" />

<input name="WINDOW" type="hidden" value="full" />
	
<input class="uportal-button" name="COMMAND" type="submit"
	value="<<Back" />
	
</form>

</c:if>

</c:otherwise>
</c:choose>

<br />

