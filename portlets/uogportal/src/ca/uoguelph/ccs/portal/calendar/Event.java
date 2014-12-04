
/* 
 * $Id: Event.java,v 1.2 2006/07/13 19:10:32 brharp Exp $
 *
 * Copyright 2006 University of Guelph
 */

package ca.uoguelph.ccs.portal.calendar;

import java.util.Date;

/**
 * Class representing an event in the portal calendar.
 */
public interface Event
{
    public String getEventId();
    public String getTitle();
    public void setTitle(String title);
    public String getDescription();
    public void setDescription(String description);
    public Date getStart();
    public void setStart(Date start);
    public long getStartTime();
    public void setStartTime(long startTime);
    public Date getEnd();
    public void setEnd(Date end);
    public long getEndTime();
    public void setEndTime(long endTime);
    public long getDuration();
    public void setDuration(long duration);
    public String getLocation();
    public void setLocation(String location);
    public int getStartYear();
    public int getStartMonth();
    public int getStartDate();
    public int getStartHour();
    public int getStartAmPm();
    public int getStartHourOfDay();
    public int getStartMinute();
    public void setStartYear(int year);
    public void setStartMonth(int month);
    public void setStartDate(int date);
    public void setStartHour(int hour);
    public void setStartAmPm(int ampm);
    public void setStartHourOfDay(int hourOfDay);
    public void setStartMinute(int minute);
    public int getEndYear();
    public int getEndMonth();
    public int getEndDate();
    public int getEndHour();
    public int getEndAmPm();
    public int getEndHourOfDay();
    public int getEndMinute();
    public void setEndYear(int year);
    public void setEndMonth(int month);
    public void setEndDate(int date);
    public void setEndHour(int hour);
    public void setEndAmPm(int ampm);
    public void setEndHourOfDay(int hourOfDay);
    public void setEndMinute(int minute);
}
