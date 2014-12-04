<?xml version='1.0' encoding='ISO-8859-1'?>

<xsl:stylesheet version='1.0' 
  xmlns:xsl='http://www.w3.org/1999/XSL/Transform'
  >
  
  <xsl:template match='/'>
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match='module'>
    <html>
      <head>
        <title><xsl:value-of select='title'/></title>
      </head>
      <body>
        <p>
          Blah blah blah, copy the URL.
        </p>
        <table>
          <tbody>
            <xsl:apply-templates/>
          </tbody>
        </table>
      </body>
    </html>
  </xsl:template>

  <xsl:template match="title">
    <tr><th>Title</th><td><xsl:value-of select='text()'/></td></tr>
  </xsl:template>

  <xsl:template match="author">
    <tr><th>Author</th><td><xsl:value-of select='text()'/></td></tr>
  </xsl:template>

  <xsl:template match="description">
    <tr><th>Description</th><td><xsl:value-of select='text()'/></td></tr>
  </xsl:template>

  <xsl:template match="*">
  </xsl:template>

</xsl:stylesheet>
