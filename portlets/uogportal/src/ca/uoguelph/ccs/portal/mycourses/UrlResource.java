/*
 * $Id: UrlResource.java,v 1.1 2006/05/01 13:37:14 brharp Exp $
 *
 * Copyright 2006 University of Guelph
 */

package ca.uoguelph.ccs.portal.mycourses;

/**
 * A simple concrete subclass of Resource, where the URL is stored as
 * a string.
 *
 * @author $Author: brharp $
 * @version $Revision: 1.1 $
 */
public class UrlResource extends Resource
{
    private String url;

    /**
     * Get the url property.
     */
    public String getUrl()
    {
        return url;
    }

    /**
     * Set the url property.
     */
    public void setUrl(String url)
    {
        this.url = url;
    }
}
