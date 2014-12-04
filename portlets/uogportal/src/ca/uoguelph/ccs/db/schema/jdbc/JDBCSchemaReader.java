
package ca.uoguelph.ccs.db.schema.jdbc;

import ca.uoguelph.ccs.db.*;
import ca.uoguelph.ccs.db.schema.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.sql.DataSource;

public class JDBCSchemaReader implements SchemaSource
{
    private String url;
    private DataSource dataSource;

    public JDBCSchemaReader()
    {
    }

    public JDBCSchemaReader(String url)
    {
        setURL(url);
    }

    public JDBCSchemaReader(DataSource dataSource)
    {
        setDataSource(dataSource);
    }

    public void setURL(String url)
    {
        this.url = url;
    }

    public void setDataSource(DataSource dataSource)
    {
        this.dataSource = dataSource;
    }

    protected Connection getConnection() throws Exception
    {
        if (dataSource != null) {
            return dataSource.getConnection();
        }
        else if (url != null) {
            String user = System.getProperty("jdbc.user");
            String password = System.getProperty("jdbc.password");
            return DriverManager.getConnection(url, user, password);
        }
        else {
            throw new DatabaseException("Must specify either dataSource or JDBC URL.");
        }
    }

    public Schema getSchema()
    {
        Schema schema = new Schema();
        
        Connection conn = null;
        PreparedStatement tableQuery = null;
        PreparedStatement colQuery = null;
        PreparedStatement conQuery = null;
        PreparedStatement seqQuery = null;

        try {
            conn = getConnection();
            tableQuery = conn.prepareStatement(TABLE_QUERY_SQL);
            colQuery = conn.prepareStatement(COLUMN_QUERY_SQL);
            conQuery = conn.prepareStatement(CONSTRAINT_QUERY_SQL);
            seqQuery = conn.prepareStatement(SEQUENCE_QUERY_SQL);
            
            // Enumerate tables.

            ResultSet tableRS = tableQuery.executeQuery();
            while(tableRS.next()) {

                String tableName = tableRS.getString("tablename");
                Table table = schema.createTable(tableName);

                colQuery.setString(1, tableName);
                conQuery.setString(1, tableName);

                // Enumerate columns.

                ResultSet colRS = colQuery.executeQuery();
                while(colRS.next()) {
                    String name = colRS.getString("name");
                    String type = colRS.getString("type");
                    String def = colRS.getString("default");
                    boolean notNull = colRS.getBoolean("not_null");
                    table.createColumn(name, type, def, notNull);
                }

                // Enumerate constraints.

                ResultSet conRS = conQuery.executeQuery();
                while(conRS.next()) {
                    String name = conRS.getString("name");
                    String type = conRS.getString("type");
                    String[] keys = (String[])conRS.getArray("keys").getArray();
                    if("p".equals(type)) {
                        Constraint con = table.createConstraint(name, Constraint.PRIMARY_KEY);
                        for(int i = 0; i < keys.length; i++) {
                            con.addKey(keys[i]);
                        }
                    }
                }
            }
            
            // Enumerate sequences.
            
            ResultSet seqRS = seqQuery.executeQuery();
            while(seqRS.next()) {
            	String seqName = seqRS.getString("relname");
            	schema.createSequence(seqName);
            }
        }
        catch(Exception sqle) {
            System.err.println(sqle);
        }
        finally {
            try { if (tableQuery != null) tableQuery.close(); }
            catch(SQLException e){}

            try { if (colQuery != null) colQuery.close(); }
            catch(SQLException e) {}

            try { if (conn != null) conn.close(); }
            catch(SQLException e){}
        }

        return schema;
    }

    private static String COLUMN_QUERY_SQL = 
        "select attname as name, typname as type, atthasdef as has_default, " +
        "pg_get_expr(adbin,pg_class.oid) as default, attnotnull as not_null from  " +
        "((pg_class join pg_attribute on pg_class.oid=pg_attribute.attrelid) " +
        "left outer join pg_attrdef on pg_class.oid=adrelid and pg_attribute.attnum=adnum), " +
        "pg_type where relname=? and attnum > 0 and pg_attribute.atttypid=pg_type.oid " +
        "order by attnum";

    private static String TABLE_QUERY_SQL =
        "select tablename from pg_tables where schemaname='public'";

    private static String CONSTRAINT_QUERY_SQL =
        "select conname as name, contype as type, array(select attname from " +
        "pg_attribute where attrelid=pg_class.oid and attnum = any(conkey)) as " +
        "keys from pg_class, pg_constraint where conrelid=pg_class.oid and " +
        "pg_class.relname=?";

    private static String SEQUENCE_QUERY_SQL =
    	"select relname from pg_class where relkind='S'";
}
