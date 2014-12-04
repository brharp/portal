
package ca.uoguelph.ccs.db.schema;

import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import java.util.Set;
import java.util.Iterator;

public class Table extends Relation
{
    private Map columns;
    private Map constraints;

    public Table(Schema schema, String name)
    {
    	super(schema, name);
        this.columns = new HashMap();
        this.constraints = new HashMap();
    }

    public boolean containsColumn(String name)
    {
        return columns.containsKey(name);
    }

    public boolean containsColumn(Column column)
    {
        return columns.containsValue(column);
    }

    public Column getColumn(String name)
    {
        return (Column)columns.get(name);
    }

    public Column createColumn(String name, String type, String def, boolean notNull)
    {
        Column c = new Column(this, name, type, def, notNull);
        addColumn(c);
        return c;
    }

    public Column createColumn(String name, String type)
    {
        Column c = new Column(this, name, type);
        addColumn(c);
        return c;
    }

    public void addColumn(Column column)
    {
        columns.put(column.getName(), column);
    }

    public void addAllColumns(Table table)
    {
        columns.putAll(table.columns);
    }

    public Column removeColumn(String name)
    {
        return (Column)columns.remove(name);
    }

    public Collection columns()
    {
        return columns.values();
    }

    public Set columnNames()
    {
        return columns.keySet();
    }

    public boolean containsConstraint(String name)
    {
        return constraints.containsKey(name);
    }

    public boolean containsConstraint(Constraint constraint)
    {
        return constraints.containsValue(constraint);
    }

    public Constraint createConstraint(String name, String type)
    {
        Constraint c = new Constraint(this, name, type);
        addConstraint(c);
        return c;
    }

    public Constraint getConstraint(String name)
    {
        return (Constraint)constraints.get(name);
    }

    public void addConstraint(Constraint constraint)
    {
        constraints.put(constraint.getName(), constraint);
    }

    public void addAllConstraints(Table table)
    {
        constraints.putAll(table.constraints);
    }

    public Constraint removeConstraint(String name)
    {
        return (Constraint)constraints.remove(name);
    }

    public Collection constraints()
    {
        return constraints.values();
    }

    public Set constraintNames()
    {
        return constraints.keySet();
    }

    public void accept(SchemaVisitor v)
    {
        if (v.visit(this)) {
            for (Iterator it = columns().iterator(); it.hasNext();) {
                Column c = (Column)it.next();
                c.accept(v);
            }
            
            for (Iterator it = constraints().iterator(); it.hasNext();) {
                Constraint c = (Constraint)it.next();
                c.accept(v);
            }
        }
    }
}
