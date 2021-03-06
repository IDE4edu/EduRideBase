package edu.berkeley.eduride.base_plugin;

import java.util.UUID;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.prefs.BackingStoreException;

import edu.berkeley.eduride.base_plugin.isafile.ISAVisitor;
import edu.berkeley.eduride.base_plugin.model.Step;
import edu.berkeley.eduride.base_plugin.ui.LoginDialog;
import edu.berkeley.eduride.base_plugin.util.Console;


//import edu.berkeley.eduride.feedbackview.EduRideFeedback;

public class EduRideBase extends AbstractUIPlugin {

	private static final String VERSION_MSG = "1.0.3.1";

	private static BundleContext context;
	private static IEclipsePreferences prefs;

	public static final String PLUGIN_ID = "EduRideBasePlugin";
	// The shared instance
	private static EduRideBase plugin = null;

	public static final String DEFAULT_DOMAIN = "eduride.berkeley.edu";
	// -1 will let java figure it out for the protocol
	public static final int DEFAULT_DOMAIN_PORT = -1;

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

		Console.msg("EDURIDE BASE: version " + VERSION_MSG);

		// get things moving a bit?
		ResourcesPlugin.getWorkspace();

		if (empty(getWorkspaceID())) {
			setWorkspaceID(generateWorkspaceID());
			flushPrefs();
		}

		if (getRemainGuestStatus()) {
			userStatus = CHOSEN_GUEST;
		}

		Console.msg("WorkspaceID: " + getWorkspaceID() + " ; Guest: " + getRemainGuestStatus());

		Step prevent1 = new Step(null, null, null, null, null, null, null, null);
		startOtherPlugins();

		// process workspace, looking for ISA files.
		Job processIsa = new Job("Uploading logs") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {

				Boolean success = ISAVisitor.processAllISAInWorkspace();

				return Status.OK_STATUS;
			}

		};
		processIsa.setSystem(true);
		processIsa.setUser(false);
		processIsa.schedule();

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

	public static int getDomainPort() {
		return prefs.getInt("domainPort", DEFAULT_DOMAIN_PORT);
	}

	public static void resetDomainPort() {
		prefs.putInt("domainPort", DEFAULT_DOMAIN_PORT);
	}

	private static void setDomainPort(int newPort) {
		prefs.putInt("domainPort", newPort);
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
			Console.err(e);
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
	// TODO obviously not finished, yo. But, lets you set the domain for
	// testing...
	public static void authenticate(String username, String password,
			String domain) throws EduRideAuthFailure {
		String newAuthToken = null;

		processDomainAndPort(domain);
		setUsernameStored(username);

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

		// setAuthToken(newAuthToken); //null now, which breaks
		// userStatus = LOGGED_IN;
		// setRemainGuestStatus(false);

		if (!flushPrefs()) {
			// I guess? you can't authenticate if we can't store your username?
			throw new EduRideAuthFailure(
					"Can't store your user name in the Eclipse preferences store... uh oh");
		}

	}

	public static String getDomainAndMaybePort() {
		String domain = getDomain();
		int port = getDomainPort();
		if (port != DEFAULT_DOMAIN_PORT) {
			domain += ":" + port;
		}
		return domain;
	}

	public static void processDomainAndPort(String domain) {
		// strip protocol (maybe do https someday - need to store protocol,
		// etc...)
		if (domain.startsWith("http://")) {
			domain = domain.substring(7);
		} else if (domain.startsWith("https://")) {
			domain = domain.substring(8);
		}
		// get the port.
		if (domain.contains(":")) {
			int indexOfColon = domain.indexOf(":");
			try {
				String portStr = domain.substring(indexOfColon + 1);
				int newPort = Integer.parseInt(portStr);
				setDomainPort(newPort);
			} catch (Exception e) {
				resetDomainPort();
			}
			domain = domain.substring(0, indexOfColon);
		} else {
			resetDomainPort();
		}
		setDomain(domain);

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

	public static void chooseGuestStatus(String domain) {
		userStatus = CHOSEN_GUEST;
		setRemainGuestStatus(true);
		clearUsernameStored();
		clearAuthToken();
		processDomainAndPort(domain);
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
		// Console.msg("POINT: " + point.getLabel() + " ... " +
		// point.getSchemaReference());
		IExtension[] extensions = point.getExtensions();

		Console.msg("Base: starting " + extensions.length + " extensions...");
		for (IExtension extension : extensions) {
			IConfigurationElement[] configElements = extension
					.getConfigurationElements();
			for (IConfigurationElement configElement : configElements) {
				try {
					// If the following line throws a crazy thread exception,
					// your plugin is
					// probably trying to do UI stuff and can't, at least not in
					// the thread
					// where this is called. See the editor-overlay stuff for
					// running
					// in the UI thread
					IStartupSync starter = (IStartupSync) configElement
							.createExecutableExtension(STARTUP_EXTENSION_POINT_CLASS_ATTRIBUTE);
					starter.install();
					Console.msg("  Started plugin: " + extension.getLabel());
				} catch (CoreException e) {
					Console.err("  Whoa, problems starting up plugin: "
							+ extension.getLabel());
				}
			}
		}

		// EdurideOverlayActivator.getDefault();
	}

}
