<%@ page contentType="text/html" isELIgnored="false" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="html" tagdir="/WEB-INF/tags/html" %>

<p>
${param.course} Delegates
</p>

<% // This page displays a list of course delegates, and includes
   // a form for deleting selected delegates. %>

<portlet:actionURL var="deleteDelegate">
  <portlet:param name="action" value="deleteDelegate"/>
  <portlet:param name="course" value="${param.course}"/>
</portlet:actionURL>

<portlet:renderURL var="newDelegate">
  <portlet:param name="action" value="addDelegate"/>
  <portlet:param name="course" value="${param.course}"/>
</portlet:renderURL>

<form action="${deleteDelegate}" method="post">

  <% // A table of delegate links. Each link is accompanied by a 
     // checkbox for marking the delegate for deletion. %>

  <c:choose>

    <c:when test="${fn:length(delegates) eq 0}">

        <blockquote>
        No delegates have been added for this course.
        To add a delegate, click 'New Delegate'.
        </blockquote>

    </c:when>

    <c:otherwise>

  <table class="myportico-sortable">

    <tbody>

      <tr>
        <th></th>
        <th>Delegate</th>
      </tr>

      <c:forEach items="${delegates}" var="delegate">

        <tr>
          <td>
            <input type="checkbox" name="marked" value="${delegate.userId}"/>
          </td>
          <td>
            ${delegate.userId}
          </td>
        </tr>

      </c:forEach>

    </tbody>

  </table>

    </c:otherwise>

  </c:choose>

  <% // Command buttons and links. %>

  <p>
    <a href="${newDelegate}">New Delegate</a>

    <input type="submit" value="Delete Selected"/>
  </p>

</form>

<% // "Back" link to return to course view. %>

<portlet:renderURL var="backURL">
  <portlet:param name="course" value="${param.course}"/>
</portlet:renderURL>

<p>
  <a class="uogportal.button" href="${backURL}">Back</a>
</p>
