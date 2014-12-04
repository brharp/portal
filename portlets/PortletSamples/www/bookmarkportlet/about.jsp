<%@ page import="com.sun.portal.portlet.samples.bookmarkportlet.BookmarkPortlet" %>
<%@ page import="javax.portlet.RenderRequest" %>
<%@ page import="javax.portlet.PortletPreferences" %>
<%@ page import="javax.portlet.RenderResponse" %>
<%@ page import="javax.portlet.PortletURL" %>
<%@ page import="com.sun.portal.portlet.samples.bookmarkportlet.BookmarkPortlet" %>
<%@ page import="com.sun.portal.portlet.samples.bookmarkportlet.Target" %>
<%@ page session="false" %>

<%
	//these lines are required for the portlet to render properly

	RenderRequest pReq = 
	    (RenderRequest)request.getAttribute("javax.portlet.request");
	RenderResponse rRes = 
	    (RenderResponse)request.getAttribute("javax.portlet.response");

        // create the action URI and the render URI
        PortletURL actionURI = rRes.createActionURL(); 

%>


<form ACTION="<%= actionURI.toString() %>" TARGET="_parent" NAME="edit_form" METHOD=POST ENCTYPE="application/x-www-form-urlencoded"> 
<table border=0 cellspacing=0 cellpadding=2 width="100%">
                          
  <tr>                
    <td colspan="2">
       <h2>About:</h2>
    </td>
  </tr>
  
  <br>
  
  <tr>
  	<td>
  		<p>This Bookmarks portlet is based on the 2003 Sun Microsystems, Inc. version.  
  		It has been modified and developed further to meet the University of Guelph's needs.  
  		Computing and Communication Services, 2005.
  		</p>
  	</td>
  </tr>
  
</table>

<center>
   <input TYPE=SUBMIT NAME="<%=BookmarkPortlet.CANCEL_EDIT%>" onClick="cancel.value='true';" VALUE="Return">
</center>

</form>