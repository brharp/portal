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

<%@ page import="com.sun.portal.rssportlet.FormNames"%>

<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/mailer-1.1" prefix="mt" %>


<!-- 
this function sets the "maximum feed age" text box either disabled or
enabled depending on the value of the "disable maximum feed age" check box
-->
<script language="javascript">
<%=FormNames.FEED_OUTPUT_TYPE%>		
<%=FormNames.INPUT_MAX_NEWS_PAPER_ENTRIES%>
<%=FormNames.INPUT_MAX_ENTRIES%>

    function setEntries() {
   	    if (document.inputForm.<%=FormNames.FEED_OUTPUT_TYPE%>[0].checked == true) {
   	       	document.inputForm.<%=FormNames.INPUT_MAX_ENTRIES%>.disabled = false;
   	    	document.inputForm.<%=FormNames.INPUT_MAX_NEWS_PAPER_ENTRIES%>.disabled = true;
   	    }
   	    if (document.inputForm.<%=FormNames.FEED_OUTPUT_TYPE%>[1].checked == true) {
   	       	document.inputForm.<%=FormNames.INPUT_MAX_ENTRIES%>.disabled = true;
   	    	document.inputForm.<%=FormNames.INPUT_MAX_NEWS_PAPER_ENTRIES%>.disabled = false;
   	    }
    }
</script>

<portlet:defineObjects />
<!-- <portlet:actionURL var="actionURL" />-->
<fmt:setBundle basename="edit" />

