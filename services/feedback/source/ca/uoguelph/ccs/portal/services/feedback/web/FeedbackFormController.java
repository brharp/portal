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
package ca.uoguelph.ccs.portal.services.feedback.web;

import org.springframework.mail.MailSender;

import ca.uoguelph.ccs.portal.services.feedback.beans.Feedback;
import ca.uoguelph.ccs.portal.services.feedback.dao.IFeedbackMessageFormatter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.mvc.SimpleFormController;

import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.jasig.portal.security.IPerson;
import org.jasig.portal.security.PersonManagerFactory;

import org.jasig.portal.security.IPrincipal;

import ca.uoguelph.ccs.portal.services.sso.util.UserCredential;

/**
 * Spring PortletMVC FormController for feedback form.
 * Note the onSubmit method throws any Exception - this is desired behavior.
 * An exceptionResolver is defined in the portlet's Spring beans config that
 * will direct any error cases to a "pretty" view.
 * 
 * @author nblair@doit.wisc.edu
 * @version $Header: /usr/local/repos/portal/services/feedback/source/ca/uoguelph/ccs/portal/services/feedback/web/FeedbackFormController.java,v 1.1 2006/07/09 11:28:14 ljairath Exp $
 */
public class FeedbackFormController extends SimpleFormController {

    private IFeedbackMessageFormatter messageFormatter;
    private MailSender mailSender;

    private static final Log LOG = LogFactory.getLog(FeedbackFormController.class);
    
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
        String username = "";
        String fullName = "";
        String role = "";
        String emailAddress = "";
        String uogCourses = "";
        String uogInstructor = "";
        IPerson person = null;
        try {
            person = UserCredential.getPerson(request);
            IPrincipal principal = UserCredential.getPrincipal(person);
            
            if (principal != null) {
                username = UserCredential.getUsername(principal);
            }
            //password = UserCredential.getPassword(person); 
        } catch (Exception e) {
            LOG.debug("User Credentials" + e.getMessage());
        }                    
        Feedback feedback = new Feedback();
        fullName = (String)person.getAttribute("displayName");
        role = (String)person.getAttribute("userRole");
        emailAddress = (String)person.getAttribute("mail");
        
        feedback.setLogin(username);
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
