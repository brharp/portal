package com.aurigalogic.weatherPortlet;

import junit.framework.*;

import java.io.IOException;

import org.apache.log4j.Logger;

public class WeatherServiceTest extends TestCase {

	protected transient Logger log = null;

    public WeatherServiceTest(String name) {
		super(name);	
		WeatherService.init(null, "8090");
		log = Logger.getLogger(this.getClass());	
		log.info(
				"\n--------------------------------------------\n"
				+ "Running Test: " + name +
				"\n--------------------------------------------\n"
				);
	}

	public void testLoadLocationId() throws IOException {
		String cityName = "mumbai";
		String locationId = WeatherService.getInstance().loadLocationId(cityName);	
		assertEquals(locationId, "INXX0087");
	}

	public void testGetWeatherByLocId() throws IOException {
		long then = System.currentTimeMillis();
		java.util.Map parameters = new java.util.HashMap(3);
		parameters.put("forecastDays", "2");
		parameters.put("par", "");
		parameters.put("key", "");
		parameters.put("unit", "m");
		Weather weather = WeatherService.getInstance().getWeatherByCity("mumbai", parameters);
		assertEquals(2, weather.getDays().size());
		log.info("Exec time testGetWeatherByLocId(): " + (System.currentTimeMillis() - then));
	}

	public void testGetWeatherByLocIdFromCache() throws IOException {
		long then = System.currentTimeMillis();
		java.util.Map parameters = new java.util.HashMap(3);
		parameters.put("forecastDays", "2");
		parameters.put("par", "");
		parameters.put("key", "");
		parameters.put("unit", "m");
		Weather weather = WeatherService.getInstance().getWeatherByCity("mumbai", parameters);
		assertEquals(2, weather.getDays().size());
		log.info("Exec time testGetWeatherByLocIdFromCache(): " + (System.currentTimeMillis() - then));
	}
}
