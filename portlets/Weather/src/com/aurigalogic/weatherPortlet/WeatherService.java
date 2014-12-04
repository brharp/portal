/*-- $Id: WeatherService.java,v 1.1 2006/12/22 14:40:02 ljairath Exp $ --*/
package com.aurigalogic.weatherPortlet;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.io.IOException;
import java.net.URLEncoder;

import org.apache.log4j.Logger;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import net.sf.ehcache.CacheManager;

import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.HttpClient;

/**
 * A facade to weather.com.
 *
 * @author Sonal Anvekar
 * @version $Revision: 1.1 $ $Date: 2006/12/22 14:40:02 $ 
 */
public class WeatherService {

	private static Logger log = Logger.getLogger(WeatherService.class);
	private Cache weatherCache = null;
	private Cache locationCache = null;
	private Cache locationsCache = null;

	private static WeatherService service = null;
	private String proxyHost = null;
	private int proxyPort = 0;
	boolean hasProxy = false;
	private static Properties info = null;
		
	private WeatherService(String proxyHost, String proxyPort) {
		this.proxyHost = proxyHost;
		hasProxy = (proxyHost != null && ! proxyHost.trim().equals(""));
		if (hasProxy) {
			try {
				this.proxyPort = Integer.parseInt(proxyPort);
			} catch (Exception e) {
				throw new SysException("The value of proxyPort: " + proxyPort + " must be a number!", e);
			}
		}
		try {
			CacheManager manager = CacheManager.create();
			weatherCache = manager.getCache("weather");
			locationCache = manager.getCache("location");
			locationsCache = manager.getCache("locations");
		} catch (Exception e) {
			log.error("Exception while initialising the cache!", e);
			throw new SysException("Exception while initialising the cache!", e);
		}

		info = new Properties();
		try {
			info.load(this.getClass().getResourceAsStream("Info.properties"));
		} catch (Exception e) {
			throw new SysException(e);
		}
	}

	public static synchronized void init(String proxyHost, String proxyPort) {
		service = new WeatherService(proxyHost, proxyPort);
	}

	public static WeatherService getInstance() {
		return service;
	}

	public Weather getWeatherByCity(String cityName, Map parameters) throws IOException {
		String locId = loadLocationId(cityName);
		String url = makeWeatherRequestUrl(locId, parameters);
		try {
			Element element = weatherCache.get(url);
			Weather weatherInfo = null;
			if (element == null) {
				weatherInfo = Weather.parse(fetchXml(url));
				weatherCache.put(new Element(url, weatherInfo));
			} else {
				weatherInfo = (Weather) element.getValue();
			}
			return weatherInfo;
		} catch (net.sf.ehcache.CacheException ce) {
			log.error("Error while getting weather from cache.", ce);
			throw new SysException("Error while getting weather from cache.", ce);
		}
	}

	protected List loadLocations(String cityName) throws IOException {
		cityName = cityName.toLowerCase();
		try {
			Element element = locationsCache.get(cityName);
			List locations = null;
			if (element == null) {
				CitySearchResult result = CitySearchResult
							.parse(fetchXml("http://xoap.weather.com/search/search?where=" 
									+ URLEncoder.encode(cityName, "UTF-8")));
				locations = result.getLocations();
				locationsCache.put(new Element(cityName, (java.util.ArrayList) locations));
			} else {
				locations = (List) element.getValue();
			}
			return locations;
		} catch (net.sf.ehcache.CacheException ce) {
			log.error("Error while getting locations from cache.", ce);
			throw new SysException("Error while getting locations from cache.", ce);
		}
	}

	protected String loadLocationId(String cityName) throws IOException {
		cityName = cityName.toLowerCase();
		try {
			Element element = locationCache.get(cityName);
			String locationId = null;
			if (element == null) {
				CitySearchResult result = CitySearchResult.parse(
								fetchXml("http://xoap.weather.com/search/search?where=" 
									+ URLEncoder.encode(cityName, "UTF-8")));
				locationId = result.getFirstLocation().getId();
				locationCache.put(new Element(cityName, locationId));
			} else {
				locationId = (String) element.getValue();
			}
			return locationId;
		} catch (net.sf.ehcache.CacheException ce) {
			log.error("Exception while getting locationId!", ce);
			throw new SysException("Exception while getting locationId!", ce);
		}
	}

	private String makeWeatherRequestUrl(String locId, Map parameters) {
		 String weatherRequestUrl = new StringBuffer("http://xoap.weather.com/weather/local/")
					.append(locId).append("?cc=*&prod=xoap")
					.append("&dayf=1")//.append(parameters.get("forecastDays")) forecast days turned off
					.append("&par=").append(parameters.get("par"))
					.append("&key=").append(parameters.get("key"))
					.append("&unit=").append(parameters.get("unit"))
					.toString();
		 //System.out.println("weatherRequestUrl*****************=" + weatherRequestUrl);
		 	return weatherRequestUrl;
	}
	
	private String fetchXml(String url) throws IOException {
		GetMethod get = new GetMethod(url);
		try {
			HttpClient httpclient = new HttpClient();
			if (hasProxy) {
				httpclient.getHostConfiguration().setProxy(proxyHost, proxyPort);
			}
			httpclient.executeMethod(get);
			//System.out.println("get.getResponseBodyAsString();" + get.getResponseBodyAsString());
			return get.getResponseBodyAsString();
		} catch (org.apache.commons.httpclient.HttpException he) {
			throw new SysException(he);
		} finally {
			get.releaseConnection();
		}
	}

	public static String getVersion() {
		return info.getProperty("version");
	}
}
