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

<h3>Got Mail?</h3>
<p>The <b><font color="#7595E5">Got Mail?</font></b> portlet checks your University of Guelph inbox for unread 
messages. It reports both the number of  unread messages and lists the 
corresponding sender, subject and the receipt date for the latest 10 unread messages. 
</p>
<p>Options: if you want the portlet to occupy as little space on your screen as 
possible you may hide the message details by pressing the Hide button. You can 
anytime enable the detailed listing by pressing the Show button. If you wish to see the actual email message and reply to it please open the 
WEBmail or your favorite email client. 
</p>
<p>Note: to see the latest messages please press the Refresh button. The portal 
will normally cache the portlet and the reported number and the message listing 
may not reflect the current status if not refreshed periodically.</p>
<p>If you have any comments or questions please contact Zdenek at 52881 
(<a href="mailto:znejedly@uoguelph.ca" title="any comments or questions welcome">znejedly@uoguelph.ca</a>).</p>
<p>&nbsp;</p>
	<form action="<portlet:actionURL />">
		<table align="center" width="100%">
		<tr><td align="center">
  		<input class="button-submit-gotMail" type="submit" name="submit" 
  			value="   OK   " title="Close the Help page and return to the mail view" />
  		</td></tr>
  		</table>
	</form>


