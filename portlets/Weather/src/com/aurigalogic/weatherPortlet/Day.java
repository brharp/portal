/*-- $Id: Day.java,v 1.1 2006/12/22 14:40:02 ljairath Exp $ --*/
package com.aurigalogic.weatherPortlet;

import java.util.List;

/**
 * @author Sonal Anvekar
 * @version $Revision: 1.1 $ $Date: 2006/12/22 14:40:02 $ 
 */
public class Day {

	private String hi = null;
	private String low = null;
	private String sunrise = null;
	private String sunset = null;
	private String dow = null;
	private String date = null;
	private List dayParts = new java.util.ArrayList(2);

	public String getHi() {
		return hi;
	}
		
	public void setHi(final String hi) {
		this.hi = hi;
	}

	public String getLow() {
		return low;
	}
		
	public void setLow(final String low) {
		this.low = low;
	}

	public String getDow() {
		return dow;
	}
		
	public void setDow(final String dow) {
		this.dow = dow;
	}

	public void setDate(final String date) {
		this.date = date;
	}

	public String getDate() {
		return date;
	}

	public String getSunrise() {
		return sunrise;
	}

	public void setSunrise(final String sunrise) {
		this.sunrise = sunrise;
	}


	public String getSunset() {
		return sunset;
	}

	public void setSunset(final String sunset) {
		this.sunset = sunset;
	}

	public List getDayParts() {
		return dayParts;
	}

	public void addPart(final DayPart part) {
		dayParts.add(part);
	}
}
