
package ca.uoguelph.ccs.portal.portlets.tinycal;

import java.util.*;
import com.sun.syndication.feed.synd.*;
import com.sun.syndication.feed.module.*;
import ca.uoguelph.ccs.syndication.feed.module.*;

public class EventBean
{
    private SyndEntry syndEntry;

    public EventBean(SyndEntry syndEntry)
    {
        if (null == syndEntry)
            throw new NullPointerException();

        this.syndEntry = syndEntry;
    }

    public String getTitle()
    {
        return syndEntry.getTitle();
    }

    public String getLink()
    {
        return syndEntry.getLink();
    }

    public Date getStartTime()
    {
        UGModule ug = (UGModule)syndEntry.getModule(UGModule.URI);
        return ug.getWhen().getStartTime();
    }

    public Date getEndTime()
    {
        UGModule ug = (UGModule)syndEntry.getModule(UGModule.URI);
        return ug.getWhen().getEndTime();
    }

    public String getSummary()
    {
        UGModule ug = (UGModule)syndEntry.getModule(UGModule.URI);
        return ug.getSummary();
    }

    public String getLocation()
    {
        UGModule ug = (UGModule)syndEntry.getModule(UGModule.URI);
        return ug.getWhere();
    }
}
