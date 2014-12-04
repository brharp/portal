<%@ page language="java" contentType="text/html" %>

<%@taglib prefix="c" uri="http://java.sun.com/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jstl/functions"%>

<% // Save the current date for later. %>

<c:set var="year"  value="${cal.year}"/>
<c:set var="month" value="${cal.month}"/>
<c:set var="date"  value="${cal.date}"/>

<html>

<body>

<table border="1">

  <% // Reset calendar and draw weekday names. %>

  <c:set target="${cal}" property="dayOfWeek" value="1"/>

  <tr>

    <c:forEach begin="1" end="7">

      <td>

        <fmt:formatDate value="${cal.time}" pattern="E"/>
        <c:set target="${cal}" property="date" value="${cal.date+1}"/>

      </td>

    </c:forEach>

  </tr>


  <% // Reset calendar and draw calendar. %>

  <c:set target="${cal}" property="year" value="${year}"/>
  <c:set target="${cal}" property="month" value="${month}"/>
  <c:set target="${cal}" property="date" value="${date}"/>
  <c:set target="${cal}" property="dayOfWeek" value="1"/>

  <% // Record current month to detect month crossover. %>

  <c:set var="m" value="${cal.month}"/>

  <c:forEach begin="1" end="5" varStatus="week">

    <tr>

      <c:forEach begin="1" end="7" varStatus="day">

        <td valign="top">

          <span class="portlet-font">

            <c:if test="${week.index == 1 && day.index == 1 || cal.month != m}">

              <fmt:formatDate value="${cal.time}" pattern="MMM"/>
              <c:set var="m" value="${cal.month}"/>

            </c:if>

            <c:out value="${cal.date}"/><br/>

            <c:set target="${ev}" property="time" value="${cal.time}"/>

            <c:forEach var="event" items="${ev.events}">

              <a href="<c:out value="${event.link}"/>"><c:out value="${event.summary}"/></a>

              <br/>

              <fmt:timeZone value="GMT">
                <fmt:formatDate value="${event.startTime}" pattern="yyyyMMdd'T'HHmmss'Z'"
                                var="googleStartTime"/>
                <fmt:formatDate value="${event.endTime}" pattern="yyyyMMdd'T'HHmmss'Z'"
                                var="googleEndTime"/>
              </fmt:timeZone>

              <c:url var="googleLink" value="http://www.google.com/calendar/event">
                <c:param name="action" value="TEMPLATE"/>
                <c:param name="text" value="${event.summary}"/>
                <c:param name="dates" value="${googleStartTime}/${googleEndTime}"/>
                <c:param name="details" value="${event.title}"/>
                <c:param name="location" value="${event.location}"/>
                <c:param name="trp" value="false"/>
                <c:param name="sprop" value="http://www.uoguelph.ca"/>
                <c:param name="sprop" value="name:UoG"/>
              </c:url>

              <a href="<c:out value="${googleLink}"/>" target="_blank">
                <img src="<c:out value='${pageContext.request.contextPath}/google.PNG'/>" 
                     border=0 alt="Add to Google Calendar"></a>

                <img src="<c:out value='${pageContext.request.contextPath}/myportico.PNG'/>" 
                     border=0 alt="Add to MyPortico Calendar"></a>

                <img src="<c:out value='${pageContext.request.contextPath}/ical.PNG'/>" 
                     border=0 alt="Export as iCal"></a>

              <br/><br/>

            </c:forEach>

          </span>

        </td>

        <c:set target="${cal}" property="date" value="${cal.date+1}"/>

      </c:forEach>

    </tr>

  </c:forEach>

</table>

</body>

</html>
