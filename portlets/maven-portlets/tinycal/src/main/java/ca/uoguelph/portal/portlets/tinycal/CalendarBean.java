package ca.uoguelph.ccs.portal.portlets.tinycal;

import java.util.Calendar;
import java.util.Date;

public class CalendarBean
{
    Calendar wrapped;

    public CalendarBean() {
        wrapped = Calendar.getInstance();
    }

    public CalendarBean(Calendar wrapped) {
        this.wrapped = wrapped;
    }

    protected void setInternal(int field, int value) {
        wrapped.set(field, value);
        wrapped.getTime();      // Recompute time.
    }

    public Date getTime() {
        return wrapped.getTime();
    }

    public void setDate(int value) {
        setInternal(Calendar.DATE, value);
    }

    public int getDate() {
        return wrapped.get(Calendar.DATE);
    }

    public void setMonth(int value) {
        setInternal(Calendar.MONTH, value);
    }

    public int getMonth() {
        return wrapped.get(Calendar.MONTH);
    }

    public void setYear(int value) {
        setInternal(Calendar.YEAR, value);
    }

    public int getYear() {
        return wrapped.get(Calendar.YEAR);
    }

    public void setDayOfWeek(int value) {
        setInternal(Calendar.DAY_OF_WEEK, value);
    }

    public int getDayOfWeek() {
        return wrapped.get(Calendar.DAY_OF_WEEK);
    }

    public boolean isToday() {
        Calendar now = Calendar.getInstance();
        return now.get(Calendar.YEAR) == wrapped.get(Calendar.YEAR) 
            && now.get(Calendar.MONTH) == wrapped.get(Calendar.MONTH)
            && now.get(Calendar.DATE) == wrapped.get(Calendar.DATE);
    }
}
