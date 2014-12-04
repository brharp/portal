<%--
	- option
	- 
	- Display an option in a bound select field (see html:select). If the 
	- value of status.value equals the option value, the option is selected.
	- Accepts dynamic attributes.
	-
	--%>
<%@ tag dynamic-attributes="attributes" isELIgnored="false" %>
<%@ include file="include.jsp" %>
<%@ attribute name="path" %>
<spring:bind path="${path}">
  <html:attributes var="attrString" attributeMap="${attributes}">
        <option ${attrString} 
          <c:if test="${status.value==attributes.value}">
            selected="selected"
          </c:if>
         ><jsp:doBody/></option>
  </html:attributes>
</spring:bind>
