<%@ page contentType="text/html" isELIgnored="false" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<%@ taglib prefix="html" tagdir="/WEB-INF/tags/html" %>

<html>

<head>
  <script src="/portal/javascript/prototype.js"></script>

  <script>

  function cancel() {
    window.close();
  }

  function onAllDayChange(allDay) { 
    if ($F(allDay) == "true") {
      Element.hide($('startTime'));
      Element.hide($('endTime'));
    }
    else {
      Element.show($('startTime'));
      Element.show($('endTime'));
    }
  }

  function resize() {
    window.resizeTo(640,480);
  }

  </script>

</head>

<body onload="resize()">

<h4>Add Event</h4>

<html:errors path="command"/>

<form method="post" action="/portal/calendar/addEvent">
  <table border="0" cellpadding="4">
    <tr>
      <th align=right valign=top>Title:</th>
      <td><html:input path="command.title" size="50"/></td>
    </tr>
    <tr>
      <th align=right valign=top>Description:</th>
      <td><html:input path="command.description" size="50"/></td>
    </tr>
    <tr>
      <th align=right valign=top>When:</th>
      <td>
        <html:date month="command.startMonth" date="command.startDate"
                   year="command.startYear"/>
        <span id="startTime" <c:if test="${command.allDay}">style="display:none"</c:if>>
          <html:time hour="command.startHourOfDay" minute="command.startMinute" />
        </span>
        <spring:bind path="command.allDay">
          <input type="hidden" name="_${status.expression}" value="visible"/>
          <input type="checkbox" name="${status.expression}"
                   <c:if test="${status.value}">checked="checked"</c:if>
                   onchange="onAllDayChange(this)" value="true"
             />All Day Event
          <span style="color:#A00000">${status.errorMessage}</span>
        </spring:bind>
      </td>
    </tr>
    <tr id='endTime' <c:if test="${command.allDay}">style="display:none"</c:if>>
      <th align=right valign=top>To:</th>
      <td>
        <html:date month="command.endMonth" date="command.endDate"
                   year="command.endYear"/>
        <span>
          <html:time hour="command.endHourOfDay" minute="command.endMinute"/>
        </span>
      </td>
    </tr>
    <tr>
      <th align=right valign=top>Location:</th>
      <td><html:input path="command.location" size="50"/></td>
    </tr>    
  </table>
  <table>
    <tr>
      <th>
        <input type="submit" value="Save &amp; Close"></input>
        <button onclick="cancel();" value="Cancel">Cancel</button>
      </th>
    </tr>
  </table>
</form>

</body>
</html>
