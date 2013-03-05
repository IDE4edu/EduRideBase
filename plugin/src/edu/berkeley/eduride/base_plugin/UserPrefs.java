package edu.berkeley.eduride.base_plugin;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;

public class UserPrefs {
	private IEclipsePreferences prefs;
	public final UserPrefs INSTANCE = new UserPrefs();
	
	private UserPrefs() {
		prefs = InstanceScope.INSTANCE.getNode("EduRideBasePlugin");
	}
	
	public boolean authenticate(String username, String password) {
		if (something(username, password)) {
			int authHash = (username + password).hashCode();
			prefs.put("username", username);
			prefs.putInt("authHash", authHash);
			return true;
		}
		return false;
	}
	
	private boolean something(String username, String password) {
		// TODO actually authenticate
		return true;
	}
	
	/**
	 * 
	 * @return String containing username
	 */
	public String whoami() {
		return prefs.get("username", null);
	}	
}
