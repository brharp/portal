<%@ page contentType="text/html" isELIgnored="false" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="html" tagdir="/WEB-INF/tags/html" %>

<% /*

Displays information on the current user's exams. Exam
information is displayed in 3 blocks, corresponding to 3 different
roles.

The first block displays information on courses in the "instructs"
list. These are courses for which the user is the course
instructor. The course instructor has full access to modify course
resources, send targetted messages to the class, etc. The instructor
may also delegate these responsibilities to another portal user.

The second block displays courses from the "delegates" list, which are
courses where the instructor has delegated responsibility to the
current user. Delegates have access to all the same functions as a
course instructor, with the exception of modifying the list of
delegates.

The third and final block of courses are from the "courses"
list. These are courses where the current user is enrolled as a
student. The information displayed to students is read only, and they
are presented with a different list of functions than instructors or
delegates.

*/ %>

<c:choose>

  <c:when test="${fn:length(courses) eq 0}">

    <p>You do not have any courses.</p>

  </c:when>

  <c:otherwise>

   <table width="100%" class="myportico-sortable">

    <tr>
      <th align="center">Code</th>
      <th align="center">Date</th>
      <th align="center">Room</th>
    </tr>

    <c:forEach items="${courses}" var="course">
      <tr>
        <td align="center">${course.code}</td>
        <td align="center">
          <fmt:formatDate value="${course.exam.startDate}" dateStyle="short" type="date"/> 
          <fmt:formatDate value="${course.exam.startDate}" timeStyle="short" type="time"/> - 
          <fmt:formatDate value="${course.exam.endDate}" timeStyle="short" type="time"/>
        </td>
        <td align="center">
          ${course.exam.room} - 
          <a href="http://www.uoguelph.ca/regweb/building~${course.exam.building}"
             target="_blank">${course.exam.building}</a>
        </td>
        <td align="center">
          <% // Generate "Add to Calendar" href %>
          <portlet:renderURL var="addEventURL">
            <portlet:param name="action"      
              value="addEvent"/>
            <portlet:param name="title"       
              value="${course.code} Exam"/>
            <portlet:param name="description" 
              value="Final examination for ${course.code} ${course.title}"/>
            <portlet:param name="startTime"   
              value="${course.exam.startDate.time}"/>
            <portlet:param name="endTime"     
              value="${course.exam.endDate.time}"/>
            <portlet:param name="duration"    
              value="${course.exam.endDate.time - course.exam.startDate.time}"/>
            <portlet:param name="location"    
              value="${course.exam.room} - ${course.exam.building}"/>
          </portlet:renderURL>
          <a href="${addEventURL}">Add to Calendar</a>
        </td>
      </tr>
    </c:forEach>

   </table>

  </c:otherwise>

</c:choose>
