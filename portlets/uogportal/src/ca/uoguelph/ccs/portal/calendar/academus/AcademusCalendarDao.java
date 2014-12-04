
package ca.uoguelph.ccs.portal.calendar.academus;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.SqlUpdate;
import org.springframework.jdbc.object.SqlFunction;
import org.springframework.jdbc.object.MappingSqlQuery;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import ca.uoguelph.ccs.portal.calendar.Calendar;
import ca.uoguelph.ccs.portal.calendar.dao.CalendarDao;

public class AcademusCalendarDao extends JdbcDaoSupport implements CalendarDao
{
    private Delete delete;
    private Insert insert;
    private Select select;
    private Update update;
    private NextId nextId;
    private Grant  grant;
    private Revoke revoke;
    private RevokeAll revokeAll;

    public void initDao()
    {
        select = new Select(getDataSource());
        update = new Update(getDataSource());
        delete = new Delete(getDataSource());
        insert = new Insert(getDataSource());
        nextId = new NextId(getDataSource());
        grant  = new Grant(getDataSource());
        revoke = new Revoke(getDataSource());
        revokeAll = new RevokeAll(getDataSource());
    }

    public Calendar get(String id) {
        if (id == null) {
            throw new NullPointerException();
        } else {
            return (AcademusCalendar) select.findObject(id);
        }
    }
    
    public Calendar update(Calendar cal) {
        if (cal == null) {
            throw new NullPointerException();
        } else {
            update.update((AcademusCalendar)cal);
            return cal;
        }
    }

    public Calendar insert(Calendar cal) {
        if (cal == null) {
            throw new NullPointerException();
        } else {
            insert.update((AcademusCalendar)cal);
            return cal;
        }
    }

    public void delete(Calendar cal) {
        if (cal == null) {
            throw new NullPointerException();
        } else {
            delete.update((AcademusCalendar)cal);
        }
    }


    public Calendar grant(Calendar cal, String principal, int mode)
    {
        if (cal == null || principal == null) {
            throw new NullPointerException();
        } else {
            String modeString = "FS";
            if ((mode & Calendar.WRITE) > 0) modeString = "W" + modeString;
            if ((mode & Calendar.READ) > 0) modeString = "R" + modeString;
            grant.update((AcademusCalendar)cal, principal, modeString);
            return cal;
        }
    }

    public Calendar revoke(Calendar cal, String principal)
    {
        if (cal == null || principal == null) {
            throw new NullPointerException();
        } else {
            revoke.update((AcademusCalendar)cal, principal);
            return cal;
        }
    }

    public Calendar revokeAll(Calendar cal)
    {
        if (cal == null) {
            throw new NullPointerException();
        } else {
            revokeAll.update((AcademusCalendar)cal);
            return cal;
        }
    }

    class Select extends MappingSqlQuery
    {
        private static final String SQL =
            "select id, calid, calname, owner from dpcs_calendar where calid=?";

        public Select(DataSource dataSource)
        {
            super(dataSource, SQL);
            declareParameter(new SqlParameter(Types.VARCHAR)); // calid
        }
        
        protected Object mapRow(ResultSet rs, int rowNum)
            throws SQLException
        {
            AcademusCalendar cal = new AcademusCalendar();
            cal.setId(new Integer(rs.getInt("id")));
            cal.setCalId(rs.getString("calid"));
            cal.setName(rs.getString("calname"));
            cal.setOwner(rs.getString("owner"));
            return cal;
        }
    }


    class NextId extends SqlFunction
    {
        private static final String SQL =
            "select max(id)+1 as nextid from dpcs_calendar";

        public NextId(DataSource dataSource)
        {
            super(dataSource, SQL);
        }
    }


    class Insert extends SqlUpdate
    {
        private static final String SQL =
            "insert into dpcs_calendar (id, calid, calname, owner) " +
            "values (?, ?, ?, ?)";

        public Insert(DataSource dataSource)
        {
            super(dataSource, SQL);
            declareParameter(new SqlParameter(Types.INTEGER)); // id
            declareParameter(new SqlParameter(Types.VARCHAR)); // calid
            declareParameter(new SqlParameter(Types.VARCHAR)); // calname
            declareParameter(new SqlParameter(Types.VARCHAR)); // owner
        }

