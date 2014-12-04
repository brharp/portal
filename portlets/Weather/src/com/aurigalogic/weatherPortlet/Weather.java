/*-- $Id: Weather.java,v 1.1 2006/12/22 14:40:02 ljairath Exp $ --*/
package com.aurigalogic.weatherPortlet;

import java.util.List;

import org.apache.commons.digester.*;

/**
 * @author Sonal Anvekar
 * @version $Revision: 1.1 $ $Date: 2006/12/22 14:40:02 $ 
 */
public class Weather implements java.io.Serializable {

	private static Digester digester = null;
	private static final String ICON_PATH_PREFIX = "/icons/";
	private static final String ICON_EXT = ".png";

	private String tempUnit = null;
	private String volumeUnit = null;
	private String pressureUnit = null;
	private String speedUnit = null;
	private String location = null;
	private String lastUpdated = null;

	private String windSpeed = null;
	private String windDirection = null;
	
	private String id = null;
	
	private String lsup = null;
	private String tmp = null;
	private String t = null;
	private String icon = null;
	private String hmid = null;
	private String flik = null;
	private String vis = null;
	private String dewp = null;
	//private String = null;
	//private String = null;
	//private String = null;
	
	private List days = new java.util.ArrayList();

	public static Weather parse(final String xml) {
		Digester _digester = getDigester();
		try {
			return (Weather) _digester.parse(new java.io.StringReader(xml));
		} catch (Exception e) {
			throw new SysException();
		}
	}

	public String getWindSpeed() {
		return windSpeed;
	}

	public void setWindSpeed(final String windSpeed) {
		this.windSpeed= windSpeed;
	}

	public String getWindDirection() {
		return windDirection;
	}

	public void setWindDirection(final String windDirection) {
		this.windDirection= windDirection;
	}

	public String getFlik() {
		return flik;
	}
	
	public void setFlik(final String flik) {
		this.flik = flik;
	}

	public String getId() {
		return id;
	}
	
	public void setId(final String id) {
		this.id = id;
	}
	
	public String getLsup() {
		return lsup;
	}
	
	public void setLsup(final String lsup) {
		this.lsup = lsup;
	}
	
	public String getTmp() {
		return tmp;
	}
	
	public void setTmp(final String tmp) {
		this.tmp = tmp;
	}
	
	public String getT() {
		return t;
	}
	
	public void setT(final String t) {
		this.t = t;
	}

	public String getHmid() {
		return hmid;
	}
	
	public void setHmid(final String hmid) {
		this.hmid = hmid;
	}

	public String getIcon() {
		return icon;
	}
	
	public void setIcon(final String icon) {
		this.icon = icon;
	}
	
	public String getIconPath() {
		return new StringBuffer(ICON_PATH_PREFIX)
					.append(icon).append(ICON_EXT).toString();
	}

	public String getTempUnit() {
		return tempUnit;
	}

	public void setTempUnit(final String tempUnit) {
		this.tempUnit = tempUnit;
	}

	public String getSpeedUnit() {
		return speedUnit;
	}

	public void setSpeedUnit(final String speedUnit) {
		this.speedUnit = speedUnit;
	}

	public String getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(final String lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public String getVolumeUnit() {
		return volumeUnit;
	}

	public void setVolumeUnit(final String volumeUnit) {
		this.volumeUnit = volumeUnit;
	}

	public String getPressureUnit() {
		return pressureUnit;
	}
		
	public void setPressureUnit(final String pressureUnit) {
		this.pressureUnit = pressureUnit;
	}
	
	public String getLocation() {
		return location;
	}
		
	public void setLocation(final String location) {
		this.location = location;
	}
	
	public List getDays() {
		return days;
	}
		
	public void addDay(final Day day) {
		days.add(day);
	}

	public int getDaysSize() {
        return getDays().size();
    }
    
	private static Digester getDigester() {
		if (digester == null) {
			digester = new Digester();
			digester.setValidating(false);
			digester.addObjectCreate("weather", Weather.class);
			digester.addBeanPropertySetter("weather/head/ut", "tempUnit");
			digester.addBeanPropertySetter("weather/head/up", "pressureUnit");
			digester.addBeanPropertySetter("weather/head/ur", "volumeUnit");
			digester.addBeanPropertySetter("weather/head/us", "speedUnit");
			digester.addBeanPropertySetter("weather/loc/dnam", "location");
			digester.addBeanPropertySetter("weather/dayf/lsup", "lastUpdated");
			digester.addSetProperties( "weather/loc", "id", "id" );
			//digester.addObjectCreate("weather/cc", CC.class);
			digester.addBeanPropertySetter("weather/cc/flik", "flik");
			digester.addBeanPropertySetter("weather/cc/lsup", "lsup");
			digester.addBeanPropertySetter("weather/cc/tmp", "tmp");
			digester.addBeanPropertySetter("weather/cc/t", "t");
			digester.addBeanPropertySetter("weather/cc/icon", "icon");
			digester.addBeanPropertySetter("weather/cc/hmid", "hmid");
			digester.addBeanPropertySetter("weather/cc/wind/s", "windSpeed");
			digester.addBeanPropertySetter("weather/cc/wind/t", "windDirection");
			
			digester.addObjectCreate("weather/dayf/day", Day.class);
			digester.addSetProperties("weather/dayf/day", new String[] {"t", "dt"}, new String[] {"dow", "date"});
			digester.addSetNext("weather/dayf/day", "addDay");
			digester.addBeanPropertySetter("weather/dayf/day/hi");
			digester.addBeanPropertySetter("weather/dayf/day/low");
			digester.addBeanPropertySetter("weather/dayf/day/sunrise");
			digester.addBeanPropertySetter("weather/dayf/day/sunset");
	
			digester.addObjectCreate("weather/dayf/day/part", DayPart.class);
			digester.addSetProperties("weather/dayf/day/part", new String[] {"p"}, new String[] {"type"});
			digester.addBeanPropertySetter("weather/dayf/day/part/icon");
			digester.addBeanPropertySetter("weather/dayf/day/part/t", "description");
			digester.addBeanPropertySetter("weather/dayf/day/part/ppcp");
			digester.addBeanPropertySetter("weather/dayf/day/part/hmid", "humidity");
			digester.addSetNext("weather/dayf/day/part", "addPart");

			digester.addObjectCreate("weather/dayf/day/part/wind", Wind.class);
			digester.addSetNext("weather/dayf/day/part/wind", "setWind");
			digester.addBeanPropertySetter("weather/dayf/day/part/wind/gust");
			digester.addBeanPropertySetter("weather/dayf/day/part/wind/t", "direction");
			digester.addBeanPropertySetter("weather/dayf/day/part/wind/d", "degree");
			digester.addBeanPropertySetter("weather/dayf/day/part/wind/s", "speed");
		}
		return digester;	
	}

}
