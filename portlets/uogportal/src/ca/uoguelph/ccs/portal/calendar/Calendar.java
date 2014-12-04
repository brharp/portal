
package ca.uoguelph.ccs.portal.calendar;

public interface Calendar
{
    public static final int READ  = 1;
    public static final int WRITE = 2;

    public String getCalendarId();
    public String getName();
    public String getOwner();
}
