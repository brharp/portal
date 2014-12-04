package ca.uoguelph.ccs.db.schema.transform;

import ca.uoguelph.ccs.db.schema.*;
import ca.uoguelph.ccs.db.query.*;

public class ColumnTransformer extends AbstractSchemaVisitor
{
	private Column have;

	private QueryProcessor sql;

	public ColumnTransformer(QueryProcessor sql, Column have)
	{
		this.have = have;
		this.sql = sql;
	}

	public boolean visit(Column want)
	{
		if (!have.getType().equals(want.getType())) {
			sql.alterTableAlterColumnType(want);
		}

		if (!(want.getDefault() == have.getDefault() || 
				want.getDefault() == null || 
				want.getDefault().equals(have.getDefault()))) {
			sql.alterTableAlterColumnSetDefault(want);
		}

		if (!have.isNotNull() == want.isNotNull()) {
			sql.alterTableAlterColumnNotNull(want);
		}

		return false;
	}
}
