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
  
  "Portions Copyrighted 2006 Lalit Jairath, Peter McCaskell"
 --%>

<%@ page import="javax.portlet.PortletSession"%>
<%@ page import="javax.portlet.*"%>
<%@ page import="java.io.StringWriter"%>
<%@ page import="java.io.PrintWriter"%>
<%@ page import="java.util.*"%>
<%@ page import="java.lang.*"%>
<%@ page import="com.sun.portal.rssportlet.FormNames"%>
<%@ page import="javax.portlet.RenderRequest"%>

<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<%@ page session="true"%>

<portlet:defineObjects />
<!--<portlet:actionURL var="actionURL" />-->
<fmt:setBundle basename="view" />

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
	<jsp:useBean id="feedBean" class="com.sun.portal.rssportlet.FeedBean" />
	<jsp:useBean id="feedHandler"
		class="com.sun.portal.rssportlet.FeedHandler" />

	<% 
	//long stime = System.currentTimeMillis();
	//System.out.println("****startTime****" + stime);
	//pageContext.setAttribute("stime", String.valueOf(stime));
 	
	settingsHandler.setSettingsBean(settingsBean);
 	// so we can get them from a portlet action
	PortletSession psession = renderRequest.getPortletSession();
	psession.setAttribute("alertHandler", alertHandler, PortletSession.PORTLET_SCOPE);
	//psession.setAttribute("startTime", startTime);

	LinkedList feeds = settingsBean.getFeeds();
	ListIterator it = feeds.listIterator();
	Map feedsMap = new HashMap();
	int pos = 0;
	while (it.hasNext()) {
		String feed = (String) it.next();
		//System.out.println("****view feed****" + feed);
		feedsMap.put(feed, String.valueOf(pos));
		pos++;
    }
	request.setAttribute("feedsMap", feedsMap);

	String feedOutputType = settingsBean.getSelectedFeedType();
	if (null == feedOutputType) {
    	feedOutputType = (String)psession.getAttribute("feedType");
		System.out.println(" edit.jsp feedType" +  feedOutputType);
	}
	request.setAttribute("feedType", feedOutputType);
	//psession.setAttribute("feedType", feedOutputType);
	//String showDesc = s 
	//	(String) psession.getAttribute("showDesc");
	//renderRequest.setAttribute("showDesc", showDesc);
    %>

