package ca.uoguelph.ccs.portal.portlets.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

import java.lang.String;

public class ElmsUtils {
	private final static String startUrl = "https://dev02.e-academy.com/guelph/index.cfm?loc=login/login";
	private final static String validateUserCredentialsUrl = "https://dev02.e-academy.com/guelph/index.cfm?loc=login/cab_cgi";
	
	private final static String parmReturnUrl = "return_url=";
	private final static String parmToken = "token=";
	
	private static final Log LOG = LogFactory.getLog(ElmsUtils.class);
	
	protected static String getRedirectURL(String uid, String organisationalStatus) throws Exception {
		String tokenAndReturnURL = getTokenAndReturnURL();
		//parse token and return url value
		int returnUrlPos = tokenAndReturnURL.indexOf(parmReturnUrl);
		String returnUrl = tokenAndReturnURL.substring(returnUrlPos + parmReturnUrl.length());
		
		int tokenPos = tokenAndReturnURL.indexOf(parmToken);
		String restOfString = tokenAndReturnURL.substring(tokenPos + parmToken.length());
		String token = restOfString.substring(0, restOfString.indexOf("&"));
		
		boolean isValidUserCredentials = validateUserCredentials(uid, organisationalStatus, token);
		if (isValidUserCredentials == false) {
			throw new Exception("ELMS did not vaildate");
		}
		//int tokenEndPos = tokenAndReturnURL.indexOf("&");
		//String redirectUrl = tokenAndReturnURL.substring(0, tokenEndPos);
		String redirectUrl = returnUrl + "&uid=" + uid + "&token=" + token;
		return redirectUrl;
	}
	
	/**
	 * information needed is in the Location header
	 * sets the token value so it can be used in the validation
	 * 
	 */
	private static String getTokenAndReturnURL() {
		HttpURLConnection connection = null;
		String tokenAndReturnUrl = null;
		try {
			URL url = new URL(startUrl);
			connection = (HttpURLConnection) url.openConnection();
			//connection.setInstanceFollowRedirects(false);
			connection.connect();
			//Test
			if (LOG.isDebugEnabled()) {
				BufferedReader dataInput = new BufferedReader(
						new InputStreamReader(connection.getInputStream()));
				String line = null;
				//string match on 0 Account created, or updated
				while ((line = dataInput.readLine()) != null) {
					LOG.debug("line = " + line);
				}
			}//Test end
			
			//Location: https://portal.uoguelph.ca?
			//token=364460500&
			//return_url=https://dev02.e-academy.com/guelph/index.cfm?loc=login/login_campus_authenticate&CFID=11823&CFTOKEN=80725633
			String location = connection.getHeaderField("Location");
			if (location == null) {
				LOG.error("URL failed: " + startUrl);
				LOG.error("Response code: " + connection.getResponseCode());
				LOG.error("Response message: " + connection.getResponseMessage());
			}
			int tokenPos = location.indexOf(parmToken);
			tokenAndReturnUrl = location.substring(tokenPos);
		} catch (IOException e) {
			LOG.error(e);
		} finally {
			if (connection != null) connection.disconnect();
		}
		return tokenAndReturnUrl;
	}
	/**
	 * 
	 * @param username
	 * @param group
	 * @param token
	 * @return
	 */
	private static boolean validateUserCredentials(String username, String organisationalStatus, String token) {
		boolean isValidUserCredentials = false;
		HttpURLConnection connection = null;
		String groups = null;
		String validateUserCredentialsUrl = null;
		try {
			//map the LDAP organisationalStatus to Elms group
			groups = getGroups(organisationalStatus);
			validateUserCredentialsUrl = validateUserCredentialsUrl + "&uid=" + username + "&groups=" + groups + "&department=Guelph&token=" + token;
			URL url = new URL(validateUserCredentialsUrl);
			//HttpURLConnection.setFollowRedirects(false);
			connection = (HttpURLConnection) url.openConnection();
			connection.connect();
			BufferedReader dataInput = new BufferedReader(
					new InputStreamReader(connection.getInputStream()));
			String line = null;
			//string match on 0 Account created, or updated
			while ((line = dataInput.readLine()) != null) {
				if (line.length() > 0) {
					if (LOG.isDebugEnabled()) {
						LOG.debug(line);
					}
					if (line.indexOf("0 Account created") >= 0 || 
							line.indexOf("0 Account updated") >= 0) {
						isValidUserCredentials = true;
						break;
					}
				}
			}
		} catch (IOException e) {
			LOG.error("Error validating user " + username, e);
		} catch (Exception e) {
			LOG.error(e);
		} finally {
			if (connection != null) connection.disconnect();
		}
		
		if (isValidUserCredentials == false) {
			LOG.error("Error: Elms returned invalid status for user "+ username);
		}
		return isValidUserCredentials;		
	}		
	
	private static final String getGroups (String organisationalStatus) {
		
		final String faculty = "Employee:Faculty";
		final String graduateStudent = "Student";
		final String student = "Student";
		final String staff = "Employee:Staff";
		final String alumni = "Alumni";
		final String retiree = "Retiree";
		final String organizationalAccount = "Organizational account";
		final String affiliate = "Affiliate";
		final String partTimeEmployee = "partTimeEmployee";
		final String other = "Other";
		//String unknown = "unknown";
		
		String groups = "Unknown"; //default
		
		if ( faculty.equalsIgnoreCase(organisationalStatus)) {
			groups = "Faculty";		 	
		} else if ( graduateStudent.equalsIgnoreCase(organisationalStatus)) {
			groups = "Graduate%20Student";		 	
		} else if ( student.equalsIgnoreCase(organisationalStatus)) {
			groups = "Undergraduate%20Student";		 	
		} else if ( staff.equalsIgnoreCase(organisationalStatus)) {
			groups = "Staff%20member";		 			 	
		} else if ( alumni.equalsIgnoreCase(organisationalStatus)) {
			groups = "Associate (Alumni)";		 	
		} else if ( retiree.equalsIgnoreCase(organisationalStatus)) {
			groups = "Associate (Retired)";		 	
		} else if ( organizationalAccount.equalsIgnoreCase(organisationalStatus)) {
			groups = "Organizational%20account";		 	
		} else if ( affiliate.equalsIgnoreCase(organisationalStatus)) {
			groups = "Affiliate";		 	
		} else if ( partTimeEmployee.equalsIgnoreCase(organisationalStatus)) {
			groups = "Part-time%20Employee";		 	
		} else if ( other.equalsIgnoreCase(organisationalStatus)) {
			groups = "Other";
		} 
		return groups;
	}
}