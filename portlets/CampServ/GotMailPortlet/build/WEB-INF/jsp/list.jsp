<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<style>
/* see gotMailTable in MyDoc */
.gotMailErrorMessage {
	color : red;
	font_weight : bold;
}	

.table-gotMail {
	font-family: Verdana; 
	font-size: 8pt; 
	color: black; 
	spacing-left: 0px 
	spacing-right: 0px;	
}

.table-header-gotMail {
	/*background-color : rgb(149,179,222); 
	font-size: 10pt; 
	color : white;
	font-weight: bold; 
*/
	/*background-color : rgb(250,250,250);*/
	font-size: 10pt; 
/*	color : rgb(0,51,153); */
	font-weight: bold; 
	
	
	border-color : rgb(0,51,153);
	border-right-width : 0px;
	border-left-width : 0px;
	border-top-width : 0px;
	border-bottom-width : 0px;
	border-style : solid;
	text-align: center;	
	
}

.table-column-names-gotMail {
	background-color : rgb(240,240,240); 
	color : rgb(0,51,153); 
	font-weight : bold;
	border-color : rgb(200,200,200);
	border-right-width : 1px;
	border-left-width : 0px;
	border-top-width : 1px;
	border-bottom-width : 1px;
	border-style : solid;
	text-align: center;	
}
.table-cell-odd { 
	border-left:0px;
	border-right:0px; 	
	border-bottom:1px solid rgb(220,220,220); 
	border-top:0px;
	background-color:rgb(250,250,250); 
	color:black;
	padding-left: 1em; 
	padding-right: 1em;	
	spacing-left: 0em; 
	spacing-right: 0em;		
}

.table-cell-even { 
	border-left:0px;
	border-right:0px; 	
	border-bottom:1px solid rgb(220,220,220);
	border-top:0px;
	background-color:rgb(250,250,250); 
	color:black;
	padding-left: 1em; 
	padding-right: 1em;	
	spacing-left: 0em; 
	spacing-right: 0em;	

}

.button-submit-gotMail {
	font-family: Verdana; 
	font-size: 8pt; 
	font-weight : bold;
	color: rgb(250,250,250); 
	spacing-left: 0px 
	spacing-right: 0px;	
	background-color : rgb(117,149,229);
}

.table-footnote-gotMail {
	background-color:rgb(250,250,250); 
	font-size: 8pt; 
	color : gray;
	font-weight: regular; 
	font-style : italic;
	border-width: 0px;
	border-style : solid;
	text-align: left;	
	
}


</style>


<c:if test="${!empty requestScope.GotMailErrorMessage}">
<!--<span class="portlet-msg-error">-->
<span class="gotMailErrorMessage">
	<c:out escapeXml="false" value="${requestScope.GotMailErrorMessage}"/>
</span>	
</c:if>

<c:if test="${empty requestScope.GotMailErrorMessage}">
	<form action="<portlet:actionURL />">
  		<c:if test="${!empty requestScope.GotMailCountUnseen}">
 			<c:if test="${requestScope.GotMailCountUnseen > 0}">
 				&nbsp;<img border="0" src="/GotMailPortlet/images/mail_notread.gif" title="You have unread messages">
 			</c:if>
 		  	You have <strong>
 			<c:if test="${requestScope.GotMailCountUnseen <1 }">no</c:if>
 			<c:if test="${requestScope.GotMailCountUnseen >0 }">
		 		<c:out value="${requestScope.GotMailCountUnseen}"/> 
		 	</c:if>
		 	unread message<c:if test="${requestScope.GotMailCountUnseen !=1 }">s</c:if>
		 	</strong>
		</c:if>

		<%--
	  	<a title="Refresh - check for unread messages now" 
		 href="<portlet:actionURL >
        	<portlet:param name="action" value="refresh"/>
			<portlet:param name="submit" value="top"/>
  		  </portlet:actionURL >">
		  <!--      <img border="0" src="/GotMailPortlet/images/refresh_16.gif">-->Refresh
	  	</a>
	  	--%>

		
		<c:if test="${!empty requestScope.GotMailCountUnseen && requestScope.GotMailCountUnseen>0}">
			<c:if test="${!empty requestScope.GotMailDetailedModeOn and requestScope.GotMailDetailedModeOn=='true'}">
				<input class="button-submit-gotMail" type="submit" name="submit" 
  			    value="Hide" title="Hide the message listing" />
  			</c:if>    
  			<c:if test="${empty requestScope.GotMailDetailedModeOn or requestScope.GotMailDetailedModeOn=='false'}">
				<input class="button-submit-gotMail" type="submit" name="submit" 
  				value="Show" title="Show the message listing" />
			</c:if>
  		</c:if>
  		<input class="button-submit-gotMail" type="submit" name="submit" 
  			value="Refresh" title="Check for unread messages now" />
  
	</form>

  <c:if test="${!empty requestScope.GotMailDetailedModeOn and requestScope.GotMailDetailedModeOn=='true'}">
	<c:choose>
  		<c:when test="${!empty requestScope.GotMailCollection}">    	
  			<table class="table-gotMail">
     		<!--<tr class="uportal-channel-table-header">-->
     		<tr class="uportal-channel-table-header">
	   			<td class="table-column-names-gotMail">Sender</td>
	   			<td class="table-column-names-gotMail">Subject</td>
	   			<td class="table-column-names-gotMail">Received</td>	   			
     		</tr>
			<c:forEach items="${requestScope.GotMailCollection}" var="row" >				
				<tr class="uportal-channel-text">
					<jsp:useBean id="row" class="ca.nejedly.mail.imap.MessageRecord"/>     
        	
        			<td class="table-cell-odd">
            			<span title='<c:out value="${row.fromAddress}"/>'><c:out value="${row.fromAddressNormalized}"/></span>
            		</td>
            		<td class="table-cell-odd">
            			<span title='<c:out value="${row.subject}"/>'><c:out value="${row.subjectNormalized}"/></span>
            		</td>

        			<td class="table-cell-odd">
            			<c:out value="${row.receivedDateString}"/>
            		</td>
            		
        		</tr>
    		</c:forEach>
    		<!-- show a descriptive note if the messages listing was restricted -->
   		  	<c:if test="${!empty requestScope.GotMailListingRestricted}"> 		    		
	    		<tr class="table-footnote-gotMail">
					<td class="table-cell-odd" colspan="3">
						Only the latest <c:out value="${requestScope.GotMailListingRestricted}"/> messages are displayed.
            		</td>            		
        		</tr>
        	</c:if>	
    		</table>
  		</c:when>
  		<c:otherwise>
  		</c:otherwise>	 
	</c:choose>
  </c:if>	
</c:if>
