<%@ page contentType="text/html" isELIgnored="false" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<portlet:defineObjects/>

<x:parse doc="${homeAreaXml}" var="doc"/>

<c:url value="/WebCTSSO" var="home">
  <c:param name="url" value="/webct/viewMyWebCT.dowebct"/>
  <c:param name="user" value="${user}"/>
  <c:param name="nonce" value="${nonce}"/>
</c:url>

<a href="${home}" target="webct">
  <!--img align="right" src="${pageContext.request.contextPath}/images/webct.gif"
       alt="WebCT" border="none"/-->
  myWebCT Home
</a>

<x:set var="homearea" select="$doc/homearea"/>

<% // Announcements %>

<x:forEach select="$homearea/campusAnnouncements">
  <h4><x:out select="title/text()"/></h4>
  <ul>
    <x:forEach select="announcement">
      <li>
        <c:set var="url">
          <x:out select="url/text()"/>
        </c:set>
        <c:url value="/WebCTSSO" var="sso">
          <c:param name="url" value="${url}"/>
          <c:param name="user" value="${user}"/>
          <c:param name="nonce" value="${nonce}"/>
        </c:url>
        <a href="${sso}" target="webct">
          <x:out select="date/text()"/>
          <x:out select="text/text()"/>
        </a>
      </li>
    </x:forEach>
  </ul>
</x:forEach>


<% // Course List %>

<x:forEach select="$homearea/enrollment_list">
  <h4><x:out select="title/text()"/></h4>
  <ul>
    <x:forEach select="enrollment/lctxt">
      <li>
        <c:set var="href1">
          <x:out select="href1/text()"/>
        </c:set>
        <c:url value="/WebCTSSO" var="sso">
          <c:param name="url" value="${href1}"/>
          <c:param name="user" value="${user}"/>
          <c:param name="nonce" value="${nonce}"/>
        </c:url>
        <a href="${sso}" target="webct">
          <x:out select="text1/text()"/>
        </a>
      </li>
    </x:forEach>
  </ul>
</x:forEach>

<c:choose>

<c:when test="${renderRequest.parameterMap.windowState[0] == 'maximized'}">

<% // External Courses %>

<x:forEach select="$homearea/externalCourses">
  <h4><x:out select="title/text()"/></h4>
  <ul>
    <x:forEach select="course">
      <li>
        <c:set var="href">
          <x:out select="url/text()"/>
        </c:set>
        <a href="${href}" target="webct">
          <x:out select="text/text()"/>
        </a>
      </li>
    </x:forEach>
  </ul>
</x:forEach>



<% // Campus Bookmarks %>

<x:forEach select="$homearea/campusbookmarks">
  <h4><x:out select="title/text()"/></h4>
  <ul>
    <x:forEach select="bookmark">
      <li>
        <c:set var="href">
          <x:out select="url/text()"/>
        </c:set>
        <a href="${href}" target="webct">
          <x:out select="text/text()"/>
        </a>
      </li>
    </x:forEach>
  </ul>
</x:forEach>


<% // Personal Bookmarks %>

<x:forEach select="$homearea/mybookmarks">
  <h4><x:out select="title/text()"/></h4>
  <ul>
    <x:forEach select="bookmark">
      <li>
        <c:set var="href">
          <x:out select="url/text()"/>
        </c:set>
        <a href="${href}" target="webct">
          <x:out select="text/text()"/>
        </a>
      </li>
    </x:forEach>
  </ul>
</x:forEach>



<% // Calendar Day %>

<x:forEach select="$homearea/calendar_day">
  <c:set var="href">
    <x:out select="editLink/text()"/>
  </c:set>
  <c:url value="/WebCTSSO" var="sso">
    <c:param name="url" value="${href}"/>
    <c:param name="user" value="${user}"/>
    <c:param name="nonce" value="${nonce}"/>
  </c:url>
  <a href="${sso}" target="webct">
    <h4><x:out select="title/text()"/></h4>
  </a>
  <ul>
    <x:forEach select="entry">
      <li>
        <x:out select="time/text()"/>
        <x:out select="summary/text()"/>
      </li>
    </x:forEach>
  </ul>
</x:forEach>


<% // Notepad (aka To Do List) %>

<x:forEach select="$homearea/notepad">
  <h4><x:out select="title/text()"/></h4>
  <ul>
    <x:forEach select="item">
      <li>
        <x:out select="priority/text()"/>
        <x:out select="shorttext/text()"/>
      </li>
    </x:forEach>
  </ul>
</x:forEach>

<portlet:renderURL var="less">
  <portlet:param name="windowState" value="normal"/>
</portlet:renderURL>

<a href="${less}">
  <img style="vertical-align:middle" border="none"
       src="${pageContext.request.contextPath}/images/collapse.gif">
  less
</a>

</c:when>

<c:otherwise>

<portlet:renderURL var="more">
  <portlet:param name="windowState" value="maximized"/>
</portlet:renderURL>

<a href="${more}">
  <img style="vertical-align:middle" border="none"
       src="${pageContext.request.contextPath}/images/expand.gif">
  more
</a>

</c:otherwise>

</c:choose>
