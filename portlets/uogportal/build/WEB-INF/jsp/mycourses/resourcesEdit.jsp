<%@ page contentType="text/html" isELIgnored="false" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="html" tagdir="/WEB-INF/tags/html" %>

<p>
${param.course} Resources
</p>

<% // This page displays a list of course resources, and includes
   // a form for deleting selected resources. %>

<form action="<portlet:actionURL>
                <portlet:param name="action" value="deleteResource"/>
                <portlet:param name="course" value="${param.course}"/>
              </portlet:actionURL>"
      method="post">

  <% // A table of resource links. Each link is accompanied by a 
     // checkbox for marking the resource for deletion. %>

<c:choose>

  <c:when test="${fn:length(resources) eq 0}">

    <blockquote>
    No resources have been added for this course. To add
    a new resource, click 'New Resource'.
    </blockquote>

  </c:when>

  <c:otherwise>

  <table class="myportico-sortable">
    <tbody>
      <tr>
        <th></th>
        <th>Resource</th>
        <th>Published?</th>
        <th></th>
      </tr>
      <c:forEach items="${resources}" var="resource">
        <tr>
          <td>
            <input type="checkbox" name="marked" value="${resource.key}"/>
          </td>
          <td>
            <a href="${resource.url}">${resource.description}</a>
          </td>
          <td>
            ${resource.published ? "Yes" : "No"}
          </td>
          <td>
            <a href="<portlet:renderURL>
                       <portlet:param name="action" value="editUrlResource"/>
                       <portlet:param name="resource" value="${resource.key}"/>
                       <portlet:param name="course" value="${param.course}"/>
                     </portlet:renderURL>">
              Edit
            </a>
          </td>
        </tr>
      </c:forEach>
    </tbody>
  </table>

  </c:otherwise>

</c:choose>

  <% // Command buttons and links. %>

  <p>
    <a href="<portlet:renderURL>
               <portlet:param name="action" value="addResource"/>
               <portlet:param name="course" value="${param.course}"/>
             </portlet:renderURL>">
      New Resource
    </a>

    <input type="submit" value="Delete Selected"/>
  </p>

</form>

<% // "Back" link to return to course view. %>

<portlet:renderURL var="backURL">
  <portlet:param name="course" value="${param.course}"/>
</portlet:renderURL>

<p>
  <a class="uportal-button" href="${backURL}">Back</a>
</p>
