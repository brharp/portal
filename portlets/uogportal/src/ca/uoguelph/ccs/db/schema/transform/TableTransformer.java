
package ca.uoguelph.ccs.db.schema.transform;

import ca.uoguelph.ccs.db.schema.*;
import ca.uoguelph.ccs.db.query.*;

public class TableTransformer extends AbstractSchemaVisitor
{
    Table tableWeHave;
    QueryProcessor sql;

    public TableTransformer(QueryProcessor sql, Table tableWeHave)
    {
        this.tableWeHave = tableWeHave;
        this.sql = sql;
    }

    public boolean visit(Column columnWeWant)
    {
        if (! tableWeHave.containsColumn(columnWeWant.getName())) {
            sql.alterTableAddColumn(columnWeWant);
        }
        else {
            Column columnWeHave = tableWeHave.getColumn(columnWeWant.getName());
            columnWeWant.accept(new ColumnTransformer(sql, columnWeHave));
        }

        return false;
    }

    public boolean visit(Constraint constraintWeWant)
    {
        
        if (! tableWeHave.containsConstraint(constraintWeWant.getName())) {
            sql.alterTableAddConstraint(constraintWeWant);
        }
        else {
            Constraint constraintWeHave = tableWeHave.getConstraint(constraintWeWant.getName());
            constraintWeWant.accept(new ConstraintTransformer(sql, constraintWeHave));
        }

        return false;
    }
}
