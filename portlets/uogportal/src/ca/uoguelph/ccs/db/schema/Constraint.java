
package ca.uoguelph.ccs.db.schema;

import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

public class Constraint
{
    public static final String PRIMARY_KEY = "primary-key";

    private Table table;
    private String name;
    private String type;
    private Set keys;           // Primary keys.

    public Constraint(Table table, String name, String type)
    {
        if (table == null) {
            throw new IllegalArgumentException("table == null");
        }

        if (name == null) {
            throw new IllegalArgumentException("name == null");
        }

        if (type == null) {
            throw new IllegalArgumentException("type == null");
        }

        this.table = table;
        this.name = name;
        this.type = type;

        this.keys = new HashSet();
    }

    public Table getTable()
    {
        return table;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public void addKey(String name)
    {
        if (name == null) {
            throw new NullPointerException("name == null");
        }

        keys.add(name);
    }

    public void removeKey(String name)
    {
        keys.remove(name);
    }

    public Iterator keys()
    {
        return keys.iterator();
    }

    public void accept(SchemaVisitor v)
    {
        v.visit(this);
    }

    public boolean equalsIgnoreTable(Object that)
    {
        if (that instanceof Constraint) {
            Constraint other = (Constraint)that;
            return this.name.equals(other.name)
                && this.type.equals(other.type)
                && this.keys.equals(other.keys);
        }
        return false;
    }
}