<!--
	<table border="0" style="align: center">
		<tr>
			<td align="left">
			<p><b><i>To add/delete news content click on the pencil icon above</i></b></p>
			</td>
		</tr>
	</table>
 -->
	<!-- dont show feed selector if there is only one feed configured -->
	<c:if test="${settingsBean.feedsSize > 1}">
	<form name="goForm" action="<portlet:actionURL></portlet:actionURL>"
		method="post">
		<!-- BEGIN select feed drop down list -->
		<table>
			<tr>
				<td style="vertical-align: top">
				<select
					name="<%=FormNames.INPUT_SELECT_FEED%>" onChange="goForm.submit()">
					<c:forEach var="feed" items="${settingsBean.feeds}"
						varStatus="statusFeed">
						<c:forEach var="title" items="${settingsBean.titleList}"
							varStatus="statusTitle">

							<c:if test="${statusFeed.index == statusTitle.index}">
								<c:choose>
									<c:when test="${feed == settingsBean.selectedFeed}">
										<option selected value="${feed}">
										<div class="portlet-font">${title}</div>
										</option>
									</c:when>
									<c:otherwise>
										<option value="${feed}">
										<div class="portlet-font">${title}</div>
										</option>
									</c:otherwise>
								</c:choose>
							</c:if>
						</c:forEach>
					</c:forEach>
				</select></td>
				<td style="vertical-align: top"><input class="uportal-button"
					name="<%=FormNames.SUBMIT_GO%>" type="submit"
					value="<fmt:message key="go"/>"></td>
			</tr>
		</table>
		</form>
	<!-- END select feed drop down list -->
	</c:if>


	<c:choose>
		<c:when test="${settingsBean.selectedFeed != null}">
			<% 
                // prepare feed bean
                // jsp doesn't seem to like me doing this in a jsp:setProperty
                feedHandler.setSettingsBean(settingsBean);
                feedHandler.setFeedBean(feedBean);
            %>

			<!-- BEGIN feed render -->
			<table style="border-style: none; padding: 4; width: 100%">
				<!-- draw feed header -->
				<tr>
					<td>
					<table
						style="border-style: solid; border-width: thin; padding: 8px">
						<tr>
						<td>
						<div class="portlet-font">
							<c:if test="${settingsBean.selectedFeed == 'http://myportico.uoguelph.ca/'}">
								<img border="0" alt="${feedBean.feed.title}" src="${feedBean.feed.image.url}" />
							</c:if> 
							<c:if test="${settingsBean.selectedFeed != 'http://myportico.uoguelph.ca/' && not empty feedBean.feed.image.url}">
								<c:if test="${not empty feedBean.feed.image.link}">
									<a href="${feedBean.feed.image.link}" target="${settingsBean.windowTarget}"> 
										<img border="0" alt="${feedBean.feed.image.link}" 
										src="<%= renderRequest.getContextPath() %>/uofglogo.gif" />
									</a>
								</c:if>
								<c:if test="${ empty feedBean.feed.image.link}">
									<a href="${feedBean.feed.image.link}" target="${settingsBean.windowTarget}"> 
										<img border="0"	alt="${feedBean.feed.image.link}" 
										src="<%= renderRequest.getContextPath() %>/uofglogo.gif" />
									</a>
								</c:if>
							</c:if> 
							<c:if test="${settingsBean.selectedFeed != 'http://myportico.uoguelph.ca/' && empty feedBean.feed.image.url}">
								<c:if test="${not empty feedBean.feed.image.link}">
									<a href="${feedBean.feed.image.link}" target="${settingsBean.windowTarget}"> 
										<img border="0"	alt="${feedBean.feed.image.link}" src="<%= renderRequest.getContextPath() %>/myporticosmall.gif" />
									</a>
								</c:if>
								<c:if test="${empty feedBean.feed.image.link}">
									<a href="${feedBean.feed.link}"	target="${settingsBean.windowTarget}">
										<img border="0"	alt="${feedBean.feed.title}" src="<%= renderRequest.getContextPath() %>/myporticosmall.gif" />
									</a>
								</c:if>
							</c:if> 
							<br />
							<span style="font-weight: bolder">${feedBean.feed.title}</span>
							<!-- 
							<c:if test="${feedBean.feed.publishedDate != null}">                                
                                - <span style="font-style: italic"><fmt:formatDate value="${feedBean.feed.publishedDate}" /></span>
							</c:if>
							 --> 
							<c:if test="${feedBean.feed.description != null}">
								; ${feedBean.feedDescription}                                                 
							</c:if>
						</div>
						</td>
						</tr>
					</table>
					<!-- START draw feed entries --> 
					<c:set var="entries" value="${feedBean.feedEntries}" /> 
					<c:set var="delim" value="%" />
					<c:set var="testTitle" value="" /> 
					
					<c:forEach var="entry"	items="${entries}" varStatus="statusFeedEntry">
						<c:set var="urlAndFeedEntryTitle" value="${fn:split(entry.title, delim)}" />
						<c:forEach var="compTitle" items="${urlAndFeedEntryTitle}" varStatus="status">
							<c:if test="${status.index == 1}">
								<c:set var="url" value="${compTitle}" />
							</c:if>
							<c:if test="${status.index == 2}">
								<c:set var="feedTitle" value="${compTitle}" />
							</c:if>
							<c:if test="${status.index == 3}">
								<c:set var="entryTitle" value="${compTitle}" />
							</c:if>
							<c:if test="${status.index == 0}">
								<c:set var="entrySize" value="${compTitle}" />
							</c:if>
						</c:forEach>
						<% 
							String srcURL = (String)pageContext.getAttribute("url");
							String index = (String)feedsMap.get(srcURL);
							pageContext.setAttribute("index", index);
						%>
						<tr>
						<td>
						<div class="portlet-font">
							<c:if test="${statusFeedEntry.index == 0 && 
								(settingsBean.selectedFeed != 'http://myportico.uoguelph.ca/' ||
								feedType == 'newspaper')}">
								<img src="<%= renderRequest.getContextPath() %>/article.png" />
								<a href="<portlet:actionURL>
									<portlet:param name="<%=FormNames.SOURCE_SELECT_FEED%>" value="${index}" />
									<portlet:param name="<%=FormNames.INPUT_SELECT_FEED%>" value="${index}" />
								</portlet:actionURL>">${feedTitle}</a>
								<br />
							</c:if>
							
							<c:if test="${feedType == 'newspaper'}">
								<c:if test="${statusFeedEntry.index > 0}">
									<c:if test="${ testTitle != feedTitle}">
										<br />
										<img src="<%= renderRequest.getContextPath() %>/article.png" />
										<a href="<portlet:actionURL>
											<portlet:param name="<%=FormNames.SOURCE_SELECT_FEED%>" value="${index}" />
											<portlet:param name="<%=FormNames.INPUT_SELECT_FEED%>" value="${index}" />
										</portlet:actionURL>">${feedTitle}</a>
										<br />										
									</c:if>
								</c:if>
								<c:set var="testTitle" value="${feedTitle}" />
							</c:if>
	
							<c:if test="${feedType == 'river' && settingsBean.selectedFeed == 'http://myportico.uoguelph.ca/'}">
								<img src="<%= renderRequest.getContextPath() %>/article.png" />
								<a href="<portlet:actionURL>
									<portlet:param name="<%=FormNames.SOURCE_SELECT_FEED%>" value="${index}" />
									<portlet:param name="<%=FormNames.INPUT_SELECT_FEED%>" value="${index}" />
								</portlet:actionURL>">${feedTitle}</a>
							</c:if>
														
							<a href="${entry.link}"	target="${settingsBean.windowTarget}"> ${entryTitle}</a> 
	
							<c:if test="${entry.publishedDate != null}"> - 
								<span style="font-style: italic;font-size:smaller"> 
	                               	<fmt:formatDate	value="${entry.publishedDate}" />
	                            <c:if test="${feedType == 'river'}">
									<c:if test="${entrySize > 3}">
										<c:if test="${settingsBean.selectedFeed == 'http://myportico.uoguelph.ca/'}">
											<a href="<portlet:actionURL>
												<portlet:param name="<%=FormNames.SOURCE_SELECT_FEED%>" value="${index}" />
												<portlet:param name="<%=FormNames.INPUT_SELECT_FEED%>" value="${index}" />
											</portlet:actionURL>">..more</a>
										</c:if>
									</c:if>
								</c:if>
                               </span>
							</c:if> 
							<br />
							<c:if test="${settingsBean.showDesc == 'on'}">
								<div>
									${entry.description.value}
								</div>
							</c:if>
						</div>
						</td>
						</tr>
					</c:forEach> 
					<!-- END draw feed entries --> 
					<c:if test="${feedBean.feedEntriesSize == 0}">
						<tr>
							<td>
							<div class="portlet-msg-alert"><c:choose>
								<c:when test="${settingsBean.disableMaxAge}">
									<fmt:message key="no_entries" />
								</c:when>
								<c:otherwise>
									<fmt:message key="no_entries_since">
										<fmt:param value="${settingsBean.maxAge}" />
									</fmt:message>
								</c:otherwise>
							</c:choose></div>
							</td>
						</tr>
					</c:if>
			</table>
		</c:when>
		<c:otherwise>
			<fmt:message key="no_feeds" />
		</c:otherwise>
	</c:choose>
	<!-- END feed render -->
