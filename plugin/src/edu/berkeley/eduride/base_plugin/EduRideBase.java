package edu.berkeley.eduride.base_plugin;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import edu.berkeley.eduride.feedbackview.EduRideFeedback;

public class EduRideBase implements BundleActivator {

	private static BundleContext context;
	private static IEclipsePreferences prefs;
	
	public static final String PLUGIN_ID = "EduRideBase";
	// The shared instance
	private static EduRideBase plugin = null;
	
	public static final String guestUserName = "Guest User (not logged in)";
	
	// TODO: make it so that EduRideBase gets loaded with eclipse, not delayed.
	
	
	static BundleContext getContext() {
		return context;
	}
	
	
	/**
	 * The constructor
	 */
	public EduRideBase() {
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
		prefs = InstanceScope.INSTANCE.getNode(PLUGIN_ID);
		plugin=this;
		
		// TODO: ensure that workspaceID is set for this workspace
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		EduRideBase.context = null;
	}
	
	
	
	public static String whoami() {
		String username = prefs.get("username", null);
		// perhaps push login dialog here (but not if they are looking at preferences)
		// TODO need to distinguish between 'havent authenticated yet' and 'want to remain a guest'
		if (username == null) {
			username = guestUserName;
		}
		return username;
	}
	
	public static long hashID() {
		return prefs.getLong("authHash", -1);
	}

	public static long workspaceID() {
		// any way to make this private?  So other plugins cant change it?
		// check out http://www.vogella.com/blog/2010/03/11/emf-unique-ids/
		return prefs.getLong("workspaceID", generateWsID());
	}

	private static long generateWsID() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	private int generateAuthHash() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	private boolean authenticate(String username, String password) {
		if (true /*something(username, password) */) {
			int authHash = generateAuthHash();
			prefs.put("username", username);
			prefs.putInt("authHash", authHash);
			return true;
		}
		return false;
	}

}
