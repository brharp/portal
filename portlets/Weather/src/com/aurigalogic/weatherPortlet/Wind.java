/*-- $Id: Wind.java,v 1.1 2006/12/22 14:40:02 ljairath Exp $ --*/
package com.aurigalogic.weatherPortlet;

/**
 * @author Sonal Anvekar
 * @version $Revision: 1.1 $ $Date: 2006/12/22 14:40:02 $ 
 */
public class Wind {

	private String gust = null;
	private String degree = null;
	private String speed = null;
	private String direction = null;

	public String getGust() {
		return gust;	
	}

	public void setGust(final String gust) {
		this.gust = gust;	
	}
			
	public String getSpeed() {
		return speed;
	}

	public void setSpeed(final String speed) {
		this.speed = speed;
	}

	public String getDegree() {
		return degree;
	}

	public void setDegree(final String degree) {
		this.degree = degree;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(final String direction) {
		this.direction = direction;
	}

}
