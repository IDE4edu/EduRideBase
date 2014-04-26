package edu.berkeley.eduride.base_plugin.model;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.eclipse.core.resources.IFile;

import edu.berkeley.eduride.base_plugin.isafile.ISABceoBoxSpec;
import edu.berkeley.eduride.base_plugin.util.Base64Coder;
import edu.berkeley.eduride.base_plugin.util.Console;

public class EduRideFile {

	
	private IFile isaFile = null;  // where it was contained
	private File file = null;
	private String resetValue = "";
	private ArrayList<ISABceoBoxSpec> boxSpecs = new ArrayList<ISABceoBoxSpec>();
	
	
	private EduRideFile(File file, IFile isaFile, ArrayList<ISABceoBoxSpec> boxSpecs, String base64) {
		super();
		this.file = file;
		String rv = Base64Coder.decodeString(base64);
		this.resetValue = rv;
		this.boxSpecs = boxSpecs;
		this.isaFile = isaFile;
		persistEduRideFile(this);
	}





	public IFile getIsaFile() {
		return isaFile;
	}
//	public void setIsaFile(IFile isaFile) {
//		this.isaFile = isaFile;
//	}





	public File getFile() {
		return file;
	}
//	public void setFile(File file) {
//		if (file.isFile()) {
//			this.file = file;
//		}
//	}



	
	public ArrayList<ISABceoBoxSpec>getBceoSpecs() {
		return boxSpecs;
	}
//	public void addBceoSpec(ISABceoBoxSpec bceoSpec) {
//		boxSpecs.add(bceoSpec);
//	}
	public boolean hasBceoSpec() {
		return (boxSpecs != null);
	}




//	public void setResetValueBase64(String base64) {
//		String v = Base64Coder.decodeString(base64);
//		this.resetValue = v;
//	}
	
	
	
	public String getResetValue() {
		return resetValue;
	}
	
	
	public boolean resetFile() {
		// not gonna ask, just gonna do it
		FileWriter fw;
		try {
			fw = new FileWriter(file,false);
			fw.write(resetValue);
			fw.close();
			return true;
		} catch (IOException e) {
			Console.err("Couldn't reset file " + file.getPath(), e);
			return false;
		}	
	}
	
	
	
	//////////// persistance
	
	private static HashMap<File, EduRideFile> edurideFiles = new HashMap<File, EduRideFile>();
	
	/**
	 * Either returns the existing EduRideFile for this File etc.
	 * or makes a new one
	 * @param file
	 */
	public static EduRideFile get(File file, IFile isaFile, ArrayList<ISABceoBoxSpec> boxSpecs, String base64) {
		EduRideFile erf = edurideFiles.get(file);
		if (erf == null) {
			// doesn't exist yet!
			erf = new EduRideFile(file, isaFile, boxSpecs, base64);
		}
		return erf;
	}
	
	
	
	public static Collection<EduRideFile> getAll() {
		return edurideFiles.values();
	}
	
	// called in the EduRideFile constructor
	private static void persistEduRideFile(EduRideFile erf) {
		if (erf.getFile() != null) {
			edurideFiles.put(erf.getFile(), erf);
		}
	}
	
	
	///////// listener
	
	
	
	
	
}
