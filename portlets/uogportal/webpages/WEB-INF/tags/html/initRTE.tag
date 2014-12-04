<%--
  -- initRTE
  -- 
  -- Initializes the rich text editing system. In addition to
  -- initializing rich text editing functions, this tag will write a
  -- JavaScript function (named by the "submitFunction" attribute) to save
  -- any changes to all rich text editors. This function *must* be called
  -- in the 'onsubmit' action of the containing form or else changes will
  -- be lost when the form is submitted. The submit function will return
  -- true if all changes are successfully saved, so a typical form.onsubmit
  -- attribute will return the value of the submit function, ie:
  --
  -- <initRTE submitFunction="submitForm"/>
  -- <form onsubmit="return submitForm()" ...>
  -- 
  -- @param submitFunction the name of the submit function (required)
  --%>
<%@ tag dynamic-attributes="attributes" isELIgnored="false" body-content="empty" %>
<%@ include file="include.jsp" %>
<%@ attribute name="submitFunction" required="true" %>
<script language="JavaScript" type="text/javascript" src="<html:scriptPath/>rte/html2xhtml.js"></script>
<script language="JavaScript" type="text/javascript" src="<html:scriptPath/>rte/richtext.js"></script>
<script language="JavaScript" type="text/javascript">
<!--
    function ${submitFunction}() {
      updateRTEs();
      //alert("${status.expression} = " + document.notificationForm.${status.expression}.value);
      //change the following line to true to submit form
      return true;
    }
    initRTE("<html:scriptPath/>rte/images/", "<html:scriptPath/>rte/", "", false);
//-->
</script>
