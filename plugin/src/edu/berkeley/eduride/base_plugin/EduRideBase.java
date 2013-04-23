package edu.berkeley.eduride.base_plugin;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.prefs.BackingStoreException;

import edu.berkeley.eduride.base_plugin.ui.LoginDialog;

//import edu.berkeley.eduride.feedbackview.EduRideFeedback;

public class EduRideBase extends AbstractUIPlugin implements IStartup {

	private static BundleContext context;
	private static IEclipsePreferences prefs;
	
	public static final String PLUGIN_ID = "EduRideBase";
	// The shared instance
	private static EduRideBase plugin = null;
	
	public static final String DEFAULT_DOMAIN = "eduride.berkeley.edu";
	// This should function like a proper name, when used in a sentence
	private static final String GUEST_USER_NAME = "Guest";
	private static UUID workspaceID = null;	
	private static PreferenceStore prefStore = null;
	
	// Authentication status
	private static int UNKNOWN = 0;
	private static int LOGGED_IN = 1;
	private static int CHOOSEN_GUEST = 2;
	private static int userStatus = UNKNOWN;

	
	static BundleContext getContext() {
		return context;
	}
	
	/**
	 * The constructor
	 */
	public EduRideBase() {
		prefs = InstanceScope.INSTANCE.getNode(PLUGIN_ID);
		prefStore = new PreferenceStore(prefs.absolutePath());
		plugin = this;
	}
	
	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static EduRideBase getDefault() {
		return plugin;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {		
		EduRideBase.context = bundleContext;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		EduRideBase.context = null;
	}
	
	
	
	
	
	
	@Override
	public void earlyStartup() {
		// if no workspace id exists, this makes sure to generate it
		String workspaceIDString = prefs.get("workspaceID", generateWsID());
		if (workspaceID == null) {
			workspaceID = UUID.fromString(workspaceIDString);
			prefs.put("workspaceID", workspaceIDString);
			try {
				prefs.flush();
			} catch (BackingStoreException e) {
				e.printStackTrace();
			}
		}
		if (prefs.getBoolean("choosenGuest", false)) {
			userStatus = CHOOSEN_GUEST;
		}
	}

	private static String generateWsID() {
		workspaceID = UUID.randomUUID();
		return workspaceID.toString();
	}

	public static String getWorkspaceID() {
		return workspaceID.toString();
	}

	
	public IPreferenceStore getPreferenceStore() {
		return prefStore;
	}
	
	
	
	/////////////////////////////////////////////////
	////////  State (in prefs)
	
	public static String getUsernameStored() {
		return (prefs.get("username", ""));
	}

	private static void setUsernameStored(String username) {
		prefs.put("username", username);
	}
	
	private static void clearUsernameStored() {
		prefs.put("username", "");
	}
	
	private  static String getAuthToken() {
		return prefs.get("authToken", null);
	}

	private static void setAuthToken(String token) {
		prefs.put("authToken", token);
	}

	private static void clearAuthToken() {
		prefs.put("authToken", "");
	}

	public static boolean getRemainGuestStatus() {
		return prefs.getBoolean("remainGuest", false);
	}
	
	private static void setRemainGuestStatus(boolean choosenGuest) {
		prefs.putBoolean("remainGuest", choosenGuest);
	}
	
	public static String getDomain() {
		return prefs.get("domain", DEFAULT_DOMAIN);
	}
	
	public static void setDomain(String domain) {
		prefs.put("domain", domain);
	}

	// maybe there are some nulls around still...
	private static boolean empty(String s) {
		return (s == null || s == "");
	}

	
	//////////////////////////////////////////////////
	///////// AUTH stuff

	
	// Will always display a login prompt, no matter what the current authentication ?
	public static void displayLoginPrompt() { 
		// TODO -- needs fixin?
		// return true if OK is pressed
		LoginDialog dialog = new LoginDialog();
		if (dialog.open() == org.eclipse.jface.window.Window.OK) {
			// authenticate should have set the username, authtoken, etc...
			if (dialog.chosenGuest()) {
				chooseGuestStatus();
			}
		} else {
			// cancelled...  do nothing I guess?
		}
	}


	
	// this won't force a login
	// Checking this *doesn't* change whether user has choosen to be guest... I guess?
	public static boolean currentlyAuthenticated() {
		boolean valid = false;
		String authToken = getAuthToken();
		if (!empty(authToken)) {
			// TODO -- check with server to see if it has expired, set valid = true if still ok.
		}
		if (valid) {
			userStatus = LOGGED_IN;
		} else if (userStatus != CHOOSEN_GUEST) {
			// not sure why we'd ever be here is user had choose to be guest, though.
			userStatus = UNKNOWN;
		}
		return valid;
	}

	
	// Successful authentication means user doesn't want to be a guest anymore
	public static boolean authenticate(String username, String password,
			String domain) {
		boolean success = false;
		String newAuthToken = null;

		// TODO -- should warn if username is guestname somehow?  Nah, I don't think so, since guestname can change maybe
		
//		try {
//			URL url = new URL("https", domain, 80, "login"); // TODO put legit target name
//			HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
//			Object content = connection.getContent();
//			success = isVerified(content);
//		    // set newAuthToken here some how
//		} catch (MalformedURLException e) {
//			success = false;
//		} catch (IOException e) {
//			success = false;
//		}
		if (success) {
			setUsernameStored(username);
			setAuthToken(newAuthToken);
			setDomain(domain);
			userStatus = LOGGED_IN;
			setRemainGuestStatus(false);
		} else {
			// should we do a logOut() here?   I guess not?
		}
		try {
			prefs.flush();
		} catch (BackingStoreException e) {
			e.printStackTrace();
			success = false;
		}
		return success;
	}


	public static void logOut() {
		clearUsernameStored();
		clearAuthToken();
		userStatus = UNKNOWN;
		setRemainGuestStatus(false);   // hm, I guess?

	}
	



	
	/////////////////////////////
	// USER NAME stuff
	
	
	/*
	 * If username "U" has been chosen by user 
	 *     (1) return "U" if they also have a *valid* authtoken
	 *     (2) prompt login dialog if invalid authtoken (empty or expired)
	 *         goto (1) if successfully authenticated with a username
	 *         goto (3) if cancel login dialog or are offline
	 *         goto (4) if they choose to remain a guest
	 *     (3) return "U (?)" -- they choose this name but didn't/couldn't authenticate  
	 * (4) Returns EduRideBase.GUEST_USER_NAME if they decided to stay a guest
	 * Since they don't have a user name, prompt with the login dialog
	 *     goto (1) if they successfully authenticate
	 *     goto (4) if they choose to remain a guest
	 *     (5) return null if they cancel or can't authenticate (e.g., offline) 
	 * 
	 */
	public static String getDisplayNameMaybeLogin() {
		String username;
		if (userStatus == CHOOSEN_GUEST) {
			return GUEST_USER_NAME;
		}
		username = getUsernameStored();
		if ((!empty(username)) && currentlyAuthenticated()) {
			return username;
		} else {
			displayLoginPrompt();			
		}
		
		if (userStatus == CHOOSEN_GUEST) {
			return GUEST_USER_NAME;
		} else if (userStatus == LOGGED_IN) {
			return getUsernameStored();
		} else {
			// user cancelled the login dialog or otherwise failed to authenticate
			return null;
		}
	}

	
	/*
	 * This differs from above in that it won't ever prompt for login dialog in response,
	 *  but it will try to hit the server to authenticate if there is a token present.  
	 *  
	 * If username "U" has been chosen by user 
	 *     (1) return "U" if they also have a *valid* authtoken
	 *     (2) return "U (?)" -- they choose this name but didn't/couldn't authenticate  
	 * (3) Returns EduRideBase.GUEST_USER_NAME if they decided to stay a guest
	 * (4) return null otherwise
	 */
	public static String getUsernameNoLogin() {

		if (userStatus == CHOOSEN_GUEST) {
			return GUEST_USER_NAME;
		}
		String username = getUsernameStored();
		Boolean valid = currentlyAuthenticated();
		if ((!empty(username)) && valid) {
			return username;
		} else if (empty(username)) {
			return null;
		} else {
			return nonAuthenticatedDisplayUsername(username);
		}
	}
	
	
	public static void chooseGuestStatus() {
		userStatus = CHOOSEN_GUEST;
		setRemainGuestStatus(true);
		clearUsernameStored();
		clearAuthToken();
	}
	
	
	private static String nonAuthenticatedDisplayUsername(String username) {
		return username + " (not authenticated)";
	}
	
	

}
