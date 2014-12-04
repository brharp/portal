/*
 * $Header: /usr/local/repos/uogportal/src/ca/uoguelph/ccs/portal/mycourses/Course.java,v 1.9 2006/08/02 12:21:00 brharp Exp $
 * 
 * Copyright 2006 University of Guelph
 */

package ca.uoguelph.ccs.portal.mycourses;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Date;
import java.util.Set;

/**
 * Course information for MyCourses. Some course properties are
 * retrieved from a data source, others are synthesized from the
 * course code.
 *
 * @author $Author: brharp $
 * @version $Revision: 1.9 $
 */
public class Course
{
    private String code; 
    private String semester;
    private String prefix;
    private String number;
    private String section;
    private String title;
    private String description;
    private Exam   exam;
    private Set    instructors;
    private Set    delegates;
    private String instructorSurname;

    /* Pattern to destructure course codes. */
    public static final String CODE_FORMAT = "([A-Z]*)\\*([0-9]*)( ([0-9]*))?";
    private static Pattern codePattern = Pattern.compile(CODE_FORMAT);

    /**
     * Constructs a new course object by course code.
     *
     * @param code course code
     */
    public Course(String code, String semester)
    {
        instructors = new java.util.HashSet();
        delegates = new java.util.HashSet();
        setCode(code);
        setSemester(semester);
    }

    /**
     * Set the semester.
     */
    public void setSemester(String semester)
    {
        this.semester = semester;
    }


    /**
     * Get the semester.
     */
    public String getSemester()
    {
        return semester;
    }

    /**
     * Set the course code property.
     *
     * @return course code
     */
    public String getCode()
    {
        return code;
    }

    /**
     * Get the course code property.
     *
     * @param code course code
     */
    public void setCode(String code) throws IllegalArgumentException
    {
        Matcher m = codePattern.matcher(code);

        if (m.matches()) {
            this.code = code;
            this.prefix = m.group(1);
            this.number = m.group(2); 
            this.section = m.group(4);
        }
        else {
            throw new IllegalArgumentException("Course code must match "+
                                               CODE_FORMAT);
        }
    }

    /**
     * Get the department fragment of the course code.
     */
    public String getPrefix()
    {
        return prefix;
    }

    /**
     * Get the course number fragment of the course code.
     */
    public String getNumber()
    {
        return number;
    }
    
    public String getSection()
    {
        return section;
    }

    /**
     * @return Returns the description.
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * @param description The description to set.
     */
    public void setDescription(String description)
    {
        this.description = description;
    }

    /**
     * @return Returns the title.
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * @param title The title to set.
     */
    public void setTitle(String title)
    {
        this.title = title;
    }

    /**
     * @return Returns the instructorSurname.
     */
    public String getInstructorSurname()
    {
        return instructorSurname;
    }

    /**
     * @param instructorSurname The instructorSurname to set.
     */
    public void setInstructorSurname(String instructorSurname)
    {
        this.instructorSurname = instructorSurname;
    }

    /**
     * @return Returns the exam info.
     */
    public Exam getExam()
    {
        return exam;
    }

    /**
     * @param exam The exam data to set.
     */
    public void setExam(Exam exam)
    {
        this.exam = exam;
    }

    /**
     * @param number The number to set.
     */
    public void setNumber(String number)
    {
        this.number = number;
    }

    /**
     * @param prefix The prefix to set.
     */
    public void setPrefix(String prefix)
    {
        this.prefix = prefix;
    }

    public void addInstructor(String userId)
    {
        instructors.add(userId);
    }

    public void removeInstructor(String userId)
    {
        instructors.remove(userId);
    }

    public void clearInstructors()
    {
        instructors.clear();
    }

    public boolean isInstructor(String userId)
    {
        return instructors.contains(userId);
    }

    public static boolean isInstructor(Course course, String userId)
    {
        return course.isInstructor(userId);
    }

    public void addDelegate(String userId)
    {
        delegates.add(userId);
    }

    public void removeDelegate(String userId)
    {
        delegates.remove(userId);
    }

    public void clearDelegates()
    {
        delegates.clear();
    }

    public boolean isDelegate(String userId)
    {
        return delegates.contains(userId);
    }

    public static boolean isDelegate(Course course, String userId)
    {
        return course.isDelegate(userId);
    }

    public String toString() {
        StringBuffer s = new StringBuffer();
        s.append(getCode());
        s.append(' ');
        s.append(getTitle());
        s.append(' ');
        s.append(getDescription().substring(0, 25));
        return s.toString();
    }
}
