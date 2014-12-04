
package ca.uoguelph.ccs.portal.modules.prefs;

public class NoSuchPreferenceException extends Exception
{
    public NoSuchPreferenceException(String name)
    {
        super(name);
    }
}
