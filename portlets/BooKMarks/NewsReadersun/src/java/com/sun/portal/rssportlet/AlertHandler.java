/*
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the License).  You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the license at
 * http://www.sun.com/cddl/cddl.html or
 * at portlet-repository/CDDLv1.0.txt.
 * See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL
 * Header Notice in each file and include the License file
 * at portlet-repository/CDDLv1.0.txt. 
 * If applicable, add the following below the CDDL Header,
 * with the fields enclosed by brackets [] replaced by
 * you own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 */

package com.sun.portal.rssportlet;

public class AlertHandler {
    private String successSummary = null;
    private String errorSummary = null;
    private String successDetail = null;
    private String errorDetail = null;
    
    private boolean successRendered = false;
    private boolean errorRendered = false;    
    
    public String getSuccessSummary() {
        String s = successSummary;
        return s;
    }
    
    public String getSuccessDetail() {
        String s = successDetail;
        return s;
    }
    
    public void setSuccess(String successSummary) {
        successRendered = true;
        this.successSummary = successSummary;
        this.successDetail = null;
    }
    
    public void setSuccess(String successSummary, String successDetail) {
        successRendered = true;
        this.successSummary = successSummary;
        this.successDetail = successDetail;
    }
    
    public void setSuccess(String successSummary, Throwable t) {
        successRendered = true;        
        this.successSummary = successSummary;
        this.successDetail = t.getMessage();
    }
    
    public String getErrorSummary() {
        String s = errorSummary;
        return s;
    }    
    
    public String getErrorDetail() {
        String s = errorDetail;
        return s;
    }
    
    public void setError(String errorSummary) {
        errorRendered = true;        
        this.errorSummary = errorSummary;
        this.errorDetail = null;        
    }
    
    public void setError(String errorSummary, String errorDetail) {
        errorRendered = true;        
        this.errorSummary = errorSummary;
        this.errorDetail = errorDetail;
    }
    
    public void setError(String errorSummary, Throwable t) {
        errorRendered = true;        
        this.errorSummary = errorSummary;
        this.errorDetail = t.getMessage();
    }
    
    
    public boolean isSuccessRendered() {
        boolean r = successRendered;
        if (successRendered) {
            successRendered = false;
        }
        return r;
    }
    
    public boolean isErrorRendered() {
        boolean r = errorRendered;
        if (errorRendered) {
            errorRendered = false;
        }
        return r;
    }
    
    public boolean isErrorExists() {
        return errorRendered;
    }
}
