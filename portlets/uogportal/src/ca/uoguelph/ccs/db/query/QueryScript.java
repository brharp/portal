
package ca.uoguelph.ccs.db.query;

import ca.uoguelph.ccs.db.schema.*;

import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;

public class QueryScript implements QueryProcessor
{
    private List queries;             // Queries

    public QueryScript()
    {
        queries = new LinkedList();
    }

    public void createTable(Table table)
    {
        queries.add(new CreateTableQuery(table));
    }

    public void dropTable(Table table)
    {
        queries.add(new DropTableQuery(table));
    }

    public void alterTableAddColumn(Column column)
    {
        queries.add(new AlterTableAddColumnQuery(column));
    }

    public void alterTableDropColumn(Column column)
    {
        queries.add(new AlterTableDropColumnQuery(column));
    }

    public void alterTableAlterColumnNotNull(Column column)
    {
        queries.add(new AlterTableAlterColumnNotNullQuery(column));
    }
    
    public void alterTableAlterColumnSetDefault(Column column)
    {
        queries.add(new AlterTableAlterColumnSetDefaultQuery(column));
    }

    public void alterTableAlterColumnType(Column column)
    {
        queries.add(new AlterTableAlterColumnTypeQuery(column));
    }

    public void alterTableAddConstraint(Constraint constraint)
    {
        queries.add(new AlterTableAddConstraintQuery(constraint));
    }

    public void alterTableDropConstraint(Constraint constraint)
    {
        queries.add(new AlterTableDropConstraintQuery(constraint));
    }
    
    public void createSequence(Sequence sequence)
    {
    	queries.add(new CreateSequenceQuery(sequence));
    }

    public void run(QueryProcessor processor)
    {
        Iterator it = queries.iterator();
        while(it.hasNext()) {
            Query query = (Query)it.next();
            query.process(processor);
        }
    }
}
