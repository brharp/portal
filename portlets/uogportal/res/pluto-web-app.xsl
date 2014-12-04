<?xml version="1.0" encoding="ISO-8859-1"?>

<!-- 
    Transforms portlet-web.xml into a web.xml file for the Pluto 
    portlet container. Requires a 'web-app-name' parameter to set
    the portlet-guid properly.

    See ../portlet-web.xml.

    Author: $Author: brharp $
    Version: $Version$
-->
<xsl:stylesheet version="1.0" 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:param name="web-app-name"/>

  <xsl:output method="xml"
              doctype-public="-//Sun Microsystems, Inc.//DTD Web Application 2.3//EN"
              doctype-system="http://java.sun.com/dtd/web-app_2_3.dtd"/>

  <xsl:template match="/portlet-web-app">
    <web-app>
      <xsl:attribute name="version">
      	<xsl:value-of select="web-app/@version"/>
      </xsl:attribute>
      <xsl:apply-templates/>
    </web-app>
  </xsl:template>

  <xsl:template match="web-app">
    <xsl:copy-of select="*"/>
  </xsl:template>

  <xsl:template match="portlet-app">
    <xsl:for-each select="portlet">
      <xsl:variable name="portlet-name">
        <xsl:value-of select="portlet-name/text()"/>
      </xsl:variable>
      <xsl:variable name="portlet-class">
        <xsl:value-of select="portlet-class/text()"/>
      </xsl:variable>
      <servlet>
        <servlet-name><xsl:value-of select="$portlet-name"/></servlet-name>
        <servlet-class>org.apache.pluto.core.PortletServlet</servlet-class>
        <display-name><xsl:value-of select="$portlet-name"/> Wrapper</display-name>
        <description>Automatically generated portlet wrapper</description>
        <init-param>
          <param-name>portlet-class</param-name>
          <param-value><xsl:value-of select="$portlet-class"/></param-value>
        </init-param>
        <init-param>
          <param-name>portlet-guid</param-name>
          <param-value><xsl:value-of select="$web-app-name"/>.<xsl:value-of select="$portlet-name"/></param-value>
        </init-param>
      </servlet>
      <servlet-mapping>
        <servlet-name><xsl:value-of select="$portlet-name"/></servlet-name> 
        <url-pattern>/<xsl:value-of select="$portlet-name"/>/*</url-pattern> 
      </servlet-mapping>
    </xsl:for-each>
  </xsl:template>

  <xsl:template match="*"/>

</xsl:stylesheet>

