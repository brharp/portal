<%@ page contentType="text/html" isELIgnored="false" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<%@ taglib prefix="html" tagdir="/WEB-INF/tags/html" %>

<h1>${resource.key == null ? 'New Resource' : 'Edit Resource'}</h1>

<html:errors path="resource" fields="true"/>

<portlet:actionURL var="editIsbnResource">
  <portlet:param name="action" value="editIsbnResource"/>
  <portlet:param name="resource" value="${resource.key}"/>
  <portlet:param name="course" value="${param.course}"/>
</portlet:actionURL>

<form method="post" action="${editIsbnResource}">
  <table border="0" cellpadding="4">
    <tr>
      <th>Description:</th>
      <td><html:input path="resource.description" size="30" maxlength="256"/></td>
    <tr>
      <th>URL</th>
      <td><html:input path="resource.isbn" size="30" maxlength="1024"/></td>
    </tr>
    <tr>
      <th>Publish?</th>
      <td><html:checkbox path="resource.published"/></td>
    <tr>
      <th colspan="2">
        <button type="submit">Save</button>
      </th>
    </tr>
  </table>
</form>
