
package ca.uoguelph.ccs.db.query;

import ca.uoguelph.ccs.db.*;
import ca.uoguelph.ccs.db.schema.*;

public interface QueryProcessor
{
    public void createTable(Table table) throws DatabaseException;
    public void dropTable(Table table) throws DatabaseException;
    public void alterTableAddColumn(Column column) throws DatabaseException;
    public void alterTableDropColumn(Column column) throws DatabaseException;
    public void alterTableAlterColumnType(Column column) throws DatabaseException;
    public void alterTableAlterColumnSetDefault(Column column) throws DatabaseException;
    public void alterTableAlterColumnNotNull(Column column) throws DatabaseException;
    public void alterTableAddConstraint(Constraint constraint) throws DatabaseException;
    public void alterTableDropConstraint(Constraint constraint) throws DatabaseException;
    public void createSequence(Sequence sequence) throws DatabaseException;
}
