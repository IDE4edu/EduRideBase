package edu.berkeley.eduride.base_plugin.model;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.swt.widgets.Group;

import edu.berkeley.eduride.base_plugin.util.Console;


public class Activity implements Comparable<Activity> {

	String projectName;
	String name;
	String intro = "";
	String category;
	String subCategory;
	int sortOrder;
	ArrayList<Step> steps;
	
	
	
	private IFile isaFile;
	private IProject iProj;

	public IFile getIsaFile() {
		return isaFile;
	}
	
	public void setIsaFile(IFile isaFile) {
		this.isaFile = isaFile;
	}

	public IProject getIProject() {
		return iProj;
	}
	
	public void setIProject(IProject iProj) {
		this.iProj = iProj;
	}
	
	
	
	
	
	public Activity(String projectName, String intro, String name, ArrayList<Step> steps,
			String category, String subCategory, String sortOrder) {
		this.projectName = projectName;
		this.intro = intro;
		this.steps = steps;
		this.name = name;
		this.category = category;
		this.subCategory = subCategory;
		try {
			this.sortOrder = Integer.parseInt(sortOrder);
		} catch (NumberFormatException e) {
			// TODO Throw exception up, please, for authors
			Console.err("hey, bad sort order " + sortOrder + " on activity " + name + " -- couldn't convert it to int");
			this.sortOrder = 1;
		}
	}

	public int compareTo(Activity that) {
	    final int BEFORE = -1;
	    final int EQUAL = 0;
	    final int AFTER = 1;
		if (this.sortOrder == that.sortOrder){
			return EQUAL; 
		} else if (this.sortOrder < that.sortOrder) {
			return BEFORE;
		} else {
			return AFTER;
		}
	}

	public String getProjectName() {
		return projectName;
	}
	
	public String getName() {
		return name;
	}

	public String getIntro() {
		return intro;
	}


	public ArrayList<Step> getSteps() {
		return steps;
	}

	public Step getStep(String name) {
		for (Step e : steps) {
			if (e.getName().equals(name))
				return e;
		}
		return null;
	}
	
	

	
	//////////////////////////////////////////////////
	/*
	 * Persistance for activities
	 */
	
	
	private static ArrayList<Activity> activityStore = new ArrayList<Activity>();
	


	
	public static ArrayList<Activity> getActivities() {
		return activityStore;
	}
	
	public static void recordActivity(Activity activity) {
		activityStore.add(activity);

	}
	
	// TODO fix this
	public static boolean hasActivity(IFile isaFile) {
		return false;
	}
	
	
	
	
	
	


}
