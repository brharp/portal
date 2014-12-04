
package ca.uoguelph.ccs.db.schema;

public class AbstractSchemaVisitor implements SchemaVisitor
{
    public boolean visit(Schema schema)         { return true; }
    public boolean visit(Table table)           { return true; }
    public boolean visit(Column column)         { return true; }
    public boolean visit(Constraint constraint) { return true; }
    public boolean visit(Sequence sequence)     { return true; }
}
