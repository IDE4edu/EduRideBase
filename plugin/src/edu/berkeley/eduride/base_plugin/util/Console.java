package edu.berkeley.eduride.base_plugin.util;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.ui.console.MessageConsole;

import edu.berkeley.eduride.base_plugin.EduRideBase;



public class Console {

	public static void msg (String msg) {
		System.out.println(msg);
	}
	
	public static void msg (Exception e) {
		msg(e.toString());
	}
	
	public static void err (String msg) {
		System.err.println(msg);
	}
	
	public static void err (Exception e) {
		err(e.toString());
	}
	
	
	// TODO this should throw an exception and put an entry into the problem view, yo
	public static void isaErr (String msg) {
		err(msg);
	}
	
	/////////////
	
	public Console() {
		ImageDescriptor imgDesc = EduRideBase.getImageDescriptor(EduRideBase.CAR_RED_IMAGE);
		fMessageConsole = new MessageConsole("EduRide Console", imgDesc);
	}
	
	
	
	
	MessageConsole fMessageConsole = null;
	
	
	

	
	

	
}
