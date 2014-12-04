
package ca.uoguelph.ccs.portal.services.calendar;

import org.springframework.validation.*;
import ca.uoguelph.ccs.portal.calendar.*;
import org.apache.commons.logging.*;

public class EventValidator implements Validator
{
    Log log = LogFactory.getLog(EventValidator.class);

    public boolean supports(Class clazz) 
    {
        Class[] interfaces = clazz.getInterfaces();
        for(int i = 0; i < interfaces.length; i++) {
            if (interfaces[i].equals(Event.class)) {
                return true;
            }
        }
        return false;
    }

    public void validate(Object command, Errors errors) 
    {
        if (log.isDebugEnabled()) {
            log.debug("Validating [command="+command+"]");
        }

        Event event = (Event)command;

        ValidationUtils.rejectIfEmpty
            (errors, "title", "required.title", "A title is required.");

        if (log.isDebugEnabled()) {
            log.debug("[endTime="+event.getEndTime()+"]");
            log.debug("[startTime="+event.getStartTime()+"]");
        }

        if (event.getEndTime() < event.getStartTime()) {
            errors.reject("invalid.endTime", "End time must be later then start time.");
        }
    }
}

                                      
