package edu.berkeley.eduride.base_plugin.isafile;

import edu.berkeley.eduride.base_plugin.util.EduRideException;

public class ISAFormatException extends EduRideException {

	private int lineNumber;
	
	public ISAFormatException(String msg) {
		super(msg);
		lineNumber = 1;  // by default
		
	}
	
	public int lineNumber() {
		return this.lineNumber;
	}
	
	public void setLineNumber(int line) {
		this.lineNumber = line;
	}
	
	

}
