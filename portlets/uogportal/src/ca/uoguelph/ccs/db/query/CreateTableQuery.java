
package ca.uoguelph.ccs.db.query;

import ca.uoguelph.ccs.db.schema.*;

public class CreateTableQuery  extends Query
{
    private Table table;

    public CreateTableQuery(Table table)
    {
        this.table = table;
    }

    public void process(QueryProcessor p)
    {
        p.createTable(table);
    }
}
