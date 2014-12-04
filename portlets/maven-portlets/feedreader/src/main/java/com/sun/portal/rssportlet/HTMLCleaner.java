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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class accepts HTML input and translates it into plain test.
 * It does this by removing all HTML tags, all Javascript blocks, and 
 * all entity references. It also replaces all repeating whitespace to
 * a single space.
 */
public class HTMLCleaner {
    private static interface Patterns {
        // javascript tags and everything in between
        public static final Pattern SCRIPTS = Pattern.compile("<(no)?script[^>]*>.*</(no)?script>", Pattern.DOTALL);
        // HTML/XML tags
        public static final Pattern TAGS = Pattern.compile("<[^>]+>");
        // entity references
        public static final Pattern ENTITY_REFS = Pattern.compile("&[^;]+;");
        // repeated whitespace
        public static final Pattern WHITESPACE = Pattern.compile("\\s\\s+");
    }
    
    /**
     * Clean the HTML input.
     */
    public String clean(String s) {
        if (s == null) {
            return null;
        }
        
        Matcher m;
        
        m = Patterns.SCRIPTS.matcher(s);
        s = m.replaceAll("");
        m = Patterns.TAGS.matcher(s);
        s = m.replaceAll("");
        m = Patterns.ENTITY_REFS.matcher(s);
        s = m.replaceAll("");
        m = Patterns.WHITESPACE.matcher(s);
        s = m.replaceAll(" ");
        
        return s;
    }
}
