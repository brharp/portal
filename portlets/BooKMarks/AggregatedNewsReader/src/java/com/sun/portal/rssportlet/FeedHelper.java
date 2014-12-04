/*
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the License).  You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the license at
 * http://www.sun.com/cddl/cddl.html or
 * at portlet-repository/CDDLv1.0.txt.
 * See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL
 * Header Notice in each file and include the License file
 * at portlet-repository/CDDLv1.0.txt. 
 * If applicable, add the following below the CDDL Header,
 * with the fields enclosed by brackets [] replaced by
 * you own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 */

package com.sun.portal.rssportlet;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.fetcher.FeedFetcher;
import com.sun.syndication.fetcher.FetcherException;
import com.sun.syndication.io.FeedException;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;

import java.net.URL;
import java.net.URLConnection;
import org.xml.sax.InputSource;

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndImage;
import com.sun.syndication.feed.synd.SyndImageImpl;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;

import java.io.IOException;
import java.util.*;
import java.text.*;
import org.xml.sax.InputSource;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.*; 

import java.io.InputStreamReader;
import java.io.PrintWriter;

import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.io.SyndFeedOutput;
import com.sun.syndication.io.XmlReader;
import com.sun.syndication.feed.synd.SyndEntryImpl;

import java.net.MalformedURLException;
import org.apache.commons.httpclient.HttpException;
import com.sun.syndication.io.ParsingFeedException;


/**
 * This class manages a cache of ROME feeds.
 */
public class FeedHelper {
	// singleton instance
	private static FeedHelper feedHelper = new FeedHelper();
    private static ClassPathResource res = new ClassPathResource("ApplicationContext.xml");
    private static XmlBeanFactory factory = new XmlBeanFactory(res);
    private static FeedFetcher fetcher = (FeedFetcher) factory.getBean("syndFeedFetcherWithCache");
    
	private FeedHelper() {
		// nothing, cannot be called
	}
	
	/**
	 * Get the feed handler singleton instance.
	 */
	public static FeedHelper getInstance() {
		return feedHelper;
	}
    
    /**
     * Get the ROME SyndFeed object for the specified feed. The object may come
     * from a cache; the data in the feed may not be read at the time
     * this method is called.
     *
     * The <code>RssPortletBean</code> object is used to identify the feed
     * of interest, and the timeout value to be used when managing this
     * feed's cached value.
     *
     * @param bean an <code>RssPortletBean</code> object that describes
     * the feed of interest, and the cache timeout value for the feed.
     * @return a ROME <code>SyndFeed</code> object encapsulating the
     * feed specified by the URL.
     */
    public SyndFeed getFeed(SettingsBean bean, String selectedFeed) throws IOException, FeedException {
    	SyndFeed feed = null;
        
        try {
            feed = fetcher.retrieveFeed(new URL(selectedFeed));
        }
        catch(MalformedURLException mfurlex){
			System.out.println("MalformedURLException: "+ mfurlex.getMessage());
			mfurlex.printStackTrace();
		}
		catch(HttpException httpex){
			System.out.println("HttpException: "+ httpex.getMessage());
			httpex.printStackTrace();
		}
		catch(IOException ioe){
			System.out.println("IOException: "+ ioe.getMessage());
			ioe.printStackTrace();
		}
		catch(ParsingFeedException parsingfeedex) {
			System.out.println("ParsingFeedException: "+ parsingfeedex.getMessage());
			parsingfeedex.printStackTrace();
		}
		catch(FeedException feedex){
			System.out.println("FeedException: "+ feedex.getMessage());
			feedex.printStackTrace();
		}
		catch (Exception ex) {
			System.out.println("Exception ERROR: "+ ex.getMessage());
			ex.printStackTrace();
		}
        return feed;        
    }
	
