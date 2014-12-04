
package ca.uoguelph.ccs.portal.calendar.dao;

import ca.uoguelph.ccs.portal.calendar.*;
import java.util.List;

public interface EventDao
{
    public Event get(String id);
    public Event update(Event event);
    public Event insert(Event event);
    public void  delete(Event event);
    public List findByCalendar(Calendar calendar);
}
