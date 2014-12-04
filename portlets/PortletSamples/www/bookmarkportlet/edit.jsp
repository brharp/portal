<%--
  Copyright 2003 Sun Microsystems, Inc.  All rights reserved.
  PROPRIETARY/CONFIDENTIAL.  Use of this product is subject to license terms.
--%>
<%@ page import="javax.portlet.RenderRequest" %>
<%@ page import="javax.portlet.PortletPreferences" %>
<%@ page import="javax.portlet.RenderResponse" %>
<%@ page import="javax.portlet.PortletURL" %>
<%@ page import="com.sun.portal.portlet.samples.bookmarkportlet.BookmarkPortlet" %>
<%@ page import="com.sun.portal.portlet.samples.bookmarkportlet.Target" %>
<%@ page session="false" %>

<%

	RenderRequest pReq = 
	    (RenderRequest)request.getAttribute("javax.portlet.request");
	RenderResponse rRes = 
	    (RenderResponse)request.getAttribute("javax.portlet.response");

        PortletPreferences pref = pReq.getPreferences();
        String[] targets = pref.getValues(BookmarkPortlet.TARGETS, null);

        // Get the resource count
        String resourceCountString = Integer.toString(targets.length);

        // Get resource name and resource URL
        String resourceName = pReq.getParameter("resourceName");

        if (resourceName == null) {
            resourceName = "";
        }else{
        	//this is required of uPortal 2.5
        	//in uPortal 2.5 a parameter is added onto the url and this is getting picked up in the resourceName
        	//the parameter is of the form #__ the blank being a number.  This is specific to portlets ONLY.
        	
		
		//therefore this loop will parse off the #__ from the resourceName
		for(int i = resourceName.length()-1; i>=0; i--){
			if(resourceName.charAt(i) == '#'){
				resourceName = resourceName.substring(0, i);
				break;
			}
		}
        }       

        String resourceURL = pReq.getParameter("resourceURL");	

    	if (resourceURL == null) {
            resourceURL = "";
        }

        // create the action URI and the render URI
        PortletURL actionURI = rRes.createActionURL(); 
        
        // Error message if there is any
        String errorMsg =
            pReq.getParameter(BookmarkPortlet.RENDER_PARAM_ERROR);        
        if (errorMsg != null) {          
%>
		<font color="red">
         	<b>
		<%= errorMsg %>
		</b>
		
		<br> 
		<hr>
		<br>
		</font>
<%
        }
%>

<form ACTION="<%= actionURI.toString() %>" TARGET="_parent" NAME="edit_form" METHOD=POST ENCTYPE="application/x-www-form-urlencoded"> 
<table BORDER=0 CELLPADDING=0 CELLSPACING=3 WIDTH="100%">
<tr>
<td WIDTH="100%" VALIGN=TOP>
<center>


<table border=0 cellspacing=0 cellpadding=2 width="100%">
                          
  <tr>                
    <td colspan="2">
       <h2>Add a Bookmark:</h2>
    </td>
  </tr>
              
              
  <tr>           
    <td colspan="2">                 
      <input type=HIDDEN name="resourceCount" value="<%= resourceCountString  %>">              
      <input type=HIDDEN name="add_more" value="">
    </td>
  </tr>
              
  <tr>               
    <td align="right"><b><label for="resourcename">Bookmark Name:</label></b></td>
    <td><input type=TEXT name="resourceName" size=45 value="<%= resourceName  %>" id="resourcename"></td>
  </tr>
              
  <tr>               
    <td align="right"><b><label for="url">URL:</label></b></td>
    <td><input type=TEXT name="resourceURL" size=45 value="<%= resourceURL  %>" id="url"></td>
  </tr>
                       
</table>

<input type=SUBMIT name="<%=BookmarkPortlet.ADD_RESOURCE%>" value="Add To Favourites" onClick="add_more.value='true';" >


<table border=0 cellspacing=0 cellpadding=2 width="100%">
<br>
<tr>
<td>
<hr>
</td>
</tr>
</table>

<br>

<table border=0 cellspacing=0 cellpadding=2 width="100%">
              
  <tr>                
    <td><h2>Delete and Edit Bookmarks:</h2></td>
  </tr>            
</table>
	
<table border=1 cellspacing=0 cellpadding=0 width="75%" align="center">

  <tr>
    <td align="center" bgcolor="#CCCCCC"><p><h3>Remove</h3></td>
    <td align="center" bgcolor="#CCCCCC"><p><h3>Name</h3></td>
    <td align="center" bgcolor="#CCCCCC"><p><h3>URL</h3></td>
  </tr>

<%
    	//construct the html for the [] NAME  URL listing 
        StringBuffer resourceList = new StringBuffer("");

    	for (int i = 0; i < targets.length; i++) {
    	    String targ = (String)targets[i];
    	    Target target = new Target(targ);
%>        
	    <TR>
  	    <TD>
    	        <CENTER>
                <INPUT TYPE="CHECKBOX" VALUE="1" NAME="remove<%= String.valueOf(i)%>">
            	</CENTER>
  	    </TD>
  	    <TD>
    	    	<INPUT TYPE="TEXT" VALUE="<%= BookmarkPortlet.HTML_ENCODER.encode(target.getName()) %>" SIZE="40" NAME="name<%= String.valueOf(i) %>">
  	    </TD>             
  	    <TD>
            	<INPUT TYPE="TEXT" RPROXY-NOPARSE VALUE="<%= BookmarkPortlet.HTML_ENCODER.encode(target.getValue()) %>" SIZE="40" NAME="url<%= String.valueOf(i)  %>">
            </TD>
  	    </TR>
<%
        }
    	String all_new_checked = new String( "" );
    	String one_new_checked = new String( "" );
    	String same_checked = new String( "" );

        // Get the window preference
        String wp = pref.getValue("windowPref", "");
    	if (wp.equals("all_new")){
    	    all_new_checked = "CHECKED";
    	}    
    	if (wp.equals("one_new")) {
    	    one_new_checked = "CHECKED";
    	}
    	if (wp.equals("same")) {
    	    same_checked = "CHECKED";
    	}
%>
</table>

<table border=0 cellspacing=0 cellpadding=2 width="100%">
<br>
<tr>
<td>
<hr>
</td>
</tr>
</table>


</center>
</td>
</tr>
</table>


<br>
<center>
	<input TYPE=SUBMIT NAME="<%=BookmarkPortlet.FINISHED_EDIT%>" onClick="finish.value='true';" VALUE="Finished">
	<input TYPE=SUBMIT NAME="<%=BookmarkPortlet.CANCEL_EDIT%>" onClick="cancel.value='true';" VALUE="Cancel">
</center>
<br>

</form>
<br>
