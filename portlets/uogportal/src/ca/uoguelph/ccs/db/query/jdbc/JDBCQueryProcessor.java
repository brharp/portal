
package ca.uoguelph.ccs.db.query.jdbc;

import ca.uoguelph.ccs.db.*;
import ca.uoguelph.ccs.db.schema.*;
import ca.uoguelph.ccs.db.query.*;
import ca.uoguelph.ccs.db.query.sql.*;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JDBCQueryProcessor implements QueryProcessor
{
    Connection conn;
    Statement  stmt;
    SQLFormat  sql;

    public JDBCQueryProcessor()
    {
        sql = new SQLFormat();
    }

    public void connect(String url, String user, String password)
        throws DatabaseException
    {
        try {
            conn = DriverManager.getConnection(url, user, password);
            stmt = conn.createStatement();
        }
        catch(SQLException sqle) {
            throw new DatabaseException(sqle);
        }
    }

    public void close()
    {
        try { 
            if (stmt != null) {
                stmt.close(); 
            }
        } 
        catch(SQLException e) {}

        try { 
            if (conn != null) {
                conn.close(); 
            }
        }
        catch(SQLException e) {}
    }
    
    public void createTable(Table table) throws DatabaseException
    {
        try {
            stmt.execute(sql.createTable(table));
        }
        catch(SQLException e) {
            throw new DatabaseException(e);
        }
    }

    public void dropTable(Table table) throws DatabaseException
    {
        try {
            stmt.execute(sql.dropTable(table));
        }
        catch(SQLException e) {
            throw new DatabaseException(e);
        }
    }

    public void alterTableAddColumn(Column column) throws DatabaseException
    {
        try {
            stmt.execute(sql.alterTableAddColumn(column));
        }
        catch(SQLException e) {
            throw new DatabaseException(e);
        }
    }

    public void alterTableDropColumn(Column column) throws DatabaseException
    {
        try {
            stmt.execute(sql.alterTableDropColumn(column));
        }
        catch(SQLException e) {
            throw new DatabaseException(e);
        }
    }
    
    public void alterTableAlterColumnType(Column column) throws DatabaseException
    { 
        try {
            stmt.execute(sql.alterTableAlterColumnType(column)); 
        }
        catch(SQLException e) {
            throw new DatabaseException(e);
        }
    }

    public void alterTableAlterColumnSetDefault(Column column) throws DatabaseException
    { 
        try {
            stmt.execute(sql.alterTableAlterColumnSetDefault(column)); 
        }
        catch(SQLException e) {
            throw new DatabaseException(e);
        }
    }

    public void alterTableAlterColumnNotNull(Column column) throws DatabaseException
    { 
        try {
            stmt.execute(sql.alterTableAlterColumnNotNull(column)); 
        }
        catch(SQLException e) {
            throw new DatabaseException(e);
        }
    }

    public void alterTableAddConstraint(Constraint constraint) throws DatabaseException
    { 
        try {
            stmt.execute(sql.alterTableAddConstraint(constraint)); 
        }
        catch(SQLException e) {
            throw new DatabaseException(e);
        }
    }

    public void alterTableDropConstraint(Constraint constraint) throws DatabaseException
    { 
        try {
            stmt.execute(sql.alterTableDropConstraint(constraint)); 
        }
        catch(SQLException e) {
            throw new DatabaseException(e);
        }
    }
    
    public void createSequence(Sequence sequence) throws DatabaseException
    {
    	try {
    		stmt.execute(sql.createSequence(sequence));
    	}
    	catch(SQLException e) {
    		throw new DatabaseException(e);
    	}
    }
}
