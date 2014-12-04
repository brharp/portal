
package ca.uoguelph.ccs.db.schema;

public interface SchemaVisitor
{
    public boolean visit(Schema schema);
    public boolean visit(Table table);
    public boolean visit(Column column);
    public boolean visit(Constraint constraint);
    public boolean visit(Sequence sequence);
}
