
/* 
 * $Id: EventAddController.java,v 1.2 2006/11/14 16:25:39 brharp Exp $
 *
 * Copyright 2006 University of Guelph
 */

package ca.uoguelph.ccs.portal.services.calendar;

import java.util.Enumeration;
import javax.servlet.http.*;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.ModelAndView;
import org.jasig.portal.security.IPerson;
import ca.uoguelph.ccs.portal.calendar.*;
import ca.uoguelph.ccs.portal.calendar.academus.*;

/**
 * Controller to add an event to your calendar.
 *
 * <p>
 *
 * TODO: Form validation.
 *
 * @author $Author: brharp $
 * @version $Revision: 1.2 $
 */
public class EventAddController extends SimpleFormController
{
    private CalendarService service;

    /**
     * Public constructor to set default properties.
     */
    public EventAddController()
    {
        setSessionForm(true);
        setBindOnNewForm(true);
    }

    protected Object formBackingObject(HttpServletRequest request)
        throws Exception
    {
        return new AcademusEvent();
    }

    /**
     * Inserts an event into the calendar. For now, all events are
     * inserted into the user's personal calendar.
     *
     * @param request portlet action request
     * @param response portlet action response
     * @param command the Resource object
     * @param errors validation errors
     */
    protected ModelAndView onSubmit(HttpServletRequest request, 
                                    HttpServletResponse response,
                                    Object command, BindException errors)
        throws Exception
    {
        Event event = (Event) command;

        HttpSession session = request.getSession(false);
        IPerson user = (IPerson)session.getAttribute(IPerson.class.getName());
        String userId = (String)user.getAttribute("username");
        if (logger.isDebugEnabled()) {
            logger.debug("[userId="+userId+"]");
        }
        if (userId == null || userId.length() == 0) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return null;
        }

        AcademusCalendar cal = (AcademusCalendar)service.getCalendar(userId);
        if (cal == null) {
            cal = new AcademusCalendar();
            cal.setName(userId);
            cal.setOwner(userId);
            service.insert(cal);
        }
        service.insert(event);
        CalendarAndEvent calEvent = new CalendarAndEvent();
        calEvent.setCalendarId(cal.getCalendarId());
        calEvent.setEventId(event.getEventId());
        service.insert(calEvent);
        
        return new ModelAndView(getSuccessView());
    }

    /**
     * Sets the calendarService property.
     */
    public void setCalendarService(CalendarService service)
    {
        this.service = service;
    }
}
