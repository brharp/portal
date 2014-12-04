<!-- B: Day Part -->
<table align="center" style="width:80%;">
<tr>
	<td colspan="2" style="text-align:center;">
	<!-- B: Weather Summary -->
	<div style="font-weight:bold;width:100%;border-top:1px solid silver; background-color:#eee;">
		<c:if test="${dayPart.type == 'd'}">
		<fmt:message key="label.day"/>
		</c:if>
		<c:if test="${dayPart.type == 'n'}">
		<fmt:message key="label.night"/>
		</c:if>
	</div>

<table align="center" style="border-bottom:1px solid silver;width:85%;margin-bottom:10px;">
<tr>
	<td style="text-align:center;font-weight:bold;">
	<img src="<%= renderRequest.getContextPath() %>${dayPart.iconPath}" border="1"/>
	<div style="white-space:nowrap;">
	 ${dayPart.description}
	 </div>
	</td>
	<td style="text-align:center;vertical-align:middle;">
		<div class="portlet-font" style="font-size:larger;font-weight:bold;white-space:nowrap;">
		<c:if test="${dayPart.type == 'd'}">
			<div style="font-size:smaller;"><fmt:message key="label.hi"/></div>${day.hi}
		</c:if>
		<c:if test="${dayPart.type == 'n'}">
			<div style="font-size:smaller;"><fmt:message key="label.low"/></div>${day.low}
		</c:if>
		&deg;
		${weatherInfo.tempUnit}
		</div>
		<div class="portlet-font" style="font-size:smaller;">
		<fmt:message key="label.precip"/> ${dayPart.ppcp}%
		</div>
	</td>
</tr>
</table>
	<!-- E: Weather Summary -->
	</td>
</tr>
<c:set var="propertyNameStyle" value="class='portlet-section-body' style='padding-right:5px;text-align:right'"/>
<c:set var="propertyValueStyle" value="class='portlet-section-body' style='padding-left:5px;text-align:left;white-space:nowrap;'"/>
<tr>
 	<td ${propertyNameStyle}><fmt:message key="label.humidity"/></td>
	<td ${propertyValueStyle}>${dayPart.humidity}%</td>
</tr>
<tr class="portlet-section-alternate">
	<td ${propertyNameStyle} ><fmt:message key="label.speed"/></td>
	<td ${propertyValueStyle}>${dayPart.wind.speed}${weatherInfo.speedUnit}</td>
</tr>
<tr>
	<td ${propertyNameStyle}><fmt:message key="label.direction"/></td>
	<td ${propertyValueStyle}>${dayPart.wind.degree}&deg;${dayPart.wind.direction}</td>
</tr>
<tr class="portlet-section-alternate">
	<td ${propertyNameStyle}><fmt:message key="label.gust"/></td>
	<td ${propertyValueStyle}>${dayPart.wind.gust}</td>
</tr>
</table>
<!-- E: Day Part -->

