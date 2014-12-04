<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1"/>
    <title>Jive SSO</title>

   <script LANGUAGE="JavaScript">
	<!--
	// This will resize the window when it is opened or
	// refresh/reload is clicked to a width and height of 500 x 500
	// with is placed first, height is placed second
	window.resizeTo(800,600)
	-->
	</script>
	    
	<script LANGUAGE="JavaScript">
	  function submitLogin()
	  {
	    document.login.submit();
	    return;
	  }
	</script>    
  </head>

<body onLoad="submitLogin();">
<img src="media/net/unicon/academusTheme/guelph/images/imap.gif" />
  <form action="https://forums.ccs.uoguelph.ca:1443/jive4/login.jspa" method="post" id="login" name="login">
    <input type="hidden" id="username01" name="username" value="<%= request.getAttribute("username") %>">
    <input TYPE="hidden" id="password01" name="password" value="<%= request.getAttribute("password") %>">
	<input type="hidden" id="button" name="login" value="Login">
  </form>
  
</body>
</html>
