<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<style>
/* see gotMailTable in MyDoc */
.button-submit-gotMail {
	font-family: Verdana; 
	font-size: 8pt; 
	font-weight : bold;
	color: rgb(250,250,250); 
	spacing-left: 0px 
	spacing-right: 0px;	
	background-color : rgb(117,149,229);
}
</style>

Please, specify your prefefences below and press the Submit button...<p/>

	<form action="<portlet:actionURL />">
		<table align="center" >
		<tr>
			<td >Show the message headers?</td>
			<td><input type="checkbox" name="messageInfoOn" title="Check here to enable the message listing" value="true"
				<c:if test='${!empty requestScope.messageInfoOn && requestScope.messageInfoOn=="true"}'>checked</c:if> /></td>
		</tr>
		<tr>
			<td>Maximum number of messages displayed:</td>
			<td>
            <input type="text" name="messageInfoMax" title="Can be between 1 and 100" size="4"
            	value="<c:if test='${!empty requestScope.messageInfoMax}'><c:out value='${requestScope.messageInfoMax}'/></c:if>" />
            </td>
		</tr>
		<tr>
			<td>Maximum length of Sender field:</td>
			<td>
            <input type="text" name="senderMaxLen" title="Can be between 10 and 100" size="4" 
            	value="<c:if test='${!empty requestScope.senderMaxLen}'><c:out value='${requestScope.senderMaxLen}'/></c:if>" /></td>
		</tr>
		<tr>
			<td>Maximum length of Subject field:</td>
			<td>
            <input type="text" name="subjectMaxLen" title="Can be between 10 and 100" size="4"
                value="<c:if test='${!empty requestScope.subjectMaxLen}'><c:out value='${requestScope.subjectMaxLen}'/></c:if>"/></td>
		</tr>
		<tr>
			<td>Show a usage tip?</td>
			<td><input type="checkbox" name="usageTipOn"  title="Check here to show the usage tip" value="true"
				<c:if test='${!empty requestScope.usageTipOn && requestScope.usageTipOn=="true"}'>checked</c:if> /></td>
		</tr>
		
		<tr><td align="center" colspan="2">
		<input type="hidden" name="mode" value="edit" />
  		<input class="button-submit-gotMail" type="submit" name="submit" 
  			value="Update" title="Update your preferences" />
  		</td></tr>
  		</table>
	</form>
