package ca.uoguelph.ccs.portal.mycourses;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.MappingSqlQuery;
import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

public class CourseDaoJdbc implements CourseDao, InitializingBean
{
    private DataSource dataSource;

    private static final String SELECT_BY_CODE_SQL =
        "select * from uog_courses where code=?";

    abstract class AbstractSelect extends MappingSqlQuery
    {
        public AbstractSelect(DataSource dataSource, String sql)
        {
            super(dataSource,sql);
        }

        protected Object mapRow(ResultSet rs, int rowNum)
            throws SQLException
        {
            Course course = new Course();
            course.setCode(rs.getString("code"));
            course.setWebsiteUrl(rs.getString("website"));
            course.setResourcesUrl(rs.getString("resources"));
            course.setClassListUrl(rs.getString("classlist"));
            return course;
        }
    }

    class SelectByCode extends AbstractSelect
    {
        public SelectByCode(DataSource dataSource)
        {
            super(dataSource, SELECT_BY_CODE_SQL);
            declareParameter(new SqlParameter(Types.VARCHAR));
        }
    }

    private SelectByCode selectByCode;

    public void afterPropertiesSet() throws Exception
    {
        if (dataSource == null)
            throw new BeanCreationException("null dataSource");

        selectByCode = new SelectByCode(dataSource);
    }

    public List getByCode(String code)
    {
        return selectByCode.execute(new Object[]{ code });
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
