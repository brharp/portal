<?xml version='1.0' encoding='ISO-8859-1'?>

<!DOCTYPE stylesheet [
<!ENTITY rdf 'http://www.w3.org/1999/02/22-rdf-syntax-ns#'>
]>

<xsl:stylesheet version='1.0' 
  xmlns:rdf='http://www.w3.org/1999/02/22-rdf-syntax-ns#'
  xmlns='http://www.w3.org/2002/12/cal#'
  xmlns:xsl='http://www.w3.org/1999/XSL/Transform'>
  
  <xsl:output method='xml' indent='yes'/>

  <xsl:template match='iCalendar'>
    <rdf:RDF>
      <xsl:apply-templates select='vcalendar'/>
    </rdf:RDF>
  </xsl:template>

  <xsl:template match='vcalendar'>
    <Vcalendar>
      <prodid
          ><xsl:value-of select='prodid'/></prodid>
      <version
          ><xsl:value-of select='version'/></version>
      <xsl:apply-templates
          select='vevent'/>
    </Vcalendar>
  </xsl:template>

  <xsl:template match='vevent'>
    <component>
      <Vevent>
        <xsl:attribute name='about' namespace='&rdf;'
            >#<xsl:value-of select='x-oracle-eventinstance-guid'/></xsl:attribute>
        <uid
            ><xsl:value-of select='x-oracle-eventinstance-guid'/></uid>
        <dtstart
            ><xsl:value-of select='dtstart'/></dtstart>
        <dtend
            ><xsl:value-of select='dtend'/></dtend>
        <organizer>
          <xsl:attribute name='parseType' namespace='&rdf;'
              >Resource</xsl:attribute>
          <calAddress>
            <xsl:attribute name='resource' namespace='&rdf;'
                ><xsl:value-of select='organizer'/></xsl:attribute>
          </calAddress>
          <cn><xsl:value-of select='organizer/@cn'/></cn>
        </organizer>
        <transp
            ><xsl:value-of select='transp'/></transp>
        <summary
            ><xsl:value-of select='summary'/></summary>
        <status
            ><xsl:value-of select='status'/></status>
        <sequence
            ><xsl:attribute name='datatype' namespace='&rdf;'
                 >http://www.w3.org/2001/XMLSchema#integer</xsl:attribute
                 ><xsl:value-of select='sequence'/></sequence>
        <priority
            ><xsl:attribute name='datatype' namespace='&rdf;'
                 >http://www.w3.org/2001/XMLSchema#integer</xsl:attribute
                 ><xsl:value-of select='priority'/></priority>
        <class
            ><xsl:value-of select='class'/></class>
        <!--xsl:apply-templates select='attendee'/-->
      </Vevent>
    </component>
  </xsl:template>

  <xsl:template match='attendee'>
    <attendee>
      <xsl:attribute name='parseType' namespace='&rdf;'
          >Resource</xsl:attribute>
      <calAddress>
        <xsl:attribute name='resource' namespace='&rdf;'
            ><xsl:value-of select='text()'/></xsl:attribute>
      </calAddress>
      <cn
          ><xsl:value-of select='@cn'/></cn>
      <cutype
          ><xsl:value-of select='@cutype'/></cutype>
      <partstat
          ><xsl:value-of select='@partstat'/></partstat>
      <rsvp
          ><xsl:value-of select='@rsvp'/></rsvp>
    </attendee>
  </xsl:template>

</xsl:stylesheet>
