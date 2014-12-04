package ca.uoguelph.ccs.portal.portlets.tinycal;

import java.util.*;
import com.sun.syndication.feed.synd.*;
import com.sun.syndication.feed.module.*;

public class DCDateComparator implements Comparator
{
    public int compare(Object o1, Object o2)
    {
        SyndEntry e1 = (SyndEntry)o1;
        SyndEntry e2 = (SyndEntry)o2;

        DCModule m1 = (DCModule)e1.getModule(DCModule.URI);
        DCModule m2 = (DCModule)e2.getModule(DCModule.URI);
        
        // Treat a null date is infinite. If the first entry has no
        // date, it is later than the second entry, unless both have
        // no date, in which case they are equal.

        Date d1 = (m1 == null) ? null : m1.getDate();
        Date d2 = (m2 == null) ? null : m2.getDate();

        if(d1 == null && d2 == null) return 0;
        if(d1 == null) return  1;
        if(d2 == null) return -1;
        return d1.compareTo(d2);
    }
}
