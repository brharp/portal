/**
 * $RCSfile: JiveAuthFactory.java,v $
 * $Revision: 1.2 $
 * $Date: 2006/01/03 19:55:49 $
 *
 * Copyright (C) 1999-2005 Jive Software. All rights reserved.
 *
 * This software is the proprietary information of Jive Software.
 * Use is subject to license terms.
 */

package ca.uoguelph.ccs.portal.portlets.jiveportlet;


import javax.naming.directory.*;

import com.jivesoftware.base.ldap.*;
import com.jivesoftware.base.database.ConnectionManager;

import com.jivesoftware.util.Blowfish;

import com.jivesoftware.util.*;
import com.jivesoftware.base.*;
import com.jivesoftware.base.event.UserEvent;
import com.jivesoftware.base.event.UserEventDispatcher;
import com.jivesoftware.base.database.*;
import com.jivesoftware.base.database.sequence.SequenceManager;
import javax.servlet.http.*;

import java.util.*;
import java.sql.*;

/**
 * A AuthFactory implementation that uses LDAP to verify the username
 * and password. If it's the first time that an LDAP user has logged into Jive,
 * then a database record is automatically created for them.<p>
 *
 * To use this AuthFactory, you must set the Jive property
 * <tt>AuthFactory.className</tt> to
 * "com.jivesoftware.forum.ldap.LdapAuthFactory".
 *
 * @edition enterprise
 * @author Matt Tucker
 * @edition enterprise
 */
public class JiveAuthFactory extends AuthFactory {
	
	private static final String FIND_USER_ID = "SELECT userID FROM jiveUser WHERE username=?";
	
	private LdapManager manager;
	
	private Cache authCache = null;
	
	public JiveAuthFactory() {
		if (Log.isDebugEnabled()) {
			Log.debug("Creating a new JiveAuthFactory.");
		}
		this.manager = LdapManager.getInstance();
		if (JiveGlobals.getJiveBooleanProperty("ldap.authCache.enabled")) {
			int maxSize = JiveGlobals.getJiveIntProperty("ldap.authCache.size", 512*1024);
			long maxLifetime = (long)JiveGlobals.getJiveIntProperty("ldap.authCache.maxLifetime",
					(int)JiveConstants.HOUR * 2);
			authCache = new DefaultCache("LDAP Auth Cache", maxSize, maxLifetime);
		}
	}
	
	// AuthFactory Interface
	
