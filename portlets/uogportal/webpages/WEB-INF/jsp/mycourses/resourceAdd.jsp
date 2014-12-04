<%@ page contentType="text/html" isELIgnored="false" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<%@ taglib prefix="html" tagdir="/WEB-INF/tags/html" %>

<h1>New Resource</h1>

<portlet:renderURL var="addUrlResource">
  <portlet:param name="action" value="editUrlResource"/>
  <portlet:param name="course" value="${param.course}"/>
</portlet:renderURL>

<portlet:renderURL var="addIsbnResource">
  <portlet:param name="action" value="editIsbnResource"/>
  <portlet:param name="course" value="${param.course}"/>
</portlet:renderURL>

<ul>
  <li>Add an <a href="${addUrlResource}">URL</a> resource.</li>
  <li>Add a book by <a href="${addIsbnResource}">ISBN</a></li>
</ul>

