<%@ page contentType="text/html" isELIgnored="false" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="html" tagdir="/WEB-INF/tags/html" %>

<table>
  <tbody>
  </tbody>
</table>

<portlet:renderURL var="backURL">
  <portlet:param name="course" value="${param.course}"/>
</portlet:renderURL>

<a href="${backURL}">Back</a>
