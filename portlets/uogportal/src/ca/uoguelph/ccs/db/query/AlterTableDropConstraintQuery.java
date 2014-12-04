
package ca.uoguelph.ccs.db.query;

import ca.uoguelph.ccs.db.schema.*;

public class AlterTableDropConstraintQuery extends Query
{
    private Constraint constraint;

    public AlterTableDropConstraintQuery(Constraint constraint)
    {
        this.constraint = constraint;
    }

    public void process(QueryProcessor p)
    {
        p.alterTableDropConstraint(constraint);
    }
}
