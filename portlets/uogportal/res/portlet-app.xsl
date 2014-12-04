<?xml version="1.0" encoding="ISO-8859-1"?>

<!-- 
    Transforms portlet-web.xml into a portlet.xml file.

    See ../portlet-web.xml.

    Author: $Author: brharp $
    Version: $Version$
-->
<xsl:stylesheet version="1.0" 
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns="http://java.sun.com/xml/ns/portlet/portlet-app_1_0.xsd">

  <xsl:template match="/portlet-web-app">
    <portlet-app version="1.0">
      <xsl:apply-templates/>
    </portlet-app>
  </xsl:template>

  <xsl:template match="portlet-app">
    <xsl:copy-of select="*"/>
  </xsl:template>

  <xsl:template match="*"/>

</xsl:stylesheet>

