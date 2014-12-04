<%@ page contentType="text/html" isELIgnored="false" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="html" tagdir="/WEB-INF/tags/html" %>


<c:choose>
  <c:when test="${fn:length(courses) gt 0}">
    <c:forEach items="${courses}" var="course">
      <ul>
        <li>${course.code}</li>
        <ul>
          <c:if test="${course.websiteUrl != null}">
            <li>Course <a href="${course.websiteUrl}">Website</a></li>
          </c:if>
          <c:if test="${course.resourcesUrl != null}">
            <li>${course.code} <a href="${course.resourcesUrl}">Resources</a></li>
          </c:if>
          <c:if test="${course.classListUrl != null}">
            <li>Class <a href="${course.classListUrl}">List</a></li>
          </c:if>
        </ul>
      </ul>
    </c:forEach>
  </c:when>
  <c:otherwise>
    You are not registered in any courses.
  </c:otherwise>
</c:choose>
