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
       <h2>Help</h2>
    </td>
  </tr>
  <br>
  <tr>
  	<td>
  		<h3>Policies:</h3> Currently under progress
  	</td>
  </tr>
  <br>
  <tr>
  	<td>
  		<h3><a href="" target="_blank">Help:</a></h3> Detailed steps on using the Bookmarks channel.  Currently under progress
  	</td>
  </tr>
  <br>
  <tr>
  	<td>
  		<h3><a href="http://www.uoguelph.ca/~portal/helpFiles/channels.html#bookmarks" target="_blank">FAQ's:</a></h3>Frequently Asked Question about the bookmarks channels.
  	</td>
  </tr>
  <br>
  <tr>
  	<td>
  		<h3><a href="http://www.uoguelph.ca/~portal/viewlets/RSSBookmarks/RSS_Bookmarks/RSS_Bookmarks_viewlet_swf.html" target="_blank">Viewlets:</a></h3> Short clips on performing key tasks
  	</td>
  </tr>
 
</table>

<center>
   <input TYPE=SUBMIT NAME="<%=BookmarkPortlet.CANCEL_EDIT%>" onClick="cancel.value='true';" VALUE="Return">
</center>

</form>