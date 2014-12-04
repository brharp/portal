/*
 * $Id: CourseService.java,v 1.9 2006/09/05 16:31:09 brharp Exp $
 * 
 * Copyright 2006 University of Guelph
 */

package ca.uoguelph.ccs.portal.mycourses;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.Date;
import java.util.Calendar;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.ParseException;

import javax.naming.*;
import javax.naming.directory.*;
import javax.naming.ldap.*;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.DOMBuilder;
import org.jdom.xpath.XPath;

import ca.uoguelph.ccs.ws.XMLHttpRequestObject;
import ca.uoguelph.ccs.portal.UserInfo;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

import org.springframework.beans.factory.InitializingBean;

/**
 * Service for accessing course and exam information.
 *
 * <p>
 *
 * TODO: Instead of formal parameters, it may be possible to pass all
 * query parameters as a map, and replace key variables in the URL
 * (ie. ${courses}) with map values. This would require handling
 * multiple courses in the XML response.
 *
 * @author brharp
 */
public class CourseService implements InitializingBean
{
    private Map semesterCache = new HashMap();

    private XPath coursePath;
    private XPath prefixPath;
    private XPath numberPath;
    private XPath titlePath;
    private XPath descriptionPath;
	
    private XPath examPath;
    private XPath examDatePath;
    private DateFormat examDateFormat;
    private XPath examStartPath;
    private XPath examEndPath;
    private DateFormat examTimeFormat;
    private XPath examRoomPath;
    private XPath examBldgPath;

    private Log log;

    private DelegateDao delegateDao;

    /** Address of the course information service. */
    private String courseServiceUrl;

    /** Address of the exam information service. */
    private String examServiceUrl;

    /** Character(s) prefixing replacement tokens in URLs. */
    private String replacementTokenPrefix = "\\{";

    /** Character(s) following replacement tokens in URLs. */
    private String replacementTokenSuffix = "\\}";

    /** Placeholder token to replace with course code in request
     * URLs. */
    private String codeReplacementToken = "code";

    /** Placeholder token to replace with course code in request
     * URLs. */
    private String semesterReplacementToken = "sem";

    /** List of user info attributes to replace as tokens in URLs. */
    private List userInfoReplacementTokens = new java.util.ArrayList();

    /** Current semester. */
    private String semester;

