package ca.uoguelph.ccs.portal.launchpad;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

/**
 * A launch pad link. Links are organized into topics, which are
 * organized into launch pads.
 *
 * <p>
 *
 * Either the entire description or specific words in the description
 * may be linked. To get the whole description as a single string, use
 * {@link getDescription}. To link only the key words, iterate over
 * the segments returned by {@link #getSegments}. Call isLinked on
 * each segment to determine whether or not to link the segment of
 * text.
 *
 * <p>
 *
 * For applications rendering the link as HTML, a {@link #getHtml}
 * convenience method is provided that returns the description as
 * HTML, with key words linked to the URL.
 *
 * @author M. Brent Harp
 * @see Topic
 * @see LaunchPad
 */
public class Link
{
    public static final int LINK_MARKER = '_';

    private String url;
    private String description;
    private BigDecimal hitCount;
    private List segments;
    
    /**
     * Returns the URL for this link.
     *
     * @return the link's URL
     */
    public String getUrl()
    {
        return url;
    }

    /**
     * Sets the URL for this link.
     *
     * @param url the new URL
     */
    public void setUrl(String url)
    {
        if (url == null)
            throw new NullPointerException("url");
        this.url = url;
    }

    /**
     * Gets the full description of this link. To get the description
     * in parts for the purpose of key word linking, use getPrefix,
     * getKeyWords, and getSuffix.
     *
     * @return the link description
     * @see #getPrefix
     * @see #getKeyWords
     * @see #getSuffix
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * Sets a description for the link. To link specific words of the
     * description, delimit them with underscores (_). If there are no
     * underscores in the description, the entire description is
     * linked.
     *
     * @param description the new description
     */
    public void setDescription(String description)
    {
        if (description == null)
            throw new NullPointerException("description");
        this.description = description;
        parse(description);
    }

    /**
     * Returns the description as a list of {@link Segments}. Iterator
     * over the segments to reconstruct the description, calling
     * {@link Segment#isLinked} on each segment to determine if it
     * should be linked or not.
     */
    public List getSegments()
    {
        return segments;
    }

    /**
     * Convenience method to get an HTML version of the description
     * with key words linked.
     */
    public String getHtml()
    {
        StringBuffer html = new StringBuffer();
        for(Iterator it = segments.iterator(); it.hasNext();) {
            Segment seg = (Segment)it.next();
            if(seg.isLinked() && getUrl() != null) {
                html.append("<a href=\"" + getUrl() + "\">");
            }
            html.append(seg.toString());
            if(seg.isLinked() && getUrl() != null) {
                html.append("</a>");
            }
        }
        return html.toString();
    }

    /**
     * Returns the number of times this link has been clicked.
     */
    public BigDecimal getHitCount()
    {
        return hitCount;
    }

    /**
     * Sets the number of times this link has been clicked.
     */
    public void setHitCount(BigDecimal hitCount)
    {
        if (hitCount == null)
            throw new NullPointerException("hitCount");
        this.hitCount = hitCount;
    }

    /**
     * Breaks the description text into linked and non-linked
     * segments.
     */
    public class Segment
    {
        private int begin;
        private int end;
        private boolean linked;

        public Segment(int begin, int end, boolean linked)
        {
            this.begin = begin;
            this.end = end;
            this.linked = linked;
        }

        public boolean isLinked()
        {
            return linked;
        }

        public int getBegin()
        {
            return begin;
        }

        public int getEnd()
        {
            return end;
        }

        public String getText()
        {
            return description.substring(begin,end);
        }

        public String toString()
        {
            return getText();
        }
    }

    /**
     * Parses description text into segments. Text surrounded by
     * underscores (_) will be marked as a linked segment, all other
     * text will be marked as a non-linked segment. If the description
     * contains no underscores, the whole description is considered
     * linked.
     */
    private void parse(String description)
    {
        int mark = 0, nextMark = 0;
        boolean linked = false;
        segments = new ArrayList(3);
        while((nextMark = description.indexOf(LINK_MARKER,mark)) > -1) {
            segments.add(new Segment(mark,nextMark,linked));
            linked = !linked;
            mark = nextMark+1;
        }
        segments.add(new Segment(mark,description.length(),linked));
    }

}
