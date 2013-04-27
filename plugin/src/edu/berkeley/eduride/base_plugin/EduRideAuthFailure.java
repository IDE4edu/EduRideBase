package edu.berkeley.eduride.base_plugin;

public class EduRideAuthFailure extends Exception {

	static final Long serialVersionUID = (long) 23847234;
	
	public EduRideAuthFailure(String msg) {
		super(msg);
	}
}
