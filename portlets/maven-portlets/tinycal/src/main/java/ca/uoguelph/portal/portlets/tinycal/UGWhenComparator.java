package ca.uoguelph.ccs.portal.portlets.tinycal;

import java.util.*;
import com.sun.syndication.feed.synd.*;
import ca.uoguelph.ccs.syndication.feed.module.*;

public class UGWhenComparator implements Comparator
{
    public int compare(Object o1, Object o2)
    {
        SyndEntry e1 = (SyndEntry)o1;
        SyndEntry e2 = (SyndEntry)o2;

        UGModule m1 = (UGModule)e1.getModule(UGModule.URI);
        UGModule m2 = (UGModule)e2.getModule(UGModule.URI);

        UGWhen w1 = (m1 == null) ? null : m1.getWhen();
        UGWhen w2 = (m2 == null) ? null : m2.getWhen();

        // Treat a null date is infinite. If the first entry has no
        // date, it is later than the second entry, unless both have
        // no date, in which case they are equal.

        Date d1 = (w1 == null) ? null : w1.getStartTime();
        Date d2 = (w2 == null) ? null : w2.getStartTime();

        if(d1 == null && d2 == null) return 0;
        if(d1 == null) return  1;
        if(d2 == null) return -1;
        return d1.compareTo(d2);
    }
}
