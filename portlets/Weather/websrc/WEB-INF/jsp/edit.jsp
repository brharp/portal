<%@ page import="javax.portlet.*"%>

<%@ page isELIgnored ="false" %> 

<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!-- 
this function sets the "maximum feed age" text box either disabled or
enabled depending on the value of the "disable maximum feed age" check box
-->
<script language="javascript">
    function setLocation() {
   	    if (document.inputForm.location[0].checked == true) {
   	       	document.inputForm.selectCampus.disabled = false;
   	    	document.inputForm.cityName.disabled = true;
   	    }
	    if (document.inputForm.location[1].checked == true) {
			document.inputForm.selectCampus.disabled = true;
			document.inputForm.cityName.disabled = false;
		}
    }
</script>

<fmt:setBundle basename="Resources"/>
<%@ page session="true"%>

<portlet:defineObjects/>

<c:if test="${errorMessage != null}">
	<div class="portlet-msg-error" style="text-align:center;">
		<fmt:message key="${errorMessage}"/> <%= (String)renderRequest.getPortletSession().getAttribute("cityName") %>. Please, select a city from the options below.
	</div>
</c:if>

<c:if test="${not empty campus}">
	<c:set var="checkCampus" value="CHECKED" />
	<c:set var="checkCity" value="" />
	<c:set var="disableCampus" value="" />
	<c:set var="disableCity" value="DISABLED" />
</c:if>

<c:if test="${not empty cityName}">
	<c:set var="checkCampus" value="" />
	<c:set var="checkCity" value="CHECKED" />
	<c:set var="disableCampus" value="DISABLED" />
	<c:set var="disableCity" value="" />
</c:if>

<form name="inputForm" method="post" action="<portlet:actionURL></portlet:actionURL>">
<table style="font-size=smaller;font-width=bold;width:100%;background-color:gold;align:center;">
	<tr>
		<td><input type="radio" name="location" value="campus" ${checkCampus} id="campus" onClick="setLocation();"/>&nbsp;Campus
			<script language="javascript">setLocation();</script>
		</td>
		
		<td style="font-size=smaller;font-width=bold;">
		<select ${disableCampus} name="selectCampus">
			<option value="0" <c:if test="${campus == '0'}"> selected="true"</c:if>><fmt:message key="label.campus0"/></option>
			<option value="1" <c:if test="${campus == '1'}"> selected="true"</c:if>><fmt:message key="label.campus1"/></option>
			<option value="2" <c:if test="${campus == '2'}"> selected="true"</c:if>><fmt:message key="label.campus2"/></option>
			<option value="3" <c:if test="${campus == '3'}"> selected="true"</c:if>><fmt:message key="label.campus3"/></option>
			<option value="4" <c:if test="${campus == '4'}"> selected="true"</c:if>><fmt:message key="label.campus4"/></option>
			<option value="5" <c:if test="${campus == '5'}"> selected="true"</c:if>><fmt:message key="label.campus5"/></option>
			<option value="6" <c:if test="${campus == '6'}"> selected="true"</c:if>><fmt:message key="label.campus6"/></option>
			<option value="7" <c:if test="${campus == '7'}"> selected="true"</c:if>><fmt:message key="label.campus7"/></option>
			<option value="8" <c:if test="${campus == '8'}"> selected="true"</c:if>><fmt:message key="label.campus8"/></option>
			<option value="9" <c:if test="${campus == '9'}"> selected="true"</c:if>><fmt:message key="label.campus9"/></option>
			<option value="10" <c:if test="${campus == '10'}"> selected="true"</c:if>><fmt:message key="label.campus10"/></option>
			<option value="11" <c:if test="${campus == '11'}"> selected="true"</c:if>><fmt:message key="label.campus11"/></option>
			<option value="12" <c:if test="${campus == '12'}"> selected="true"</c:if>><fmt:message key="label.campus12"/></option>
			<option value="13" <c:if test="${campus == '13'}"> selected="true"</c:if>><fmt:message key="label.campus13"/></option>
			<option value="14" <c:if test="${campus == '14'}"> selected="true"</c:if>><fmt:message key="label.campus14"/></option>
			<option value="15" <c:if test="${campus == '15'}"> selected="true"</c:if>><fmt:message key="label.campus15"/></option>
			<option value="16" <c:if test="${campus == '16'}"> selected="true"</c:if>><fmt:message key="label.campus16"/></option>
			<option value="17" <c:if test="${campus == '17'}"> selected="true"</c:if>><fmt:message key="label.campus17"/></option>
			<option value="18" <c:if test="${campus == '18'}"> selected="true"</c:if>><fmt:message key="label.campus18"/></option>
			<option value="19" <c:if test="${campus == '19'}"> selected="true"</c:if>><fmt:message key="label.campus19"/></option>
		</select>
		</td>
	</tr>

	<tr>
		<td><input type="radio" name="location" value="city" ${checkCity} id="city" onClick="setLocation();"/>&nbsp;City
			<script language="javascript">setLocation();</script>
		</td>
		<td>
			<input id="cityNameTxt" type="text" name="cityName" value="${cityName}" ${disableCity}/>
			<c:choose>
				<c:when test="${locations !=null && (campus == '0' || campus == null || empty campus)}">
					<div id="selectBox">
					<select id="cityNameSelect" name="cityName">
						<c:forEach var="loc" items="${locations}">
						<option value="${loc.name}">${loc.name}</option>
						</c:forEach>
					</select>
					<a href="#" onclick="document.getElementById('cityNameTxt').style.display ='block';
								document.getElementById('cityNameTxt').name = 'cityName';
								document.getElementById('cityNameSelect').name = 'ignore';
								document.getElementById('selectBox').style.display = 'none';
								return false;"><fmt:message key="label.clear"/></a>
								
					</div>
					<script type="text/javascript">
						document.getElementById("cityNameTxt").style.display = 'none';
						document.getElementById("cityNameTxt").name = 'ignore';
					</script>
				</c:when>
			</c:choose>
		</td>
	</tr>
	<tr><td>&nbsp;</td></tr>
	<tr>
		<td>Units</td>
		<td>
			<input type="radio" name="unit" value="m" id="metric"
				<c:if test="${unit =='m'}"> checked="true" </c:if> />
				<label for="metric"><!--<fmt:message key="label.kms"/>-->&deg;C</label>
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				<input type="radio" name="unit" value="s" id="standard" 
				<c:if test="${unit =='s'}"> checked="true" </c:if> />
				<label for="standard"><!--<fmt:message key="label.fps"/>-->&deg;F</label>
		</td>
	</tr>
	<tr><td>&nbsp;</td></tr>
	<tr>
		<td colspan="2" style="text-align:center;align:center;width=100%;">
			<input class="uportal-button" name="save" type="submit" value="Save and Exit"/>
			<input class="uportal-button" name="cancel" type="submit" value="Cancel"/>
		</td>
	</tr>
</table>
</form>
