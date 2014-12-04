/*
 * $Header: /usr/local/repos/uogportal/src/ca/uoguelph/ccs/portal/mycourses/ResourceDao.java,v 1.2 2006/04/25 19:03:42 brharp Exp $
 *
 * Copyright 2006 University of Guelph
 */

package ca.uoguelph.ccs.portal.mycourses;

import java.util.List;

/**
 * Interface for course resource data access objects.
 *
 * @author $Author: brharp $
 * @version $Revision: 1.2 $
 */
interface ResourceDao
{
    /**
     * Retrieves published resources by course code.
     */
    public List getPubByCourse(String course);

    /**
     * Retrieves all resources (published and unpublished) by course
     * code.
     */
    public List getAllByCourse(String course);

    /**
     * Retrieves resources by resource ID.
     */
    public List getByKey(Integer key);

    /**
     * Inserts a resource into the database.
     */
    public int insertResource(Resource resource);

    /**
     * Updates an existing resource in the database.
     */
    public int updateResource(Resource resource);

    /**
     * Removes a resource from the database.
     */
    public int deleteByKey(Integer key);
}
