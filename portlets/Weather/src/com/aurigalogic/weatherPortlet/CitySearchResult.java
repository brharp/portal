/*-- $Id: CitySearchResult.java,v 1.1 2006/12/22 14:40:02 ljairath Exp $ --*/
package com.aurigalogic.weatherPortlet;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.apache.commons.digester.*;

/**
 * @author Sonal Anvekar
 * @version $Revision: 1.1 $ $Date: 2006/12/22 14:40:02 $ 
 */
public class CitySearchResult {

	private static Logger log = Logger.getLogger(CitySearchResult.class);
	private ArrayList locations = new ArrayList();
	private static Digester digester = null;

	public static CitySearchResult parse(String xml) {
		Digester digester = getDigester();
		try {
			return (CitySearchResult) digester.parse(new java.io.StringReader(xml));
		} catch (Exception e) {
			log.error("Exception while parsing xml", e);
			throw new SysException();
		}	
	}

	public void addLocation(final Location loc) {
		locations.add(loc);
	}

	public Location getFirstLocation() {
		return (Location) locations.get(0);
	}

	public ArrayList getLocations() {
		return locations;
	}

	private static Digester getDigester() {
		if (digester == null) {
			digester = new Digester();
			digester.setValidating(false);
			digester.addObjectCreate("search", CitySearchResult.class);
			digester.addObjectCreate("search/loc", Location.class);
			digester.addBeanPropertySetter("search/loc", "name");
			digester.addSetProperties("search/loc");
			digester.addSetNext("search/loc", "addLocation");
		}
		return digester;
	}

}
