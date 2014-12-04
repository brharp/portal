
package ca.uoguelph.ccs.portal.calendar.dao;

import ca.uoguelph.ccs.portal.calendar.*;
import java.util.List;

public interface CalendarEventDao
{
    public void insert(CalendarAndEvent calendarAndEvent);
    public void delete(CalendarAndEvent calendarAndEvent);
    public List findByCalendar(Calendar calendar);
}

