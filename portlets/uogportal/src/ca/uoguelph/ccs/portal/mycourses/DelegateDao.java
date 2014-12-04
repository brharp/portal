/*
 * $Id: DelegateDao.java,v 1.1 2006/04/28 14:04:19 brharp Exp $
 *
 * Copyright 2006 University of Guelph
 */

package ca.uoguelph.ccs.portal.mycourses;

import java.util.List;

/**
 * A delegate may act on behalf of the instructor.
 *
 * @author $Author: brharp $
 * @version $Revision: 1.1 $
 */
interface DelegateDao
{
    List getByUserId(String userId);
    List getByCourse(String course);
    int insertDelegate(Delegate delegate);
    int delete(String userId, String course);
}
