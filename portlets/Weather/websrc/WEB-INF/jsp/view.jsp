<%@ page isELIgnored ="false" %> 
<%@ page import="java.util.*"%>
<%@ page import="java.text.*"%>
<%@ page language="java"  contentType="text/html; charset=utf-8" %>

<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<fmt:setBundle basename="Resources"/>

<portlet:defineObjects/>
<c:if test="${errorMessage != null}">
	<div class="portlet-msg-error" style="text-align:center;">
		<fmt:message key="${errorMessage}"/>
	</div>
</c:if>


<%
	Format formatter = new SimpleDateFormat("E, MMM dd yyyy");
	String date = formatter.format(new Date());
	
	//create interval dates 
	formatter = new SimpleDateFormat("MM/dd/yy");
	String cDate = formatter.format(new Date());
	//begin date
	String bDate = cDate + " 02:00";
	//end date
	String eDate = cDate + " 14:00";

	pageContext.setAttribute("cDate", cDate);
	pageContext.setAttribute("bDate", bDate);
	pageContext.setAttribute("eDate", eDate);
%>
			<table align="center" style="width:100%;">
				<tr>
				<td colspan="2" style="text-align:center; width:100%;">
					<!-- B: Weather Summary -->
					<div style="color:white;font-size:smaller;font-weight:bold;width:100%;border-top:1px solid silver; background-color:#990000;">
						<c:choose>
							<c:when test="${(campus != '0' || empty campus) && empty cityName}">
								<c:choose>
									<c:when  test="${mapCampusName[campus] == 'Guelph'}" >
										University of Guelph
									</c:when>
									<c:when  test="${mapCampusName[campus] == 'GuelphHumber'}" >
										Guelph-Humber
									</c:when>
									<c:otherwise>
										${mapCampusName[campus]}
									</c:otherwise>
								</c:choose>
							</c:when>
							<c:otherwise>
								${weatherInfo.location}
							</c:otherwise>
						</c:choose>
					</div>
				</td>
				</tr>
			</table>

			<!-- border-bottom:1px solid silver style="width:85%;margin-bottom:10px;border-color=#ffcc00;border-style=solid;border-width=medium;-->
			<table class ="weather" align="center" style="width:100%;">
				<tr>
					<td rowspan="2" style="text-align:center;font-weight:bold;">
						<a target="_blank" 
							href="http://www.weather.com/outlook/travel/businesstraveler/hourbyhour/${weatherInfo.id}?
							from=search"><img src="<%= renderRequest.getContextPath() %>${weatherInfo.iconPath}" 
							border="0" alt="Hour by hour forecast" />
						</a>
						<div style="white-space:nowrap;">
					 		${weatherInfo.t}
					 	</div> 
					</td>
					<td style="text-align:center;vertical-align:middle;">
						<div class="portlet-font" style="font-size:larger;font-weight:bold;white-space:nowrap;">
							${weatherInfo.tmp}&deg;${weatherInfo.tempUnit}
						</div>
						
						<div class="portlet-font" style="font-size:smaller;">
							<c:choose>
	                            <c:when test="${weatherInfo.flik == 'N/A'}">
									<fmt:message key="label.flik"/> N/A
                             	</c:when>
	                            <c:otherwise>
									<fmt:message key="label.flik"/> ${weatherInfo.flik}&deg;${weatherInfo.tempUnit}
	                            </c:otherwise>
                            </c:choose>
						</div>
						
						<div class="portlet-font" style="font-size:smaller;">
							<fmt:message key="label.humidity"/> ${weatherInfo.hmid}% 
						</div>

						<div class="portlet-font" style="font-size:smaller;">
							<c:choose>
	                            <c:when test="${weatherInfo.windDirection == 'CALM'|| weatherInfo.windSpeed == 'CALM'}">
	                            	<fmt:message key="label.wind"/>:&nbsp;CALM
                             	</c:when>
	                            <c:otherwise>
									<fmt:message key="label.wind"/>:&nbsp;${weatherInfo.windDirection}&nbsp;${weatherInfo.windSpeed}&nbsp;${weatherInfo.speedUnit}
	                            </c:otherwise>
                            </c:choose>
						</div>
					
                                                <c:set var="localtime" value="${fn:substring(weatherInfo.lsup,0,14)}" />

                                                <c:choose>
                                                        <c:when test="${localtime > bDate && localtime < eDate}">
                                                                <c:set var="ppcp" value="0" />
                                                        </c:when>
                                                        <c:otherwise>
                                                                <c:set var="ppcp" value="1" />
                                                        </c:otherwise>
                                                </c:choose>


        <c:forEach var="day" items="${weatherInfo.days}">
        <c:forEach var="dayPart" items="${day.dayParts}" varStatus="status">

                <c:if test="${status.index == ppcp}">
                <div class="portlet-font" style="font-size:smaller;">
                        <fmt:message key="label.precip"/> ${dayPart.ppcp}%
                </div>
                </c:if>
        </c:forEach>
        </c:forEach>	
					</td>
				</tr>
			</table>

		<div class="portlet-section-subheader" style="text-align:center;"><span style="font-size:smaller;white-space:nowrap;"> As of <%= date%>
			<c:set var="datetime" value="${fn:split(weatherInfo.lsup, ' ')}" />
			<c:forEach var="time" items="${datetime}" varStatus="status">
				<c:if test="${status.index != 0}">
					${time}
				</c:if>
			</c:forEach>
			</span>
		</div>
 			
			<table align="center" width="100%">
			<tr>
				<td style="width:100%;text-align:center;vertical-align:middle;white-space:nowrap;">
							<a target="_blank" href="http://www.weather.com/?prod=xoap&par=<%= renderRequest.getPreferences().getValue("par", null) %>">
							<img align="center" src="<%= renderRequest.getContextPath() %>/TWClogo_64px.png" hspace="4" border="0" alt="The Weather Channel"></a>
				</td>
				<td style="width:100%;text-align:left;vertical-align:middle;white-space:nowrap;">
						<li>
							<a target="_blank" href="http://www.weather.com/outlook/travel/businesstraveler/hourbyhour/${weatherInfo.id}?from=search">
							Hour-by-hour
						</a> 
						</li>
						<li>
							<a  target="_blank" href="http://www.weather.com/outlook/travel/businesstraveler/tenday/${weatherInfo.id}?from=search_10day"/>
							10 day</a>
						</li>
				</td>
			</tr>
		</table>						



	<hr />
 
<a target="_blank" href="http://www.weatheroffice.ec.gc.ca/" >
	<span style="font-size:smaller;font-style:italic;font-weight:bold"></span>
	Environment Canada</a> warnings for

<a  target="_blank" href="http://weatheroffice.ec.gc.ca/warnings/son_e.html">
	<span style="font-size:smaller;font-style:italic;font-weight:bold"></span>
	Southern Ontario</a> and
	
<a  target="_blank" href="http://weatheroffice.ec.gc.ca/warnings/warnings_e.html">
	<span style="font-size:smaller;font-style:italic;font-weight:bold"></span>
	 Canada</a>	
	 
<span style="font-size:smaller;"></span>  
 
