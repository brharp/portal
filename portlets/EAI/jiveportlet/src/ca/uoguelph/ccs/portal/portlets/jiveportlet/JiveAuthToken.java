package ca.uoguelph.ccs.portal.portlets.jiveportlet;
import com.jivesoftware.base.AuthToken;
import java.io.Serializable;

/**
 * Implementation of the AuthToken interface.
 */
public final class JiveAuthToken implements AuthToken, Serializable {
	
	private long userID;
	
	/**
	 * Constucts a new auth token with the specified userID.
	 *
	 * @param userID the userID to create an authToken token with.
	 */
	protected JiveAuthToken(long userID) {
		this.userID = userID;
	}
	
	// AuthToken Interface
	
	public long getUserID() {
		return userID;
	}
	
	public boolean isAnonymous() {
		return userID == -1;
	}
}