    private SyndFeed aggregateFeed(SettingsBean bean) throws IOException, FeedException {
    	String selectedFeed = bean.getSelectedFeed();
    	String feedType = bean.getSelectedFeedType();
    	int maxNumEntries = bean.getMaxEntries();
    	int maxNumPaperEntries = bean.getMaxNewsPaperEntries();
		//System.out.println("***Agg Feed feedType" + feedType);
		//System.out.println("***Agg Feed numEntries" + numEntries);
    	
    	String aggregateFeed[] = null;
    	SyndFeed feed = null;
		List entries = new ArrayList();
    	if ("http://myportico.uoguelph.ca/".equalsIgnoreCase(selectedFeed)) {
    		feed = new SyndFeedImpl();
    		SyndImage image = new SyndImageImpl();
    		String outputType = "atom_0.3";//"RSS 2.0";//;//args[0];
    		feed.setFeedType(outputType);
    		image.setUrl("https://myportico.uoguelph.ca/portal/UofGNews.jpg");
    		feed.setImage(image);
    		feed.setTitle("U of G News");
    		feed.setDescription("All my University of Guelph News");
    		feed.setAuthor("Lalit Jairath");
    		LinkedList feeds = (LinkedList)bean.getFeeds();
    		feeds.remove(0);
    		aggregateFeed = (String[])feeds.toArray(new String[0]);
    		//feed.setLink("http://myportico.uoguelph.ca/");
    	} else {
    		feed = getFeed(bean, selectedFeed);
    		aggregateFeed = new String[] {selectedFeed};
    	}
    	
    	int maxNumRiverEntries = 3;
        for (int i=0; i<aggregateFeed.length;i++) {
            //System.out.println("*** aggregateFeed ****" + aggregateFeed[i]);
            //URL inputUrl = new URL(aggregateFeed[i]);
            try {
                //SyndFeed inFeed = getFeed(bean, inputUrl);
            	SyndFeed inFeed = getFeed(bean, aggregateFeed[i]);
        		DateFormat formatter;
                //Get today's date
                Date date1 = new Date();
                formatter = new SimpleDateFormat("MM/dd/yy");
                String dateString = formatter.format(date1);
                Date d1 = null;
                try {
                	d1 = formatter.parse(dateString);
                } catch (ParseException pe) {
                	System.out.println("AggregateFeed ParseException:" + pe.getMessage());
                }
                String feedTitle = inFeed.getTitle();
                if (null == feedTitle || feedTitle.length() == 0) {
                    feedTitle = aggregateFeed[i];
                }
    			//System.out.println("***Agg Feed feedTitle" + feedTitle);
                List feedEntry = inFeed.getEntries();
                Collections.sort(feedEntry, new ColumnSorter());
                
                Iterator it = feedEntry.listIterator();
                List entryTitleDate = new ArrayList();
                int entrySize = 0;
                /*
        		if (null != feedEntry) {
        			entrySize = feedEntry.size();
        			System.out.println("***fedentry size" + feedEntry.size());
        		}*/		
        		//int numPaperEntry = 3;
        		
                int entryCounter = 0;
                while (it.hasNext()) {
                    SyndEntryImpl syndEntryImpl = (SyndEntryImpl) it.next();
                    Date entryDate = syndEntryImpl.getPublishedDate();
                    dateString = formatter.format(entryDate);
                    Date d2 = null;
                    try {
                    	d2 = formatter.parse(dateString);
                    } catch (ParseException pe) {
                    	System.out.println("AggregateFeed ParseException:" + pe.getMessage());
                    }

                    if (d2.after(d1)) {
                    	it.remove();
                    	continue;
                    }
                    String entryTitle = syndEntryImpl.getTitle();
                    String[] titleArr = entryTitle.split("%");
                    
                    if (aggregateFeed[i].equalsIgnoreCase("http://myportico.uoguelph.ca/portal/rss/cipexchange.xml")) {
                        feedTitle = "CIP Exchange Student News";
                    } else if (aggregateFeed[i].equalsIgnoreCase("http://myportico.uoguelph.ca/portal/rss/cipfacstaff.xml")){
                        feedTitle = "CIP Faculty and Staff News";
                    } else if (aggregateFeed[i].equalsIgnoreCase("http://myportico.uoguelph.ca/portal/rss/cipstudent.xml")){
                        feedTitle = "CIP Student News";
                    } 
                    
                    entryTitle = titleArr[titleArr.length-1];

                    StringBuffer newEntryTitle = new StringBuffer();  
                    newEntryTitle.append(feedEntry.size()).append("%").append(aggregateFeed[i]).append("%").append(feedTitle).
                    	append(":").append("%").append(entryTitle);
                    syndEntryImpl.setTitle(newEntryTitle.toString());
                    entryTitle = syndEntryImpl.getTitle();
                    /*
                    if (! "http://myportico.uoguelph.ca/".equalsIgnoreCase(selectedFeed)) {
	                    System.out.println("entryTitle1:" + entryTitle);
	                    System.out.println("newEntryTitle:" + newEntryTitle);
	                    System.out.println("entryTitle2:" + entryTitle);
                    }*/
                    entries.add(syndEntryImpl);
                    entryCounter++;
                    if ( 
                    	(entryCounter == maxNumRiverEntries && 
                    		"http://myportico.uoguelph.ca/".equalsIgnoreCase(selectedFeed) && 
                    		feedType.equalsIgnoreCase("river")) ||
                    	(entryCounter == maxNumPaperEntries && 
                           		"http://myportico.uoguelph.ca/".equalsIgnoreCase(selectedFeed) && 
                           		feedType.equalsIgnoreCase("newspaper")) ||
                         (entryCounter == maxNumEntries && 
                           		! "http://myportico.uoguelph.ca/".equalsIgnoreCase(selectedFeed)) ) {
                    	break;
                    }
                } 
                /*
        		if ("newspaper".equalsIgnoreCase(feedType)) {
        			List numListEntry = new ArrayList();
        			if (null != feedEntry && feedEntry.size() != 0) {
        				for (int j=0; j<maxNumPaperEntry && j<feedEntry.size(); j++) {
        					numListEntry.add(feedEntry.get(j));
        				}
        				entries.addAll(numListEntry);
        			}
        		} else {
        			List numListEntry = new ArrayList();
        			if (null != feedEntry && feedEntry.size() != 0) {
        				for (int j=0; j<numEntries && j<feedEntry.size(); j++) {
        					numListEntry.add(feedEntry.get(j));
        				}
        				entries.addAll(numListEntry);
        			}
        		}*/
            }
            catch(MalformedURLException mfurlex){
				System.out.println("MalformedURLException: "+ mfurlex.getMessage());
				mfurlex.printStackTrace();
			}
			catch(HttpException httpex){
				System.out.println("HttpException: "+ httpex.getMessage());
				httpex.printStackTrace();
			}
			catch(IOException ioe){
				System.out.println("IOException: "+ ioe.getMessage());
				ioe.printStackTrace();
			}
			catch(ParsingFeedException parsingfeedex) {
				System.out.println("ParsingFeedException: "+ parsingfeedex.getMessage());
				parsingfeedex.printStackTrace();
			}
			catch(FeedException feedex){
				System.out.println("FeedException: "+ feedex.getMessage());
				feedex.printStackTrace();
			}
			catch (Exception ex) {
				System.out.println("Exception ERROR: "+ ex.getMessage());
				ex.printStackTrace();
			}
		}
		if("river".equalsIgnoreCase(feedType)) {
			Collections.sort(entries, new ColumnSorter());
		}
		
		feed.setEntries(entries);
		return feed;       
    }

	/**
	 * Get the ROME SyndFeed object for the feed specified by the 
	 * SettingsBean's selectedFeed field.
	 */
    public SyndFeed getFeed(SettingsBean bean) throws IOException, FeedException {
    	return aggregateFeed(bean);
		//return getFeed(bean, bean.getSelectedFeed());
	}
}

class ColumnSorter implements Comparator {
	public int compare(Object a, Object b) {
		SyndEntryImpl rowMap1 = (SyndEntryImpl)a;
		SyndEntryImpl rowMap2 = (SyndEntryImpl)b;
		Object o1 = rowMap1.getPublishedDate();
		Object o2 = rowMap2.getPublishedDate();
		if (o1 == null || o2 == null) {
			return 0;
		}
		return ((Comparable)o2).compareTo(o1);
	}
}

