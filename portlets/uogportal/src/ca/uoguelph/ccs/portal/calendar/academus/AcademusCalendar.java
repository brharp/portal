
package ca.uoguelph.ccs.portal.calendar.academus;

import ca.uoguelph.ccs.portal.calendar.Calendar;

public class AcademusCalendar implements Calendar
{
    private Integer id;
    private String calId;
    private String name;
    private String owner;

    public String getCalendarId()
    {
        return getCalId();
    }

    Integer getId() {
        return id;
    }

    void setId(Integer id) {
        this.id = id;
    }

    String getCalId() {
        return calId;
    }

    void setCalId(String calId) {
        this.calId = calId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
