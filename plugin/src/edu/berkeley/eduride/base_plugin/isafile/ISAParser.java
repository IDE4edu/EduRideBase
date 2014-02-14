package edu.berkeley.eduride.base_plugin.isafile;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import edu.berkeley.eduride.base_plugin.model.Activity;
import edu.berkeley.eduride.base_plugin.model.Step;

public class ISAParser {
	
	
	
	
	// 1 to 1 relationship between Activities and isaFiles
	// ?when a file gets reparsed, it replaces the associated activity?
	
	public static Activity parseISA(IFile isaIfile) {
		ISAParseHandler handler;
		try {
			// project is that which contains the isa file
			String projectName = isaIfile.getFullPath().segment(0);
			URI uri = isaIfile.getLocationURI();
			File isaFile = new File(uri);
			IProject iproj = isaIfile.getProject();

			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			handler = new ISAParseHandler();
			handler.setProjectName(projectName);
			handler.setIProject(iproj);
			saxParser.parse(isaFile, handler);

			Activity activity = handler.getActivity();
			
			activity.setIsaFile(isaIfile);
			activity.setIProject(iproj);
			
			postProcess(activity);
			return activity;

			// THESE EXCEPTIONS NEED TO WARN USER (ISA AUTHOR) SOMEHOW
//		} catch (ISAFormatException e) {
//			// TODO: ISA file has bad content
//			e.printStackTrace();
//			return null; // ?
		} catch (SAXParseException e) {
			// problem in the XML somewhere
			System.err.println("ISA File Problem: tag " + e.getPublicId()
					+ ", line " + e.getLineNumber() + ", column "
					+ e.getColumnNumber());
			e.printStackTrace();
			return null;
		} catch (SAXException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return null;
		}

	}

	
	

	private static void postProcess(Activity activity) {

		ArrayList<Step> steps = activity.getSteps();
		for (Step step : steps) {
			try {
				postProcess(step);				
			} catch (ISAFormatException e) {
				// TODO: ISA file has bad content
				System.err.println("Problem processing step " + step.getName());
				e.printStackTrace();
			}
		}
	}
	
	
	
	
	private static void postProcess(Step step) throws ISAFormatException {
		
		if (step.isCODE()) {
			codeStepCreated(step);
		}

		if (step.isHTML()) {
			htmlStepCreated(step);
		}
		
	}
	

	
	private static void codeStepCreated (Step step) throws ISAFormatException {
		for (StepCreatedListener l : stepCreatedListeners) {
			l.codeStepCreated(step);
		}
	}
	
	
	private static void htmlStepCreated (Step step) throws ISAFormatException {
		for (StepCreatedListener l : stepCreatedListeners) {
			l.htmlStepCreated(step);
		}
	}
	
	
	
	/*
	 * Persistance for activities
	 */
	
	
	private static HashMap<IFile, Activity> activityStore = new HashMap<IFile, Activity>();

	public static boolean hasActivity(IFile isaFile) {
		return activityStore.containsKey(isaFile);
	}
	public static Activity getActivity(IFile isaFile) {
		return activityStore.get(isaFile);
	}
	
	private static void recordActivity(IFile isaFile, Activity activity) {
		activityStore.put(isaFile, activity);
	}
	
	
	/*
	 * Step created listeners
	 */

	
	private static ArrayList<StepCreatedListener> stepCreatedListeners = new ArrayList<StepCreatedListener>();
	
	public static boolean registerStepCreatedListener(StepCreatedListener l) {
		boolean result = stepCreatedListeners.add(l);
		// TODO call listener on all existing steps.
		return (result);
	}
	
	public static boolean removeStepCreatedListener(StepCreatedListener l) {
		return (stepCreatedListeners.remove(l));
	}

	

	
}
