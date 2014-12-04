
package ca.uoguelph.ccs.portal.calendar.academus;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.object.SqlUpdate;
import org.springframework.jdbc.object.MappingSqlQuery;
import java.sql.Types;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;
import java.util.List;
import ca.uoguelph.ccs.portal.calendar.Calendar;
import ca.uoguelph.ccs.portal.calendar.Event;
import ca.uoguelph.ccs.portal.calendar.CalendarAndEvent;
import ca.uoguelph.ccs.portal.calendar.dao.CalendarEventDao;

public class AcademusCalendarEventDao extends JdbcDaoSupport
    implements CalendarEventDao
{
    private Delete delete;
    private Insert insert;
    private FindByCalendar findByCalendar;

    public void initDao() throws Exception
    {
        delete = new Delete(getDataSource());
        insert = new Insert(getDataSource());
        findByCalendar = new FindByCalendar(getDataSource());
    }

    public void insert(CalendarAndEvent calendarAndEvent) {
        if (calendarAndEvent == null) {
            throw new NullPointerException();
        } else {
            insert.update(calendarAndEvent);
        }
    }

    public void delete(CalendarAndEvent calendarAndEvent) {
        if (calendarAndEvent == null) {
            throw new NullPointerException();
        } else {
            delete.update(calendarAndEvent);
        }
    }

    public List findByCalendar(Calendar cal) {
        if (cal == null) {
            throw new NullPointerException();
        } else {
            return findByCalendar.execute((AcademusCalendar)cal);
        }
    }

    class Insert extends SqlUpdate
    {
        private static final String SQL =
            "insert into dpcs_cal_x_entry (calid, ceid, ceseq) " +
            "values (?, ?, 0)";

        public Insert(DataSource dataSource)
        {
            super(dataSource, SQL);
            declareParameter(new SqlParameter(Types.VARCHAR)); // calid
            declareParameter(new SqlParameter(Types.VARCHAR)); // ceid
        }

        public int update(CalendarAndEvent calendarAndEvent)
        {
            Object params[] = new Object[] {
                calendarAndEvent.getCalendarId(),
                calendarAndEvent.getEventId()
            };
            return update(params);
        }
    }



    class Delete extends SqlUpdate
    {
        private static final String SQL =
            "delete from dpcs_cal_x_entry where calid=? and ceid=?";

        public Delete(DataSource dataSource)
        {
            super(dataSource, SQL);
            declareParameter(new SqlParameter(Types.VARCHAR)); // calid
            declareParameter(new SqlParameter(Types.VARCHAR)); // ceid
        }

        public int update(CalendarAndEvent calendarAndEvent)
        {
            Object params[] = new Object[] {
                calendarAndEvent.getCalendarId(),
                calendarAndEvent.getEventId()
            };
            return update(params);
        }
    }

    class FindByCalendar extends MappingSqlQuery
    {
        private static final String SQL =
            "select calid, ceid from dpcs_cal_x_entry where calid=?";

        public FindByCalendar(DataSource dataSource)
        {
            super(dataSource, SQL);
            declareParameter(new SqlParameter(Types.VARCHAR));
        }

        public List execute(AcademusCalendar cal)
        {
            Object params[] = new Object[] {
                cal.getCalId()
            };
            return execute(params);
        }

        public Object mapRow(ResultSet rs, int rowNumber)
            throws SQLException
        {
            CalendarAndEvent ce = new CalendarAndEvent();
            ce.setCalendarId(rs.getString("calid"));
            ce.setEventId(rs.getString("ceid"));
            return ce;
        }
    }
}
