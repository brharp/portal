<?xml version='1.0' encoding='ISO-8859-1'?>

<xsl:stylesheet version='1.0' 
                xmlns:xsl='http://www.w3.org/1999/XSL/Transform'
                xmlns:date="http://exslt.org/dates-and-times"
                xmlns:regexp="http://exslt.org/regular-expressions"
                xmlns:ug='http://www.uoguelph.ca/ccs'
                xmlns:atom='http://www.w3.org/2005/Atom'
                xmlns:dc='http://purl.org/dc/elements/1.1/'
                xmlns:fn='http://www.w3.org/2005/02/xpath-functions'>
  
  <xsl:output method='xml' indent='yes'/>

  <xsl:param name='param_G'/>
  <xsl:param name='param_S'/>
  <xsl:param name='param_viw'/>
  <xsl:param name='param_xen'/>
  <xsl:param name='param_server'/>
  <xsl:param name='env_SERVER_NAME'/>
  <xsl:param name='env_SERVER_PORT'/>
  <xsl:param name='env_QUERY_STRING'/>

  <xsl:template match='iCalendar'>
    <feed xmlns='http://www.w3.org/2005/Atom'>
      <id></id>
      <updated><xsl:value-of select="date:date-time()"/></updated>
      <title>Agenda of <xsl:call-template name='author'/></title>
      <link rel='self' type='application/atom+xml'>
        <xsl:attribute name='href'>
          <xsl:call-template name='id'/>
        </xsl:attribute>
      </link>
      <link rel='alternate' type='text/html'>
        <xsl:attribute name='href'>
          <xsl:call-template name='self-link'/>
        </xsl:attribute>
      </link>
      <link rel='alternate' type='text/calendar'>
        <xsl:attribute name='href'>
          <xsl:call-template name='ical'/>
        </xsl:attribute>
      </link>
      <author>
        <name><xsl:call-template name='author'/></name>
        <email></email>
      </author>
      <generator version='1.0' uri='http://www.uoguelph.ca/ccs/atomcal'>AtomCal</generator>
      <xsl:apply-templates/>
    </feed>
  </xsl:template>

  <xsl:template match="prodid">
  </xsl:template>

  <xsl:template match="version">
  </xsl:template>

  <xsl:template match='vevent'>
    <entry xmlns="http://www.w3.org/2005/Atom">
      <id><xsl:value-of select='uid'/></id>
      <title type='text'><xsl:value-of select='date:format-date(dtstart,"yyyy-MM-dd")'/>: <xsl:value-of select='summary'/></title>
      <updated><xsl:value-of select='date:date-time()'/></updated>
      <published><xsl:value-of select='created'/></published>
      <category scheme='http://www.uoguelph.ca/schema/ccs/2006#kind'
                term='http://www.uoguelph.ca/schema/ccs/2006#event'>
      </category>
      <content type='text'><xsl:value-of select='date:format-date(dtstart,"EEE, MMM d, yyyy")'/><xsl:choose><xsl:when test="*/@value='DATE'"></xsl:when><xsl:otherwise> at <xsl:value-of select='date:format-date(dtstart,"h:mm a z")'/></xsl:otherwise></xsl:choose>: <xsl:value-of select="summary"/><xsl:if test="location"> (<xsl:value-of select='location'/>)</xsl:if></content>
      <link rel='alternate' type='text/html'>
        <xsl:attribute name='href'>
          <xsl:call-template name='event-link'/>
        </xsl:attribute>
      </link>
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
    </entry>
  </xsl:template>

  <xsl:template name='author'>
  <xsl:if test='$param_G'><xsl:value-of select='$param_G'/><xsl:text> </xsl:text></xsl:if><xsl:value-of select='$param_S'/>
  </xsl:template>

  <xsl:template name="self-link">https://schedule.uoguelph.ca/ocas-bin/ocas.fcgi?sub=web&amp;web=gbl&amp;viw=<xsl:value-of select='$param_viw'/>&amp;xen=z65gLuB4MIDZwXwzMAx%2fsA%3d%3d&amp;server=tbtzPzHLjyw%3d&amp;ver=2</xsl:template>

  <xsl:template name="event-link">https://schedule.uoguelph.ca/ocas-bin/ocas.fcgi?sub=web&amp;web=gbl&amp;type=0&amp;id=<xsl:value-of select='regexp:replace(string(x-oracle-event-guid),"E1\+(.*?)\+(.*?)\+.*","$1/$2/1","g")'/>&amp;load=on&amp;cs_initview=on&amp;date=<xsl:value-of select='date:year(dtstart)'/>/<xsl:value-of select='date:month-in-year(dtstart)'/>/<xsl:value-of select='date:day-in-month(dtstart)'/>&amp;viw=<xsl:value-of select='$param_viw'/>&amp;xen=z65gLuB4MIDZwXwzMAx%2fsA%3d%3d&amp;ver=2&amp;server=tbtzPzHLjyw%3d&amp;view=week</xsl:template>

  <xsl:template name='id'>http://<xsl:value-of select='$env_SERVER_NAME'/>:<xsl:value-of select='$env_SERVER_PORT'/>/atomcal/.atom?<xsl:value-of select='$env_QUERY_STRING'/></xsl:template>

  <xsl:template name='ical'>http://<xsl:value-of select='$env_SERVER_NAME'/>:<xsl:value-of select='$env_SERVER_PORT'/>/atomcal/export.ics?<xsl:value-of select='$env_QUERY_STRING'/></xsl:template>

</xsl:stylesheet>
