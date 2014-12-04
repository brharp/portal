/*-- $Id: DayPart.java,v 1.1 2006/12/22 14:40:02 ljairath Exp $ --*/
package com.aurigalogic.weatherPortlet;

/**
 * @author Sonal Anvekar
 * @version $Revision: 1.1 $ $Date: 2006/12/22 14:40:02 $ 
 */
public class DayPart {

	private static final String ICON_PATH_PREFIX = "/icons/";
	private static final String ICON_EXT = ".png";

	private String type = null;
	private String icon = null;
	private String description = null;
	private String humidity = null;
	private String ppcp = null;
	private Wind wind = null;

	public String getType() {
		return type;	
	}

	public void setType(final String type) {
		this.type = type;	
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

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public String getHumidity() {
		return humidity;
	}

	public void setHumidity(final String humidity) {
		this.humidity = humidity;
	}

	public String getPpcp() {
		return ppcp;
	}

	public void setPpcp(final String ppcp) {
		this.ppcp = ppcp;
	}

	public Wind getWind() {
		return wind;
	}

	public void setWind(final Wind wind) {
		this.wind = wind;
	}
}
