/* 
 * $Header: /usr/local/repos/uogportal/src/ca/uoguelph/ccs/portal/mycourses/DelegateEditController.java,v 1.3 2006/05/26 12:57:50 brharp Exp $
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
 * Controller to add or edit a course delegate. This controller is
 * used to both edit and add simple link delegates to the delegates
 * section of the MyCourses portlet. If a "key" parameter is
 * specified, the delegate named by "key" is retrieved from the
 * database and modified. If no "key" parameter is specified, a new
 * delegate is inserted into the database.
 *
 * <p>
 *
 * TODO: Form validation.
 *
 * @author $Author: brharp $
 * @version $Revision: 1.3 $
 * @see MyCoursesController
 * @see "WEB-INF/jsp/mycourses/delegateEdit.jsp"
 * @see "WEB-INF/jsp/mycourses/delegatesEdit.jsp"
 */
public class DelegateEditController extends SimpleFormController
{
    private DelegateDao delegateDao;

    /**
     * Public constructor to set default properties.
     *
     * <table border="1">
     *   <tr><th>Property</th><th>Default</th></tr>
     *   <tr><td>commandName</td><td>"delegate"</td></tr>
     *   <tr><td>commandClass</td><td>Delegate.class</td></tr>
     *   <tr><td>formView</td><td>"mycourses/delegateEdit"</td></tr>
     *   <tr><td>successView</td><td>"mycourses/delegatesEdit"</td></tr>
     * </table>
     */
    public DelegateEditController()
    {
        setSessionForm(true);
        setCommandName("delegate");
        setCommandClass(Delegate.class);
        setFormView("mycourses/delegateEdit");
        setSuccessView("mycourses/delegatesEdit");
    }

    public void afterPropertiesSet() throws Exception
    {
    }

    /**
     * Inserts or updates the delegate in the database. If the "key"
     * parameter is null, it's assumed this is a new delegate and it
     * is inserted in the database. Otherwise, the delegate named by
     * "key" is updated.
     * <P>
     * This controller does not implement a render phase, it redirects
     * to the render phase of the "editDelegates" action (typically
     * {@link DelegatesEditController}). The course code is passed as
     * the "course" render parameter.
     *
     * @param request portlet action request
     * @param response portlet action response
     * @param command the Delegate object
     * @param errors validation errors
     */
    public void onSubmitAction(ActionRequest request, ActionResponse response,
                               Object command, BindException errors)
        throws Exception
    {
        Delegate delegate = (Delegate) command;
        
        delegateDao.insertDelegate(delegate);

        // Instead of a "success" page, this controller redirects the
        // user back to the "edit delegates" page by modifying to the
        // request parameters to appear as an edit delegates
        // request. This works because the controller mapping is run
        // twice by the PortletDispatcher -- once for the action phase
        // and once for the render phase. See {@see
        // org.springframework.web.portlet.mvc.AbstractController}
        PortletUtils.clearAllRenderParameters(response);
        response.setRenderParameter("action", "editDelegates");
        response.setRenderParameter("course", request.getParameter("course"));
    }

    /**
     * Creates or retrieves a Delegate object. If the "key" parameter
     * is null, a fresh Delegate object is created and updated with
     * the course code from the "course" parameter. Otherwise, the
     * delegate identified by "key" is retrieved from the database.
     *
     * @param request portlet request
     * @return a {@link Delegate} object
     */
    protected Object formBackingObject(PortletRequest request)
        throws Exception
    {
        Delegate delegate;

        delegate = new Delegate();
        delegate.setCourse(request.getParameter("course"));

        return delegate;
    }

    /**
     * Sets allowed form fields.
     *
     */
    protected void initBinder(PortletRequest request, 
                              PortletRequestDataBinder binder)
        throws Exception
    {
        String[] fields = new String[] {
            "userId"
        };
        binder.setAllowedFields(fields);
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
     * Sets the delegateDao property.
     */
    public void setDelegateDao(DelegateDao delegateDao)
    {
        this.delegateDao = delegateDao;
    }
}
