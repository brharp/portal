package ca.uoguelph.ccs.portal.mycourses;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.LinkedList;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.portlet.mvc.AbstractController;
import org.springframework.web.portlet.ModelAndView;

public class ResourcesViewController extends AbstractController 
    implements InitializingBean 
{
    private ResourceDao resourceDao;

    public void afterPropertiesSet() throws Exception 
    {
        if (this.resourceDao == null)
            throw new IllegalArgumentException("resourcesDao can't be null");
    }
    
    public ModelAndView handleRenderRequestInternal(RenderRequest request, 
                                                    RenderResponse response) 
        throws Exception 
    {
        String course = request.getParameter("course");
        List resources = resourceDao.getPubByCourse(course);
        return new ModelAndView("mycourses/resourcesView", "resources", resources);
    }

    public void handleActionRequestInternal(ActionRequest request,
                                            ActionResponse response)
        throws Exception
    {
    }

    public void setResourceDao(ResourceDao resourceDao) 
    {
        this.resourceDao = resourceDao;
    }
}
