<?xml version='1.0' encoding='ISO-8859-1'?>

<xsl:stylesheet version='1.0' 
                xmlns:xsl='http://www.w3.org/1999/XSL/Transform'
                xmlns:date="http://exslt.org/dates-and-times"
                xmlns:regexp="http://exslt.org/regular-expressions"
                xmlns:ug='http://www.uoguelph.ca/ccs/elements/2006/'
                xmlns:dc='http://purl.org/dc/elements/1.1/'>
  
  <xsl:output method='xml' indent='yes'/>

  <xsl:param name='param_G'/>
  <xsl:param name='param_S'/>
  <xsl:param name='param_viw'/>
  <xsl:param name='param_xen'/>
  <xsl:param name='param_server'/>

  <xsl:template match='iCalendar'>
    <rss version="2.0">
      <channel>
        <title>Agenda of <xsl:if test='$param_G'><xsl:value-of select='$param_G'/><xsl:text> </xsl:text></xsl:if><xsl:value-of select='$param_S'/></title>
        <description>Agenda of <xsl:if test='$param_G'><xsl:value-of select='$param_G'/><xsl:text> </xsl:text></xsl:if><xsl:value-of select='$param_S'/></description>
        <link><xsl:call-template name="self-link"/></link>
        <language>en-ca</language>
        <generator>AtomCal</generator>
        <xsl:apply-templates/>
      </channel>
    </rss>
  </xsl:template>

  <xsl:template match="prodid">
  </xsl:template>

  <xsl:template match="version">
  </xsl:template>

  <xsl:template match='vevent'>
    <item>
      <title><xsl:value-of select='date:format-date(dtstart,"yyyy-MM-dd")'/>: <xsl:value-of select='summary'/></title>
      <description><xsl:call-template name='event'/></description>
      <author><xsl:value-of select='organizer/@cn'/></author>
      <link><xsl:call-template name="event-link"/></link>
      <guid><xsl:value-of select="uid"/></guid>
      <!-- Omit pubDate. The true pubDate may prevent some readers from displaying current events. -->
      <!--<pubDate><xsl:value-of select='date:format-date(created,"EEE, dd MMM yyyy hh:mm:ss z")'/></pubDate>-->
      <dc:created><xsl:value-of select='created'/></dc:created>
      <!-- DC:Date is included for backwards compatability. See ug:when. -->
      <dc:date><xsl:value-of select='dtstart'/></dc:date>
      <ug:when startTime='{dtstart}' endTime='{dtend}'>
        <xsl:if test='dtstart/@value="DATE"'>
          <xsl:attribute name="value">
            <xsl:value-of select='dtstart/@value'/>
          </xsl:attribute>
        </xsl:if>
      </ug:when>
      <xsl:if test='location'>
        <ug:where><xsl:value-of select='location'/></ug:where>
      </xsl:if>
      <ug:status><xsl:value-of select='status'/></ug:status>
      <ug:priority><xsl:value-of select='priority'/></ug:priority>
      <ug:summary><xsl:value-of select='summary'/></ug:summary>
    </item>
  </xsl:template>

  <xsl:template name='event'>
    <xsl:value-of select='date:format-date(dtstart,"EEE, MMM d, yyyy")'/>
    <xsl:choose><xsl:when test="*/@value='DATE'"></xsl:when><xsl:otherwise> at <xsl:value-of select='date:format-date(dtstart,"h:mm a z")'/></xsl:otherwise></xsl:choose>: <xsl:value-of select="summary"/><xsl:if test="location"> (<xsl:value-of select='location'/>)</xsl:if>
  </xsl:template>

  <xsl:template name="self-link">https://schedule.uoguelph.ca/ocas-bin/ocas.fcgi?sub=web&amp;web=gbl&amp;viw=<xsl:value-of select='$param_viw'/>&amp;xen=<xsl:value-of select='$param_xen'/>&amp;server=<xsl:value-of select='$param_server'/>&amp;ver=2</xsl:template>

  <xsl:template name="event-link">https://schedule.uoguelph.ca/ocas-bin/ocas.fcgi?sub=web&amp;web=gbl&amp;type=0&amp;id=<xsl:value-of select='regexp:replace(string(x-oracle-event-guid),"E1\+(.*?)\+(.*?)\+.*","$1/$2/1","g")'/>&amp;load=on&amp;cs_initview=on&amp;date=<xsl:value-of select='date:year(dtstart)'/>/<xsl:value-of select='date:month-in-year(dtstart)'/>/<xsl:value-of select='date:day-in-month(dtstart)'/>&amp;viw=<xsl:value-of select='$param_viw'/>&amp;xen=<xsl:value-of select='$param_xen'/>&amp;ver=2&amp;server=<xsl:value-of select='$param_server'/>&amp;view=week</xsl:template>

</xsl:stylesheet>