	public AuthToken createAuthToken(String username, String password)
	throws UnauthorizedException
	{
		long time = -1L;
		boolean debug = Log.isDebugEnabled();
		if (debug) {
			Log.debug("Creating an AuthToken in JiveAuthFactory.createAuthToken(username,password) - " +
					"username is " +  username + "...");
			time = System.currentTimeMillis();
		}
		
		// If cache is enabled, see if the auth is in cache.
		if (authCache != null && authCache.containsKey(username)) {
			String [] authInfo = (String[]) authCache.get(username);
			String hash = authInfo[0];
			if (StringUtils.hash(password).equals(hash)) {
				return new JiveAuthToken(Long.parseLong(authInfo[1]));
			}
		}
		
		long userID = -1;
		String userDN = null;
		try {
			// The username by itself won't help us much with LDAP since we
			// need a fully qualified dn. We could make the assumption that
			// the baseDN would always be the location of user profiles. For
			// example if the baseDN was set to "ou=People, o=jivesoftare, o=com"
			// then we would be able to directly load users from that node
			// of the LDAP tree. However, it's a poor assumption that only a
			// flat structure will be used. Therefore, we search all subtrees
			// of the baseDN for the username. So, if the baseDN is set to
			// "o=jivesoftware, o=com" then a search will include the "People"
			// node as well all the others under the base.
			userDN = manager.findUserDN(username);
			if (debug) {
				Log.debug("Found userDN: " + userDN + " for username: " + username + ".");
			}
			
			// See if the user authenticates.
			boolean authenticates = manager.checkAuthentication(userDN, password);
			if (!authenticates) {
				if (debug) {
					Log.debug("Authentication based on userDN: " + userDN + " failed, throwing UnauthorizedException");
				}
				throw new UnauthorizedException("Username and password don't match");
			}
			else {
				if (debug) {
					Log.debug("Authentication based on userDN: " + userDN + " succeeded.");
				}
			}
		}
		catch (Exception e) {
			throw new UnauthorizedException(e);
		}
		
		// Finally, we need to lookup a userID for the user. In the ALL_LDAP_MODE, the userID
		// will come from LDAP. In the LDAP_DB_MODE, this info comes from the database.
		if (manager.getMode() == LdapManager.ALL_LDAP_MODE) {
			
			if (debug) {
				Log.debug("In all LDAP mode so looking for user in LDAP.");
			}
			
			DirContext ctx = null;
			try {
				ctx = manager.getContext();
				if (debug) {
					Log.debug("Context retrieved...");
				}
				
				String [] attributes = new String [] { "jiveUserID" };
				Attributes attrs = ctx.getAttributes(userDN, attributes);
				// If the userID isn't found in the LDAP entry, that means the extra Jive data
				// hasn't been added for the user yet. Add it by creating a new LDAP user object,
				// then get the ID.
				if (attrs.get("jiveUserID") == null) {
					//userID = (new LdapUser(username)).getID();
					if (debug) {
						Log.debug("LDAPUser*********Found LDAP user based on username '" + username + "', userID is: " + userID);
						Log.debug("Found LDAP user based on username '" + username + "', userID is: " + userID);
					}
				}
				else {
					userID = Long.parseLong((String)attrs.get("jiveUserID").get());
					if (debug) {
						Log.debug("Found user ID from attributes, userID is: " + userID);
					}
				}
			}
			catch (Exception e) {
				if (debug) {
					Log.debug("Exception while getting userID.", e);
				}
				throw new UnauthorizedException(e);
			}
			finally {
				try { ctx.close(); }
				catch (Exception e) { }
			}
		}
		// Otherwise, we should get the ID from the database.
		else {
			if (debug) {
				Log.debug("In mixed LDAP/Jive mode so looking for user in the Jive DB.");
			}
			
			Connection con = null;
			PreparedStatement pstmt = null;
			try {
				con = ConnectionManager.getConnection();
				pstmt = con.prepareStatement(FIND_USER_ID);
				// Use lower case always (LDAP is case-insensitive)
				String lowerUsername;
				if (username == null) {
					lowerUsername = "";
				}
				else {
					lowerUsername = username.toLowerCase();
				}
				pstmt.setString(1, lowerUsername);
				ResultSet rs = pstmt.executeQuery();
				// If we're able, get the userID from the database.
				if (rs.next()) {
					userID = rs.getLong(1);
					rs.close();
					pstmt.close();
					if (debug) {
						Log.debug("Found userID " + userID + " based on query: " + FIND_USER_ID
								+ ", using username: " + username);
					}
				}
				else {
					rs.close();
					pstmt.close();
					// We failed to find the userID in the database. That means the extra Jive data
					// hasn't been added for the user yet. Add it by creating a new LDAP user object,
					// then get the ID.
					//userID = (new LdapUser(username)).getID();
					if (debug) {
						Log.debug("Created new LdapUser*******");
						Log.debug("Created new LdapUser");
					}
				}
			}
			catch (Exception e) {
				if (debug) {
					Log.debug("Exception while retrieving user info from the Jive DB.");
				}
				throw new UnauthorizedException(e);
			}
			finally {
				ConnectionManager.closeConnection(con);
			}
		}
		if (debug) {
			time = System.currentTimeMillis() - time;
			Log.debug("Creating auth token for username " + username + " took " + time + " ms.");
		}
		// If cache is enabled, add the item to cache.
		if (authCache != null) {
			authCache.put(username, new String[] { StringUtils.hash(password),
					String.valueOf(userID)});
		}
		return new JiveAuthToken(userID);
	}
	
	public AuthToken createAnonymousAuthToken() {
		return new JiveAuthToken(-1);
	}
	
	/**
	 * Overridden from default to check for SSO cookie.
	 *
	 * @param request a HttpServletRequest object.
	 * @param request a HttpServletResponse object.
	 * @throws UnauthorizedException if no authToken information is found.
	 */
	protected AuthToken createAuthToken(HttpServletRequest request, HttpServletResponse response)
	throws UnauthorizedException
	{
		boolean debug = Log.isDebugEnabled();
		HttpSession session = request.getSession();
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		
		// Check 1: look for the Jive authentication token in the user's session.
		AuthToken authToken = (AuthToken)session.getAttribute(SESSION_AUTHORIZATION);
		if (debug) {
			Log.debug("check1 - " +
					"username is " +  username + "..." + "authToken is " + authToken +
					"session Id =" + session.getId());
		}
		
		if (authToken != null) {
			return authToken;
		}
		// Check 2: see if a cookie storing the username and password is there.
		Cookie cookie = CookieUtils.getCookie(request, COOKIE_AUTOLOGIN);
		if (debug) {
			Log.debug("check2 - " +
					"username is " +  username + "..." + "authToken is " + authToken
					+ "cookie = " + cookie + "session Id =" + session.getId());
		}		
		if (cookie != null) {
			try {
				// We found a cookie, so get the username and password from it,
				// create an AuthToken token, then store it in the session.
				String [] authInfo = decryptAuthInfo(cookie.getValue());
				if (authInfo != null) {
					username = authInfo[0];
					password = authInfo[1];
					// Try to validate the user based on the info from the cookie.
					authToken = getAuthToken(username, password);
					session.setAttribute(SESSION_AUTHORIZATION, authToken);
					return authToken;
				}
				else {
					// We must have found an old cookie format, so delete it.
					CookieUtils.deleteCookie(request, response, cookie);
				}
			}
			catch (UnauthorizedException ue) {
				// Remove the authToken cookie as the exception indicates
				// the username and/or password are no longer valid
				CookieUtils.deleteCookie(request, response, cookie);
				throw ue;
			}
		}
		//Check 3: create autologin
		boolean autoLogin = true;
		authToken = loginUser(username, password, autoLogin, request, response);
		return authToken;
	}
}