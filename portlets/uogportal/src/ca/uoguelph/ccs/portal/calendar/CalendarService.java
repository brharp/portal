
package ca.uoguelph.ccs.portal.calendar;

import ca.uoguelph.ccs.portal.calendar.dao.*;
import java.util.List;
import java.util.Iterator;

public class CalendarService
{
    CalendarDao calDao;
    EventDao eventDao;
    CalendarEventDao calEventDao;

    public void setCalendarDao(CalendarDao calendarDao) 
    {
        this.calDao = calendarDao;
    }

    public void setEventDao(EventDao eventDao) 
    {
        this.eventDao = eventDao;
    }

    public void setCalendarEventDao(CalendarEventDao calEventDao) 
    {
        this.calEventDao = calEventDao;
    }

    /**
     * Retrieves a specific calendar.
     */
    public Calendar getCalendar(String id)
    {
        return calDao.get(id);
    }

    /**
     * Updates calendar details.
     */
    public Calendar update(Calendar cal) throws NoSuchCalendarException
    {
        if (cal == null) {
            throw new NullPointerException();
        } else {
            return calDao.update(cal);
        }
    }

    /**
     * Insert a new calendar.
     */
    public Calendar insert(Calendar cal)
    {
        if (cal == null) {
            throw new NullPointerException();
        } else {
            cal = calDao.insert(cal);
            cal = calDao.grant(cal, cal.getOwner(), Calendar.READ|Calendar.WRITE);
            return cal;
        }
    }

    /**
     * Deletes a calendar.
     */
    public void delete(Calendar cal) throws NoSuchCalendarException
    {
        if (cal == null) {
            throw new NullPointerException();
        } else {
            // Delete all events.
            List calEvents = calEventDao.findByCalendar(cal);
            Iterator calEventIter = calEvents.iterator();
            while(calEventIter.hasNext()) {
                CalendarAndEvent calEvent = (CalendarAndEvent)calEventIter.next();
                String eventId = calEvent.getEventId();
                Event event = eventDao.get(eventId);
                eventDao.delete(event);
                calEventDao.delete(calEvent);
            }
            // Revoke permissions.
            calDao.revokeAll(cal);
            // Delete calendar.
            calDao.delete(cal);
        } 
    }

    /**
     * Grants permission to access a calendar.
     */
    public Calendar grant(Calendar cal, String principal, int mode)
    {
        if (cal == null || principal == null) {
            throw new NullPointerException();
        } else {
            calDao.grant(cal, principal, mode);
            return cal;
        }
    }

    /**
     * Revokes permission to access a calendar.
     */
    public Calendar revoke(Calendar cal, String principal)
    {
        if (cal == null || principal == null) {
            throw new NullPointerException();
        } else {
            calDao.revoke(cal, principal);
            return cal;
        }
    }


    /**
     * Retrieves a specific event.
     */
    public Event getEvent(String id)
    {
        return eventDao.get(id);
    }

    /**
     * Updates and event.
     */
    public Event update(Event event) throws NoSuchEventException
    {
        if (event == null) {
            throw new NullPointerException();
        } else {
            return eventDao.update(event);
        }
    }

    /**
     * Inserts and event.
     */
    public Event insert(Event event)
    {
        if (event == null) {
            throw new NullPointerException();
        } else {
            return eventDao.insert(event);
        }
    }

    /**
     * Deletes an event.
     */
    public void delete(Event event) throws NoSuchEventException
    {
        if (event == null) {
            throw new NullPointerException();
        } else {
            eventDao.delete(event);
        }
    }

    /**
     * Inserts an event into a calendar.
     */
    public void insert(CalendarAndEvent calendarAndEvent) 
        throws NoSuchEventException, NoSuchCalendarException
    {
        if (calendarAndEvent == null) {
            throw new NullPointerException();
        } else {
            calEventDao.insert(calendarAndEvent);
        }
    }


    /**
     * Deletes an event from a calendar.
     */
    public void delete(CalendarAndEvent calendarAndEvent)
    {
        if (calendarAndEvent == null) {
            throw new NullPointerException();
        } else {
            calEventDao.delete(calendarAndEvent);
        }
    }
}
