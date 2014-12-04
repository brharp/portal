package com.aurigalogic.weatherPortlet;

import org.apache.log4j.Logger;

import java.util.List;

import javax.portlet.PortletPreferences;
import javax.portlet.ValidatorException;

public class WeatherPortletValidator implements javax.portlet.PreferencesValidator {
	private static Logger log = Logger.getLogger(WeatherPortletValidator.class);
	
	public void validate(PortletPreferences pref) throws ValidatorException {
		String cityName = pref.getValue("cityName", null);
		System.out.println("cityName valid" + cityName);
		try {
			List locations = WeatherService.getInstance().loadLocations(cityName);
			int size = locations.size();
			System.out.println("size valid" + size);
			if (size != 1) {
				throw new ValidatorException("Could not find unique city.", null);
			}
		} catch (Exception e) {
			throw new ValidatorException(e, null);
		}
	}
}
