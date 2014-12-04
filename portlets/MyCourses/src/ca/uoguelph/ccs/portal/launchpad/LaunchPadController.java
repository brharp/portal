package ca.uoguelph.ccs.portal.launchpad;

import java.util.HashMap;
import java.util.Map;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.portlet.mvc.AbstractController;
import org.springframework.web.portlet.ModelAndView;

public class LaunchPadController extends AbstractController 
    implements InitializingBean 
{
    private LaunchPadService launchPadService;
    
    public void afterPropertiesSet() throws Exception {
        if (this.launchPadService == null)
            throw new IllegalArgumentException("launchPadService");
    }
    
    public ModelAndView handleRenderRequestInternal(RenderRequest request, 
                                                    RenderResponse response) 
        throws Exception 
    {
        Map model = new HashMap();
        model.put("launchPad", launchPadService.getLaunchPad("foo"));
        return new ModelAndView("launchPadView", "model", model);
    }

    public void setLaunchPadService(LaunchPadService launchPadService) {
        this.launchPadService = launchPadService;
    }
}
