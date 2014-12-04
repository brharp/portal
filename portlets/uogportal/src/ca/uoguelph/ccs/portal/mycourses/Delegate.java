/*
 * $Id: Delegate.java,v 1.1 2006/04/28 14:04:19 brharp Exp $
 *
 * Copyright 2006 University of Guelph
 */

package ca.uoguelph.ccs.portal.mycourses;

/**
 * A delegate may make act on behalf of the instructor.
 *
 * @author $Author: brharp $
 * @version $Revision: 1.1 $
 */
public class Delegate
{
    private String course;
    private String userId;

    public String getCourse()
    {
        return course;
    }

    public void setCourse(String course)
    {
        this.course = course;
    }

    public String getUserId()
    {
        return userId;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }
}
