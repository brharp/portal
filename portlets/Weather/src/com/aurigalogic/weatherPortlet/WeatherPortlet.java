package com.aurigalogic.weatherPortlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.List;

import javax.portlet.GenericPortlet;
import javax.portlet.ActionRequest;
import javax.portlet.RenderRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;
import javax.portlet.PortletSession;
import javax.portlet.PortletMode;
import javax.portlet.PortletPreferences;
import javax.portlet.WindowState;
import javax.portlet.ValidatorException;

import org.apache.log4j.Logger;

public class WeatherPortlet extends GenericPortlet {
	private static Logger log = Logger.getLogger(WeatherPortlet.class);
	private static final String DEFAULT_CITY = "Guelph, Canada";
	private static String DEFAULT_CAMPUS = "9";
	private static final String DEFAULT_FORECAST_DAYS = "1";
	private static final String DEFAULT_PART_DAY = "d";
	
	private static final String DEFAULT_UNIT = "m";
	private static final PortletMode CUSTOM_CONFIG_MODE = new PortletMode("about");

	private static final String[] campusName = new String[] {
		//"Alfred", "GuelphHumber", "Kemptville", "New Liskeard", "Ridgetown", "Guelph"/*University of Guelph"*/};
		"Alma", "Alfred", "Arkell", "Cambridge", "Elora", "Emo", "Exeter", "Fergus", "Guelph", "Guelph-Humber", 
		"Kemptville", "Kettleby", "New Liskeard", "Ponsonby", "Ridgetown", "Simcoe", "Thunder Bay",
		"Vineland", "Woodstock"};	
	private static final String[] campusLocation = new String[] {
		//"Ottawa, Canada", "Malton, Canada", "Kanata, Canada", "Haileybury, Canada", "Chatham, Canada", "Guelph, Canada"};
		"Guelph, Canada", "Ottawa, Canada", "Guelph, Canada", "Cambridge, Canada", "Guelph, Canada",
		"Fort Frances, Canada", "London, Canada", "Guelph, Canada", "Guelph, Canada", "Malton, Canada", "Kanata, Canada", 
		"Newmarket, Canada", "Haileybury, Canada", "Guelph, Canada", "Chatham, Canada", "Woodstock, Canada",  
		"Thunder Bay, Canada", "Saint Catharines, Canada", "Woodstock, Canada"};

	private Map mapCampusLocation; //weather city
	private Map mapCampusName;
	
	public void init() throws PortletException {
		WeatherService.init(getPortletConfig().getInitParameter("proxyHost"),
				getPortletConfig().getInitParameter("proxyPort"));
		//init campus-location map
		initCampusLocation(campusName, campusLocation);
	}
	
	public void initCampusLocation(String[] campusName, String[] campusLocation) {
		mapCampusLocation = new HashMap();
		mapCampusName = new HashMap();
		for (int i=0; i<campusName.length; i++) {
			for (int j=0; j<campusLocation.length; j++) {
				if (i==j) {
					mapCampusName.put(String.valueOf(i+1), campusName[j]);
					mapCampusLocation.put(String.valueOf(i+1),campusLocation[j]);
				}
			}
		}	
	}
	public void processAction(ActionRequest request, ActionResponse response) throws PortletException, IOException {
		if (null != request.getParameter("cancel")) {
			//System.out.println("Cancel: " + request.getParameter("cancel"));
			processCancelAction(request, response);
		} else if (null != request.getParameter("save")) {
			//System.out.println("save: " +request.getParameter("save"));
			processSaveAndExitAction(request, response);
		}
	}
	
	public void processCancelAction(ActionRequest request, ActionResponse response) throws PortletException, IOException {
		PortletMode portletMode = PortletMode.VIEW;
		response.setPortletMode(portletMode);
	}
	
