/*******************************************************************************
* Copyright 2005, The Board of Regents of the University of Wisconsin System.
* All rights reserved.
*
* A non-exclusive worldwide royalty-free license is granted for this Software.
* Permission to use, copy, modify, and distribute this Software and its
* documentation, with or without modification, for any purpose is granted
* provided that such redistribution and use in source and binary forms, with or
* without modification meets the following conditions:
*
* 1. Redistributions of source code must retain the above copyright notice,
* this list of conditions and the following disclaimer.
*
* 2. Redistributions in binary form must reproduce the above copyright notice,
* this list of conditions and the following disclaimer in the documentation
* and/or other materials provided with the distribution.
*
* 3. Redistributions of any form whatsoever must retain the following
* acknowledgement:
*
* "This product includes software developed by The Board of Regents of
* the University of Wisconsin System.”
*
*THIS SOFTWARE IS PROVIDED BY THE BOARD OF REGENTS OF THE UNIVERSITY OF
*WISCONSIN SYSTEM "AS IS" AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING,
*BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
*PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE BOARD OF REGENTS OF
*THE UNIVERSITY OF WISCONSIN SYSTEM BE LIABLE FOR ANY DIRECT, INDIRECT,
*INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
*LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
*PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
*LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
*OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
*ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*******************************************************************************/
package edu.wisc.my.portlets.feedback.web;

import org.springframework.mail.MailSender;

import edu.wisc.my.portlets.feedback.beans.Feedback;
import edu.wisc.my.portlets.feedback.dao.IFeedbackMessageFormatter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.mvc.SimpleFormController;

import java.util.*;
//import ca.uoguelph.ccs.portal.util.*;

/**
 * Spring PortletMVC FormController for feedback form.
 * Note the onSubmit method throws any Exception - this is desired behavior.
 * An exceptionResolver is defined in the portlet's Spring beans config that
 * will direct any error cases to a "pretty" view.
 * 
 * @author nblair@doit.wisc.edu
 * @version $Header: /usr/local/repos/portal/portlets/FeedbackPortlet/source/edu/wisc/my/portlets/feedback/web/FeedbackFormController.java,v 1.1 2006/07/07 02:12:03 ljairath Exp $
 */
public class FeedbackFormController extends SimpleFormController {

    private IFeedbackMessageFormatter messageFormatter;
    private MailSender mailSender;
    
    /**
     * @param mailSender The mailSender to set.
     */
    public void setMailSender(MailSender mailSender) {
        this.mailSender = mailSender;
    }
    /**
     * @param messageFormatter The messageFormatter to set.
     */
    public void setMessageFormatter(IFeedbackMessageFormatter messageFormatter) {
        this.messageFormatter = messageFormatter;
    }
    
    protected Object formBackingObject(HttpServletRequest request) throws Exception
    {
        Feedback feedback = new Feedback();
        Map userInfo = (Map) request.getAttribute(PortletRequest.USER_INFO);
        String login = (String) userInfo.get("username");
        String fullName = (String) userInfo.get("displayName");
        String role = (String) userInfo.get("userRole");
        String emailAddress = (String) userInfo.get("mail");
        
        feedback.setLogin(login);
        feedback.setName(fullName);
        feedback.setEmailAddress(emailAddress);
        feedback.setRole(role);
        feedback.setServerName(getPortalServerName());
        return feedback;
    }
    
    /* (non-Javadoc)
     * @see org.springframework.web.portlet.mvc.SimpleFormController#onSubmit(java.lang.Object)
     */
    protected void doSubmitAction(Object command) throws Exception {
        Feedback feedback = (Feedback) command;
        mailSender.send(messageFormatter.format(feedback));
    }
    
    public static String getPortalServerName() {
        String portalHostName = "Undefined";    
        try
        {
            java.net.InetAddress localMachine =
                java.net.InetAddress.getLocalHost();
            String hostname = localMachine.getHostName();
            
            /*coded host names for the production portal "P?"
             coded host names for the qa portal "QA?"
             */
            //hostname = (String) request.getServerName(); //for domain name qa.myportico.uoguelph.ca
            System.out.println("hostname: " + hostname);
            if ("happy".equalsIgnoreCase(hostname)) {
                portalHostName = "P1";
            } else if ("doc".equalsIgnoreCase(hostname)) {
                portalHostName = "P2";
            } else if ("bashful".equalsIgnoreCase(hostname)) {
                portalHostName = "P3";
            } else if ("sneezy".equalsIgnoreCase(hostname)) {
                portalHostName = "P4";
                //} else if ("grumpy".equals(hostname)) { //given out to forums herbie.cs.uoguelph.ca
            } else if ("herbie.cs.uoguelph.ca".equalsIgnoreCase(hostname)|| "herbie".equalsIgnoreCase(hostname)) {
                portalHostName = "P5";
            } else if ("sleepy".equalsIgnoreCase(hostname)) {
                portalHostName = "QA1";
            } else if ("dopey".equalsIgnoreCase(hostname)) {
                portalHostName = "QA2";
            } else if ("snowwhite".equalsIgnoreCase(hostname)) {
                portalHostName = "L1";
            } else if ("prince".equalsIgnoreCase(hostname)) {
                portalHostName = "L2";
            }
        }
        catch(java.net.UnknownHostException uhe)
        {
        }
        return portalHostName;
    }
}
