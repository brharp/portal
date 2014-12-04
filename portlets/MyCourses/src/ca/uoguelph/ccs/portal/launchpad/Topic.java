package ca.uoguelph.ccs.portal.launchpad;

import java.util.List;

public class Topic extends Link
{
    private List links;

    public List getLinks()
    {
        return links;
    }

    public void setLinks(List links)
    {
        if (links == null)
            throw new NullPointerException("links");
        this.links = links;
    }
}
