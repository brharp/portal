package ca.uoguelph.ccs.portal.mycourses;

import java.util.List;

interface CourseDao
{
    public List getByCode(String code);
}
