package edu.berkeley.eduride.base_plugin.model;

import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.swt.widgets.Group;


public class Activity implements Comparable<Activity> {

	String projectName;
	String name;
	String intro = "";
	String category;
	String subCategory;
	int sortOrder;
	ArrayList<Step> steps;
	public IFile isaFile;

	public IFile getIsaFile() {
		return isaFile;
	}

	public void setIsaFile(IFile isaFile) {
		this.isaFile = isaFile;
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
			System.err.println("hey, bad sort order " + sortOrder + " on activity " + name + " -- couldn't convert it to int");
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
	
	

	
	


}
