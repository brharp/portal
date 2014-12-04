package ca.uoguelph.ccs.portal.modules;

public class ModuleAttrs extends StringArrayMap
{
    private static final int ALIAS = 0;

    public ModuleAttrs()
    {
        super();
    }
    
    public void setAttribute(String name, String alias)
    {
        putEntry(name, new String[]{ alias });
    }

    public String getAlias(String name)
    {
        return getEntry(name, ALIAS);
    }
}
