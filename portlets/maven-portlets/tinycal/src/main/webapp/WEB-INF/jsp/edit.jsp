<%@ page language="java" contentType="text/html" %>

<%@taglib prefix="c" uri="http://java.sun.com/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jstl/functions"%>

<form action="${form.submit}">
  <c:forEach var="entry" items="${model}">
    <input type="CHECKBOX" <c:out value="${entry.checked}"/>/>
    <c:out value="${entry.feed}"/><br/>
  </c:forEach>
  <input type="submit"/>
</form>


