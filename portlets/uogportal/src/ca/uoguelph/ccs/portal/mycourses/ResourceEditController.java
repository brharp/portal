/* 
 * $Header: /usr/local/repos/uogportal/src/ca/uoguelph/ccs/portal/mycourses/ResourceEditController.java,v 1.5 2006/05/26 12:57:50 brharp Exp $
 *
 * Copyright 2006 University of Guelph
 */

package ca.uoguelph.ccs.portal.mycourses;

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

/**
 * Controller to add or edit a course resource. This controller is
 * used to both edit and add simple link resources to the resources
 * section of the MyCourses portlet. If a "key" parameter is
 * specified, the resource named by "key" is retrieved from the
 * database and modified. If no "key" parameter is specified, a new
 * resource is inserted into the database.
 *
 * <p>
 *
 * TODO: Form validation.
 *
 * @author $Author: brharp $
 * @version $Revision: 1.5 $
 * @see MyCoursesController
 * @see "WEB-INF/jsp/mycourses/resourceEdit.jsp"
 * @see "WEB-INF/jsp/mycourses/resourcesEdit.jsp"
 */
public class ResourceEditController extends SimpleFormController
{
    private ResourceDao resourceDao;

    /**
     * Public constructor to set default properties.
     *
     * <table border="1">
     *   <tr><th>Property</th><th>Default</th></tr>
     *   <tr><td>commandName</td><td>"resource"</td></tr>
     *   <tr><td>commandClass</td><td>Resource.class</td></tr>
     *   <tr><td>formView</td><td>"mycourses/resourceEdit"</td></tr>
     *   <tr><td>successView</td><td>"mycourses/resourcesEdit"</td></tr>
     * </table>
     */
    public ResourceEditController()
    {
        setSessionForm(true);
        setCommandName("resource");
        setFormView("mycourses/resourceEdit");
        setSuccessView("mycourses/resourcesEdit");
    }

    public void afterPropertiesSet() throws Exception
    {
    }

    /**
     * Inserts or updates the resource in the database. If the "key"
     * parameter is null, it's assumed this is a new resource and it
     * is inserted in the database. Otherwise, the resource named by
     * "key" is updated.
     * <P>
     * This controller does not implement a render phase, it redirects
     * to the render phase of the "editResources" action (typically
     * {@link ResourcesEditController}). The course code is passed as
     * the "course" render parameter.
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
        Resource resource = (Resource) command;
        
        Integer key;
        try {
            key = new Integer(request.getParameter("resource"));
        } catch (NumberFormatException nfe) {
            key = null;
        }

        if (key == null) {
            resourceDao.insertResource(resource);
        } else {
            resourceDao.updateResource(resource);
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

    /**
     * Creates or retrieves a Resource object. If the "key" parameter
     * is null, a fresh Resource object is created and updated with
     * the course code from the "course" parameter. Otherwise, the
     * resource identified by "key" is retrieved from the database.
     *
     * @param request portlet request
     * @return a {@link Resource} object
     */
    protected Object formBackingObject(PortletRequest request)
        throws Exception
    {
        Resource resource;

        try {
            Integer key = new Integer(request.getParameter("resource"));
            resource = (Resource)resourceDao.getByKey(key).get(0);
        } catch (Exception nfe) {
            resource = (Resource)getCommandClass().newInstance();
            resource.setCourse(request.getParameter("course"));
        }

        return resource;
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
     * Sets the resourceDao property.
     */
    public void setResourceDao(ResourceDao resourceDao)
    {
        this.resourceDao = resourceDao;
    }
}
