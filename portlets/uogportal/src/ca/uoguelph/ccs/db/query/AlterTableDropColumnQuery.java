
package ca.uoguelph.ccs.db.query;

import ca.uoguelph.ccs.db.schema.*;

public class AlterTableDropColumnQuery extends Query
{
    private Column column;

    public AlterTableDropColumnQuery(Column column)
    {
        this.column = column;
    }

    public void process(QueryProcessor p)
    {
        p.alterTableDropColumn(column);
    }
}
