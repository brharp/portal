package ca.uoguelph.ccs.portal.mycourses;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.MappingSqlQuery;
import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.LinkedList;
import java.util.List;

public class CourseDaoJdbc implements CourseDao, InitializingBean
{
    private DataSource dataSource;

    private static final String SELECT_BY_CODE_SQL =
        "select code from uog_courses where code=?";

    abstract class AbstractSelect extends MappingSqlQuery
    {
        public AbstractSelect(DataSource dataSource, String sql)
        {
            super(dataSource,sql);
        }

        protected Object mapRow(ResultSet rs, int rowNum)
            throws SQLException
        {
            String code = rs.getString("code");
            String term = "";
            Course course = new Course(code, term);
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

    public List getByCodes(String[] codes)
    {
    	List all = new LinkedList();
    	for(int i = 0; i < codes.length; i++) {
    		all.addAll(getByCode(codes[i]));
    	}
    	return all;
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