<c:catch var="exception">
	<jsp:useBean id="alertHandler"
		class="com.sun.portal.rssportlet.AlertHandler" scope="session" />
	<jsp:useBean id="settingsBean"
		class="com.sun.portal.rssportlet.SettingsBean" />
	<jsp:useBean id="settingsHandler"
		class="com.sun.portal.rssportlet.SettingsHandler">
		<jsp:setProperty name="settingsHandler" property="portletRequest"
			value="<%=renderRequest%>" />
		<jsp:setProperty name="settingsHandler" property="portletConfig"
			value="<%=portletConfig%>" />
	</jsp:useBean>

	<%// jsp doesn't seem to like me doing this in a jsp:setProperty
				settingsHandler.setSettingsBean(settingsBean);
				// so we can get them from a portlet action
				PortletSession psession = renderRequest.getPortletSession();
				psession.setAttribute("alertHandler", alertHandler,
						PortletSession.PORTLET_SCOPE);

				Map mapFeeds = (HashMap) psession.getAttribute("mapFeeds");
				renderRequest.setAttribute("mapFeeds", mapFeeds);

				String[] initFeeds = (String[]) psession
						.getAttribute("initFeeds");
				renderRequest.setAttribute("initFeeds", initFeeds);

				LinkedList feeds = (LinkedList) settingsBean.getFeeds();
				LinkedList titleList = (LinkedList) settingsBean.getTitleList();

				Map feedsMap = new HashMap();
				Iterator it = feeds.listIterator();
				while (it.hasNext()) {
					String feed = (String) it.next();
					feedsMap.put(feed, feed);
				}
				renderRequest.setAttribute("feedsMap", feedsMap);
				String feedOutputType = settingsBean.getSelectedFeedType();
				if (null == feedOutputType) {
					feedOutputType = (String) psession.getAttribute("feedType");
					System.out.println(" edit.jsp feedType" + feedOutputType);
				}
				renderRequest.setAttribute("feedType", feedOutputType);
				psession.setAttribute("feedType", feedOutputType);

				String showDesc = (String) psession.getAttribute("showDesc");
				renderRequest.setAttribute("showDesc", showDesc);
				%>

	<c:if test="${alertHandler.errorRendered}">
		<p>
		<div class="portlet-msg-error" style="color:white">
		${alertHandler.errorSummary}
		<div style="font-size: smaller">${alertHandler.errorDetail}</div>
		</div>
		</p>
	</c:if>

	<form name="inputForm" target="_self" method="POST"	action="<portlet:actionURL></portlet:actionURL>">
	<table background-color="gold" width="100%" border="0" style="align: center;background-color:gold">
		<!-- START "maximum entries" text box -->
		<c:choose>
			<c:when test="${feedType == 'river'}">
				<c:set var="checkRiver" value="CHECKED" />
				<c:set var="checkPaper" value="" />
				<c:set var="optionRiver" value="" />
				<c:set var="optionPaper" value="DISABLED" />
				<c:set var="feedRiver" value="${feedType}" />
				<c:set var="feedPaper" value="" />
			</c:when>
			<c:otherwise>
				<c:set var="checkRiver" value="" />
				<c:set var="checkPaper" value="CHECKED" />
				<c:set var="optionRiver" value="DISABLED" />
				<c:set var="optionPaper" value="" />
				<c:set var="feedRiver" value="" />
				<c:set var="feedPaper" value="${feedType}" />
			</c:otherwise>
		</c:choose>
		<tr>
			<td><INPUT TYPE=RADIO NAME="<%=FormNames.FEED_OUTPUT_TYPE%>" VALUE="river" ${checkRiver} onClick="setEntries();"/>
				<script language="javascript">setEntries();</script>
			</td>
			<td>Show <select ${optionRiver} name="<%=FormNames.INPUT_MAX_ENTRIES%>">
				<c:forEach var="i" begin="5" end="25" step="5">
					<c:choose>
						<c:when test="${i == settingsBean.maxEntries}">

							<option SELECTED="selecetd" value="${i}">
						</c:when>
						<c:otherwise>
							<option value="${i}">
						</c:otherwise>
					</c:choose>
					<c:if test="${i < 10}">
						<div class="portlet-font">&nbsp;${i}</div>
					</c:if>
					<c:if test="${i > 9}">
						<div class="portlet-font">${i}</div>
					</c:if>
					</option>
				</c:forEach>
				</select> <fmt:message key="maximum_entries" />
			</td>
		</tr>

		<tr>
			<td><INPUT TYPE=RADIO NAME="<%=FormNames.FEED_OUTPUT_TYPE%>"
				VALUE="newspaper" ${checkPaper} onClick="setEntries();"/>
				<script language="javascript">setEntries();</script>
			</td>
			<td>Show <select ${optionPaper} name="<%=FormNames.INPUT_MAX_NEWS_PAPER_ENTRIES%>">
				<c:forEach var="i" begin="1" end="5" step="1">
					<c:choose>
						<c:when test="${i == settingsBean.maxNewsPaperEntries}">
							<option SELECTED="selecetd" value="${i}">
						</c:when>
						<c:otherwise>
							<option value="${i}">
						</c:otherwise>
					</c:choose>
					<c:if test="${i < 10}">
						<div class="portlet-font">&nbsp;${i}</div>
					</c:if>
					<c:if test="${i > 9}">
						<div class="portlet-font">${i}</ div>
					</c:if>
					</option>
				</c:forEach>
				</select> <fmt:message key="maximum_newspaper_entries" />
			</td>
		</tr>

		<tr>
			<td colspan="2">&nbsp;</td>
		</tr>

		<c:choose>
			<c:when test="${settingsBean.showDesc == 'off'}">
				<tr>
					<td colspan="2"><input class="portlet-font" type="checkbox"	name="<%=FormNames.SHOW_DESC%>" /> I want to
					see the first few lines of each news article</td>
				</tr>
			</c:when>
			<c:otherwise>
				<tr>
					<td colspan="2"><input class="portlet-font" type="checkbox"	name="<%=FormNames.SHOW_DESC%>" CHECKED /> I
					want to see the first few lines of each news article</td>
				</tr>
			</c:otherwise>
		</c:choose>

		<tr>
			<td colspan="2">&nbsp;</td>
		</tr>
		<!-- START "finished", "custom" and "cancel" buttons -->
		<tr>
			<td align="center" colspan="2"><input class="uportal-button"
				name="<%=FormNames.SUBMIT_FINISH%>" type="submit"
				value="<fmt:message key="finished"/>" /> <input
				class="uportal-button" name="<%=FormNames.SUBMIT_CANCEL%>"
				type="submit" value="<fmt:message key="cancel"/>" /></td>
		</tr>
		<!-- END "finished" and "cancel" buttons -->
		<tr>
			<td colspan="2">&nbsp;</td>
		</tr>
		<!-- START feed checboxes -->
		<c:choose>
			<c:when test="${settingsBean.feedsSize != 0}">
				<c:forEach var="inFeeds" items="${initFeeds}"
					varStatus="statusInFeeds">
					<c:if test="${statusInFeeds.index == 0}">
						<tr>
							<td align="center" colspan="2">
							<p><b><i>To include a news source check the box</i></b></p>
							</td>
						</tr>
					</c:if>
					<tr>
						<c:set var="unCheckedFeeds" value="${fn:split(inFeeds, '%')}" />
						<c:forEach var="ucFeedTitle" items="${unCheckedFeeds}"
							varStatus="statusUnCheckedFeeds">
							<c:if test="${statusUnCheckedFeeds.index == 0}">
								<c:set var="ucTitle" value="${ucFeedTitle}" />
							</c:if>

							<c:if test="${statusUnCheckedFeeds.index == 1}">
								<c:set var="ucFeed" value="${ucFeedTitle}" />
							</c:if>
						</c:forEach>

						<c:choose>
							<c:when test="${statusInFeeds.index < 2}">
								<td><input class="portlet-font" type="checkbox"
									name="<%=FormNames.INPUT_FEEDS%>" value="${ucFeed}" CHECKED
									DISABLED /></td>
								<td colspan="1">${ucTitle}</td>
							</c:when>

							<c:otherwise>
								<c:choose>
									<c:when test="${empty feedsMap[ucFeed]}">
										<td><input class="portlet-font" type="checkbox"
											name="<%=FormNames.INPUT_FEEDS%>" value="${ucFeed}" /></td>
										<td colspan="1">${ucTitle}</td>
									</c:when>
									<c:otherwise>
										<td><input class="portlet-font" type="checkbox"
											name="<%=FormNames.INPUT_FEEDS%>" value="${ucFeed}" CHECKED />
										</td>
										<td colspan="1">${ucTitle}</td>
									</c:otherwise>
								</c:choose>
							</c:otherwise>
						</c:choose>
					</tr>
				</c:forEach>
			</c:when>
			<c:otherwise>
				<tr>
					<td colspan="2" align="left"><fmt:message key="no_feeds_list" /></td>
				</tr>
			</c:otherwise>
		</c:choose>

		<!-- END feed checboxes -->
		<!--   <tr><td><hr style="width: 100%; text-align: center"/></td></tr> -->
		<!-- START "finished", "custom" and "cancel" buttons -->
		<tr>
			<td colspan="2">&nbsp;</td>
		</tr>
		<tr>
			<td width="100%" align="center" colspan="2"><input
				class="uportal-button" name="<%=FormNames.SUBMIT_FINISH%>"
				type="submit" value="<fmt:message key="finished"/>" /> <input
				class="uportal-button" name="<%=FormNames.SUBMIT_CANCEL%>"
				type="submit" value="<fmt:message key="cancel"/>" /></td>
		</tr>
		<!-- END "finished" and "cancel" buttons -->
		<tr>
			<td colspan="2">&nbsp;</td>
		</tr>

		<tr>
			<td width="100%" colspan="2"><a target="feedback" href="/portal/Feedback">Suggest a news source</a>
			</td>
		</tr>
		<tr>
			<td width="100%" colspan="2"><b>You can also add any news source to the NewsReader on your MyCampus tab.</b></td>
		</tr>

	</table>
	</form>
</c:catch>

<c:if test="${exception != null}">
	<%Throwable t = (Throwable) pageContext.getAttribute("exception");
				StringWriter st = new StringWriter();
				PrintWriter pw = new PrintWriter(st);
				t.printStackTrace(pw);
				portletConfig.getPortletContext().log(
						"exception occured in edit.jsp", t);

				%>
	<div class="portlet-msg-error" style="color: #990000"><fmt:message
		key="exception" />
	<div style="font-size: smaller"><pre><%=t.getMessage()%></pre></div>
	</div>
	<!--
   <%= st %>
   -->
</c:if>
