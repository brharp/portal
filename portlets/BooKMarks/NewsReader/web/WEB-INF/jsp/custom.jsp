<%--
  "Copyrighted 2006 Lalit Jairath, Barbara Edwards, Brent Harp, Peter McCaskell"
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

<!-- 
this function sets the "maximum feed age" text box either disabled or
enabled depending on the value of the "disable maximum feed age" check box
-->
<script language="javascript">
    function setMaxAge() {
    if (document.inputForm.<%=FormNames.INPUT_DISABLE_MAX_AGE%>.checked == true) {
    document.inputForm.<%=FormNames.INPUT_MAX_AGE%>.disabled = true;    
    } else {
    document.inputForm.<%=FormNames.INPUT_MAX_AGE%>.disabled = false;    
    }
    }
</script>

<portlet:defineObjects/>
<!--<portlet:actionURL var="actionURL"/>-->
<fmt:setBundle basename="custom"/>

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
        // so we can get them from a portlet action
        renderRequest.getPortletSession().setAttribute("alertHandler", alertHandler, PortletSession.PORTLET_SCOPE);
    %>
    
    <!-- START title bar -->
    <table width="50%" border="0" style="align: center;">
        <tr>                
            <td style="background-color: #990000; align: center; colspan=2">
                <div style="font-weight: bold; color: white; font-size: small; left: 10px">
                    <fmt:message key="customize_rss_feeds" />                
                </div>
            </td>
        </tr>
        <tr><td>&nbsp;</td></tr>    
    </table>
    <!-- END title bar -->
   
    <c:if test="${alertHandler.errorRendered}">
        <p>
            <div class="portlet-msg-error" style="color: #990000">
                ${alertHandler.errorSummary}
                <div style="font-size: smaller">
                    ${alertHandler.errorDetail}            
                </div>
            </div>
        </p>
    </c:if>

    <form name="inputForm" target="_self" method="POST" action="<portlet:actionURL></portlet:actionURL>">
        <table border="0" style="align: center">
                
            <!-- START start feed drop down list -->        
            <tr>
                <td style="align: left">
                    <table border="0">
                    <tr>
                        <td style="text-align: right; vertical-align: top">
                            <div class="portlet-form-field-label">
                                <fmt:message key="start_feed" />
                            </div>
                        </td>
                        <td style="vertical-align: top">
                            <c:choose>
                                <c:when test="${settingsBean.feedsSize > 0}">                   
                                    <select name="<%=FormNames.INPUT_START_FEED%>">
                                        <c:forEach var="feed" items="${settingsBean.feeds}" varStatus="statusFeed">	
					                    <c:forEach var="title" items="${settingsBean.titleList}" varStatus="statusTitle">
                    					<c:if test="${statusFeed.index == statusTitle.index}"> 
                                            <c:choose>
                                                <c:when test="${feed == settingsBean.startFeed}">
                                                    <option value="${feed}" selected>
                                                        <div class="portlet-font">                    
                                                            ${title}        
                                                        </div>
                                                    </option>                           
                                                </c:when>
                                                <c:otherwise>
                                                    <option value="${feed}">
                                                        <div class="portlet-font">                    
                                                            ${title}        
                                                        </div>
                                                    </option>                                                       
                                                </c:otherwise>
                                            </c:choose>
                                         </c:if>    
                                        </c:forEach>
                                        </c:forEach>
                                    </select>
                                </c:when>
                                <c:otherwise>
                                    <fmt:message key="no_feeds_start" />
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                </td>
            </tr>
                <!-- END start feed drop down list -->
                       
                <!-- START "maximum feed age" text box + disable -->

                <tr>
                    <td style="text-align: right; vertical-align: top">
                        <div class="portlet-form-field-label">
                            <fmt:message key="maximum_feed_age_hours" />
                        </div>
                    </td>
                    <td style="vertical-align: top">
                            <input type="text" name="<%=FormNames.INPUT_MAX_AGE%>" value="${settingsBean.maxAge}">
                    </td>
                </tr>  
                <tr>
                    <td style="text-align: right; vertical-align: top">
                        <div class="portlet-form-field-label">
                            <fmt:message key="disable" />
                        </div>
                    </td>
                    <td style="vertical-align: top">
                        <c:choose>
                            <c:when test="${settingsBean.disableMaxAge}">
                                <input type="checkbox" name="<%=FormNames.INPUT_DISABLE_MAX_AGE%>" value="" checked onclick='javascript:setMaxAge();'>                                                    
                            </c:when>
                            <c:otherwise>
                                <input type="checkbox" name="<%=FormNames.INPUT_DISABLE_MAX_AGE%>" value="" onclick='javascript:setMaxAge();'>                                                
                            </c:otherwise>
                        </c:choose>
                        <script language="javascript">setMaxAge();</script>                        
                    </td>
                </tr>
                
                <!-- END "maximum feed age" text box + disable -->
                
               <!-- <tr><td>&nbsp;</td></tr>              -->
                
                <!-- START "maximum entries" text box -->                
                <tr>
                    <td style="text-align: right; vertical-align: top">
                        <div class="portlet-form-field-label">
                            <fmt:message key="maximum_entries" />
                        </div>
                    </td>
                    <td style="vertical-align: top">
                        <input type="text" name="<%=FormNames.INPUT_MAX_ENTRIES%>" value="${settingsBean.maxEntries}"/>
                    </td>
                </tr>                                
                <!-- END "maximum entries" text box -->
                
                <!-- START "new window" check box -->                
                <tr>
                    <td style="text-align: right; vertical-align: top">
                        <div class="portlet-form-field-label">
                            <fmt:message key="new_window_for_entries" />
                        </div>
                    </td>
                    <td style="vertical-align: top">
                        <c:choose>
                            <c:when test="${settingsBean.newWindow}">
                                <input type="checkbox" name="<%=FormNames.INPUT_NEWWIN%>" value="" checked/>                                                    
                            </c:when>
                            <c:otherwise>
                                <input type="checkbox" name="<%=FormNames.INPUT_NEWWIN%>" value=""/>                                                
                            </c:otherwise>
                        </c:choose>
                    </td>
                </tr>        
                <!-- END "new window" check box -->
            </table>
            </td>
            </tr>
        	<tr><td><hr style="width: 100%; text-align: center"/></td></tr>
            <!-- START "finished", "edit" and "cancel" buttons -->        
            <tr><td align="center">
                <input class="uportal-button" name="<%=FormNames.SUBMIT_FINISH%>" type="submit" value="<fmt:message key="finished"/>"/>
                <input class="uportal-button" name="<%=FormNames.SUBMIT_CANCEL%>" type="submit" value="<fmt:message key="cancel"/>"/>
                <input name="<%=FormNames.WINDOW_CUSTOM%>" type="hidden" value="<fmt:message key="window_custom"/>"/>

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
