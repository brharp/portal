/*
 * $Header: /usr/local/repos/uogportal/src/ca/uoguelph/ccs/portal/mycourses/MyCoursesController.java,v 1.12 2006/06/16 13:41:10 brharp Exp $
 *
 * Copyright 2006 University of Guelph
 */

package ca.uoguelph.ccs.portal.mycourses;

import java.util.Map;
import java.util.List;

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
 * Controller to display a list of courses.
 *
 * <P>
 *
 * This controller displays a list of courses affiliated with the
 * current user. Students see a list of courses they are enrolled in,
 * faculty see a list of courses they are instructing.
 *
 * <P>
 *
 * Students and faculty see different views of their courses {@link
 * #handleRenderRequestInternal}. Students have read only access to a
 * list of course resources, in addition to course outlines, library
 * reserves, and class announcements. Instructors may edit the list of
 * resources, and they can also see class lists, and send new
 * announcements to the class.
 *
 * @author $Author: brharp $
 * @version $Revision: 1.12 $
 */
public class MyCoursesController extends AbstractController 
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
     * Renders the MyCourses view.
     *
     * <p>
     *
     * This method puts three lists of Course objects in the model,
     * named "student", "faculty", and "delegate".
     *
     * <p>
     *
     * The list named "student" contains all of the courses for which
     * this user is currently enrolled a student.
     *
     * <p>
     *
     * The list named "faculty" contains all of the courses for
     * which this user is the instructor, or to which the instructor
     * has delegated responsibilty.
     *
     * <p>
     *
     * The list named "delegate" contains all the courses for which
     * the instructor has delegated responsibility for the course to
     * this user, such as in the case of a TA.
     *
     * <p>
     *
     * The courses this user is enrolled in as a student are
     * identified by the user attribute named by the
     * "coursesAttribute" property. The value of this user attribute
     * should be a comma delimited list of course codes. Likewise, the
     * courses this user instructs are identified by the user
     * attribute named by the "instructsAttribute" property. Courses
     * for which this user has been delegated responsibility are
     * defined by the course delegate data source.
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
        Map userInfo = (Map)request.getAttribute(RenderRequest.USER_INFO);

        PortletSession session = request.getPortletSession();
        synchronized(session) {
            courses = session.getAttribute(COURSES, PortletSession.APPLICATION_SCOPE);
            if (courses == null) {
                courses = courseService.getByUser(userInfo);
                session.setAttribute(COURSES, courses, PortletSession.APPLICATION_SCOPE);
            }
        }

        Map model = new java.util.HashMap();
        model.put("user", new UserInfo(userInfo));
        model.put("courses", courses);
        
        return new ModelAndView("mycourses/coursesView", "model", model);
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
