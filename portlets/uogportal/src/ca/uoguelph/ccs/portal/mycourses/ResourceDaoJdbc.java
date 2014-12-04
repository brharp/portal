/*
 * $Header: /usr/local/repos/uogportal/src/ca/uoguelph/ccs/portal/mycourses/ResourceDaoJdbc.java,v 1.6 2006/07/10 15:35:48 brharp Exp $
 *
 * Copyright 2006 University of Guelph
 */

package ca.uoguelph.ccs.portal.mycourses;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.SqlUpdate;
import org.springframework.jdbc.object.MappingSqlQuery;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;
import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

/**
 * JDBC implementation of the Resource Data Access Object interface.
 *
 * @author $Author: brharp $
 * @version $Revision: 1.6 $
 */
public class ResourceDaoJdbc implements ResourceDao, InitializingBean
{
    private DataSource dataSource;
    private DataFieldMaxValueIncrementer incrementer;

    private static final String SELECT_PUB_BY_COURSE_SQL =
        "select resource_id, course, description, url, published "+
        "from uog_course_resources where course=? "+
        "and published=TRUE order by resource_id";

    private static final String SELECT_ALL_BY_COURSE_SQL =
        "select resource_id, course, description, url, published " +
        "from uog_course_resources where course=? " +
        "order by resource_id";

    private static final String SELECT_BY_RESOURCE_ID_SQL =
        "select resource_id, course, description, url, published " +
        "from uog_course_resources where resource_id=?";

    private static final String UPDATE_RESOURCE_SQL =
        "update uog_course_resources " +
        "set course=?, description=?, url=?, published=? " +
        "where resource_id=?";

    private static final String INSERT_RESOURCE_SQL =
        "insert into uog_course_resources " +
        "(resource_id, course, description, url, published) " +
        "values (?, ?, ?, ?, ?)";

    private static final String DELETE_BY_RESOURCE_ID_SQL =
        "delete from uog_course_resources where resource_id=?";

    /**
     * Reusable query which converts each row of the result set into a
     * Resource object.
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
            UrlResource resource = new UrlResource();
            resource.setKey(new Integer(rs.getInt("resource_id")));
            resource.setCourse(rs.getString("course"));
            resource.setDescription(rs.getString("description"));
            resource.setUrl(rs.getString("url"));
            resource.setPublished(new Boolean(rs.getBoolean("published")));
            return resource;
        }
    }

    /**
     * Concrete subclass of AbstractSelect to query all resources by
     * course code.
     */
    class SelectAllByCourse extends AbstractSelect
    {
        public SelectAllByCourse(DataSource dataSource)
        {
            super(dataSource, SELECT_ALL_BY_COURSE_SQL);
            declareParameter(new SqlParameter(Types.VARCHAR));
        }
    }

    /**
     * Concrete subclass of AbstractSelect to query published
     * resources by course code.
     */
    class SelectPubByCourse extends AbstractSelect
    {
        public SelectPubByCourse(DataSource dataSource)
        {
            super(dataSource, SELECT_PUB_BY_COURSE_SQL);
            declareParameter(new SqlParameter(Types.VARCHAR));
        }
    }

    /**
     * Concrete subclass of AbstractSelect to query by resource id.
     */
    class SelectByKey extends AbstractSelect
    {
        public SelectByKey(DataSource dataSource)
        {
            super(dataSource, SELECT_BY_RESOURCE_ID_SQL);
            declareParameter(new SqlParameter(Types.INTEGER));
        }
    }
        
    /**
     * Subclass of SqlUpdate to insert resources.
     */
    class InsertResource extends SqlUpdate
    {
        public InsertResource(DataSource dataSource)
        {
            super(dataSource, INSERT_RESOURCE_SQL);
            declareParameter(new SqlParameter(Types.INTEGER));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.BOOLEAN));
        }
        
        public int update(Resource resource)
        {
            Integer key = new Integer(incrementer.nextIntValue());
            Object params[] = new Object[] {
                key,
                resource.getCourse(),
                resource.getDescription(),
                resource.getUrl(),
                resource.getPublished()
            };
            int rowCount = update(params);
            resource.setKey(key);
            return rowCount;
        }
    }

    /**
     * Subclass of SqlUpdate to update resources.
     */
    class UpdateResource extends SqlUpdate
    {
        public UpdateResource(DataSource dataSource)
        {
            super(dataSource, UPDATE_RESOURCE_SQL);
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.BOOLEAN));
            declareParameter(new SqlParameter(Types.INTEGER));
        }
        
        public int update(Resource resource)
        {
           Object params[] = new Object[] {
               resource.getCourse(),
               resource.getDescription(),
               resource.getUrl(),
               resource.getPublished(),
               resource.getKey()
           };
           return update(params);
        }
    }

    /**
     * Subclass of SqlUpdate to delete resources by key.
     */
    class DeleteByKey extends SqlUpdate
    {
        public DeleteByKey(DataSource dataSource)
        {
            super(dataSource, DELETE_BY_RESOURCE_ID_SQL);
            declareParameter(new SqlParameter(Types.INTEGER));
        }

        public int delete(Integer key)
        {
            return update(new Object[]{ key });
        }
    }

    private SelectPubByCourse selectPubByCourse;
    private SelectAllByCourse selectAllByCourse;
    private SelectByKey       selectByKey;
    private UpdateResource    updateResource;
    private InsertResource    insertResource;
    private DeleteByKey       deleteByKey;

    public void afterPropertiesSet() throws Exception
    {
        if (dataSource == null)
            throw new BeanCreationException("null dataSource");

        selectAllByCourse = new SelectAllByCourse(dataSource);
        selectPubByCourse = new SelectPubByCourse(dataSource);
        selectByKey       = new SelectByKey(dataSource);
        updateResource    = new UpdateResource(dataSource);
        insertResource    = new InsertResource(dataSource);
        deleteByKey       = new DeleteByKey(dataSource);
    }

    public List getPubByCourse(String course)
    {
        return selectPubByCourse.execute(new Object[]{ course });
    }

    public List getAllByCourse(String course)
    {
        return selectAllByCourse.execute(new Object[]{ course });
    }

    public List getByKey(Integer key)
    {
        return selectByKey.execute(new Object[]{ key });
    }

    public int updateResource(Resource resource)
    {
        return updateResource.update(resource);
    }

    public int insertResource(Resource resource)
    {
        return insertResource.update(resource);
    }

    public int deleteByKey(Integer key)
    {
        return deleteByKey.delete(key);
    }

    public DataSource getDataSource()
    {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource)
    {
        this.dataSource = dataSource;
    }

    public void setIncrementer(DataFieldMaxValueIncrementer incrementer)
    {
        this.incrementer = incrementer;
    }
}
