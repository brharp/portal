<%@ page session="false" %>
<%@ page import="javax.portlet.*"%>
<%@ page import="java.util.*"%>
<%@ taglib uri='/WEB-INF/tld/portlet.tld' prefix='portlet'%>
<jsp:useBean id="addUrl" scope="request" class="java.lang.String" />
<jsp:useBean id="cancelUrl" scope="request" class="java.lang.String" />
<portlet:defineObjects/>
<%
ResourceBundle myText = ResourceBundle.getBundle("Text", request.getLocale());
%>
<B><%=myText.getString("available_bookmarks")%></B><br>
<FORM ACTION="<%=addUrl%>" METHOD="POST">
<TABLE CELLPADDING=0 CELLSPACING=4>
  <TR>
    <TD>
      <B><%=myText.getString("name")%></B>
    </TD>
    <TD>
      <B><%=myText.getString("url")%></B>
    </TD>
    <TD>
    </TD>
  </TR>
<%
PortletPreferences prefs = renderRequest.getPreferences();
Enumeration e = prefs.getNames();
while (e.hasMoreElements())
{
    String name = (String)e.nextElement();
    String value = prefs.getValue(name,"<"+myText.getString("undefined")+">");
%>
  <TR>
    <TD>
      <%=name%>
    </TD>
    <TD>
      <%=value%>
    </TD>
    <TD>
<portlet:actionURL var="removeUrl">
	<portlet:param name="remove" value="<%=name%>"/>
</portlet:actionURL>
      <A HREF ="<%=removeUrl.toString()%>">[<%=myText.getString("delete")%>]</A>
    </TD>
  </TR>
<%
}
%>
  <TR>
    <TD>
      <INPUT NAME="name" TYPE="text">
    </TD>
    <TD>
      <INPUT NAME="value" TYPE="text">
    </TD>
    <TD>
      <INPUT NAME="add" TYPE="submit" value="<%=myText.getString("add")%>">
    </TD>
  </TR>
</TABLE>
</FORM>
<FORM ACTION="<%=cancelUrl%>" METHOD="POST">
  <INPUT NAME="cancel"  TYPE="submit" VALUE="<%=myText.getString("cancel")%>">
</FORM>
