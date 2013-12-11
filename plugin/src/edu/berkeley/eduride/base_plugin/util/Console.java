package edu.berkeley.eduride.base_plugin.util;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.ui.console.MessageConsole;

import edu.berkeley.eduride.base_plugin.EduRideBase;



public class Console {

	
	
	
	
	
	
	public Console() {
		ImageDescriptor imgDesc = EduRideBase.getImageDescriptor(EduRideBase.CAR_RED_IMAGE);
		fMessageConsole = new MessageConsole("EduRide Console", imgDesc);
	}
	
	MessageConsole fMessageConsole = null;
	
	
	

	
	

	
}
