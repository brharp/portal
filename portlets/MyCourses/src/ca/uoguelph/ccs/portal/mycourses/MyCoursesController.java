package ca.uoguelph.ccs.portal.mycourses;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.LinkedList;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.portlet.mvc.AbstractController;
import org.springframework.web.portlet.ModelAndView;

public class MyCoursesController extends AbstractController 
    implements InitializingBean 
{
    private CourseDao courseDao;

    public void afterPropertiesSet() throws Exception 
    {
        if (this.courseDao == null)
            throw new IllegalArgumentException("courseDao can't be null");
    }
    
    public ModelAndView handleRenderRequestInternal(RenderRequest request, 
                                                    RenderResponse response) 
        throws Exception 
    {
        List courses = new LinkedList();

        Map userInfo = (Map)request.getAttribute(RenderRequest.USER_INFO);
        if (userInfo != null) {
            String courseInfo = 
                (String)userInfo.get("user.courses");
                //"CIS*3700 0101,CIS*4000 01";
            if (courseInfo != null) {
                String[] courseCodes = courseInfo.split(",");
                for(int i = 0; i < courseCodes.length; i++) {
                    courses.addAll(courseDao.getByCode(courseCodes[i]));
                }
            }
        }

        return new ModelAndView("myCoursesView", "courses", courses);
    }

    public void setCourseDao(CourseDao courseDao) 
    {
        this.courseDao = courseDao;
    }
}
