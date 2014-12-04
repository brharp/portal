package com.sun.portal.rssportlet;

import java.util.*;
import com.sun.syndication.feed.synd.SyndEntry;

/**
 * Filters SyndEntry objects based on pubDate.
 */
public class PubDateFilter
{
    private long epoch;
    private boolean after;

    /**
     * Constructs a filter that accepts SyndEntry objects with a
     * pubDate greater than EPOCH if AFTER is true, or less then or
     * equal to EPOCH if AFTER is false.
     */
    public PubDateFilter(long epoch, boolean after) {
        this.epoch = epoch;
        this.after = after;
    }

    /**
     * Constructs a filter that accepts SyndEntry objects with a
     * pubDate less than or equal to EPOCH.
     */
    public PubDateFilter(long epoch) {
        this(epoch,false);
    }

    /**
     * Constructs a filter that accepts SyndEntry objects with a
     * pubDate less than or equal to the current time, as given by
     * System.currentTimeMillis().
     */
    public PubDateFilter() {
        this(System.currentTimeMillis());
    }

    public boolean accept(Object o) {
        Date pubDate = ((SyndEntry)o).getPublishedDate();
        long pubTime = pubDate.getTime();
        return (after ? pubTime > epoch : pubTime <= epoch);
    }

    public List filter(List list) {
        List result = new LinkedList();
        Iterator i = list.iterator();
        while(i.hasNext()) {
            Object o = i.next();
            if (accept(o)) {
                System.out.println("PubDateFilter: adding " + o);
                result.add(o);
            }
        }
        return result;
    }
}
