<%@ page import="javax.portlet.*"%>
<%@ page import="java.util.*"%>

<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="x" uri="http://java.sun.com/jstl/xml"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt"%>


<%@ page session="true"%>

<portlet:defineObjects />

<%PortletSession psession = renderRequest.getPortletSession();
		String payload = (String) psession.getAttribute("message_text");
		String subject = (String) psession.getAttribute("SUBJECT");

		String display_msg_id = (String) psession.getAttribute("DISPLAY_MSG_ID");
		String window = (String)renderRequest.getParameter("WINDOW");
		
		//System.out.println("******Window******* :" + window);
		//System.out.println("******display_msg_id******* :" + display_msg_id);
		
		if (window == null) {
			window = "full";
		}
		//System.out.println("####Window##### :" + window);
		String message_text = payload;
		/*
		String[] messageParts = payload.split(":txet-lmth:");
		int size = messageParts.length;
		//System.out.println("size :" + size);
		boolean startHtml = false;
		if (payload.startsWith(":lmth:")) {
			startHtml = true;
		}

		String message_text = null;
		if (size > 1) { //html part is there
			int dlmIndex = payload.indexOf(":txet-lmth:");
			if (startHtml) {
				//System.out.println("dlmIndex :" + dlmIndex);
				message_text = payload.substring(6, dlmIndex);
				//System.out.println("message_text=" + message_text);
			} else {
				message_text = payload.substring(dlmIndex + 11 + 6);
				//System.out.println("message_text=" + message_text);

			}
		} else {
			message_text = payload.substring(6);
			//System.out.println("message_text=" + message_text);

		}*/
		pageContext.setAttribute("message_text", message_text);
		pageContext.setAttribute("subject", subject);
		pageContext.setAttribute("window", window);
		pageContext.setAttribute("DISPLAY_MSG_ID", display_msg_id);
		%>
<table>
	<tr>
		<b><%=subject%></b>
		<%=message_text%>
	</tr>
</table>
	<br />
<form
	action="<portlet:actionURL><portlet:param name="delete_msg_id" value="<%=display_msg_id%>"/><portlet:param name="mark_msg_id" value="<%=display_msg_id%>"/><portlet:param name="WINDOW" value="<%=window%>"/></portlet:actionURL>"
	method="post">
	<input class="uportal-button" name="COMMAND" type="submit" value="Back">
	<input class="uportal-button" name="COMMAND" type="submit" value="Mark Read" />

	</br>
</form>
