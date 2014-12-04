<%@ page contentType="text/html" isELIgnored="false" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="html" tagdir="/WEB-INF/tags/html" %>

<table>
  <tbody>
    <tr><th>Key</th><th>Value</th></tr>
    <c:forEach items="${userInfo}" var="entry">
      <tr><td>${entry.key}</td><td>${entry.value}</td></tr>
    </c:forEach>
  </tbody>
</table>
