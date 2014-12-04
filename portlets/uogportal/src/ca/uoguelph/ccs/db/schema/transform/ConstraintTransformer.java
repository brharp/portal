
package ca.uoguelph.ccs.db.schema.transform;

import ca.uoguelph.ccs.db.schema.*;
import ca.uoguelph.ccs.db.query.*;

public class ConstraintTransformer extends AbstractSchemaVisitor
{
    private Constraint have;
    private QueryProcessor sql;

    public ConstraintTransformer(QueryProcessor sql, Constraint have)
    {
        this.have = have;
        this.sql = sql;
    }

    public boolean visit(Constraint want)
    {
        if (! have.equalsIgnoreTable(want)) {
            sql.alterTableDropConstraint(have);
            sql.alterTableAddConstraint(want);
        }

        return false;
    }
}
