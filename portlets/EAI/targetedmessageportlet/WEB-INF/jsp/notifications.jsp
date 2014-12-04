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
            return ((Comparable)o1).compareTo(o2);
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
		pageContext
				.setAttribute("release_notificationsMap", release_notificationsMap);
								
	%>

<c:choose>
	<c:when test="${empty ownerNotificationSize && empty userNotificationSize}">
		<br />
            No new notifications.
	<br />
		<br />
		<form
			action="<portlet:renderURL><portlet:param name="COMMAND" value="REFRESH"/></portlet:renderURL>"
			method="post"><input class="uportal-button" type="submit"
			value="Refresh"></form>

	</c:when>
	<c:otherwise>


		<c:if test="${not empty ownerNotificationSize}">
				<form action="<portlet:actionURL> windowState="maximized" </portlet:actionURL>" method="post">
			<div>
			<table class="myportico-sortable" id="tmTable1" cellpadding="0" cellspacing="0" border="0" width="98%">
					<tr>
						<th width="20%" >Release</th>
						<th width="35%">Message</th>
						<th width="20%">From</th>
						<th width="5%">Importance</th>
						<th width="20%">Expiry</th>
					</tr>
					<c:forEach var='item' items='${release_notificationsMap}'>
						
						<c:set var='messageId' value='${item["message_id"]}' />
						<tr>
								<td class="myportico-sortable-formElement" ><input
									align="middle" type="checkbox" name="release_msg_id"
									value="${messageId}" /></td>

							<td><a href="<portlet:renderURL>
				<portlet:param name="COMMAND" value="DISPLAY" />
				<portlet:param name="DISPLAY_MSG_ID" value="${messageId}" />
			</portlet:renderURL>">${item["message_subject"]}
							</a></td>
							<td>${item["message_from"]}
							</td>
							<td>${item["importance"]}</td>
							<td><fmt:formatDate value="${markExpiry}" pattern="dd/MM/yyyy" /></td>
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
		<form action="<portlet:actionURL></portlet:actionURL>" method="post">
<div>
<table class="myportico-sortable" id="tmTable2" cellpadding="0" cellspacing="0" border="0" width="98%">
		<tr>
			<th width="13%" >Mark Read</th>
			<th width="7%" >Delete</th>
			<th width="35%">Message</th>
			<th width="20%">From</th>
			<th width="5%">Importance</th>
			<th width="20%" >Expiry</th>
		</tr>
		<c:forEach var='item' items='${user_notificationsMap}'>			
			<c:set var='messageId' value='${item["message_id"]}' />

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
				<td><a href="<portlet:renderURL>
				<portlet:param name="COMMAND" value="DISPLAY" />
				<portlet:param name="DISPLAY_MSG_ID" value="${messageId}" />
			</portlet:renderURL>">${item["message_subject"]}
				</a></td>
				<td>${item["message_from"]}
				</td>
				<td>${item["importance"]}</td>
				<td>${item["mark_expiry"]}</td>
			</tr>
		</c:forEach>
</table>
</div>

<br />
<br />

<input class="uportal-button" name="COMMAND" type="submit"
	value="Mark Read" />

<input class="uportal-button" name="COMMAND" type="submit"
	value="Purge Delete" />

<input class="uportal-button" name="COMMAND" type="submit"
	value="Refresh" />
</form>

</c:if>

</c:otherwise>
</c:choose>

<br />

