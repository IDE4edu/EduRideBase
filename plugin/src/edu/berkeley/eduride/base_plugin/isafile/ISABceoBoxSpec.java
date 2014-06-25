package edu.berkeley.eduride.base_plugin.isafile;

public class ISABceoBoxSpec {
	
	public enum BceoBoxType {
		UNKNOWN, INLINE, MULTILINE
	}
	public BceoBoxType type;
	public String name;
	public int start;
	public int stop;
	
	public ISABceoBoxSpec(BceoBoxType type, String name, int start, int stop) {
		super();
		this.type = type;
		this.name = name;
		this.start = start;
		this.stop = stop;
	}
	

	
	
	
	
}
