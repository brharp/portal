/*
 * $Id: DelegateDaoJdbc.java,v 1.2 2006/05/05 12:42:04 brharp Exp $
 *
 * Copyright 2006 University of Guelph
 */

package ca.uoguelph.ccs.portal.mycourses;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.SqlUpdate;
import org.springframework.jdbc.object.MappingSqlQuery;
import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

/**
 * A delegate may make act on behalf of the instructor.
 *
 * @author $Author: brharp $
 * @version $Revision: 1.2 $
 */
public class DelegateDaoJdbc implements DelegateDao, InitializingBean
{
    private DataSource dataSource;

    private static final String SELECT_BY_USER_ID_SQL =
        "select userid, course from uog_course_delegates where userid=?";

    private static final String SELECT_BY_COURSE_SQL =
        "select userid, course from uog_course_delegates where course=?";

    private static final String INSERT_DELEGATE_SQL =
        "insert into uog_course_delegates (userid, course) values (?,?)";

    private static final String DELETE_DELEGATE_SQL =
        "delete from uog_course_delegates where userid=? and course=?";

    /**
     * Reusable query which converts each row of the result set into a
     * Delegate object.
     */
    abstract class AbstractSelect extends MappingSqlQuery
    {
        public AbstractSelect(DataSource dataSource, String sql)
        {
            super(dataSource,sql);
        }

        protected Object mapRow(ResultSet rs, int rowNum)
            throws SQLException
        {
            Delegate delegate = new Delegate();
            delegate.setUserId(rs.getString("userid"));
            delegate.setCourse(rs.getString("course"));
            return delegate;
        }
    }

    class SelectByUserId extends AbstractSelect
    {
        public SelectByUserId(DataSource dataSource)
        {
            super(dataSource, SELECT_BY_USER_ID_SQL);
            declareParameter(new SqlParameter(Types.VARCHAR));
        }
    }

    class SelectByCourse extends AbstractSelect
    {
        public SelectByCourse(DataSource dataSource)
        {
            super(dataSource, SELECT_BY_COURSE_SQL);
            declareParameter(new SqlParameter(Types.VARCHAR));
        }
    }

    class InsertDelegate extends SqlUpdate
    {
        public InsertDelegate(DataSource dataSource)
        {
            super(dataSource, INSERT_DELEGATE_SQL);
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
        }
        
        public int update(Delegate delegate)
        {
            Object params[] = new Object[] {
                delegate.getUserId(),
                delegate.getCourse()
            };
            return update(params);
        }
    }

    class DeleteDelegate extends SqlUpdate
    {
        public DeleteDelegate(DataSource dataSource)
        {
            super(dataSource, DELETE_DELEGATE_SQL);
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
        }

        public int update(Delegate delegate)
        {
            Object params[] = new Object[] {
                delegate.getUserId(),
                delegate.getCourse()
            };
            return update(params);
        }
    }
    
    private SelectByUserId selectByUserId;
    private SelectByCourse selectByCourse;
    private InsertDelegate insertDelegate;
    private DeleteDelegate deleteDelegate;

    public void afterPropertiesSet() throws Exception
    {
        if (dataSource == null)
            throw new BeanCreationException("null dataSource");

        selectByUserId = new SelectByUserId(dataSource);
        selectByCourse = new SelectByCourse(dataSource);
        insertDelegate = new InsertDelegate(dataSource);
        deleteDelegate = new DeleteDelegate(dataSource);
    }
    
    public List getByUserId(String userId)
    {
        return selectByUserId.execute(userId);
    }
    
    public List getByCourse(String course)
    {
        return selectByCourse.execute(course);
    }

    public int insertDelegate(Delegate delegate)
    {
        return insertDelegate.update(delegate);
    }

    public int deleteDelegate(Delegate delegate)
    {
        return deleteDelegate.update(delegate);
    }

    public int delete(String userId, String course)
    {
        return deleteDelegate.update(new Object[]{userId,course});
    }

    public DataSource getDataSource()
    {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource)
    {
        this.dataSource = dataSource;
    }
}
