/*
 * $Id: Courses.java,v 1.1 2006/06/01 19:38:10 brharp Exp $
 * 
 * Copyright 2006 University of Guelph
 */

package ca.uoguelph.ccs.portal.mycourses;

import java.util.LinkedList;
import java.util.List;

/**
 * @author brharp
 *
 */
public class Courses
{
	private List courses = new LinkedList();
	
	public List getCourses()
	{
		return courses;
	}
	
	public void add(Course course) {
		courses.add(course);
	}
}
