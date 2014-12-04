
package ca.uoguelph.ccs.db.query;

import ca.uoguelph.ccs.db.schema.*;

public class DropTableQuery  extends Query
{
    private Table table;

    public DropTableQuery(Table table)
    {
        this.table = table;
    }

    public void process(QueryProcessor p)
    {
        p.dropTable(table);
    }
}
