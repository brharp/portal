/*-- $Id: CC.java,v 1.1 2006/12/22 14:40:02 ljairath Exp $ --*/
package com.aurigalogic.weatherPortlet;

/**
 * @author lalit
 *  
 */
public class CC implements java.io.Serializable {
	private String lsup = null;
	private String obst = null;
	private String tmp = null;
	private String flik = null;
	private String t = null;
	private String icon = null;
	private String dewp = null;
	
	public CC() {}
	
	public String getLsup() {
		return lsup;
	}
	
	public void setLsup(final String lsup) {
		this.lsup = lsup;
	}
	
	public String getObst() {
		return obst;
	}
	
	public void setObst(final String obst) {
		this.obst = obst;
	}
	public String getTmp() {
		return tmp;
	}
	
	public void setTmp(final String tmp) {
		this.tmp = tmp;
	}
	
	public String getFlik() {
		return flik;
	}
	
	public void setFlik(final String flik) {
		this.flik = flik;
	}
	
	public String getT() {
		return t;
	}
	
	public void setT(final String t) {
		this.t = t;
	}
	
	public String getIcon() {
		return icon;
	}
	
	public void setIcon(final String icon) {
		this.icon = icon;
	}
	
	public String getDewp() {
		return dewp;
	}
	
	public void setDewp(final String dewp) {
		this.dewp = dewp;
	}
	
	/*
	 public String get() {
	 return ;
	 }
	 
	 public void set(final String ) {
	 this. = ;
	 }
	 */
	
}
