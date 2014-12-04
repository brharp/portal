
package ca.uoguelph.ccs.portal.calendar.academus;

import java.util.Date;
import java.util.Calendar;
import ca.uoguelph.ccs.portal.calendar.Event;

public class AcademusEvent implements Event
{
    private String ceId;
    private String title;
    private String description;
    private Calendar start;
    private Calendar end;
    private long duration;
    private String location;

    public AcademusEvent() {
        start = Calendar.getInstance();
        end   = Calendar.getInstance();
    }

    public String getEventId()
    {
        return getCeId();
    }

    public String getCeId() {
        return ceId;
    }

    public void setCeId(String ceId) {
        this.ceId = ceId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getStart() {
        return start.getTime();
    }

    public void setStart(Date start) {
        this.start.setTime(start);
    }

    public long getStartTime() {
        return start.getTimeInMillis();
    }

    public void setStartTime(long startTime) {
        this.start.setTimeInMillis(startTime);
    }
    
    public Date getEnd() {
        return end.getTime();
    }

    public void setEnd(Date end) {
        this.end.setTime(end);
    }

    public long getEndTime() {
        return end.getTimeInMillis();
    }

    public void setEndTime(long endTime) {
        this.end.setTimeInMillis(endTime);
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getStartYear() {
        return start.get(Calendar.YEAR);
    }

    public int getStartMonth() {
        return start.get(Calendar.MONTH);
    }

    public int getStartDate() {
        return start.get(Calendar.DAY_OF_MONTH);
    }

    public int getStartHour() {
        return start.get(Calendar.HOUR);
    }

    public int getStartAmPm() {
        return start.get(Calendar.AM_PM);
    }

    public int getStartHourOfDay() {
        return start.get(Calendar.HOUR_OF_DAY);
    }

    public int getStartMinute() {
        return start.get(Calendar.MINUTE);
    }

    public void setStartYear(int year) {
        start.set(Calendar.YEAR, year);
    }

    public void setStartMonth(int month) {
        start.set(Calendar.MONTH, month);
    }

    public void setStartDate(int date) {
        start.set(Calendar.DAY_OF_MONTH, date);
    }

    public void setStartHour(int hour) {
        start.set(Calendar.HOUR, hour);
    }

    public void setStartAmPm(int ampm) {
        start.set(Calendar.AM_PM, ampm);
    }

    public void setStartHourOfDay(int hour) {
        start.set(Calendar.HOUR_OF_DAY, hour);
    }

    public void setStartMinute(int minute) {
        start.set(Calendar.MINUTE, minute);
    }

    public int getEndYear() {
        return end.get(Calendar.YEAR);
    }

    public int getEndMonth() {
        return end.get(Calendar.MONTH);
    }

    public int getEndDate() {
        return end.get(Calendar.DAY_OF_MONTH);
    }

    public int getEndHour() {
        return end.get(Calendar.HOUR);
    }

    public int getEndAmPm() {
        return end.get(Calendar.AM_PM);
    }

    public int getEndHourOfDay() {
        return end.get(Calendar.HOUR_OF_DAY);
    }

    public int getEndMinute() {
        return end.get(Calendar.MINUTE);
    }

    public void setEndYear(int year) {
        end.set(Calendar.YEAR, year);
    }

    public void setEndMonth(int month) {
        end.set(Calendar.MONTH, month);
    }

    public void setEndDate(int date) {
        end.set(Calendar.DAY_OF_MONTH, date);
    }

    public void setEndHour(int hour) {
        end.set(Calendar.HOUR, hour);
    }

    public void setEndAmPm(int ampm) {
        end.set(Calendar.AM_PM, ampm);
    }

    public void setEndHourOfDay(int hour) {
        end.set(Calendar.HOUR_OF_DAY, hour);
    }

    public void setEndMinute(int minute) {
        end.set(Calendar.MINUTE, minute);
    }
}

    
