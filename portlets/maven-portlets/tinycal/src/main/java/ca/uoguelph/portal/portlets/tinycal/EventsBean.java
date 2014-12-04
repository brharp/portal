
package ca.uoguelph.ccs.portal.portlets.tinycal;

import java.util.*;
import com.sun.syndication.feed.synd.*;
import com.sun.syndication.feed.module.*;
import ca.uoguelph.ccs.syndication.feed.module.*;

/**
 * Provides date-based access to events.
 */
public class EventsBean
{
    Date date;
    List events;
    int cursor;

    public EventsBean(List events) {
        this.events = events;
        this.cursor = 0;
    }

    public void setTime(Date date) {
        this.date = date;
    }

    /**
     * Get unread events occuring before the midnight on the date set
     * by setTime().
     */
    public List getEvents() 
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        Date startTime = calendar.getTime();

        calendar.add(Calendar.DATE, 1);

        Date endTime = calendar.getTime();

        // Back up to the earliest event that occurs after start time.

        while(cursor > 0 && startTime.compareTo(eventDate(cursor-1)) < 0) {
            cursor--;
        }

        // Add all events occuring before the end time to the result.

        List results = new ArrayList();
        while(cursor < events.size() && endTime.compareTo(eventDate(cursor)) > 0) {
            results.add(new EventBean((SyndEntry)events.get(cursor)));
            cursor++;
        }

        return results;
    }

    private Date eventDate(int index)
    {
        SyndEntry e = (SyndEntry)events.get(index);
        UGModule ug = (UGModule)e.getModule(UGModule.URI);

        if (ug == null) {
            throw new ClassCastException("Entry does not implement UG module.");
        }

        return ug.getWhen().getStartTime();
    }
}
