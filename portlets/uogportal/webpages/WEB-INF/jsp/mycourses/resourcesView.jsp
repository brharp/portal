<%@ page contentType="text/html" isELIgnored="false" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="html" tagdir="/WEB-INF/tags/html" %>

<p>
${param.course} Resources
</p>

<c:choose>

  <c:when test="${fn:length(resources) eq 0}">

    <blockquote>
    No resources have been added for this course.
    </blockquote>

  </c:when>

  <c:otherwise>

    <table class="myportico-sortable">
      <tbody>
        <tr><th>Name</th></tr>
        <c:forEach items="${resources}" var="resource">
          <tr>
            <td><a href="${resource.url}">${resource.description}</a></td>
          </tr>
        </c:forEach>
      </tbody>
    </table>

  </c:otherwise>

</c:choose>  


<portlet:renderURL var="backURL">
  <portlet:param name="course" value="${param.course}"/>
</portlet:renderURL>

<p>
  <a class="uportal-button" href="${backURL}">Back</a>
</p>
