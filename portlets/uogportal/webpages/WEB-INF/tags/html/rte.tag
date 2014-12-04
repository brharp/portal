<%--
  -- rte
  -- 
  -- Renders a rich text editor bound to a Spring command bean
  -- path. The containing form must commit any changes before submitting,
  -- see "initRTE.tag" for details.
  -- 
  -- If JavaScript is disabled a plain text area will be displayed.
  -- 
  -- @param path the name of the field to bind to (required)
  --%>
<%@ tag dynamic-attributes="attributes" isELIgnored="false" body-content="empty" %>
<%@ include file="include.jsp" %>
<%@ attribute name="path" required="true" %>
<spring:bind path="${path}">
  <script language="JavaScript" type="text/javascript">
  <!--
      //Usage: writeRichText(fieldname, html, width, height, buttons, readOnly)
      writeRichText('${status.expression}', '${status.value}', 350, 100, true, false);
  //-->
  </script>
  <noscript>
    <textarea name="${status.expression}">${status.value}</textarea>
  </noscript>    
</spring:bind>


