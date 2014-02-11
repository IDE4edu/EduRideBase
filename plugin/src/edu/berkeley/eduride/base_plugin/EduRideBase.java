package edu.berkeley.eduride.base_plugin;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.prefs.BackingStoreException;

import edu.berkeley.eduride.base_plugin.ui.LoginDialog;

//import edu.berkeley.eduride.feedbackview.EduRideFeedback;

public class EduRideBase extends AbstractUIPlugin {

	private static BundleContext context;
	private static IEclipsePreferences prefs;

	public static final String PLUGIN_ID = "EduRideBasePlugin";
	// The shared instance
	private static EduRideBase plugin = null;

	public static final String DEFAULT_DOMAIN = "eduride.berkeley.edu";

	// This should function like a proper name, when used in a sentence
	private static final String GUEST_USER_NAME = "Guest";
	private static PreferenceStore prefStore = null;

	// Authentication status
	private static int UNKNOWN = 0;
	private static int LOGGED_IN = 1;
	private static int CHOSEN_GUEST = 2;
	private static int userStatus = UNKNOWN;

	static BundleContext getContext() {
		return context;
	}

	/**
	 * The constructor
	 */
	public EduRideBase() {
		super();
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
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext bundleContext) throws Exception {

		EduRideBase.context = bundleContext;

		// Note: if we need stuff at 'earlyStartup', then it has to go into
		// the EarlyStartup class. Sigh, most of the functionality in this
		// activator should
		// go there.

		if (empty(getWorkspaceID())) {
			setWorkspaceID(generateWorkspaceID());
			flushPrefs();
		}

		if (getRemainGuestStatus()) {
			userStatus = CHOSEN_GUEST;
		}

		startOtherPlugins();

		// start up logger here, why not
		// Bundle b = Platform.getBundle("edu.berkeley.eduride.loggerplugin");
		// System.out.println("eduridebase starting logger");
		// b.start();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		EduRideBase.context = null;
	}

	private static String generateWorkspaceID() {
		return UUID.randomUUID().toString();
	}

	public IPreferenceStore getPreferenceStore() {
		return prefStore;
	}

	// ///////////////////////////////////////////////
	// ////// State (in prefs)

	public static String getWorkspaceID() {
		return (prefs.get("workspaceID", ""));
	}

	private static void setWorkspaceID(String wsID) {
		prefs.put("workspaceID", wsID);
	}

	public static String getUsernameStored() {
		return (prefs.get("username", ""));
	}

	private static void setUsernameStored(String username) {
		prefs.put("username", username);
	}

	private static void clearUsernameStored() {
		prefs.put("username", "");
	}