	public void processSaveAndExitAction(ActionRequest request, ActionResponse response) throws PortletException, IOException {
		PortletMode portletMode = PortletMode.VIEW;

		PortletPreferences pref = request.getPreferences();
		PortletSession psession = request.getPortletSession();
		
		String cityName = request.getParameter("cityName");
		//System.out.println("processAction param cityName " + cityName);

		String campus = null;
		
		if (cityName == null || cityName.trim().equals("")) {
			cityName = null;
			psession.setAttribute("cityName", cityName);
			//System.out.println("processAction cityName NULL" + cityName);
			campus = request.getParameter("selectCampus");
			//campus = request.getParameter("location");
			//System.out.println("processAction param campus" + campus);
			if (campus == null || Integer.parseInt(campus) == 0) {
				campus = null;
				psession.setAttribute("campus", campus);
			} else {
				psession.setAttribute("campus", campus);
				pref.setValue("campus", campus);
			}
			//System.out.println("processAction campus" + campus);
		} 
		
		if (cityName != null) {
			//psession.setAttribute("campus", null); //overrides campus
			psession.setAttribute("cityName", cityName);
			pref.setValue("cityName", cityName);
			//System.out.println("processAction cityName " + cityName);
		}
		
		String errorMessage = null;
		request.setAttribute("errorMessage", errorMessage);
		
		pref.setValue("unit", request.getParameter("unit"));
		pref.setValue("forecastDays", request.getParameter("forecastDays"));
		pref.setValue("partDay", request.getParameter("partDay"));
		
		try {
			pref.store();
		} catch(ValidatorException ve) {
			response.setRenderParameter("cityName", cityName);
			portletMode = PortletMode.EDIT;
		}
		response.setPortletMode(portletMode);
	}
	
	public void doView(RenderRequest request, RenderResponse response) throws PortletException,IOException {
		PortletPreferences pref = request.getPreferences();
		PortletSession psession = request.getPortletSession();
		
		String campus = (String)psession.getAttribute("campus");
		//System.out.println("doView campus session" + campus);
		
		String cityName = (String)psession.getAttribute("cityName");
		//System.out.println("doView city session" + cityName);
		
		String location = null;
		if (null == campus && null == cityName) { //first access
			String noCampus = null;
        	campus = pref.getValue("campus", noCampus);
	        //System.out.println("doView pref campus " + campus);        	
	        //campus ="Guelph";
	        if (null == campus || campus.length() == 0 || "".equals(campus) || campus == "0") {
		        Map userInfo = (Map) request.getAttribute(PortletRequest.USER_INFO);
		        campus = (String)userInfo.get("campus"); //ldap
		        //System.out.println("doView ldap campus " + campus);
		        //campus="Guelph";
		        //System.out.println("doView campus length" + campusName.length);
		    	boolean isCampus = false;
		        if (campus != null) {
		        	campus = campus.trim();
			        for (int i=0; i<campusName.length; i++) {
		        		//System.out.println("doView campusName" + campusName[i]);
			        	if (campus.equalsIgnoreCase(campusName[i])) {
			        		//System.out.println("doView campusName" + campusName[i]);
			        		campus = String.valueOf(i+1);
			        		DEFAULT_CAMPUS = campus;
			        		isCampus = true;
				        	break;
			        	}
			        }
		        }
		        if (!isCampus) {
		        	campus = DEFAULT_CAMPUS;	
		        }
	        }
	        if (null == campus || campus.length() == 0 || "".equals(campus) || campus == "0") {
	        	campus = DEFAULT_CAMPUS;
	        }
	        //System.out.println("doView ldap campus " + campus);
	        psession.setAttribute("campus", campus);
			location = (String)mapCampusLocation.get(campus);
		} else {
			if (cityName == null || "".equals(cityName)) {
				if (campus == null || "".equals(campus) || Integer.parseInt(campus) == 0) {
					campus = pref.getValue("campus",DEFAULT_CAMPUS);
				} 
				location = (String)mapCampusLocation.get(campus);
			} else {	
				location = cityName;
			}
		}
		request.setAttribute("campus", campus);
		request.setAttribute("cityName", cityName);
		//System.out.println("doView campus req att" + campus);
		//System.out.println("doView city req att" + cityName);
		//System.out.println("doView location" + location);
		
		java.util.Map parameters = new java.util.HashMap(5);
		parameters.put("unit", pref.getValue("unit", DEFAULT_UNIT));
		parameters.put("key", pref.getValue("key", null));
		parameters.put("par", pref.getValue("par", null));
		parameters.put("forecastDays", pref.getValue("forecastDays", DEFAULT_FORECAST_DAYS));
		request.setAttribute("partDay", pref.getValue("partDay", DEFAULT_PART_DAY));
		
		String errorMessage = null;
		request.setAttribute("errorMessage", errorMessage);
		try {
			Weather weatherInfo = WeatherService.getInstance().getWeatherByCity(location, parameters);
			//System.out.println("weatherInfo: " + weatherInfo);
			request.setAttribute("weatherInfo", weatherInfo);
			request.setAttribute("location", weatherInfo.getLocation());
			request.setAttribute("id", weatherInfo.getId());
			request.setAttribute("lsup", weatherInfo.getLsup());
			//System.out.println("locId" + weatherInfo.getId());
			//System.out.println("lsup" + weatherInfo.getLsup());
			response.setTitle("Weather for " + weatherInfo.getLocation());
		} catch (IOException ioe) {
			request.setAttribute("errorMessage", "error.communication");
			log.error("Exception while fetching weather", ioe);
		}
		request.setAttribute("mapCampusName", mapCampusName);
		response.setContentType(request.getResponseContentType());
		PortletRequestDispatcher dispatcher = getPortletContext().getRequestDispatcher("/WEB-INF/jsp/view.jsp");
		dispatcher.include(request, response);
	}
	
