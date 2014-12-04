package ca.uoguelph.ccs.db.schema.transform;

import ca.uoguelph.ccs.db.schema.*;
import ca.uoguelph.ccs.db.query.*;

public class SchemaTransformer extends AbstractSchemaVisitor
{
	Schema schemaWeHave;

	QueryProcessor sql;

	public SchemaTransformer(QueryProcessor sql, Schema schemaWeHave)
	{
		this.sql = sql;
		this.schemaWeHave = schemaWeHave;
	}

	public boolean visit(Schema schemaWeWant)
	{
		return true; // Visit relations.
	}

	public boolean visit(Table tableWeWant)
	{
		// If the schema we have does not contain the table we want,
		// add the table.
		if (!schemaWeHave.containsRelation(tableWeWant.getName())) {
			sql.createTable((Table) tableWeWant);
			tableWeWant.accept(new TableCopier(sql));
		}
		// If the table already exists in our schema, update the
		// schema with any differences. TODO: Handle sequence/table relation
		// name clashes.
		else {
			Table tableWeHave = (Table) schemaWeHave.getRelation(tableWeWant.getName());
			tableWeWant.accept(new TableTransformer(sql, tableWeHave));
		}

		return false;
	}

	public boolean visit(Sequence sequenceWeWant)
	{
		// If the schema we have does not contain the sequence we want, add the
		// sequence.
		if (!schemaWeHave.containsRelation(sequenceWeWant.getName())) {
			sql.createSequence((Sequence) sequenceWeWant);
		}
		
		return false;
	}
}
