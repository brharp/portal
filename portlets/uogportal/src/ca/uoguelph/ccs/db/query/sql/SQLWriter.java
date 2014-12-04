
package ca.uoguelph.ccs.db.query.sql;

import ca.uoguelph.ccs.db.schema.*;
import ca.uoguelph.ccs.db.query.*;

import java.io.PrintStream;

public class SQLWriter implements QueryProcessor
{
    public PrintStream out;
    private SQLFormat sql;
    
    public SQLWriter(PrintStream out)
    {
        this.out = out;
        this.sql = new SQLFormat();
    }

    public SQLWriter()
    {
        this(System.out);
    }

    public void createTable(Table table)
    {
        println(sql.createTable(table));
    }

    public void dropTable(Table table)
    {
        println(sql.dropTable(table));
    }

    public void alterTableAddColumn(Column column)
    {
        println(sql.alterTableAddColumn(column));
    }

    public void alterTableDropColumn(Column column)
    {
        println(sql.alterTableDropColumn(column));
    }

    public void alterTableAlterColumnNotNull(Column column)
    {
        println(sql.alterTableAlterColumnNotNull(column));
    }
    
    public void alterTableAlterColumnSetDefault(Column column)
    {
        println(sql.alterTableAlterColumnSetDefault(column));
    }

    public void alterTableAlterColumnType(Column column)
    {
        println(sql.alterTableAlterColumnType(column));
    }

    public void alterTableAddConstraint(Constraint constraint)
    {
        println(sql.alterTableAddConstraint(constraint));
    }

    public void alterTableDropConstraint(Constraint constraint)
    {
        println(sql.alterTableDropConstraint(constraint));
    }
    
    public void createSequence(Sequence sequence)
    {
    	println(sql.createSequence(sequence));
    }
    
    private void println(String stmt)
    {
        out.print(stmt);
        out.println(";");
    }
}
