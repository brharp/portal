/*
 * $Id: Resource.java,v 1.3 2006/05/01 13:37:14 brharp Exp $
 *
 * Copyright 2006 University of Guelph
 */

package ca.uoguelph.ccs.portal.mycourses;

/**
 * Course resource data.
 *
 * @author $Author: brharp $
 * @version $Revision: 1.3 $
 */
public abstract class Resource
{
    private Integer key;
    private String  course;
    private String  description;
    private Boolean published;

    /**
     * Get the key property.
     */
    public Integer getKey()
    {
        return key;
    }

    /**
     * Set the key property.
     */
    public void setKey(Integer key)
    {
        this.key = key;
    }

    /**
     * Get the course property.
     */
    public String getCourse()
    {
        return course;
    }

    /**
     * Set the course property.
     */
    public void setCourse(String course)
    {
        this.course = course;
    }

    /**
     * Get the description property.
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * Set the description property.
     */
    public void setDescription(String description)
    {
        this.description = description;
    }

    /**
     * Get the published property.
     */
    public Boolean getPublished()
    {
        return published;
    }

    /**
     * Set the published property.
     */
    public void setPublished(Boolean published)
    {
        this.published = published;
    }

    /**
     * Get the url for this resource.
     */
    public abstract String getUrl();
}
