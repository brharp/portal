/*
 * $Id: Exam.java,v 1.1 2006/06/02 16:52:07 brharp Exp $
 *
 * Copyright 2006 University of Guelph
 */

package ca.uoguelph.ccs.portal.mycourses;

import java.util.Date;

public class Exam
{
    private Date startDate;
    private Date endDate;
    private String room;
    private String building;

    public Date getStartDate()
    {
        return startDate;
    }

    public void setStartDate(Date startDate)
    {
        this.startDate = startDate;
    }

    public Date getEndDate()
    {
        return endDate;
    }

    public void setEndDate(Date endDate)
    {
        this.endDate = endDate;
    }

    public String getRoom()
    {
        return room;
    }

    public void setRoom(String room)
    {
        this.room = room;
    }

    public String getBuilding()
    {
        return building;
    }

    public void setBuilding(String building)
    {
        this.building = building;
    }
}
