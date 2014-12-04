
package ca.uoguelph.ccs.db.query.sql;

import ca.uoguelph.ccs.db.schema.*;
import ca.uoguelph.ccs.db.query.*;

import java.io.PrintStream;
import java.util.Iterator;

public class SQLFormat
{
    public String createTable(Table table)
    {
        return "CREATE TABLE " + table.getName() + " ()";
    }

    public String dropTable(Table table)
    {
        return "DROP TABLE " + table.getName();
    }

    public String alterTableAddColumn(Column column)
    {
        return "ALTER TABLE " + column.getTable().getName() 
            +  " ADD COLUMN " + column.getName() 
            +  " " + column.getType()
            ;
    }

    public String alterTableDropColumn(Column column)
    {
        return "ALTER TABLE " + column.getTable().getName() 
            +  " DROP COLUMN " + column.getName()
            ;
    }

    public String alterTableAlterColumnNotNull(Column column)
    {
        return "ALTER TABLE " + column.getTable().getName() 
            +  " ALTER COLUMN " + column.getName() 
            +  " " + (column.isNotNull() ? "SET" : "DROP") + " NOT NULL"
            ;
    }
    
    public String alterTableAlterColumnSetDefault(Column column)
    {
        return "ALTER TABLE " + column.getTable().getName() 
            +  " ALTER COLUMN " + column.getName() 
            +  (column.getDefault() == null ? " DROP DEFAULT" 
                : " SET DEFAULT " + column.getDefault())
            ;

    }

    public String alterTableAlterColumnType(Column column)
    {
        return "ALTER TABLE " + column.getTable().getName() 
            +  " ALTER COLUMN " + column.getName() 
            +  " TYPE " + column.getType()
            ;
    }
    
    public String alterTableAddConstraint(Constraint constraint)
    {
        StringBuffer stmt = new StringBuffer();

        stmt.append("ALTER TABLE " + constraint.getTable().getName() + 
                    " ADD CONSTRAINT " + constraint.getName());

        if (Constraint.PRIMARY_KEY.equals(constraint.getType())) {
            stmt.append(" PRIMARY KEY (");
            for(Iterator it = constraint.keys(); it.hasNext();) {
                String nextKey = (String)it.next();
                stmt.append(nextKey);
                if (it.hasNext()) {
                    stmt.append(", ");
                }
            }
            stmt.append(")");
        }

        return stmt.toString();
    }

    public String alterTableDropConstraint(Constraint constraint)
    {
        return "ALTER TABLE " + constraint.getTable().getName() 
            +  " DROP CONSTRAINT " + constraint.getName()
            ;
    }
    
    public String createSequence(Sequence sequence)
    {
    	return "CREATE SEQUENCE " + sequence.getName();
    }
}
