<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<title>VPN BriefCase</title>
<script language="JavaScript" type="text/javascript">
          function submitLogin()
          {
            document.login.submit();
            return;
          }
        </script>
</head>

<body onload="submitLogin();">
<img src="media/net/unicon/academusTheme/guelph/images/imap.gif" alt="" />

	<form action="https://vpn.uoguelph.ca/+webvpn+/index.html" method="post" id="login" name="login">
		<input type="hidden" name="username" value="<%= request.getAttribute("username") %>" /> 
		<input type="hidden" name="password" value="<%= request.getAttribute("password") %>" /> 
		<input type="hidden" name="Login" value="Login" /> 
<!--
		<input type="hidden" name="Clear" value="Clear" />
		<INPUT type="hidden" name="next" value="">
		<INPUT type="hidden" name="tgroup" value="">
		<INPUT type="hidden" name="tgcookieset" value="">
-->
	</form>
</body>
</html>
