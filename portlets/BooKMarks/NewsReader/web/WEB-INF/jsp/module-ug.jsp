<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<% // Events %>

<c:if test="${!empty module.when}">

  <c:choose>
    <c:when test="${module.when.value == 'DATE'}">
      <c:set var="googleDatePattern" value="yyyyMMdd"/>
      <c:set var="yahooDatePattern" value="yyyyMMdd"/>
    </c:when>
    <c:otherwise>
      <c:set var="googleDatePattern" value="yyyyMMdd'T'HHmmss'Z'"/>
      <c:set var="yahooDatePattern" value="yyyyMMdd'T'HHmmss"/>
    </c:otherwise>
  </c:choose>


  <% // Add to MyPortico Calendar %>

  <c:url var="myporticoLink" value="/../portal/calendar/addEvent">
    <c:param name="action" value="add"/>
    <c:param name="title" value="${module.summary}"/>
    <c:param name="startTime" value="${module.when.startTime.time}"/>
    <c:param name="endTime" value="${module.when.endTime.time}"/>
    <c:param name="details" value="${module.summary}"/>
    <c:param name="location" value="${module.where}"/>
  </c:url>

  <a href="<c:out value='${myporticoLink}'/>" target="_blank">
  <img src="<c:out value='${pageContext.request.contextPath}/img/myportico.PNG'/>" 
       border=0 alt="<fmt:message key='add_to_myportico_calendar'/>"
       title="<fmt:message key='add_to_myportico_calendar'/>"></a>



  <% // Add to Google Calendar. %>

  <fmt:timeZone value="GMT">
    <fmt:formatDate value="${module.when.startTime}" 
                    pattern="${googleDatePattern}"
                    var="googleStartTime"/>
    <fmt:formatDate value="${module.when.endTime}" 
                    pattern="${googleDatePattern}"
                    var="googleEndTime"/>
  </fmt:timeZone>

  <c:url var="googleLink" value="http://www.google.com/calendar/event">
    <c:param name="action" value="TEMPLATE"/>
    <c:param name="text" value="${module.summary}"/>
    <c:param name="dates" value="${googleStartTime}/${googleEndTime}"/>
    <c:param name="details" value="${module.summary}"/>
    <c:param name="location" value="${module.where}"/>
    <c:param name="trp" value="false"/>
    <c:param name="sprop" value="http://www.uoguelph.ca"/>
    <c:param name="sprop" value="name:UoG"/>
  </c:url>

  <a href="<c:out value='${googleLink}'/>" target="_blank">
    <img src="<c:out value='${pageContext.request.contextPath}/img/google.PNG'/>" 
         border=0 alt="<fmt:message key='add_to_google_calendar'/>"
         title="<fmt:message key='add_to_google_calendar'/>"></a>



  <% // Add to Yahoo Calendar %>

  <fmt:formatDate value="${module.when.startTime}" 
                  pattern="${yahooDatePattern}"
                  var="yahooStartTime"/>

  <c:set var="duration" value="${(module.when.endTime.time - module.when.startTime.time)}"/>
  <fmt:formatNumber var="dhours" pattern="00" value="${duration / 3600000}"/>
  <fmt:formatNumber var="dminutes" pattern="00" value="${duration % 3600000 / 60000}"/>

  <c:url var="yahooLink" value="http://calendar.yahoo.com/">
    <c:param name="V" value="60"/>
    <c:param name="ST" value="${yahooStartTime}"/>
    <c:param name="TITLE" value="${module.summary}"/>
    <c:if test="${module.when.value != 'DATE'}">
      <c:param name="DUR" value="${dhours}${dminutes}"/>
    </c:if>
    <c:param name="VIEW" value="d"/>
    <c:param name="in_loc" value="${module.where}"/>
  </c:url>

  <a href="<c:out value='${yahooLink}'/>" target="_blank">
  <img src="<c:out value='${pageContext.request.contextPath}/img/yahoo.PNG'/>" 
       border=0 alt="<fmt:message key='add_to_yahoo_calendar'/>"
       title="<fmt:message key='add_to_yahoo_calendar'/>"></a>

</c:if>
