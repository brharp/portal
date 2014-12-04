
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
import ca.uoguelph.ccs.portal.calendar.Event;
import ca.uoguelph.ccs.portal.calendar.dao.EventDao;

public class AcademusEventDao extends JdbcDaoSupport implements EventDao
{
    private Delete delete;
    private Insert insert;
    private Select select;
    private Update update;
    private NextId nextId;
    private FindByCalendar findByCalendar;

    public void initDao()
    {
        select = new Select(getDataSource());
        update = new Update(getDataSource());
        delete = new Delete(getDataSource());
        insert = new Insert(getDataSource());
        nextId = new NextId(getDataSource());
        findByCalendar = new FindByCalendar(getDataSource());
    }

    public Event get(String id) {
        if (id == null) {
            throw new NullPointerException();
        } else {
            return (AcademusEvent)select.findObject(id);
        }
    }
    
    public Event update(Event event) {
        if (event == null) {
            throw new NullPointerException();
        } else {
            update.update((AcademusEvent)event);
            return event;
        }
    }

    public Event insert(Event event) {
        if (event == null) {
            throw new NullPointerException();
        } else {
            insert.update((AcademusEvent)event);
            return event;
        }
    }

    public void delete(Event event) {
        if (event == null) {
            throw new NullPointerException();
        } else {
            delete.update((AcademusEvent)event);
        }
    }

    public List findByCalendar(Calendar cal) {
        if (cal == null) {
            throw new NullPointerException();
        } else {
            return findByCalendar.execute(cal);
        }
    }

    class Select extends MappingSqlQuery
    {
        private static final String SQL =
            "select ceid, day, eor, length, title, description from dpcs_entry where ceid=?";

        public Select(DataSource dataSource)
        {
            super(dataSource, SQL);
            declareParameter(new SqlParameter(Types.VARCHAR)); // ceid
        }

        protected Object mapRow(ResultSet rs, int rowNum)
            throws SQLException
        {
            AcademusEvent event = new AcademusEvent();
            event.setCeId(rs.getString("ceid"));
            event.setStart(rs.getDate("day"));
            event.setEnd(rs.getDate("eor"));
            event.setDuration(rs.getLong("length"));
            event.setTitle(rs.getString("title"));
            event.setDescription(rs.getString("description"));
            return event;
        }
    }


    class NextId extends SqlFunction
    {
        private static final String SQL =
            "select max(id)+1 as nextid from dpcs_entry";

        public NextId(DataSource dataSource)
        {
            super(dataSource, SQL);
        }
    }


    class Insert extends SqlUpdate
    {
        private static final String SQL =
            "insert into dpcs_entry (id, ceid, ceseq, day, eor, length, title, description) " +
            "values (?, ?, 0, ?, ?, ?, ?, ?)";

        public Insert(DataSource dataSource)
        {
            super(dataSource, SQL);
            declareParameter(new SqlParameter(Types.INTEGER)); // id
            declareParameter(new SqlParameter(Types.VARCHAR)); // ceid
            declareParameter(new SqlParameter(Types.TIMESTAMP)); // day
            declareParameter(new SqlParameter(Types.TIMESTAMP)); // eor
            declareParameter(new SqlParameter(Types.INTEGER)); // length
            declareParameter(new SqlParameter(Types.VARCHAR)); // title
            declareParameter(new SqlParameter(Types.VARCHAR)); // description
        }

        public int update(AcademusEvent event)
        {
            Integer id;
            try {
                id = new Integer(nextId.run());
            } catch(org.springframework.dao.TypeMismatchDataAccessException e) {
                id = new Integer(0);
            }
            String ceId = id.toString();
            Object params[] = new Object[] {
                id,
                ceId,
                event.getStart(),
                event.getEnd(),
                new Long(event.getDuration()),
                event.getTitle(),
                event.getDescription()
            };
            int rowCount = update(params);
            event.setCeId(ceId);
            return rowCount;
        }
    }


    class Update extends SqlUpdate
    {
        private static final String SQL =
            "update dpcs_entry set day, eor, length, title, description where ceid=?";
    
        public Update(DataSource dataSource)
        {
            super(dataSource, SQL);
            declareParameter(new SqlParameter(Types.TIMESTAMP)); // day
            declareParameter(new SqlParameter(Types.TIMESTAMP)); // eor
            declareParameter(new SqlParameter(Types.INTEGER)); // length
            declareParameter(new SqlParameter(Types.VARCHAR)); // title
            declareParameter(new SqlParameter(Types.VARCHAR)); // description
            declareParameter(new SqlParameter(Types.VARCHAR)); // ceid
        }

        public int update(AcademusEvent event)
        {
            Object params[] = new Object[] {
                event.getStart(),
                event.getEnd(),
                new Long(event.getDuration()),
                event.getTitle(),
                event.getDescription(),
                event.getCeId()
            };
            return update(params);
        }
    }


    class Delete extends SqlUpdate
    {
        private static final String SQL =
            "delete from dpcs_entry where ceid=?";

        public Delete(DataSource dataSource)
        {
            super(dataSource, SQL);
            declareParameter(new SqlParameter(Types.VARCHAR)); // ceid
        }

        public int update(AcademusEvent event)
        {
            Object params[] = new Object[] {
                event.getCeId()
            };
            return update(params);
        }
    }

    class FindByCalendar extends MappingSqlQuery
    {
        private static final String SQL =
            "select ceid, day, eor, length, title, description from dpcs_entry where ceid=?";

        public FindByCalendar(DataSource dataSource)
        {
            super(dataSource, SQL);
            declareParameter(new SqlParameter(Types.VARCHAR)); // ceid
        }

        public List execute(Calendar cal)
        {
            return null;
        }

        protected Object mapRow(ResultSet rs, int rowNum)
            throws SQLException
        {
            AcademusEvent event = new AcademusEvent();
            event.setCeId(rs.getString("ceid"));
            event.setStart(rs.getDate("day"));
            event.setEnd(rs.getDate("eor"));
            event.setDuration(rs.getLong("length"));
            event.setTitle(rs.getString("title"));
            event.setDescription(rs.getString("description"));
            return event;
        }
    }
}