</c:catch>

<c:if test="${exception != null}">
	<% 
        Throwable t = (Throwable)pageContext.getAttribute("exception");
        StringWriter st = new StringWriter();
        PrintWriter pw = new PrintWriter(st);
        t.printStackTrace(pw);
        portletConfig.getPortletContext().log("exception occured in view.jsp", t);
    %>
	<div class="portlet-msg-error" style="color: white"><c:choose>
		<c:when test="${settingsBean.selectedFeed != null}">
			<fmt:message key="exception_url">
				<fmt:param value="${settingsBean.selectedFeed}" />
			</fmt:message>
		</c:when>
		<c:otherwise>
			<fmt:message key="exception" />
		</c:otherwise>
	</c:choose>
	<div style="font-size: smaller"><pre><%= t.getMessage() %></pre></div>
	</div>
	<!--
   <%= st %>
   -->
   </c:if>
   
   <div class="myinbox-checkmail"><a href="<portlet:actionURL></portlet:actionURL>">Refresh</a></div>
   
<%--
	PortletSession psession = renderRequest.getPortletSession();

	String stime = (String)pageContext.getAttribute("stime");
	long s2time = Long.parseLong(stime);
	long etime = System.currentTimeMillis();

	long tcounter = 0;
	if (null == psession.getAttribute("tcounter")) {
		tcounter = 1;
	} else {
		tcounter = ((Long)psession.getAttribute("tcounter")).longValue() + (1);
	}
	
	long time = 0;
	if (null == psession.getAttribute("time")) {
		time = etime-s2time;	
	} else {
		time = ((Long)psession.getAttribute("time")).longValue() + (etime-s2time);
	}
	
	out.println("<br />");
	out.println("<b>Benchmark:</b>");
	out.println("<br />");
	out.println("On average took ");
	long bench = time/tcounter;
	out.println(bench);
	out.println("mSec. to process this portlet for the session.");
	
	psession.setAttribute("tcounter", new Long(tcounter));
	psession.setAttribute("time", new Long(time));
	out.println("<br />");
	out.println("Took ");
	out.println(etime-s2time);
	out.println("mSec. to process this portlet for this request.");
	//System.out.println("elapsed time:" + (etime-s2time));
	//System.out.println("Total time:" + time);
	//System.out.println("session counter:" + tcounter);
--%>
