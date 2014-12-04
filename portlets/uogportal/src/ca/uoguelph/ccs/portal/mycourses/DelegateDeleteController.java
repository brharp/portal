/*
 * $Header: /usr/local/repos/uogportal/src/ca/uoguelph/ccs/portal/mycourses/DelegateDeleteController.java,v 1.2 2006/05/17 16:32:45 brharp Exp $
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
 * Controller to delete course delegates. The delegates to delete must
 * be named (by primary key) in the "marked" request parameter. This
 * controller redirects to the mycourses/delegatesEdit view.
 *
 * @author $Author: brharp $
 * @version $Revision: 1.2 $
 */
public class DelegateDeleteController extends AbstractController 
    implements InitializingBean 
{
    private DelegateDao delegateDao;

    public void afterPropertiesSet() throws Exception 
    {
        if (this.delegateDao == null)
            throw new IllegalArgumentException("delegateDao can't be null");
    }
    
    /**
     * Redirects to the mycourses/delegatesEdit view.
     */
    public ModelAndView handleRenderRequestInternal(RenderRequest request, 
                                                    RenderResponse response) 
        throws Exception 
    {
        return null;
    }

    /**
     * Deletes marked delegates. Marked delegates are given by the
     * "marked" parameter, which should be a list of delegate keys.
     */
    public void handleActionRequestInternal(ActionRequest request,
                                            ActionResponse response)
        throws Exception
    {
        String[] marked = request.getParameterValues("marked");
        String course = request.getParameter("course");
        
        if (marked != null) {
            for(int i = 0; i < marked.length; i++) {
                try {
                    delegateDao.delete(marked[i],course);
                } catch (NumberFormatException nfe) {}
            }
        }

        // Instead of a "success" page, this controller redirects the
        // user back to the "edit delegates" page by modifying to the
        // request parameters to appear as an edit delegates
        // request. This works because the controller mapping is run
        // twice by the PortletDispatcher -- once for the action phase
        // and once for the render phase. See {@see
        // org.springframework.web.portlet.mvc.AbstractController}
        PortletUtils.clearAllRenderParameters(response);
        response.setRenderParameter("action", "editDelegates");
        response.setRenderParameter("course", course);
    }

    public void setDelegateDao(DelegateDao delegateDao) 
    {
        this.delegateDao = delegateDao;
    }
}
