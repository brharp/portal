<%@ page contentType="text/html" isELIgnored="false" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<%@ taglib prefix="html" tagdir="/WEB-INF/tags/html" %>

<h1>New Delegate</h1>

<html:errors path="delegate" fields="true"/>

<form method="post" action="<portlet:actionURL>
                              <portlet:param name="action" value="addDelegate"/>
                              <portlet:param name="course" value="${param.course}"/>
                            </portlet:actionURL>">

  <table class="myportico-sortable" border="0" cellpadding="4">
    <tr>
      <th>User ID:</th>
      <td><html:input path="delegate.userId" size="30" maxlength="256"/></td>
    </tr>
    <tr>
      <th colspan="2">
        <button type="submit">Save</button>
        <portlet:renderURL var="cancelURL">
          <portlet:param name="action" value="editDelegates"/>
          <portlet:param name="course" value="${param.course}"/>
        </portlet:renderURL>
        <a href="${cancelURL}">Cancel</a>
      </th>
    </tr>
  </table>

</form>
