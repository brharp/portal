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

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * This class is a wrapper around the Java <code>ResourceBundle</code> 
 * mechanism. It catches the missing resource exception that would
 * normally result from a missing resource and instead returns the 
 * string key.This allows the application to function with a missing
 * resource string, but still allows the problem to be identified.
 *
 * This class does not catch the missing resource exception that results
 * from a non-existent reosurce bundle.
 */
public class Resources {    
    private ResourceBundle rb;
    
	/** 
	 * Construct a new Resources object.
	 *
	 * @throws MissingResourceException if the named bundle
	 * is not found.
	 */
    public Resources(String bundleName, Locale locale) {
        init(bundleName, locale);
    }
    
    private void init(String bundleName, Locale locale) {
        rb = ResourceBundle.getBundle(bundleName, locale);
    }        
    
	/** 
	 * Get a resource string from this resource object.
	 * If the resource string identified by the key argument 
	 * is not found, the key itself is returned.
	 */
    public String get(String key) {
        try {
            return rb.getString(key);            
        } catch (MissingResourceException mre) {
            return key;
        }
    }
}