	public void doEdit(RenderRequest request, RenderResponse response) throws PortletException,IOException {
		PortletPreferences pref = request.getPreferences();
		PortletSession psession = request.getPortletSession();
		
		String cityName = request.getParameter("cityName");
		//System.out.println("doEdit param cityname" + cityName);
		String location = null;
		String campus = null;
		if (cityName == null || "".equals(cityName)) {
			cityName = (String)psession.getAttribute("cityName");
			//System.out.println("doEdit session cityname" + cityName);
			if (cityName != null) {
				request.setAttribute("campus", "");
				request.setAttribute("cityName", cityName);
			} else {
				campus = (String)request.getAttribute("campus");
				//System.out.println("doEdit req att campus" + campus);
				if (campus == null || "".equals(campus) || Integer.parseInt(campus) == 0) {
					campus = (String)psession.getAttribute("campus");
					//System.out.println("doEdit session campus" + campus);
				}
				if (campus == null || "".equals(campus) || Integer.parseInt(campus) == 0) {
					campus = pref.getValue("campus", DEFAULT_CAMPUS);
					//System.out.println("doEdit def campus" + campus);
				}
				request.setAttribute("campus", campus);
				request.setAttribute("cityName", "");
			}
		} else {
			location = cityName;
			request.setAttribute("campus", "");
			request.setAttribute("cityName", cityName);
			
			String errorMessage = null;
			//load locations
			errorMessage = null;
			request.setAttribute("errorMessage", errorMessage);
			// either no cities or too many cities were found.
			try {
				//System.out.println("**********doEdit  location" + location);
				List locations = WeatherService.getInstance().loadLocations(location);
				if (locations.size() == 0) {
					errorMessage = "error.cityNotFound";
				} else {
					errorMessage = "error.multipleCitiesFound";
					request.setAttribute("locations", locations);
				}
			} catch (IOException ioe) {
				log.error("Exception while fetching locations", ioe);
				errorMessage = "error.communication";
			}
			request.setAttribute("errorMessage", errorMessage);
		}
		/*	
		 if (cityName == null && campus == null) {
		 //cityName = pref.getValue("cityName", DEFAULT_CITY);
		  campus = pref.getValue("campus", DEFAULT_CAMPUS);
		  request.setAttribute("campus", campus);
		  //System.out.println("doEdit  campus" + campus);
		  }*/
		
		request.setAttribute("unit", pref.getValue("unit", DEFAULT_UNIT));
		request.setAttribute("forecastDays", pref.getValue("forecastDays", DEFAULT_FORECAST_DAYS));
		//request.setAttribute("campus", pref.getValue("campus", DEFAULT_CAMPUS));
		request.setAttribute("partDay", pref.getValue("partDay", DEFAULT_PART_DAY));
		
		response.setContentType(request.getResponseContentType());
		PortletRequestDispatcher dispatcher = getPortletContext().getRequestDispatcher("/WEB-INF/jsp/edit.jsp");
		dispatcher.include(request, response);
	}
	
	protected void doDispatch(RenderRequest request, RenderResponse response) throws PortletException, IOException {
		if (! WindowState.MINIMIZED.equals(request.getWindowState())) {
			PortletMode mode = request.getPortletMode();
			if (CUSTOM_CONFIG_MODE.equals(mode)) {
				doCustomConfigure(request, response);
				return;
			}
		}
		super.doDispatch(request, response);
	}
	
	private void doCustomConfigure(RenderRequest request, RenderResponse response) throws PortletException, 
	
	IOException {
		response.setContentType(request.getResponseContentType());
		PortletRequestDispatcher dispatcher = getPortletContext().getRequestDispatcher("/WEB-INF/jsp/about.jsp");
		dispatcher.include(request, response);
	}
} 
