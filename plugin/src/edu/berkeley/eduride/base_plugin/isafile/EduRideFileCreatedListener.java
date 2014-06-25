package edu.berkeley.eduride.base_plugin.isafile;

import edu.berkeley.eduride.base_plugin.model.EduRideFile;

public interface EduRideFileCreatedListener {

	// could be called multiple times?
	public void bceoSpecified(EduRideFile erf) throws ISAFormatException;
	
	
}
