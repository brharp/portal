<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<c:forEach var="module" items="${entry.modules}">

  <c:choose>

    <c:when test="${module.uri == 'http://www.uoguelph.ca/ccs/elements/2006/'}">

      <%@ include file="module-ug.jsp" %>

    </c:when>

  </c:choose>

</c:forEach>
