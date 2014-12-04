<%--
	- select
	- 
	- Display an select field and bind it to the attribute
	- of a command or bean. If name and/or value attributes are specified,
	- they will be used instead of status.expression and/or status.value
	- respectively. Accepts dynamic attributes.
	-
	- @param path the name of the field to bind to (required)
	- @param name use this attribute to override the input name
	- @param value use this attribute to override the input value
	--%>
<%@ tag dynamic-attributes="attributes" isELIgnored="false" %>
<%@ include file="include.jsp" %>
<%@ attribute name="path" required="true" %>
<spring:bind path="${path}">
	<html:attributes var="attrString" attributeMap="${attributes}" name="${status.expression}">
		<select ${attrString}>
		  <jsp:doBody/>
		</select>
	</html:attributes>
	<span style="color:#A00000">${status.errorMessage}</span>
</spring:bind>