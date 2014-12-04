package ca.uoguelph.ccs.portal.launchpad;

import java.util.List;
import java.io.Serializable;

public class LaunchPad implements Serializable
{
    private String _name;
    private List _topics;

    public String getName()
    {
        return _name;
    }

    public void setName(String description)
    {
        if (description == null)
            throw new NullPointerException("description");
        _name = description;
    }

    public List getTopics()
    {
        return _topics;
    }

    public void setTopics(List topics)
    {
        _topics = topics;
    }
}