	private static String getAuthToken() {
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

	private static void setDomain(String domain) {
		prefs.put("domain", domain);
	}

	// maybe there are some nulls around still...
	private static boolean empty(String s) {
		return (s == null || s == "");
	}

	private static boolean flushPrefs() {
		try {
			prefs.flush();
		} catch (BackingStoreException e) {
			// uh oh.
			e.printStackTrace();
			return false;
		}
		return true;
	}

	// ////////////////////////////////////////////////
	// /////// AUTH stuff

	// Will always display a login prompt, no matter what the current
	// authentication ?
	public static void displayLoginPrompt() {
		LoginDialog dialog = new LoginDialog();
		if (dialog.open() == org.eclipse.jface.window.Window.OK) {
			// user should be authenticated or have chosen to be guest if here.
		} else {
			// cancelled... do nothing I guess?
		}
	}

	// this won't force a login
	// Checking this *doesn't* change whether user has chosen to be guest... I
	// guess?
	public static boolean currentlyAuthenticated() {
		boolean valid = false;
		String authToken = getAuthToken();
		if (!empty(authToken)) {
			// TODO -- confirmAuthentication(authToken);
			// check with server to see if it has expired, set valid = true if
			// still ok.
		}
		if (valid) {
			userStatus = LOGGED_IN;
		} else if (userStatus != CHOSEN_GUEST) {
			// not sure why we'd ever be here is user had choose to be guest,
			// though.
			userStatus = UNKNOWN;
		}
		return valid;
	}

	// Successful authentication means user doesn't want to be a guest anymore
	public static void authenticate(String username, String password,
			String domain) throws EduRideAuthFailure {
		String newAuthToken = null;

		// TODO -- should warn if username is guestname somehow? Nah, I don't
		// think so, since guestname can change maybe
		// throw the exception if we fail, with a good message

		// try {
		// URL url = new URL("https", domain, 80, "login"); // TODO put legit
		// target name
		// HttpsURLConnection connection = (HttpsURLConnection)
		// url.openConnection();
		// Object content = connection.getContent();
		// success = isVerified(content);
		// // set newAuthToken here some how
		// } catch (MalformedURLException e) {
		// success = false;
		// } catch (IOException e) {
		// success = false;
		// }

		// should we logOut() if the authentication failed for some reason?

		setUsernameStored(username);
		setAuthToken(newAuthToken);
		setDomain(domain);
		userStatus = LOGGED_IN;
		setRemainGuestStatus(false);
		if (!flushPrefs()) {
			// I guess? you can't authenticate if we can't store your username?
			throw new EduRideAuthFailure(
					"Can't store your user name in the Eclipse preferences store... uh oh");
		}

	}

	public static void logOut() {
		clearUsernameStored();
		clearAuthToken();
		userStatus = UNKNOWN;
		setRemainGuestStatus(false); // hm, I guess?
		flushPrefs();
	}

	// ///////////////////////////
	// USER NAME stuff

	/*
	 * If username "U" has been chosen by user (1) return "U" if they also have
	 * a *valid* authtoken (2) prompt login dialog if invalid authtoken (empty
	 * or expired) goto (1) if successfully authenticated with a username goto
	 * (3) if cancel login dialog or are offline goto (4) if they choose to
	 * remain a guest (3) return "U (?)" -- they choose this name but
	 * didn't/couldn't authenticate (4) Returns EduRideBase.GUEST_USER_NAME if
	 * they decided to stay a guest Since they don't have a user name, prompt
	 * with the login dialog goto (1) if they successfully authenticate goto (4)
	 * if they choose to remain a guest (5) return null if they cancel or can't
	 * authenticate (e.g., offline)
	 */
	public static String getDisplayNameMaybeLogin() {
		String username;
		if (userStatus == CHOSEN_GUEST) {
			return GUEST_USER_NAME;
		}
		username = getUsernameStored();
		if ((!empty(username)) && currentlyAuthenticated()) {
			return username;
		} else {
			displayLoginPrompt();
		}

		if (userStatus == CHOSEN_GUEST) {
			return GUEST_USER_NAME;
		} else if (userStatus == LOGGED_IN) {
			return getUsernameStored();
		} else {
			// user cancelled the login dialog or otherwise failed to
			// authenticate
			return null;
		}
	}

	/*
	 * This differs from above in that it won't ever prompt for login dialog in
	 * response, but it will try to hit the server to authenticate if there is a
	 * token present.
	 * 
	 * If username "U" has been chosen by user (1) return "U" if they also have
	 * a *valid* authtoken (2) return "U (?)" -- they choose this name but
	 * didn't/couldn't authenticate (3) Returns EduRideBase.GUEST_USER_NAME if
	 * they decided to stay a guest (4) return null otherwise
	 */
	public static String getUsernameNoLogin() {

		if (userStatus == CHOSEN_GUEST) {
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
		userStatus = CHOSEN_GUEST;
		setRemainGuestStatus(true);
		clearUsernameStored();
		clearAuthToken();
		flushPrefs();
	}

	private static String nonAuthenticatedDisplayUsername(String username) {
		return username + " (not authenticated)";
	}

	// //////////

	// ////shared images

	/**
	 * setup shared images
	 */

	public static final String CAR_RED_IMAGE = "icons/car-red.gif";

	@Override
	protected void initializeImageRegistry(ImageRegistry registry) {
		super.initializeImageRegistry(registry);

		putImageIntoRegistry(CAR_RED_IMAGE, registry);
	}

	// ////////
	// how to get an image

	public static ImageDescriptor getImageDescriptor(String imgID) {
		// makes use of fact that img id is a relative path
		return imageDescriptorFromPlugin(PLUGIN_ID, imgID);
	}

	public static Image getImage(String imageID) {
		ImageRegistry registry = getDefault().getImageRegistry();
		return (registry.get(imageID));
	}

	// ////

	private void putImageIntoRegistry(String pathStr, ImageRegistry registry) {
		Bundle bundle = Platform.getBundle(PLUGIN_ID);

		ImageDescriptor selection = ImageDescriptor.createFromURL(FileLocator
				.find(bundle, new Path(pathStr), null));
		registry.put(pathStr, selection);
	}

	
	
	// ////////////
	// / Other plugin stuff

	public static final String STARTUP_EXTENSION_POINT_ID = "startup";
	public static final String STARTUP_EXTENSION_POINT_CLASS_ATTRIBUTE = "class";

	private void startOtherPlugins() {
		IExtensionPoint point = Platform.getExtensionRegistry()
				.getExtensionPoint(PLUGIN_ID, STARTUP_EXTENSION_POINT_ID);
		//System.out.println("POINT: " + point.getLabel() + " ... " + point.getSchemaReference());
		IExtension[] extensions = point.getExtensions();

		System.out.println("BASE: starting " + extensions.length + " extensions...");
		for (IExtension extension : extensions) {
			IConfigurationElement[] configElements = extension
					.getConfigurationElements();
			for (IConfigurationElement configElement : configElements) {
				try {
					IStartupSync starter = (IStartupSync) configElement
							.createExecutableExtension(STARTUP_EXTENSION_POINT_CLASS_ATTRIBUTE);
					starter.install();
					System.out.println("Started plugin: " + extension.getLabel());
				} catch (CoreException e) {
					// TODO Auto-generated catch block
					System.err
							.println("Whoa, problems starting up plugin: " + extension.getLabel());
				}
			}
		}

		// EdurideOverlayActivator.getDefault();
	}

	// used in the feedback model to figure out which testclass to use for a
	// source file
	// What is the right thing here? Easy when navigator view is open, but what
	// about when it isn't?
	// - use the active project? Is this specific enough for situations where
	// source class doesn't uniquely identify test class?
	// - editor? No, this doesn't help anything, we already have the source
	// class...
	// - do this a different way and say which test classes can work for any
	// source class,
	// so they can all be shown?
	public static String getCurrentStep() {
		// TODO make this work for situations where a single source file has
		// multiple test classes (for different activities/steps)
		// this step key is passed to FeedbackModelProvider.setup as *.isa files
		// are parsed.
		return null;
	}

}
