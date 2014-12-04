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
  "Portions Copyrighted 2006 Lalit Jairath, Barbara Edwards, Brent Harp, Peter McCaskell"
 --%>

<%@ page import="javax.portlet.PortletURL" %>
<%@ page import="javax.portlet.PortletMode" %>
<%@ page import="javax.portlet.PortletSession" %>

<%@ page import="java.util.*" %>
<%@ page import="java.io.StringWriter" %>
<%@ page import="java.io.PrintWriter" %>

<%@ page import="com.sun.portal.rssportlet.FormNames" %>

<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<portlet:defineObjects/>
<!--<portlet:actionURL var="actionURL"/>-->
<fmt:setBundle basename="edit"/>

<c:catch var="exception">    
    <jsp:useBean id="alertHandler" class="com.sun.portal.rssportlet.AlertHandler" scope="session"/>           
    <jsp:useBean id="settingsBean" class="com.sun.portal.rssportlet.SettingsBean"/>       
    <jsp:useBean id="settingsHandler" class="com.sun.portal.rssportlet.SettingsHandler">
        <jsp:setProperty name="settingsHandler" property="portletRequest" value="<%=renderRequest%>"/>
        <jsp:setProperty name="settingsHandler" property="portletConfig" value="<%=portletConfig%>"/>  
    </jsp:useBean>

    <% 
        // jsp doesn't seem to like me doing this in a jsp:setProperty
        settingsHandler.setSettingsBean(settingsBean);
    	//number of locked feeds = mandatory+role feeds
		PortletSession psession = renderRequest.getPortletSession();
		String[] mandate_feeds = (String[])psession.getAttribute(settingsHandler.MANDATE_FEEDS);
		String[] role_feeds = (String[])psession.getAttribute(settingsHandler.ROLE_FEEDS);
		
		int numLockedFeeds = 0;
		if (null != mandate_feeds) {
			numLockedFeeds += mandate_feeds.length;
		}

		if (null != role_feeds) {
			numLockedFeeds += role_feeds.length;
		}
		
		LinkedList feeds = settingsBean.getFeeds();
	    pageContext.setAttribute("numLockedFeeds", new Integer(numLockedFeeds));
        // so we can get them from a portlet action
        renderRequest.getPortletSession().setAttribute("alertHandler", alertHandler, PortletSession.PORTLET_SCOPE);
    %>
   
    <c:if test="${alertHandler.errorRendered}">
	    <div class="portlet-msg-error" style="color: #990000">
	        ${alertHandler.errorSummary}
	        <div style="font-size: smaller">
	            ${alertHandler.errorDetail}            
	        </div>
	    </div>
    </c:if>

     <form name="inputForm" target="_self" method="POST" action="<portlet:actionURL></portlet:actionURL>">
        <table border="0" style="align: center">
            <!-- START feed checboxes -->
            <c:choose>
                <c:when test="${settingsBean.feedsSize != 0}">
                     <c:forEach var="feed" items="${settingsBean.feeds}" varStatus="statusFeed">	
                    <c:forEach var="title" items="${settingsBean.titleList}" varStatus="statusTitle">

                    <c:if test="${statusFeed.index == statusTitle.index}"> 
                    <c:if test="${statusFeed.index == 0 && statusTitle.index == 0}">
                    <tr>
                        <td align="left">
                            <p><b><i>To delete a feed uncheck the box</i></b></p>
                        </td>
                        <td>
	                        <input class="uportal-button" name="<%=FormNames.SUBMIT_CUSTOM%>" type="submit" value="<fmt:message key="custom"/>"/>
                        </td>
                    </tr>        
                     <tr><td colspan="2">&nbsp;</td></tr>    
                     </c:if>         
                        <tr>
                            <td align="left">
                            <c:choose>
                            <c:when test="${statusFeed.index < numLockedFeeds }">
  								                          &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="${feed}" title="${feed}">${title}</a>
                                </c:when>
                                <c:otherwise>
                                <input class="portlet-font" type="checkbox" name="<%=FormNames.INPUT_FEEDS%>" value="${feed}" CHECKED/>
                                <a href="${feed}" title="${feed}">${title}</a>
                                </c:otherwise>
            					</c:choose>                    
                            </td>
                        </tr>   
                     </c:if>  
                    </c:forEach>
                    </c:forEach>
					<!-- START "finished", "custom" and "cancel" buttons -->        
            		<tr><td align="center" colspan="2">
                		<input class="uportal-button" name="<%=FormNames.SUBMIT_FINISH%>" type="submit" value="<fmt:message key="finished"/>"/>
                		<input class="uportal-button" name="<%=FormNames.SUBMIT_CANCEL%>" type="submit" value="<fmt:message key="cancel"/>"/>
            			</td>
                </c:when>
                <c:otherwise>
                    <tr>
                        <td align="left">
                            <fmt:message key="no_feeds_list" />
                        </td>
                    </tr>
					<tr><td align="center" colspan="2">
                		<input class="uportal-button" name="<%=FormNames.SUBMIT_CANCEL%>" type="submit" value="<fmt:message key="cancel"/>"/>
            			</td>
					</tr>
                </c:otherwise>
            </c:choose>


            <!-- END feed checboxes -->

            <tr><td colspan="2"><hr style="width: 100%; text-align: center"/></td></tr>
        
            <!-- START "add feed" text box + button -->     
            <tr>
                <td style="align: left" colspan="2">
                <table border="0">

                <tr>
                    <td style="text-align: right; vertical-align: top">
                        <div class="portlet-form-field-label">                
                            <fmt:message key="additional_rss_feed" />
                        </div>
                    </td>
                    <td style="vertical-align: top">
                        <input type="text" name="<%=FormNames.INPUT_ADD_FEED%>" value="">&nbsp;
                        <input class="uportal-button" name="<%=FormNames.SUBMIT_ADD%>" type="submit" value="<fmt:message key="add"/>">     
                    </td>
                </tr>
                
            </table>
            </td>
            </tr>
            <!-- END "finished" and "cancel" buttons -->                

        </table>
    </form>
</c:catch>

<c:if test="${exception != null}">
    <% 
        Throwable t = (Throwable)pageContext.getAttribute("exception");
        StringWriter st = new StringWriter();
        PrintWriter pw = new PrintWriter(st);
        t.printStackTrace(pw);
        portletConfig.getPortletContext().log("exception occured in edit.jsp", t);
    %>
    <div class="portlet-msg-error" style="color: #990000">
        <fmt:message key="exception"/>
        <div style="font-size: smaller">
            <pre><%= t.getMessage() %></pre>            
        </div>
    </div>
   <!--
   <%= st %>
   -->   
</c:if>
