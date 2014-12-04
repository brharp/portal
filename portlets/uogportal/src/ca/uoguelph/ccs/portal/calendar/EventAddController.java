
/* 
 * $Id: EventAddController.java,v 1.2 2006/07/13 19:10:32 brharp Exp $
 *
 * Copyright 2006 University of Guelph
 */

package ca.uoguelph.ccs.portal.calendar;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.validation.BindException;
import org.springframework.web.portlet.bind.PortletRequestDataBinder;
import org.springframework.web.portlet.mvc.SimpleFormController;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.util.PortletUtils;

import java.util.Map;

import ca.uoguelph.ccs.portal.UserInfo;

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

    /**
     * Inserts an event into the calendar. For now, all events are
     * inserted into the user's personal calendar.
     *
     * @param request portlet action request
     * @param response portlet action response
     * @param command the Resource object
     * @param errors validation errors
     */
    public void onSubmitAction(ActionRequest request, ActionResponse response,
                               Object command, BindException errors)
        throws Exception
    {
        Event event = (Event) command;
        Map userInfo = (Map)request.getAttribute(RenderRequest.USER_INFO);
        String userId = UserInfo.getId(userInfo);
        Calendar cal = service.getCalendar(userId);
        /*
          if (cal == null) {
          cal = new Calendar();
          cal.setName(userId);
          cal.setOwner(userId);
          service.insert(cal);
          }
        */
        service.insert(event);
        CalendarAndEvent calEvent = new CalendarAndEvent();
        calEvent.setCalendarId(cal.getCalendarId());
        calEvent.setEventId(event.getEventId());
        service.insert(calEvent);

        PortletUtils.clearAllRenderParameters(response);
        response.setRenderParameter("action", "view");
    }

    /**
     * Sets allowed form fields.
     *
     */
    protected void initBinder(PortletRequest request, 
                              PortletRequestDataBinder binder)
        throws Exception
    {
        //binder.setAllowedFields(getAllowedFields());
    }

    protected ModelAndView renderInvalidSubmit(RenderRequest request, 
                                               RenderResponse response)
        throws Exception
    {
        return null;
    }

    protected void handleInvalidSubmit(ActionRequest request, 
                                       ActionResponse response)
        throws Exception
    {
        response.setRenderParameter("action","view");
    }

    /**
     * Sets the calendarService property.
     */
    public void setCalendarService(CalendarService service)
    {
        this.service = service;
    }
}
