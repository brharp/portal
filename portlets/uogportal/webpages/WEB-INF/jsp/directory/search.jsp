
<%@ page contentType="text/html" isELIgnored="false" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>

<portlet:defineObjects/>

<form action="<portlet:actionURL/>" method="POST">
  <b>First Name</b>
  <input type="checkbox" name="firstNameBeginsWith" value="*" checked="checked"/>
  Begins with
  <input type="text" name="firstName"/>
  <br/>
  <b>Last name</b>
  <input type="checkbox" name="lastNameBeginsWith" value="*" />
  Begins with
  <input type="text" name="lastName"/>
  <br/>
  <input type="submit"/>
  <a href="<portlet:renderURL/>">Clear</a>
</form>

<c:if test="${results != null}">
  <b>Found ${fn:length(results)} result(s):</b><br/>
  <table cellpadding="5px">
    <tr>
  <c:forEach items="${results}" var="entry" varStatus="status">
    <c:if test="${status.index mod 3 == 0}">
      </tr><tr>
    </c:if>
    <td valign="top">
    <b>${entry.cn}</b><br/>
    <c:if test="${not empty entry.ou}">
      ${entry.ou}<br/>
    </c:if>
    <c:if test="${not empty entry.roomnumber}">
      ${entry.roomnumber}<br/>
    </c:if>
    <c:if test="${not empty entry.telephonenumber}">
      ${entry.telephonenumber}<br/>
    </c:if>
    <c:if test="${not empty entry.mail}">
      <a href="mailto:${entry.mail}">${entry.mail}</a><br/>
    </c:if>
    </td>
  </c:forEach>
  </tr>
  </table>
</c:if>

