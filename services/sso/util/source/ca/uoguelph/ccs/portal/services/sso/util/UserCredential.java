package ca.uoguelph.ccs.portal.services.sso.util;

import javax.servlet.http.*;
import javax.servlet.*;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.jasig.portal.security.IPerson;
import org.jasig.portal.security.PersonManagerFactory;

import org.jasig.portal.security.ISecurityContext;
import org.jasig.portal.security.provider.NotSoOpaqueCredentials;
import org.jasig.portal.security.IOpaqueCredentials;
import org.jasig.portal.security.IPrincipal;

/*
 * @author lalit
 * Util class to get username and password
 */
public class UserCredential {
	private static final Log LOG = LogFactory.getLog(UserCredential.class);
	
	public static IPerson getPerson(HttpServletRequest request) {
        IPerson person = null;
		try {
			// Get person details
			person = PersonManagerFactory.getPersonManagerInstance().getPerson(request);
		} catch (Exception e) {
			LOG.debug("UserCredential Person" + e.getMessage());
		}        
		return person;
	}
	
	public static IPrincipal getPrincipal(IPerson person) {
        IPrincipal principal =null;
		try {
			ISecurityContext personSecurityContext = person.getSecurityContext();
			principal = personSecurityContext.getPrincipal();
		} catch (Exception e) {
			LOG.debug("UserCredential Principal" + e.getMessage());
		}        
		return principal;
	}
	
	public static String getUsername(IPrincipal principal) {
		return principal.getUID();
	}
	
	/**
	 * Retrieves the users cached credentials
	 * @param p the IPerson object of the person of whose credentials is being looked for
	 * @return the users cached password
	 */
	public static String getPassword (IPerson p) throws Exception {
		String sPassword = null;
		try {
			ISecurityContext ic = (ISecurityContext) p.getSecurityContext();
			IOpaqueCredentials oc = ic.getOpaqueCredentials();
			if (oc instanceof NotSoOpaqueCredentials) {
				NotSoOpaqueCredentials nsoc = (NotSoOpaqueCredentials)oc;
				sPassword = nsoc.getCredentials();
			}
			
			// If still no password, loop through subcontexts to find cached credentials
			if (sPassword == null) {
				java.util.Enumeration en = ic.getSubContexts();
				while (en.hasMoreElements()) {
					ISecurityContext sctx = (ISecurityContext)en.nextElement();
					IOpaqueCredentials soc = sctx.getOpaqueCredentials();
					if (soc instanceof NotSoOpaqueCredentials) {
						NotSoOpaqueCredentials nsoc = (NotSoOpaqueCredentials)soc;
						sPassword = nsoc.getCredentials();
					}
				}
			}
		} catch (Exception e) {
			throw e;
		}
		return sPassword;
	}
}