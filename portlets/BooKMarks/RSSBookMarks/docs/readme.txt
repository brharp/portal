RSSBookMarks Portlet - October 31, 2005 Lalit Jairath, Barbara Edwards - This portlet has been modified from the SourceForge.net Portlet Open Trading Site RSS Portlet, last updated December 2, 2003.

Changes:

html.xsl: 

Changed value of 'exclude-result-prefixes' attribute, which caused a validation exception error, to "rdf dc dcterms rss content annotate admin image cc reqv" 

RSS20.xsl:

Changed so that links open in a new browser window

RSSPortlet.java:

In doView, the selectable RSS feed is displayed by a title, rather than the URL. This makes the display more user-friendly. The title is appended to the URL string, and is delimited by '='.
The first item in the RSS feed list is automatically displayed.

In doEdit, an extra input text box has been added in order for the user to specify a title for the RSS feed. A button has been added to supply the user with a more evident method of returning to the doView mode.

In doHelp, a button has been added to supply the user with a more evident method of returning to the doView mode. The help text has also been modified to give the user basic instructions on how to use the portlet.