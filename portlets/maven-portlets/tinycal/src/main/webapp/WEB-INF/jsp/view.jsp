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

<a href="<c:out value="${request.contextPath}?view=month"/>"/>
<fmt:formatDate value="${cal.time}" pattern="MMMM yyyy"/>
</a>

<table>

  <% // Reset calendar and draw weekday names. %>

  <c:set target="${cal}" property="date" value="1"/>
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

  <c:set target="${cal}" property="date" value="1"/>
  <c:set target="${cal}" property="dayOfWeek" value="1"/>

  <c:forEach begin="1" end="5" varStatus="week">

    <tr>

      <c:forEach begin="1" end="7" varStatus="day">

        <td>

          <c:choose>
            <c:when test="${cal.month != monthInView}">
              <span class="portlet-font-dim">
            </c:when>
            <c:when test="${cal.today}">
              <span class="portlet-font-em">
            </c:when>
            <c:otherwise>
              <span class="portlet-font">
            </c:otherwise>
          </c:choose>

            <c:out value="${cal.date}"/>

          </span>

        </td>

        <c:set target="${cal}" property="date" value="${cal.date+1}"/>

      </c:forEach>

    </tr>

  </c:forEach>

</table>

  <% // Reset calendar and print upcoming events. %>

  <c:set target="${cal}" property="year"  value="${year}"/>
  <c:set target="${cal}" property="month" value="${month}"/>
  <c:set target="${cal}" property="date"  value="${date}"/>

  <c:forEach begin="1" end="3" varStatus="eventLoop">

    <c:choose>

      <c:when test="${eventLoop.index == 1}">

        <h4>Today</h4>

      </c:when>

      <c:otherwise>

        <h4><fmt:formatDate value="${cal.time}" pattern="EEEE"/></h4>

      </c:otherwise>

    </c:choose>

    <c:set target="${ev}" property="time" value="${cal.time}"/>

    <c:choose>

      <c:when test="${empty ev.events}">

        <i>No events scheduled.</i>

      </c:when>

      <c:otherwise>

        <ul>

          <c:forEach var="event" items="${ev.events}">

            <li>

              <a href="<c:out value="${event.link}"/>">

                <c:out value="${event.summary}"/>

              </a>

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
                <img src="http://www.google.com/calendar/images/ext/gc_button1.gif" border=0>
              </a>

            </li>

          </c:forEach>

        </ul>

      </c:otherwise>

    </c:choose>

    <c:set target="${cal}" property="date" value="${cal.date+1}"/>

  </c:forEach>

</table>

</body>

</html>
