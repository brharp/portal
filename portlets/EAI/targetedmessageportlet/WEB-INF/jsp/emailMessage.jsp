<%--
  The contents of this file are subject to the terms
  of the Common Development and Distribution License
  (the License).  You may not use this file except in
  compliance with the License.
 
  You can obtain a copy of the license at
  http://www.sun.com/cddl/cddl.html or
  at portlet-repository/CDDLv1.0.txt.
  See the License for the specific language governing
  permissions and limitations under the License.
 
  When distributing Covered Code, include this CDDL
  Header Notice in each file and include the License file
  at portlet-repository/CDDLv1.0.txt.
  If applicable, add the following below the CDDL Header,
  with the fields enclosed by brackets [] replaced by
  you own identifying information:
  "Portions Copyrighted [year] [name of copyright owner]"
 
  Copyright 2006 Sun Microsystems, Inc. All rights reserved.
  
  "Portions Copyrighted 2006 Lalit Jairath, Barbara Edwards"
 --%>

<%@ page import="javax.portlet.PortletURL"%>
<%@ page import="javax.portlet.PortletMode"%>
<%@ page import="javax.portlet.PortletSession"%>

<%@ page import="java.util.*"%>
<%@ page import="java.io.StringWriter"%>
<%@ page import="java.io.PrintWriter"%>


<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<%@ taglib uri="http://jakarta.apache.org/taglibs/mailer-1.1" prefix="mt" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/request-1.0" prefix="req" %>


<%@ page session="true"%>

<portlet:defineObjects />

       <mt:mail>
        <req:existsParameter name="server">
          <mt:server><req:parameter name="server"/></mt:server>
        </req:existsParameter>
        <req:existsParameter name="to">
          <mt:setrecipient type="to"><req:parameter name="to"/></mt:setrecipient>
        </req:existsParameter>
        <req:existsParameter name="from">
          <mt:from><req:parameter name="from"/></mt:from>
        </req:existsParameter>
        <req:existsParameter name="cc">
          <mt:setrecipient type="cc"><req:parameter name="cc"/></mt:setrecipient>
        </req:existsParameter>
        <req:existsParameter name="subject">
          <mt:subject><req:parameter name="subject"/></mt:subject>
        </req:existsParameter>
        
        <mt:header name="X-Forwarded-By">MyPortico: UofG Notifications</mt:header>
        
        <req:existsParameter name="message">
          <mt:message><req:parameter name="message"/></mt:message>
        </req:existsParameter>
	<mt:send>
	  <p>The following errors occured<br/><br/>
	  <mt:error id="err">
	    <jsp:getProperty name="err" property="error"/><br/>
	  </mt:error>
	  <!-- <br/>Please back up a page, fix the error and resubmit.</p>  -->
	</mt:send>
     </mt:mail>

     <p>The message has been successfully sent.</p>
	<a href="<portlet:actionURL></portlet:actionURL>">Return to default view</a>  