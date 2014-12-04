
package ca.uoguelph.ccs.db.schema.transform;

import ca.uoguelph.ccs.db.schema.*;
import ca.uoguelph.ccs.db.query.*;

public class TableCopier extends AbstractSchemaVisitor
{
    QueryProcessor sql;

    public TableCopier(QueryProcessor sql)
    {
        this.sql = sql;
    }

    public boolean visit(Table table)
    {
    	return true;
    }
    
    public boolean visit(Column column)
    {
        sql.alterTableAddColumn(column);
        if (column.isNotNull()) {
            sql.alterTableAlterColumnNotNull(column);
        }
        if (column.getDefault() != null) {
            sql.alterTableAlterColumnSetDefault(column);
        }
        return false;
    }

    public boolean visit(Constraint constraint)
    {
        sql.alterTableAddConstraint(constraint);
        return false;
    }
}
