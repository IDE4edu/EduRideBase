package edu.berkeley.eduride.base_plugin.isafile;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.core.resources.IFile;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import edu.berkeley.eduride.base_plugin.model.Activity;
import edu.berkeley.eduride.base_plugin.model.Step;

public class ISAParser {

	
	
	
	public static Activity parseISA(IFile isa) {
		ISAHandler handler;
		try {
			// project is that which contains the isa file
			String projectName = isa.getFullPath().segment(0);
			URI uri = isa.getLocationURI();
			File file = new File(uri);

			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			handler = new ISAHandler();
			handler.projectName = projectName;
			saxParser.parse(file, handler);

			Activity activity = handler.getActivity();

			postProcessSteps(activity);
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


	private static void postProcessSteps(Activity activity) {

		ArrayList<Step> steps = activity.getSteps();
		for (Step step : steps) {
			try {
				
				
				if (step.isCODE()) {
					codeStepCreated(step);
				}

				if (step.isHTML()) {
					htmlStepCreated(step);
				}
				
				
				
			} catch (ISAFormatException e) {
				// TODO: ISA file has bad content
				System.err.println("Problem processing step " + step.getName());
				e.printStackTrace();
			}
		}
	}
	

	
	private static ArrayList<StepCreatedListener> stepCreatedListeners = new ArrayList<StepCreatedListener>();
	
	public static boolean registerStepCreatedListener(StepCreatedListener l) {
		boolean result = stepCreatedListeners.add(l);
		// shit.  what about all those already parsed ISA files.  process the steps in them?
		return (result);
	}
	
	public static boolean removeStepCreatedListener(StepCreatedListener l) {
		return (stepCreatedListeners.remove(l));
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
	
	
}
