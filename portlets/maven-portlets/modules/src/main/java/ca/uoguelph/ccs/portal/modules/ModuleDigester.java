package ca.uoguelph.ccs.portal.modules;

import org.apache.commons.digester.*;

public class ModuleDigester extends Digester
{
    public ModuleDigester()
    {
        super();
        addCallMethod("module/title", "setTitle", 0);
        // open preference
        addObjectCreate("module/pref", "ca.uoguelph.ccs.portal.modules.prefs.Preference");
        addCallMethod("module/pref/name", "setName", 0);
        addCallMethod("module/pref/type", "setType", 0);
        addCallMethod("module/pref/default", "setDefault", 0);
        // open option
        addObjectCreate("module/pref/option", "ca.uoguelph.ccs.portal.modules.prefs.Option");
        addCallMethod("module/pref/option", "setName", 0);
        addSetProperties("module/pref/option");
        addSetNext("module/pref/option", "addOption"); 
        // close option
        addSetNext("module/pref", "addPreference");
        // close preference
        // open attribute
        addCallMethod("module/attr", "addAttribute", 2);
        addCallParam("module/attr/name", 0);
        addCallParam("module/attr/alias", 1);
        // close attribute
        // open content
        addCallMethod("module/content", "setContent", 0);
        addSetProperties("module/content");
        // close content
        addCallMethod("module/cached", "setCached", 0);
        addCallMethod("module/help", "setHelp", 0);
        addCallMethod("module/author", "setAuthor", 0);
        addCallMethod("module/description", "setDescription", 0);
    }
}
