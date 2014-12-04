package ca.uoguelph.ccs.db.schema;

public class Sequence extends Relation
{
	public Sequence(Schema schema, String name)
	{
		super(schema, name);
	}
	
	public void accept(SchemaVisitor v)
	{
		v.visit(this);
	}
}
