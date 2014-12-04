
package ca.uoguelph.ccs.portal.modules.prefs;

import java.util.*;

public class Preference
{
    private String name;
    private String type;
    private String def;
    private List options;

    public Preference()
    {
    }

    public Preference(String name, String type, String def, List options)
    {
        setName(name);
        setType(type);
        setDefault(def);
        setOptions(options);
    }

    public Preference(String name, String type, String def)
    {
        this(name,type,def,null);
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

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        if (type==null) throw new NullPointerException(type);
        this.type = type;
    }

    public String getDefault()
    {
        return def;
    }

    public void setDefault(String def)
    {
        if (def==null) throw new NullPointerException(def);
        this.def = def;
    }

    public List getOptions()
    {
        return options==null ? Collections.EMPTY_LIST : options;
    }

    public void setOptions(List options)
    {
        this.options = options;
    }

    public void addOption(Option option)
    {
        if (options==null) options = new LinkedList();
        options.add(option);
    }

    public void addOption(String name, String value)
    {
        addOption(new Option(name, value));
    }

    public void addOption(String name)
    {
        addOption(new Option(name));
    }
}
