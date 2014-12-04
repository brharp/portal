package ca.uoguelph.ccs.portal.userinfo;

import java.util.Map;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import org.springframework.web.portlet.mvc.AbstractController;
import org.springframework.web.portlet.ModelAndView;

public class UserInfoController extends AbstractController
{
    public ModelAndView handleRenderRequestInternal(RenderRequest request,
                                              RenderResponse response)
        throws Exception
    {
        Map userInfo = (Map)request.getAttribute(RenderRequest.USER_INFO);
        return new ModelAndView("userInfoView", "userInfo", userInfo.entrySet());
    }
}
