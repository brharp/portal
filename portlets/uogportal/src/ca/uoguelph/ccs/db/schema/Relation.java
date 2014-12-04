package ca.uoguelph.ccs.db.schema;

public abstract class Relation {

	protected Schema schema;
	protected String name;

	public Relation(Schema schema, String name)
	{
		this.schema = schema;
		this.name = name;
	}
	
	public Schema getSchema() {
	    return schema;
	}

	public String getName() {
	    return name;
	}

	public abstract void accept(SchemaVisitor v);
}
