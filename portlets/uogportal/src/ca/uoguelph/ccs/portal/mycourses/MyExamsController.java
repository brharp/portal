/*
 * $Header: /usr/local/repos/uogportal/src/ca/uoguelph/ccs/portal/mycourses/MyExamsController.java,v 1.3 2006/06/16 13:41:10 brharp Exp $
 *
 * Copyright 2006 University of Guelph
 */

package ca.uoguelph.ccs.portal.mycourses;

import java.util.Map;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.portlet.mvc.AbstractController;
import org.springframework.web.portlet.ModelAndView;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ca.uoguelph.ccs.portal.UserInfo;

import javax.portlet.PortletSession;

/**
 * Controller to display a list of exams.
 *
 * @author $Author: brharp $
 * @version $Revision: 1.3 $
 */
public class MyExamsController extends AbstractController 
    implements InitializingBean 
{
    public static final String COURSES = "courses";

    private CourseService courseService;

    public void afterPropertiesSet() throws Exception 
    {
        if (this.courseService == null)
            throw new IllegalArgumentException("courseService == null");
    }
    
    /**
     * Renders the MyExams view.
     *
     * @param request portlet render request
     * @param response portlet render response
     * @return a model and view
     */
    public ModelAndView handleRenderRequestInternal(RenderRequest request, 
                                                    RenderResponse response) 
        throws Exception 
    {
        Object courses = null;

        PortletSession session = request.getPortletSession();
        synchronized(session) {
            courses = session.getAttribute(COURSES, PortletSession.APPLICATION_SCOPE);
            if (courses == null) {
                Map userInfo = (Map)request.getAttribute(RenderRequest.USER_INFO);
                String semester = request.getPreferences().getValue("semester", null);
                courses = courseService.getByUser(userInfo);
                session.setAttribute(COURSES, courses, PortletSession.APPLICATION_SCOPE);
            }
        }

        return new ModelAndView("mycourses/examsView", "courses", courses);
    }

    /**
     * Empty method.
     */
    public void handleActionRequestInternal(ActionRequest request,
                                            ActionResponse response)
        throws Exception
    {
    }

    /**
     * Set the CourseService property.
     */
    public void setCourseService(CourseService courseService) 
    {
        this.courseService = courseService;
    }
}
