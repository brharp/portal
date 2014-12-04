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
* the University of Wisconsin System.�
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
package edu.wisc.my.portlets.feedback.dao;

import org.springframework.mail.SimpleMailMessage;

import edu.wisc.my.portlets.feedback.beans.Feedback;

/**
 * Basic IFeedbackMessageFormatter implementation
 * Sends name, netid, email, phone, details
 * 
 * @author nblair@doit.wisc.edu
 * @version $Header: /usr/local/repos/portal/portlets/EAI/portal_portlets/FeedbackPortlet/source/edu/wisc/my/portlets/feedback/dao/SimpleFeedbackMessageFormatterImpl.java,v 1.1 2006/07/05 14:57:41 ljairath Exp $
 */
public class SimpleFeedbackMessageFormatterImpl implements
		IFeedbackMessageFormatter {

	// recipient address
    private String targetEmail;
    // from address - so bounces can be properly handled
    private String fromAddress;
    /**
     * @param fromAddress The fromAddress to set.
     */
    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }
    /**
     * @param targetEmail The targetEmail to set.
     */
    public void setTargetEmail(String targetEmail) {
        this.targetEmail = targetEmail;
    }
    
	/* (non-Javadoc)
	 * @see edu.wisc.my.portlets.feedback.dao.IFeedbackMessageFormatter#format(edu.wisc.my.portlets.feedback.beans.Feedback)
	 */
	public SimpleMailMessage format(Feedback feedback) {
		
		SimpleMailMessage message = new SimpleMailMessage();
		
		message.setTo(targetEmail);
		message.setFrom(fromAddress);
		        
		message.setSubject("[Feedback] " + feedback.getSubject());
		         
		StringBuffer details = new StringBuffer();
		details.append("NAME: ");
		details.append(null == feedback.getName() ?  "<empty>" : feedback.getName());
		details.append("\n");
		details.append("\n");
		        
		details.append("NETID: ");
		details.append(null == feedback.getNetid() ? "<empty>" : feedback.getNetid());
		details.append("\n");
		details.append("\n");
		
		details.append("EMAIL: ");
		details.append(null == feedback.getEmailAddress() ? "<empty>" : feedback.getEmailAddress());
		details.append("\n");
		details.append("\n");
		         
		details.append("PHONE: ");
		details.append(null == feedback.getPhoneNumber() ? "<empty>" : feedback.getPhoneNumber());
		details.append("\n");
		details.append("\n");
		       
		details.append("DETAILS: ");
		details.append(feedback.getDetails());
       
		message.setText(details.toString());
		
		return message;
	}

}
