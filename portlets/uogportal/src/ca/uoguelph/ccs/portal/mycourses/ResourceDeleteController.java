/*
 * $Header: /usr/local/repos/uogportal/src/ca/uoguelph/ccs/portal/mycourses/ResourceDeleteController.java,v 1.3 2006/05/17 16:32:45 brharp Exp $
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
import org.springframework.web.portlet.util.PortletUtils;

/**
 * Controller to delete course resources. The resources to delete must
 * be named (by primary key) in the "marked" request parameter. This
 * controller redirects to the mycourses/resourcesEdit view.
 *
 * @author $Author: brharp $
 * @version $Revision: 1.3 $
 */
public class ResourceDeleteController extends AbstractController 
    implements InitializingBean 
{
    private ResourceDao resourceDao;

    public void afterPropertiesSet() throws Exception 
    {
        if (this.resourceDao == null)
            throw new IllegalArgumentException("resourceDao can't be null");
    }
    
    /**
     * Redirects to the mycourses/resourcesEdit view.
     */
    public ModelAndView handleRenderRequestInternal(RenderRequest request, 
                                                    RenderResponse response) 
        throws Exception 
    {
        return null;
    }

    /**
     * Deletes marked resources. Marked resources are given by the
     * "marked" parameter, which should be a list of resource keys.
     */
    public void handleActionRequestInternal(ActionRequest request,
                                            ActionResponse response)
        throws Exception
    {
        String[] marked = request.getParameterValues("marked");
        
        if (marked != null) {
            for(int i = 0; i < marked.length; i++) {
                try {
                    resourceDao.deleteByKey(new Integer(marked[i]));
                } catch (NumberFormatException nfe) {}
            }
        }

        // Instead of a "success" page, this controller redirects the
        // user back to the "edit resources" page by modifying to the
        // request parameters to appear as an edit resources
        // request. This works because the controller mapping is run
        // twice by the PortletDispatcher -- once for the action phase
        // and once for the render phase. See {@see
        // org.springframework.web.portlet.mvc.AbstractController}
        PortletUtils.clearAllRenderParameters(response);
        response.setRenderParameter("action", "editResources");
        response.setRenderParameter("course", request.getParameter("course"));
    }

    public void setResourceDao(ResourceDao resourceDao) 
    {
        this.resourceDao = resourceDao;
    }
}
