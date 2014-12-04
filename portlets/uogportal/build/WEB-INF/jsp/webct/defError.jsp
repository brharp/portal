<%@ page contentType="text/html" isELIgnored="false" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<portlet:defineObjects/>

<c:url value="/WebCTSSO" var="home">
  <c:param name="url" value="/webct/viewMyWebCT.dowebct"/>
</c:url>

<!--a href="${home}" target="webct">
  <img align="right" 
       src="${pageContext.request.contextPath}/images/webct.gif"
       alt="WebCT"/>
</a-->

Sorry, we couldn't find a WebCT account for you. If you have an account,
please <a href="${home}" target="webct">log in</a> directly to WebCT.
