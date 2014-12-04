
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1"/>
    <title>SSOWebmail</title>
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

    <form action="https://webmail.uoguelph.ca/redirect.php" method="post" id="login" name="login">
        <input type="hidden" name="url" value=""/>
        <input type="hidden" name="imapuser" value="<%= request.getAttribute("username") %>"/>
        <input type="hidden" name="server" value="template.mail.uoguelph.ca"/>
        <input type="hidden" name="mailbox" value="INBOX"/>
        <input type="hidden" id="actionID" name="actionID" value="105"/>
        <input type="hidden" id="pass" name="pass" value="<%= request.getAttribute("password") %>"/>
        <input type="hidden" id="port" name="port" value="143"/>
        <input type="hidden" id="namespace" name="namespace" value=""/>
        <input type="hidden" id="maildomain" name="maildomain" value="uoguelph.ca"/>
        <input type="hidden" id="protocol" name="protocol" value="imap%2Fnotls"/>
        <input type="hidden" id="realm" name="realm" value="uoguelph.ca"/>
        <input type="hidden" id="folders" name="folders" value="mail%2F"/>
        <input type="hidden" id="button" name="button" value="Log+in"/>
    </form>
</body>
</html>
