package ca.uoguelph.ccs.portal.modules;

import java.util.*;

public class StringArrayMap
{
    private Map arrays;

    public StringArrayMap()
    {
        arrays = new HashMap();
    }
    
    protected String getEntry(String name, int index)
    {
        String[] entry;
        if ((entry = (String[])arrays.get(name)) != null) {
            return entry[index];
        }
        return null;
    }

    protected void putEntry(String name, String[] entry)
    {
        arrays.put(name, entry);
    }
    
    public Iterator getNames() {
        return arrays.keySet().iterator();
    }
}
