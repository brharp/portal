<%--
  - checkbox
  - 
  - Display an checkbox field and binds it to the attribute
  - of a command or bean. 
  -
  - @param path the name of the field to bind to (required)
  --%>
<%@ tag dynamic-attributes="attributes" isELIgnored="false" body-content="empty" %>
<%@ include file="include.jsp" %>
<%@ attribute name="path" required="true" %>
<spring:bind path="${path}">
  <input type="hidden" name="_<c:out value="${status.expression}"/>"/>
  <input type="checkbox" name="<c:out value="${status.expression}"/>"
         value="true" <c:if test="${status.value}">checked="checked"</c:if>/>
  <span style="color:#A00000">${status.errorMessage}</span>
</spring:bind>
