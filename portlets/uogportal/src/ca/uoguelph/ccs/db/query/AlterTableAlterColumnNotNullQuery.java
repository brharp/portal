
package ca.uoguelph.ccs.db.query;

import ca.uoguelph.ccs.db.schema.*;

public class AlterTableAlterColumnNotNullQuery extends Query
{
    private Column column;

    public AlterTableAlterColumnNotNullQuery(Column column)
    {
        this.column = column;
    }

    public void process(QueryProcessor p)
    {
        p.alterTableAlterColumnNotNull(column);
    }
}
