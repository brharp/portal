/*
 * $Header: /usr/local/repos/uogportal/src/ca/uoguelph/ccs/portal/mycourses/ResourcesEditController.java,v 1.2 2006/04/25 19:03:42 brharp Exp $
 *
 * Copyright 2006 University of Guelph
 */

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

/**
 * Controller that displays an editable list of course
 * resources. Builds a list of resources based on the value of the
 * "course" request parameter, and redirects to the
 * mycourses/resourcesEdit view which has controls for
 * editing/deleting/adding resources.
 *
 * This controller is invoked for faculty and individuals designated
 * by the course instructor (teaching assistants, for example).
 *
 * @author $Author: brharp $
 * @version $Revision: 1.2 $
 */
public class ResourcesEditController extends AbstractController 
    implements InitializingBean 
{
    private ResourceDao resourceDao;

    /**
     * Validates the bean.
     *
     * @throws IllegalArgumentException if the resourceDao property is
     * null
     */
    public void afterPropertiesSet() throws Exception 
    {
        if (this.resourceDao == null)
            throw new IllegalArgumentException("resourcesDao can't be null");
    }
    
    /**
     * Inserts a list of Resource objects into the model under the key
     * "resources", and redirects to the mycourses/resourcesEdit view.
     *
     * @param request portlet render request
     * @param response portlet render response
     * @return a model and view
     */
    public ModelAndView handleRenderRequestInternal(RenderRequest request, 
                                                    RenderResponse response) 
        throws Exception 
    {
        String course = request.getParameter("course");
        List resources = resourceDao.getAllByCourse(course);
        return new ModelAndView("mycourses/resourcesEdit", "resources", 
                                resources);
    }

    /**
     * Empty method. This controller does not handle action requests.
     */
    public void handleActionRequestInternal(ActionRequest request,
                                            ActionResponse response)
        throws Exception
    {
    }

    /**
     * Sets the resourceDao property.
     */
    public void setResourceDao(ResourceDao resourceDao) 
    {
        this.resourceDao = resourceDao;
    }
}
