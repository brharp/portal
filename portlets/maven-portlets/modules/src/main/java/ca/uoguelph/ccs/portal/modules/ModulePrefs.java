package ca.uoguelph.ccs.portal.modules;

import java.util.*;
import ca.uoguelph.ccs.portal.modules.prefs.*;

public class ModulePrefs
{
    private Map prefs;
    private List order;

    public ModulePrefs()
    {
        prefs = new HashMap();
        order = new LinkedList();
    }
    
    public void setPreference(Preference pref)
    {
        prefs.put(pref.getName(), pref);
        order.add(pref.getName());
    }

    public void setPreference(String name, String type, String def)
    {
        setPreference(new Preference(name, type, def));
    }

    public Preference getPreference(String name) 
        throws NoSuchPreferenceException
    {
        if (prefs.containsKey(name)) {
            return (Preference)prefs.get(name);
        } else {
            throw new NoSuchPreferenceException(name);
        }
    }

    public String getType(String name) 
    {
        try {
            return getPreference(name).getType();
        }
        catch (NoSuchPreferenceException e) {
            return null;
        }
    }

    public String getDefault(String name) 
    {
        try {
            return getPreference(name).getDefault();
        }
        catch(NoSuchPreferenceException e) {
            return null;
        }
    }

    public Iterator getNames() {
        return order.iterator();
    }

    public int size()
    {
        return prefs.size();
    }
}