        public int update(AcademusCalendar cal)
        {
            Integer id = new Integer(nextId.run());
            String calId = cal.getOwner() + "_" + id;
            Object params[] = new Object[] {
                id,
                calId,
                cal.getName(),
                cal.getOwner()
            };
            int rowCount = update(params);
            cal.setId(id);
            cal.setCalId(calId);
            return rowCount;
        }
    }


    abstract class Permit extends SqlUpdate
    {
        private static final String SQL =
            "insert into up_permission " +
            "(owner, principal_type, principal_key, activity, target, permission_type) " + 
            "value (?, ?, ?, ?, ?, ?)";

        private final Integer PRINCIPAL_TYPE = new Integer(2);

        public Permit(DataSource dataSource)
        {
            super(dataSource, SQL);
            declareParameter(new SqlParameter(Types.VARCHAR)); // owner
            declareParameter(new SqlParameter(Types.INTEGER)); // principal_type
            declareParameter(new SqlParameter(Types.VARCHAR)); // principal_key
            declareParameter(new SqlParameter(Types.VARCHAR)); // activity
            declareParameter(new SqlParameter(Types.VARCHAR)); // target
            declareParameter(new SqlParameter(Types.VARCHAR)); // permission_type
        }

        public int update(AcademusCalendar cal, String principal, String mode)
        {
            Object params[] = new Object[] {
                "net.unicon.academus.apps.calendar.DPCS",
                PRINCIPAL_TYPE,
                principal,
                mode,
                cal.getCalId(),
                getPermissionType()
            };
            return update(params);
        }

        protected abstract String getPermissionType();
    }

    class Grant extends Permit
    {
        public Grant(DataSource dataSource)
        {
            super(dataSource);
        }

        protected String getPermissionType()
        {
            return "GRANT";
        }
    }

    class Revoke extends SqlUpdate
    {
        private static final String SQL =
            "delete from up_permission where owner=? and principal_key=? and target=?";

        public Revoke(DataSource dataSource)
        {
            super(dataSource, SQL);
            declareParameter(new SqlParameter(Types.VARCHAR)); // owner
            declareParameter(new SqlParameter(Types.VARCHAR)); // principal_key
            declareParameter(new SqlParameter(Types.VARCHAR)); // target
        }

        public int update(AcademusCalendar cal, String principal)
        {
            Object params[] = new Object[] {
                "net.unicon.academus.apps.calendar.DPCS",
                principal,
                cal.getCalId()
            };
            return update(params);
        }
    }

    class RevokeAll extends SqlUpdate
    {
        private static final String SQL =
            "delete from up_permission where owner=? and target=?";

        public RevokeAll(DataSource dataSource)
        {
            super(dataSource, SQL);
            declareParameter(new SqlParameter(Types.VARCHAR)); // owner
            declareParameter(new SqlParameter(Types.VARCHAR)); // target
        }

        public int update(AcademusCalendar cal)
        {
            Object params[] = new Object[] {
                "net.unicon.academus.apps.calendar.DPCS",
                cal.getCalId()
            };
            return update(params);
        }
    }

    class Update extends SqlUpdate
    {
        private static final String SQL =
            "update dpcs_calendar set calname=?, owner=? where id=?";
    
        public Update(DataSource dataSource)
        {
            super(dataSource, SQL);
            declareParameter(new SqlParameter(Types.VARCHAR)); // calname
            declareParameter(new SqlParameter(Types.VARCHAR)); // owner
            declareParameter(new SqlParameter(Types.INTEGER)); // id
        }

        public int update(AcademusCalendar cal)
        {
            Object params[] = new Object[] {
                cal.getName(),
                cal.getOwner(),
                cal.getId()
            };
            return update(params);
        }
    }


    class Delete extends SqlUpdate
    {
        private static final String SQL =
            "delete from dpcs_calendar where id=?";

        public Delete(DataSource dataSource)
        {
            super(dataSource, SQL);
            declareParameter(new SqlParameter(Types.INTEGER)); // id
        }

        public int update(AcademusCalendar cal)
        {
            Object params[] = new Object[] {
                cal.getId()
            };
            return update(params);
        }
    }

}
