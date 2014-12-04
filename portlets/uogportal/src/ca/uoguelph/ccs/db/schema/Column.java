
package ca.uoguelph.ccs.db.schema;

public class Column
{
    private Table table;
    private String name;
    private String type;
    private String def;
    private boolean notNull;

    public Column(Table table, String name, String type)
    {
        this(table, name, type, null, false);
    }

    public Column(Table table, String name, String type, String def, boolean notNull)
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
        this.def = def;
        this.notNull = notNull;
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

    public String getDefault()
    {
        return def;
    }

    public void setDefault(String def)
    {
        this.def = def;
    }

    public boolean isNotNull()
    {
        return notNull;
    }

    public void setNotNull(boolean notNull)
    {
        this.notNull = notNull;
    }

    public void accept(SchemaVisitor v)
    {
        v.visit(this);
    }
}
