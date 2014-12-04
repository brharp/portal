package ca.uoguelph.ccs.portal.launchpad;

import java.util.HashMap;
import java.util.Map;

import javax.portlet.PortletException;
import javax.portlet.PortletSecurityException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.web.portlet.mvc.AbstractController;
import org.springframework.web.portlet.ModelAndView;

public class ExceptionsController extends AbstractController {

    private final static String THROW_PARAMETER = "throw";
    
    public ModelAndView handleRenderRequestInternal(RenderRequest request, 
                                                    RenderResponse response) 
        throws Exception 
    {
        String throwme = request.getParameter(THROW_PARAMETER);
        
        // build map of throwable exceptions
        Map exceptions = new HashMap();
        exceptions.put(PortletException.class.getName(),
                       new PortletException("This is a test"));
        exceptions.put(PortletSecurityException.class.getName(),
                       new PortletSecurityException("This is a test"));
        
        // see if we have been asked to throw something
        if (throwme != null) {
            Exception ex = (Exception)exceptions.get(throwme);
            if (ex != null)
                throw ex;
        }
        
        // didn't throw anything -- build model and view
        Map model = new HashMap();
        model.put("exceptions", exceptions.keySet());
        return new ModelAndView("exceptions", "model", model);
    }
}
