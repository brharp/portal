<%@ page language="java"  contentType="text/html; charset=utf-8" %>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet"%>
<portlet:defineObjects/>
<div>
<p>
<a href="http://www.aurigalogic.com/"><img src="<%= renderRequest.getContextPath() %>/auriga_logo.gif" border="0" alt="Auriga Logic Pvt. Ltd."/></a>
<div class="portlet-section-header">Auriga Weather Portlet (<%= com.aurigalogic.weatherPortlet.WeatherService.getVersion() %>)</div>
	The Auriga Weather Portlet is a JSR 168 compliant portlet that provides weather information from weather.com&reg;.
	<br />
	<br />
	For more info, visit <a href="http://www.aurigalogic.com/portlets">http://www.aurigalogic.com/portlets</a>.
	<br />Please send comments, feedback or bugs to <a href="mailto:sanvekar@REMOVE-THISaurigalogic.com">Sonal Anvekar</a>
</p>
<p>
	<div class="portlet-section-header">Lincensed Under</div>
	<a href="http://www.opensource.org/licenses/gpl-license.php">GPL (General Public License)</a>
</p>
<p>
	<div class="portlet-section-header">Acknowledgements</div>
	
	<p>
	<div class="portlet-section-subheader">Apache</div>
	This product includes software developed by the Apache Software Foundation (<a href="http://www.apache.org/">http://www.apache.org</a>).</p>

	<p>
	<div class="portlet-section-subheader">EH Cache</div>
	<a href="http://ehcache.sourceforge.net">http://ehcache.sourceforge.net<a/><p>
</p>


	
</div>
