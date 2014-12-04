
package ca.uoguelph.ccs.db.query;

import ca.uoguelph.ccs.db.schema.*;

public class AlterTableAddColumnQuery extends Query
{
    private Column column;

    public AlterTableAddColumnQuery(Column column)
    {
        this.column = column;
    }

    public void process(QueryProcessor p)
    {
        p.alterTableAddColumn(column);
    }
}
