/*
 * $Id: DelegatesEditController.java,v 1.1 2006/04/28 14:04:19 brharp Exp $
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
 * delegates. Builds a list of delegates based on the value of the
 * "course" request parameter, and redirects to the
 * mycourses/delegatesEdit view which has controls for
 * editing/deleting/adding delegates.
 *
 * This controller is invoked for faculty and individuals designated
 * by the course instructor (teaching assistants, for example).
 *
 * @author $Author: brharp $
 * @version $Revision: 1.1 $
 */
public class DelegatesEditController extends AbstractController 
    implements InitializingBean 
{
    private DelegateDao delegateDao;

    /**
     * Validates the bean.
     *
     * @throws IllegalArgumentException if the delegateDao property is
     * null
     */
    public void afterPropertiesSet() throws Exception 
    {
        if (this.delegateDao == null)
            throw new IllegalArgumentException("delegatesDao can't be null");
    }
    
    /**
     * Inserts a list of Delegate objects into the model under the key
     * "delegates", and redirects to the mycourses/delegatesEdit view.
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
        List delegates = delegateDao.getByCourse(course);
        return new ModelAndView("mycourses/delegatesEdit", "delegates", 
                                delegates);
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
     * Sets the delegateDao property.
     */
    public void setDelegateDao(DelegateDao delegateDao) 
    {
        this.delegateDao = delegateDao;
    }
}
