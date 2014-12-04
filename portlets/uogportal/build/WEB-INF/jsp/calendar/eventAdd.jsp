<%@ page contentType="text/html" isELIgnored="false" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<%@ taglib prefix="html" tagdir="/WEB-INF/tags/html" %>

<h4>Add Event</h4>

<portlet:actionURL var="postURL">
  <portlet:param name="action" value="addEvent"/>
</portlet:actionURL>

<form method="post" action="${postURL}">
  <table border="0" cellpadding="4">
    <tr>
      <th>Title:</th>
      <td><html:input path="command.title"/></td>
    </tr>
    <tr>
      <th>Description:</th>
      <td><html:input path="command.description"/></td>
    </tr>
    <tr>
      <th>Start Time:</th>
      <td><html:date month="command.startMonth" date="command.startDate"
                     year="command.startYear" hour="command.startHour"
                     minute="command.startMinute" ampm="command.startAmPm"/>
      </td>
    </tr>
    <tr>
      <th>End Time:</th>
      <td><html:date month="command.endMonth" date="command.endDate"
                     year="command.endYear" hour="command.endHour"
                     minute="command.endMinute" ampm="command.endAmPm"/>
    </tr>
<!--
    <tr>
      <th>Duration:</th>
      <td>${command.duration / 1000 / 60} min.</td>
    </tr>
-->
    <tr>
      <th>Location:</th>
      <td><html:input path="command.location"/></td>
    </tr>    
    <tr>
      <th colspan="2">
        <button type="submit">Save</button>
        <a href="<portlet:renderURL/>">Cancel</a>
      </th>
    </tr>
  </table>
</form>

