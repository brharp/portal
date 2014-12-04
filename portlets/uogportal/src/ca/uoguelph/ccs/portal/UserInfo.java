/*
 * $Id: UserInfo.java,v 1.7 2006/09/05 16:31:09 brharp Exp $
 *
 * Copyright 2006 University of Guelph
 */

package ca.uoguelph.ccs.portal;

import java.util.Map;

/**
 * Constants and accessors for entries in the USER_INFO map used by
 * the UofG portal.
 * 
 * <p>
 *
 * KEEP THIS FILE SYNCHRONIZED WITH THE USER ATTRIBUTES DEFINED IN:
 * portlet-web.xml
 *
 * <p>
 *
 * This class can be used in 3 ways: 1) use the public constants as
 * keys to get data directly from the user info map, 2) use the static
 * accessor methods to get fields from the user info map, cast to the
 * correct data type, or 3) wrap a user info map with this object so
 * it may be treated as a bean. This final option is especially useful
 * when writing JSP.
 *
 * @author $Author: brharp $
 * @version $Revision: 1.7 $
 */
public class UserInfo
{
    public static final String USER_ID = "username";
    public static final String USER_PASSWORD = "password";
    public static final String USER_COURSES = "uogcourses";
    public static final String USER_INSTRUCTS = "uoginstructor";
    public static final String USER_ROLE = "userRole";
    public static final String USER_EMAIL = "mail";
    public static final String USER_TERM = "term";
    public static final String USER_SURNAME = "lastName";
    public static final String USER_DEGREE = "degree";
    public static final String USER_ACADEMIC_LEVEL = "academiclevel";

    public static final String ROLE_STUDENT = "Student";
    public static final String ROLE_EMPLOYEE = "Employee";
    public static final String ROLE_FACULTY = ROLE_EMPLOYEE + ":Faculty";
    public static final String ROLE_STAFF = ROLE_EMPLOYEE + ":Staff";

    private Map userInfo;

    public UserInfo(Map userInfo)
    {
        this.userInfo = userInfo;
    }

    public Object get(Object key)
    {
        return userInfo.get(key);
    }

    // Bean Properties

    public String getId()
    {
        return getId(userInfo);
    }

    public String getPassword()
    {
        return getPassword(userInfo);
    }

    public String getCourses()
    {
        return getCourses(userInfo);
    }

    public String getInstructs()
    {
        return getInstructs(userInfo);
    }

    public String getRole()
    {
        return getRole(userInfo);
    }

    public String getEmail()
    {
        return getEmail(userInfo);
    }

    public String getTerm()
    {
        return getTerm(userInfo);
    }

    public boolean isStudent()
    {
        return isStudent(userInfo);
    }

    public boolean isEmployee()
    {
        return isEmployee(userInfo);
    }

    public boolean isFaculty()
    {
        return isFaculty(userInfo);
    }

    public boolean isStaff()
    {
        return isStaff(userInfo);
    }

    public String getSurname()
    {
        return getSurname(userInfo);
    }

    public String getDegree()
    {
        return getDegree(userInfo);
    }

    public String getAcademicLevel()
    {
        return getAcademicLevel(userInfo);
    }

    // Static Accessor Methods

    public static String getId(Map userInfo)
    {
        return (String)userInfo.get(USER_ID);
    }

    public static String getPassword(Map userInfo)
    {
        return (String)userInfo.get(USER_PASSWORD);
    }

    public static String getCourses(Map userInfo)
    {
        return (String)userInfo.get(USER_COURSES);
    }
    
    public static String getInstructs(Map userInfo)
    {
        return (String)userInfo.get(USER_INSTRUCTS);
    }
    
    public static String getRole(Map userInfo)
    {
        return (String)userInfo.get(USER_ROLE);
    }

    public static String getEmail(Map userInfo)
    {
        return (String)userInfo.get(USER_EMAIL);
    }
    
    public static String getTerm(Map userInfo)
    {
        return (String)userInfo.get(USER_TERM);
    }
    
    private static boolean isRole(Map userInfo, String rolePrefix)
    {
        String role = getRole(userInfo);
        if (null != role) {
            return role.startsWith(rolePrefix);
        } else {
            return false;
        }
    }

    public static boolean isStudent(Map userInfo)
    {
        return isRole(userInfo, ROLE_STUDENT);
    }
    
    public static boolean isFaculty(Map userInfo)
    {
        return isRole(userInfo, ROLE_FACULTY);
    }

    public static boolean isStaff(Map userInfo)
    {
        return isRole(userInfo, ROLE_STAFF);
    }

    public static boolean isEmployee(Map userInfo)
    {
        return isRole(userInfo, ROLE_EMPLOYEE);
    }

    public static String getSurname(Map userInfo)
    {
        return (String)userInfo.get(USER_SURNAME);
    }

    public static String getDegree(Map userInfo)
    {
        return (String)userInfo.get(USER_DEGREE);
    }

    public static String getAcademicLevel(Map userInfo)
    {
        return (String)userInfo.get(USER_ACADEMIC_LEVEL);
    }
}

