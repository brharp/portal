
package ca.uoguelph.ccs.db.query;

import ca.uoguelph.ccs.db.schema.*;

public class AlterTableAlterColumnSetDefaultQuery extends Query
{
    private Column column;

    public AlterTableAlterColumnSetDefaultQuery(Column column)
    {
        this.column = column;
    }

    public void process(QueryProcessor p)
    {
        p.alterTableAlterColumnSetDefault(column);
    }
}
