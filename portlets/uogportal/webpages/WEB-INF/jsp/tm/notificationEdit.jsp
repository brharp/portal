<%@ page contentType="text/html" isELIgnored="false" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<%@ taglib prefix="html" tagdir="/WEB-INF/tags/html" %>

<html:errors path="notification" fields="true"/>

<portlet:actionURL var="editNotification">
  <portlet:param name="action" value="editNotification"/>
  <portlet:param name="notification" value="${notification.messageId}"/>
</portlet:actionURL>

<html:initRTE submitFunction="submitForm"/>

<form method="post" name="notificationForm" action="${editNotification}" onsubmit="return submitForm()">

  <table border="0" cellpadding="4">

    <c:if test="${empty param.nouid}">
      <tr>
        <th><spring:message code="role.uid"/>:</th>
        <td>
          <spring:bind path="notification.userId">
      		<input name="${status.expression}" value="${status.value}" 
      		  <c:if test="${param.lockuserid}">disabled="disabled"</c:if>/>     		  
          </spring:bind>
        </td>
      </tr>
    </c:if>
    
    <c:if test="${empty param.nocourses}">
      <tr>
        <th><spring:message code="role.uogcourses"/>:</th>
        <td>
          <spring:bind path="notification.courses">
            <input name="${status.expression}" value="${status.value}"
              <c:if test="${param.lockcourses}">disabled="disabled"</c:if>/>
          </spring:bind>
        </td>
      </tr>
    </c:if>
    
    <c:if test="${empty param.noorganizationalstatus}">
      <tr>
        <th><spring:message code="role.organizationalstatus"/>:</th>
        <td>
          <html:select path="notification.organizationalStatus">
            <html:option path="notification.organizationalStatus" value=""></html:option>
            <html:option path="notification.organizationalStatus" value="Student">Student</html:option>
            <html:option path="notification.organizationalStatus" value="Employee:Staff">Staff</html:option>
            <html:option path="notification.organizationalStatus" value="Employee:Faculty">Faculty</html:option>
          </html:select>
        </td>
      </tr>
    </c:if>

    <tr>
      <th>Subject:</th>
      <td><html:input path="notification.messageSubject" size="60" maxlength="256"/></td>
    </tr>

    <tr>
      <td colspan="2">
        <html:rte path="notification.message"/>
      </td>
    </tr>

  </table>


  <portlet:renderURL var="cancelURL">
  </portlet:renderURL>

  <button type="submit">Send</button>
  <a href="${cancelURL}">Cancel</a>

</form>

