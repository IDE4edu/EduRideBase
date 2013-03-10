package edu.berkeley.eduride.base_plugin;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class EduRideBase implements BundleActivator {

	private static BundleContext context;
	private static IEclipsePreferences prefs;

	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		EduRideBase.context = bundleContext;
		prefs = InstanceScope.INSTANCE.getNode("EduRideBasePlugin");
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		EduRideBase.context = null;
	}
	
	public static String whoami() {
		return prefs.get("username", null);
	}
	
	public static long hashID() {
		return prefs.getLong("authHash", -1);
	}

	public static long workspaceID() {
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
