package edu.berkeley.eduride.base_plugin;

import org.eclipse.ui.IStartup;

public class EarlyStartup implements IStartup {

	
	// separating out the early startup stuff for EduRideBase
	// per http://help.eclipse.org/helios/index.jsp?topic=/org.eclipse.platform.doc.isv/reference/extension-points/org_eclipse_ui_startup.html
	
	@Override
	public void earlyStartup() {
		// TODO Auto-generated method stub
		
		// if no workspace id exists, this makes sure to generate it
		// arg, to use this we have to move all the prefs, etc., here from EduRideBase, 
		//  so it can use them at earlyStartup...  

	}

}
