<%@ page contentType="text/html" isELIgnored="false" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="html" tagdir="/WEB-INF/tags/html" %>
<%@ taglib prefix="uog" uri="http://myportico.uoguelph.ca/jsp/jstl/uog" %>

<% /*

Displays information on the current user's courses. Course
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

<portlet:defineObjects/>

<c:set var="userId" value="${model.user.id}"/>

<c:choose>

  <c:when test="${fn:length(model.courses) eq 0}">

    We were not able to find any course information in your user
    profile.

  </c:when>

  <c:otherwise>

    <c:set var="course" value="${model.courses[0]}"/>

    <portlet:renderURL var="courseSelectURL"/>
    <form id="courseSelectForm" method="GET" action="${courseSelectURL}">
      <select name="course" onChange="courseSelectForm.submit()">
        <c:forEach items="${model.courses}" var="next" varStatus="status">
          <c:choose>
            <c:when test="${param.course == model.courses[status.index].code}">
              <c:set var="course" value="${next}"/>
              <option value="${next.code}" selected="selected">
                ${next.code} ${next.title}
              </option>
            </c:when>
            <c:otherwise>
              <option value="${next.code}">
                ${next.code} ${next.title}
              </option>
            </c:otherwise>
          </c:choose>
        </c:forEach>
      </select>
      <noscript>
        <input type="submit" value="show"/>
      </noscript>
    </form>
    
    <% // Course Description // %>
    <p>      
      <b id="courseTitle" class="tooltip">
        ${course.code} ${course.title} (${model.user.term})
      </b>
    </p>

    <script language="javascript">
    <!--
      new xTooltipGroup(['courseTitle'], 'tooltips', 'mouse', 10, 0,
                        ["${course.description}"]);
    -->
    </script>
    <noscript>
      <p>${course.description}</p>
    </noscript>

    <ul class="course-action-list">

      <% // Links for everyone. %>
      
      <portlet:renderURL var="viewResourcesLink">
        <portlet:param name="action" value="viewResources"/>
        <portlet:param name="course" value="${course.code}"/>
      </portlet:renderURL>
      
      <li class="course-action-list-item">
        View course <a href="${viewResourcesLink}">resources</a>
      </li>

      <c:url var="reserveListURL" value="http://www.lib.uoguelph.ca/portal/getReserves.cfm">
        <c:param name="courseNo" value="${course.prefix}*${course.number}"/>
        <c:param name="lastName" value="${course.instructorSurname}"/>
      </c:url>

      <li class="course-action-list-item">
        View <a href="${reserveListURL}" target="_blank">library reserves</a>
      </li>

      <c:url var="bookstoreURL" value="http://www.bookstore.uoguelph.ca/">
      </c:url>

      <li class="course-action-list-item">
        View <a href="${bookstoreURL}" target="_blank">bookstore resources</a>
      </li>

      <% // Delegate and instructor links. %>
      
      <c:if test="${uog:isInstructor(course,userId) || uog:isDelegate(course,userId)}">
        
        <portlet:renderURL var="editResourcesLink">
          <portlet:param name="action" value="editResources"/>
          <portlet:param name="course" value="${course.code}"/>
        </portlet:renderURL>
        
        <portlet:renderURL var="sendNotificationLink">
          <portlet:param name="action" value="sendNotification"/>
          <portlet:param name="courses" value="${course.code}"/>
          <portlet:param name="lockcourses" value="true"/>
          <portlet:param name="noorganizationalstatus" value="true"/>
        </portlet:renderURL>
        
        <li class="course-action-list-item">
          Edit course <a href="${editResourcesLink}">resources</a>
        </li>
        
        <li class="course-action-list-item">
          Send <a href="${sendNotificationLink}">message</a>
        </li>
        
        <% // Instructor only links. %>
        
        <c:if test="${uog:isInstructor(course,userId)}">
          
          <portlet:renderURL var="editDelegatesLink">
            <portlet:param name="action" value="editDelegates"/>
            <portlet:param name="course" value="${course.code}"/>
          </portlet:renderURL>
          
          <li class="course-action-list-item">
            Manage <a href="${editDelegatesLink}">delegates</a>
          </li>
          
        </c:if>
        
      </c:if>
      
    </ul>
    
  </c:otherwise>

</c:choose>

<c:if test="${not empty model.user.degree}">
  <p>
    <a href="http://www.uoguelph.ca/registrar/calendars/apps/degrees/index.cfm?degree=${model.user.degree}&term=${model.user.term}&academiclevel=${model.user.academicLevel}&mode=redirect" target="_blank">
      ${model.user.degree} Calendar
    </a>
  </p>
</c:if>

<span style="font-style: italic">
  To get up-to-date course information, add/drop courses, and more,
  please visit 
  <c:choose>
    <c:when test="${model.user.student}">
      <a href="https://webadvisor.uoguelph.ca/st/index.shtml" target="_blank">WebAdvisor</a>
    </c:when>
    <c:when test="${model.user.faculty}">
      <a href="https://webadvisor.uoguelph.ca/fc/index.shtml" target="_blank">WebAdvisor</a>
    </c:when>
    <c:otherwise>
      <a href="http://webadvisor.uoguelph.ca/" target="_blank">WebAdvisor</a>
    </c:otherwise>
  </c:choose>
  .
</span>
