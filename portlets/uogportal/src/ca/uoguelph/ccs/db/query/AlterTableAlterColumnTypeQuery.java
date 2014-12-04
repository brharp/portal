
package ca.uoguelph.ccs.db.query;

import ca.uoguelph.ccs.db.schema.*;

public class AlterTableAlterColumnTypeQuery extends Query
{
    private Column column;

    public AlterTableAlterColumnTypeQuery(Column column)
    {
        this.column = column;
    }

    public void process(QueryProcessor p)
    {
        p.alterTableAlterColumnType(column);
    }
}
