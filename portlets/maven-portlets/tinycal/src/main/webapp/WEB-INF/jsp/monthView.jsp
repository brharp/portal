<%@ page language="java" contentType="text/html;charset=US-ASCII" pageEncoding="US-ASCII"%>

<%@ page import="java.util.*" %>

<%
//HttpSession session = request.getSession();
//List events = session.getAttribute(TinyCalPortlet.EVENTS_ATTRIBUTE);
//for(Iterator i = events.iterator(); i.hasNext();) {
//  SyndEntry event = (SyndEntry)i.next();
%>

<table>

  <% Calendar c = Calendar.getInstance(); %>
  <% c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY); %>
  <% int currentMonth = -1; %>

  <% for(int w = 0; w < 5; w++) { %>

    <tr>

      <% for(int d = 0; d < 7; d++) { %>

        <td>

          <% if (c.get(Calendar.MONTH) != currentMonth) { %>
            <% currentMonth = c.get(Calendar.MONTH); %>
            <%= c.get(Calendar.MONTH) + "/" %>
          <% } %>
          <%= c.get(Calendar.DAY_OF_MONTH) %>
          <% c.roll(Calendar.DAY_OF_MONTH, 1); %>


            <% // Output the month name if this is the first date displayed, or %>
            <% // if it's the first day of a month. %>

            <c:choose>
              <c:when test="${week.index == 1 && day.index == 1 || cal.date == 1}">
                <fmt:formatDate value="${cal.time}" pattern="MMM d"/>
              </c:when>
              <c:otherwise>
                <fmt:formatDate value="${cal.time}" pattern="d"/>
              </c:otherwise>
            </c:choose>

        </td>

      <% } %>

    </tr>

  <% } %>

</table>
