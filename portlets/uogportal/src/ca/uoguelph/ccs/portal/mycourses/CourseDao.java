/*
 * $Header: /usr/local/repos/uogportal/src/ca/uoguelph/ccs/portal/mycourses/CourseDao.java,v 1.3 2006/06/01 19:38:10 brharp Exp $
 * 
 * Copyright 2006 University of Guelph
 */
package ca.uoguelph.ccs.portal.mycourses;

import java.util.List;

/**
 * Interface for Course Data Access Objects.
 */
interface CourseDao
{
	/**
	 * Returns a list of 1 course with the given course code, or an empty list
	 * if the course code is not found.
	 * 
	 * @param code course code
	 * @return a list of 1 course, or an empty list.
	 */
	public List getByCode(String code);

	/**
	 * Returns a list of courses with the given course codes, or an empty list
	 * if none of the course codes are found.
	 * 
	 * @param codes course codes
	 * @return a list of courses, or an empty list.
	 */
	public List getByCodes(String[] codes);
}
