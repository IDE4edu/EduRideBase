package edu.berkeley.eduride.base_plugin.model;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;

import edu.berkeley.eduride.base_plugin.isafile.ISABceoBoxSpec;
import edu.berkeley.eduride.base_plugin.util.Base64Coder;
import edu.berkeley.eduride.base_plugin.util.Console;

public class EduRideFile {

	/*
	 * holds eduride metadata about files (generally java files). 
	 *    - Boxes for constrainted java source files
	 *    - contents for resetting
	 * Stored as xml in an .isa file; this gets instantiated when .isa file is parsed.
	 */
	
	
	private IFile isaFile = null;  // where it was defined
	private IFile file = null; //target file
	private String resetValue = "";
	private ArrayList<ISABceoBoxSpec> boxSpecs = new ArrayList<ISABceoBoxSpec>();
	
	
	private EduRideFile(IFile file, IFile isaFile, ArrayList<ISABceoBoxSpec> boxSpecs, String base64) {
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





	public IFile getFile() {
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
// WHEN WE HAD A FILE		
//		// not gonna ask, just gonna do it
//		FileWriter fw;
//		try {
//			fw = new FileWriter(file,false);
//			fw.write(resetValue);
//			fw.close();
//			return true;
//		} catch (IOException e) {
//			Console.err("Couldn't reset file " + file.getPath(), e);
//			return false;
//		}	
		
		boolean force = true;
		boolean keepHistory = true;
		InputStream is = new ByteArrayInputStream(resetValue.getBytes(StandardCharsets.UTF_8));
		try {
			// just do it
			file.setContents(is, force, keepHistory, null);
			return true;
		} catch (CoreException e) {
			//aww
			Console.err("Unable to reset file " + file.getFullPath().toString() + ": IFile.setContents threw up a CoreException.");
			return false;
		}
		
	}
	
	
	
	//////////// persistance
	
	private static HashMap<IFile, EduRideFile> edurideFiles = new HashMap<IFile, EduRideFile>();
	
	
	public static boolean persist(EduRideFile erf) {
		IFile file = erf.getFile();
		if (file == null) {
			return false;
		} else if (edurideFiles.containsKey(file)) {
			// are we sad?  ignore this?
			return false;
		} else {
			edurideFiles.put(file, erf);
			return true;
		}
	}
	
	
	/**
	 * Makes a new ERF file, or returns existing one for file target
	 */
	public static EduRideFile get(IFile file, IFile isaFile, ArrayList<ISABceoBoxSpec> boxSpecs, String base64) {
		EduRideFile erf = edurideFiles.get(file);
		if (erf == null) {
			// doesn't exist yet!
			erf = new EduRideFile(file, isaFile, boxSpecs, base64);
		}
		return erf;
	}
	
	/*
	 * convenience method -- returns box specs in an eduridefile (if it exists) for the target file
	 */
	public static ArrayList<ISABceoBoxSpec> getBceoBoxSpecs(IFile targetfile) {
		EduRideFile erf = edurideFiles.get(targetfile);
		if (erf == null) {
			return null;
		} else {
			return (erf.getBceoSpecs());
		}
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
