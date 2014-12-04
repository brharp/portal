
package ca.uoguelph.ccs.portal.modules.prefs;

public class Option
{
    private String name;
    private String value;

    public Option()
    {
    }

    public Option(String name, String value)
    {
        setName(name);
        setValue(value);
    }

    public Option(String name)
    {
        this(name,name);
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        if (name==null) throw new NullPointerException(name);
        this.name = name;
    }

    public String getValue()
    {
        return value==null? name : value;
    }

    public void setValue(String value)
    {
        if (value==null) throw new NullPointerException(value);
        this.value = value;
    }
}
