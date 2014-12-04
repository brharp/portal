<%@ page import="javax.portlet.*"%>
<%@ page import="java.util.*"%>
<%@ page import="java.text.*"%>
<%@ page import="java.sql.*"%>
<%@ page import="java.lang.String"%>

<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="x" uri="http://java.sun.com/jstl/xml"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt"%>


<%@ page session="true"%>

<portlet:defineObjects />

<%PortletSession psession = renderRequest.getPortletSession();
		String payload = (String) psession.getAttribute("message_text");
		String message_text = payload;
		pageContext.setAttribute("message_text", message_text);

		String message_subject = (String)psession.getAttribute("message_subject"); 
		String message_from = (String)psession.getAttribute("message_from");
		
		String release = (String)psession.getAttribute("release");
		if (release != null && release != "" && release.length() != 0) {
			release = release.substring(0,16);
		} else {
			release = null;
			psession.setAttribute("mark_release","MARK_RELEASE");
		}
		
		String expiry = (String)psession.getAttribute("expiry");
		if (expiry != null && expiry != "" && expiry.length() != 0) {
			expiry = expiry.substring(0,16);
		} else {
			expiry = null;
			psession.setAttribute("mark_release","MARK_RELEASE");
		}
		String read = (String)psession.getAttribute("read");
		String display_msg_id = (String)psession.getAttribute("display_msg_id");
		//System.out.println("display_msg_id displayjsp :" + display_msg_id);
		
		System.out.println("release:" + release);
		System.out.println("read:" + read);
		System.out.println("expiry:" + expiry);
		
		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd hh:mm");
		Format formatter = new SimpleDateFormat("MMM dd, yyyy hh:mm");//hh:mm MMM dd, yyyy");
		
		if (release != null) {
			java.util.Date releaseDate = (java.util.Date)dateFormatter.parse(release);
			release = formatter.format(releaseDate);
		}

		if (expiry != null) {	
			java.util.Date expiryDate = (java.util.Date)dateFormatter.parse(expiry);
			expiry = formatter.format(expiryDate);
		}
		//java.sql.Timestamp release = new java.sql.Timestamp(date.getTime());
		
		
		
				
				
		pageContext.setAttribute("message_subject", message_subject);
		pageContext.setAttribute("message_from", message_from);
		pageContext.setAttribute("release", release);
		pageContext.setAttribute("expiry", expiry);
		pageContext.setAttribute("read", read);
		pageContext.setAttribute("display_msg_id", display_msg_id);
		
		%>
	<br />
<form action="<portlet:actionURL>
				<portlet:param name="DISPLAY_MSG_ID" value="${display_msg_id}" />
				<portlet:param name="delete_msg_id" value="${display_msg_id}" />
			</portlet:actionURL>"	
	method="post">

<!-- 
<c:choose>
	<c:when test="${empty read}">
		<c:set var='submit' value='' />
		<c:set var='cancel' value='' />
	</c:when>
	<c:otherwise>
		<c:set var='submit' value='' />
		<c:set var='cancel' value='DISABLED' />
	</c:otherwise>		
</c:choose>
 -->

<c:choose>
	<c:when test="${empty read}">
	 <input class="uportal-button" type="submit" name="MARK_READ" value="I've Read This">&nbsp;&nbsp;
	 <input class="uportal-button" type="submit" name="CANCEL" value="I'll Read This Later"><br/>
	</c:when>
	<c:otherwise>
	 <input class="uportal-button" type="submit" name="COMMAND" value="Delete this">&nbsp;&nbsp;
	 <input class="uportal-button" type="submit" name="CANCEL" value="Keep this"><br/>
	</c:otherwise>		
</c:choose>
 
 <br />
 <p>
  <strong>${message_subject}</strong> - ${message_from} - ${release}
 </p>
 <hr/>

 <p>
 		<%=message_text%>
 </p>
 <hr/>
 <p>
  Expires: ${expiry} - 
  							<a href="<portlet:actionURL>
										<portlet:param name="COMMAND" value="EMAIL" />
										<portlet:param name="message_subject" value="${message_subject}" />
										<portlet:param name="message_from" value="${message_from}" />
										<portlet:param name="expiry" value="${expiry}" />
										<portlet:param name="release" value="${release}" />
									</portlet:actionURL>">Email this to me</a>
  
 </p>
<c:choose>
	<c:when test="${empty read}">
	 <input class="uportal-button" type="submit" name="MARK_READ" value="I've Read This">&nbsp;&nbsp;
	 <input class="uportal-button" type="submit" name="CANCEL" value="I'll Read This Later"><br/>
	</c:when>
	<c:otherwise>
	 <input class="uportal-button" type="submit" name="COMMAND" value="Delete this">&nbsp;&nbsp;
	 <input class="uportal-button" type="submit" name="CANCEL" value="Keep this"><br/>
	</c:otherwise>		
</c:choose>
</form>

