<html>
	<head>
		<title>MyPortico - Feedback</title>
		<style media="screen" type="text/css">
        	@import '/portal/media/net/unicon/academusTheme/guelph/css/uportal.css';
        	@import '/portal/media/net/unicon/academusTheme/guelph/css/portlet.css';
        	@import '/portal/media/net/unicon/academusTheme/guelph/css/academus_deprecated.css';
        	@import '/portal/media/net/unicon/academusTheme/guelph/css/main.css';
        	@import '/portal/media/net/unicon/academusTheme/guelph/css/login.css';
        	@import '/portal/media/net/unicon/academusTheme/guelph/css/jivestyle.css';        	
	    </style>
	</head>

	<body style="min-width: 750; overflow-x: hidden; overflow-y:scroll;">
		<div id="bodyContent">
			<div id="header">
				<span class="hide">Header</span>
				<h1 id="mainlogo">
				</h1>
				<h2 id="related1">
					<span>brand image</span>
				</h2>
				<h2 id="related2">
					<span>brand image</span>
				</h2>
				<hr class="hide">
			</div>

			<div id="content" style="text-align: left; position: relative; margin: auto;">
			<table align="left" style="margin-left: 115px;">
				<tr>
					<td>
						<%@ page session="false" %>
						
						<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
						<%@ taglib prefix="spring" uri="/spring" %>
						
						<script language="JavaScript"><!--
						function browserInfo(form) {
						form.browserName.value=navigator.appName;
						form.browserVersion.value=navigator.appVersion;
						form.platform.value=navigator.platform;
						form.userAgent.value=navigator.userAgent;
						}
						// --></script>
						
						<script LANGUAGE="JavaScript">
							<!--
							// This will resize the window when it is opened or
							// refresh/reload is clicked to a width and height of 500 x 500
							// with is placed first, height is placed second
							window.resizeTo(750,800)
							-->
						</script>

						<p style="width: 500px;">
							Use this form to send us your comments or suggestions about MyPortico. Upon 
							clicking 'Submit' your name, email address, and system information will be 
							automatically supplied along with your comments to the MyPortico development team. 
							If you want to send your feedback anonymously, please use the link to this 
							Feedback form that is provided on the login page.						
						</p>

						<p style="width: 500px;">
							For help or technical assistance, please email <a href="mailto:58888help@uoguelph.ca?subject=MyPortico Feedback">58888help@uoguelph.ca</a>.
						</p>
						
						<form method="post" onsubmit="browserInfo(this)">
						
							<p>
							<spring:bind path="command.subject">
							Subject*: <font color="#FF0000"><c:out value="${status.errorMessage}"/></font><br/>
							<input class="myportico-textbox" type="text" name="<c:out value="${status.expression}"/>" value="<c:out value="${status.value}"/>" />
							</spring:bind>
							</p>
							<p>
							<spring:bind path="command.details">
							Details*: <font color="#FF0000"><c:out value="${status.errorMessage}"/></font><br/>
							<textarea class="myportico-textbox" wrap="physical" rows="10" cols="60" name="<c:out value="${status.expression}"/>"><c:out value="${status.value}"/></textarea>
							</spring:bind>
							</p>
						
							<spring:bind path="command.name">
							<input type="hidden" name="<c:out value="${status.expression}"/>" value="<c:out value="${status.value}"/>" />
							</spring:bind>
							<spring:bind path="command.login">
							<input type="hidden" name="<c:out value="${status.expression}"/>" value="<c:out value="${status.value}"/>" />
							</spring:bind>
							<spring:bind path="command.emailAddress">
							<input type="hidden" name="<c:out value="${status.expression}"/>" value="<c:out value="${status.value}"/>" />
							</spring:bind>
							<spring:bind path="command.role">
							<input type="hidden" name="<c:out value="${status.expression}"/>" value="<c:out value="${status.value}"/>" />
							</spring:bind>
						
							<spring:bind path="command.serverName">
							<input type="hidden" name="<c:out value="${status.expression}"/>" value="<c:out value="${status.value}"/>" />
							</spring:bind>
						
							
							<spring:bind path="command.userAgent">
							<input type="hidden" name="<c:out value="${status.expression}"/>" />
							</spring:bind>
							<spring:bind path="command.browserName">
							<input type="hidden" name="<c:out value="${status.expression}"/>" />
							</spring:bind>
							<spring:bind path="command.browserVersion">
							<input type="hidden" name="<c:out value="${status.expression}"/>" />
							</spring:bind>
							<spring:bind path="command.platform">
							<input type="hidden" name="<c:out value="${status.expression}"/>" />
							</spring:bind>
							
							<p>
							<strong>* denotes a required field.</strong>
							</p>
						
							<p>
							<input type="submit" value="Submit" />
							</p>
						</form>
						</td>
					</tr>
				</table>
			</div>
			<div id="footer">
				<span class="hide">Footer</span>

				<div id="footerlogo">
					<span class="hide">University of Guelph</span>
				</div>				
				<div id="institution">
					<div id="institutionLocation">Guelph, Ontario, Canada</div>
					<div id="institutionContact">
						<div id="contact1">
							<span id="contact1Label">N1G 2W1</span>
						</div>
						<div id="contact2">
							<span id="contact2Label">Tel: (519) 824-4120</span>
						</div>
					</div>
				</div>
				
				<div id="legal">
					<p id="copyright">Copyright 2006 University of Guelph</p>
				</div>
			</div>
		</div>
	</body>
</html>

