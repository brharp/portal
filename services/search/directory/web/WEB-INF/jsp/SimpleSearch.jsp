<%
	String cname = (String)session.getAttribute("commonname");
	if (cname == null || cname.length() == 0) {
		cname = "";
	}
%>
<html>
	<head>
		<title>MyPortico - People Search</title>
		<style media="screen" type="text/css">
        	@import '/portal/media/net/unicon/academusTheme/guelph/css/uportal.css';
        	@import '/portal/media/net/unicon/academusTheme/guelph/css/portlet.css';
        	@import '/portal/media/net/unicon/academusTheme/guelph/css/academus_deprecated.css';
        	@import '/portal/media/net/unicon/academusTheme/guelph/css/main.css';
        	@import '/portal/media/net/unicon/academusTheme/guelph/css/login.css';
        	@import '/portal/media/net/unicon/academusTheme/guelph/css/jivestyle.css';        	
	    </style>
	</head>

	<body style="min-width: 750px; overflow-x: hidden; overflow-y: scroll; ">
		<script LANGUAGE="JavaScript">
			<!--
			// This will resize the window when it is opened or
			// refresh/reload is clicked to a width and height of 500 x 500
			// with is placed first, height is placed second
			window.resizeTo(750,800)
			-->
		</script>

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

<h2>Email &amp; Telephone Directory</h2>
<table border="0" class="myportico-table-search">
	<form name="phonebook" action="SearchDirectory" method="post" onSubmit="return formcheck()">
		<tr>
		  <td colspan="2" class="main"><a href="SearchDirectory?search=complex">switch to detailed search</a></td>
		  <td class="main">&nbsp;</td>
		</tr>
	    <tr> 
			<td id="field-label"><label>Name:</label>
			</td>
			<td class="main"> 
	          <input name="commonname" type="text" value="<%=cname%>" maxlength="60" required="yes" message="Please provide a name, or part of a name, for the search." class="ugdirfield" size="25">
			</td>
			<td rowspan="3" class="main"> 
	          <input type="radio" name="orgstatus" value="staff" id="staff">
	          <label for="staff">staff / faculty</label><br>
	          <input type="radio" name="orgstatus" value="students" id="students">
	          <label for="students">students</label><br>
	          <input type="radio" name="orgstatus" value="all" checked id="anyone">
	
	          <label for="anyone">anyone</label><br>
	          <input name="savegroup" type="checkbox" value="1">
	          (lock this choice)
			</td>
		</tr>
	    <tr> 
			<td class="main">&nbsp;
			</td>
			<td class="main">
				<input type="hidden" name="search" value="simple"/> 
				<input type="hidden" name="mail" value=""/> 
		        <input type="hidden" name="phone" value=""/> 
		        <input type="hidden" name="department" value="null"/> 
		        <input type="hidden" name="showresults" value="yes"/> 
		        <input type="hidden" name="givenname" value=""/> 
		        <input type="hidden" name="surname" value=""/> 
		        <input name="submit" type="submit" value="search"/>
			</td>
		</tr>
	</form>
</table>
