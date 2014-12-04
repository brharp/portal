
package ca.uoguelph.ccs.portal.calendar.dao;

import ca.uoguelph.ccs.portal.calendar.Calendar;

public interface CalendarDao
{
    public Calendar get(String id);
    public Calendar insert(Calendar cal);
    public Calendar update(Calendar cal);
    public void     delete(Calendar cal);
    public Calendar grant(Calendar cal, String principal, int mode);
    public Calendar revoke(Calendar cal, String principal);
    public Calendar revokeAll(Calendar cal);
}