    public CourseService()
    {
        log = LogFactory.getLog(CourseService.class);

        try {
            // Course information accessors.
            coursePath      = XPath.newInstance("//courses/calendar/course");
            prefixPath      = XPath.newInstance("@prefix");
            numberPath      = XPath.newInstance("@number");
            titlePath       = XPath.newInstance("title/text()");
            descriptionPath = XPath.newInstance("description/text()");
            // Exam information accessors.
            examPath       = XPath.newInstance("//exams/semester/course");
            examDatePath   = XPath.newInstance("date/@value");
            examDateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd");
            examStartPath  = XPath.newInstance("start/@value");
            examEndPath    = XPath.newInstance("end/@value");
            examTimeFormat = new java.text.SimpleDateFormat("kk:mm:ss");
            examRoomPath   = XPath.newInstance("room/text()");
            examBldgPath   = XPath.newInstance("building/text()");
        } catch (JDOMException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
	
    public void afterPropertiesSet() throws Exception 
    {
        if (this.delegateDao == null)
            throw new IllegalArgumentException("delegateDao == null");

        if (this.courseServiceUrl == null)
            throw new IllegalArgumentException("courseServiceUrl == null");
    }
    
    public Course getByCode(String code, String semester, Map userInfo)
    {		
        // Get course cache for this semester, creating one if
        // necessary.
        Map courseCache = (Map)semesterCache.get(semester);
        if (courseCache == null) {
            courseCache = new WeakHashMap();
            semesterCache.put(semester, courseCache);
        }
        // Get cached course, creating a new one if necessary.
        Course course = (Course)courseCache.get(code);
        if (course == null) {
            course = new Course(code, semester);
            courseCache.put(code, course);
        }

        // Get basic course information.
        try {
            XMLHttpRequestObject r = new XMLHttpRequestObject();
            r.open("GET", replaceTokens(courseServiceUrl, code, semester, userInfo), false);
            r.send();
            if(HttpURLConnection.HTTP_OK == r.getStatus() && r.getResponseXML() != null) {
                DOMBuilder domBuilder = new DOMBuilder();
                Document doc = domBuilder.build(r.getResponseXML());
                try {
                    Object courseNode = coursePath.selectSingleNode(doc);
                    if (courseNode != null) {
                        course.setPrefix(prefixPath.valueOf(courseNode));
                        course.setNumber(numberPath.valueOf(courseNode));
                        course.setTitle(titlePath.valueOf(courseNode));
                        course.setDescription(descriptionPath.valueOf(courseNode));
                        System.out.println(course);
                    }
                } catch (JDOMException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // Get exam information.
        course.setExam(null);
        try {
            XMLHttpRequestObject r = new XMLHttpRequestObject();
            r.open("GET", replaceTokens(examServiceUrl, code, semester, userInfo), false);
            r.send();
            if(HttpURLConnection.HTTP_OK == r.getStatus() && r.getResponseXML() != null) {
                DOMBuilder domBuilder = new DOMBuilder();
                Document doc = domBuilder.build(r.getResponseXML());
                try {
                    Object examNode = examPath.selectSingleNode(doc);
                    String date = examDatePath.valueOf(examNode);
                    String start = examStartPath.valueOf(examNode);
                    String end = examEndPath.valueOf(examNode);
                    String room = examRoomPath.valueOf(examNode);
                    String building = examBldgPath.valueOf(examNode);
                    // Create data object.
                    Exam exam = new Exam();
                    try {
                        // Parse exam start and end times.
                        Calendar s = Calendar.getInstance();
                        Calendar e = Calendar.getInstance();
                        s.setTime(examTimeFormat.parse(start));
                        e.setTime(examTimeFormat.parse(end));
                        if (date != null) {
                            // If the date is given seperately (from
                            // the time), adjust times accordingly.
                            Calendar d = Calendar.getInstance();
                            d.setTime(examDateFormat.parse(date));
                            s.set(Calendar.YEAR, d.get(Calendar.YEAR));
                            s.set(Calendar.MONTH, d.get(Calendar.MONTH));
                            s.set(Calendar.DATE, d.get(Calendar.DATE));
                            e.set(Calendar.YEAR, d.get(Calendar.YEAR));
                            e.set(Calendar.MONTH, d.get(Calendar.MONTH));
                            e.set(Calendar.DATE, d.get(Calendar.DATE));
                        }
                        exam.setStartDate(s.getTime());
                        exam.setEndDate(e.getTime());
                    } catch(ParseException e) {
                        log.error(e);
                    }
                    exam.setRoom(room);
                    exam.setBuilding(building);
                    course.setExam(exam);
                } catch(JDOMException e) {
                    e.printStackTrace();
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        
        // Get instructor.
        try {
            StringBuffer q = new StringBuffer();
            q.append("(uoginstructor=*");
            q.append(course.getPrefix());
            q.append("\\*");
            q.append(course.getNumber());
            String section = course.getSection();
            if (section != null && section.length() > 0) {
                q.append(" ");
                q.append(section);
            }
            q.append("*)");
            String query = q.toString();
            log.debug("LDAP query: "+query);
            DirContext ctx = new InitialDirContext();
            NamingEnumeration answer = ctx.search
                ("ldap://directory.uoguelph.ca:389/ou=people,o=uoguelph.ca",
                 query, null);
            if(answer.hasMore()) {
                log.debug("Found instructor for " + code);
                SearchResult nextResult = (SearchResult)answer.next();
                Attributes attributes = nextResult.getAttributes();
                String sn = (String)attributes.get("sn").get(0);
                log.debug("Instructor for " + code + " is " + sn);
                course.setInstructorSurname(sn);
            }
            ctx.close();
        }
        catch(Exception e) { 
            e.printStackTrace();
        }

        return course;
    }

    /**
     * Retrieves all courses relevant to this user.
     */
    public List getByUser(Map userInfo)
    {
        List courses = new LinkedList();

        // Get current semester.
        String semester = UserInfo.getTerm(userInfo);
        if (semester == null) {
            log.debug("semester is null, using default");
            semester = getSemester(); // Get a default.
        }
        log.debug("semester=["+semester+"]");

        // Get User ID.
        String userId = UserInfo.getId(userInfo);
        log.debug("userId=["+userId+"]");

        // Retrieve the courses in which this user is a student.
        String courseInfo = UserInfo.getCourses(userInfo);
        log.debug("courseInfo=["+courseInfo+"]");
        if (courseInfo != null) {
            String[] courseCodes = courseInfo.split(",");
            for(int i = 0; i < courseCodes.length; i++) {
                try {
                    Course c = getByCode(courseCodes[i], semester, userInfo);
                    c.removeInstructor(userId);
                    c.removeDelegate(userId);
                    courses.add(c);
                }
                catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // Retrieve the courses for which this user is the instructor.
        String facultyInfo = UserInfo.getInstructs(userInfo);
        log.debug("facultyInfo=["+facultyInfo+"]");
        if (facultyInfo != null) {
            String[] facultyCodes = facultyInfo.split(",");
            for(int i = 0; i < facultyCodes.length; i++) {
                try {
                    Course c = getByCode(facultyCodes[i], semester, userInfo);
                    c.removeDelegate(userId);
                    c.addInstructor(userId);
                    courses.add(c);
                }
                catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // Retrieve the courses for which the instructor has delegated
        // responsibility to this user.
        List delegateInfo = delegateDao.getByUserId(userId);
        if (delegateInfo != null) {
            for (Iterator it = delegateInfo.iterator(); it.hasNext();) {
                try {
                    Delegate del = (Delegate)it.next();
                    Course c = getByCode(del.getCourse(), semester, userInfo);
                    c.removeInstructor(userId);
                    c.addDelegate(userId);
                    courses.add(c);
                }
                catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return courses;
    }

    /**
     * Set the delegateDao property.
     */
    public void setDelegateDao(DelegateDao delegateDao) 
    {
        this.delegateDao = delegateDao;
    }

    /**
     * Set the courseServiceUrl property.
     *
     * @param courseServiceUrl key in the USER_INFO map
     */
    public void setCourseServiceUrl(String courseServiceUrl)
    {
        this.courseServiceUrl = courseServiceUrl;
    }

    /**
     * Set the examServiceUrl property.
     *
     * @param examServiceUrl key in the USER_INFO map
     */
    public void setExamServiceUrl(String examServiceUrl)
    {
        this.examServiceUrl = examServiceUrl;
    }

    /**
     * Set the userInfoReplacementTokens property.
     *
     * @param listOfTokens list of user attributes to treat as replacement tokens.
     */
    public void setUserInfoReplacementTokens(List listOfTokens)
    {
        this.userInfoReplacementTokens = listOfTokens;
    }

    /**
     * Get the semester.
     */
    public String getSemester()
    {
        return semester;
    }

    /**
     * Set the semester.
     */
    public void setSemester(String semester)
    {
        this.semester = semester;
    }

    private String replaceTokens(String url, String code, String semester, Map userInfo)
    {
        String u = url;

        if (code != null) {
            u = replaceToken(u, codeReplacementToken, code);
        }

        if (semester != null) {
            u = replaceToken(u, semesterReplacementToken, semester);
        }

        for(Iterator iter = userInfoReplacementTokens.iterator(); iter.hasNext();) {
            Object key = iter.next();
            if (key instanceof String) {
                Object value = userInfo.get(key);
                if (value instanceof String) {
                    u = replaceToken(u, (String)key, (String)value);
                }
            }
        }
        
        return u;
    }

    private String replaceToken(String url, String key, String value)
    {
        try {
            return url.replaceAll(replacementTokenPrefix + key + replacementTokenSuffix, urlEncode(value));
        } catch(java.io.UnsupportedEncodingException uee) {
            log.error(uee);
            return url;
        }
    }

    private String urlEncode(String value) throws java.io.UnsupportedEncodingException
    {
        return java.net.URLEncoder.encode(value, "UTF-8");
    }
}
