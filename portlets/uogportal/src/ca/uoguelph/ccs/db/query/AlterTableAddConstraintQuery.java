
package ca.uoguelph.ccs.db.query;

import ca.uoguelph.ccs.db.schema.*;

public class AlterTableAddConstraintQuery extends Query
{
    private Constraint constraint;

    public AlterTableAddConstraintQuery(Constraint constraint)
    {
        this.constraint = constraint;
    }

    public void process(QueryProcessor p)
    {
        p.alterTableAddConstraint(constraint);
    }
}
