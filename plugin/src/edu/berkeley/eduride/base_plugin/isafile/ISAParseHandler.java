package edu.berkeley.eduride.base_plugin.isafile;

import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import edu.berkeley.eduride.base_plugin.model.Activity;
import edu.berkeley.eduride.base_plugin.model.Step;
import edu.berkeley.eduride.base_plugin.model.Step.StepType;

/**
 * used by ISAParser
 * 
 * @author nate, andy
 * 
 */

class ISAParseHandler extends DefaultHandler {

	public static final String isaTag = "isa";
	public static final String stepTag = "exercise";
	public static final String categoryTag = "category";
	public static final String subcategoryTag = "subcategory";
	public static final String sortorderTag = "sortorder";

	public static final String nameTag = "name";
	public static final String typeTag = "type";
	public static final String introTag = "intro"; // optional
	public static final String sourceTag = "source";
	public static final String testclassTag = "testclass"; // unused now
	public static final String launchTag = "launch"; // optional
	public static final String launchButtonNameTag = "launchButtonName"; // optional

	public static final String edurideFileTag = "eduridefile";
	// reuse <source>
	public static final String bceo = "bceo";
	public static final String base64 = "base64";

	/*
	 * TODO
	 * 
	 * (1) PROCESS <eduRideSource>, outside of <isa> <eduRideFile> <source> ...
	 * a path </source> <BCEO> ... send contents to
	 * BCEO...Util.importBCEOXML(xmlcontents, IResource) </BCEO> <base64> ...
	 * lots o crap </base64> </eduRideFile>
	 * 
	 * 
	 * (2) make it so one .isa file can have 0+ activities, 0+ eduRideSources
	 * 
	 * 
	 * (3) persist eduRideSource stuff.
	 */

	private IFile isafile = null;

	public void setIsafile(IFile isafile) {
		this.isafile = isafile;
	}

	private String projectName;

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	private IProject iproj;

	public void setIProject(IProject iproj) {
		this.iproj = iproj;
	}

	private ArrayList<Activity> acts = new ArrayList<Activity>();
	public ArrayList<Activity> getActivities() {
		return acts;
	}



	// /////// tag state

	// isa state
	private String isaIntro;
	private String isaName;
	private String isaCategory;
	private String isaSubCategory;
	private String isaSortOrder;
	ArrayList<Step> isaSteps;
	
	private void resetIsaDefaults() {
		isaIntro = "";
		isaName = "";
		isaCategory = "";
		isaSubCategory = "";
		isaSortOrder = "1";
		isaSteps = new ArrayList<Step>();
	}

	// step state
	private String stepName;
	private StepType stepType; // default comes from parseStepType()
	private String stepIntro;
	private String stepSource;
	private String stepTestClass;
	private String stepLaunch;
	private String stepLaunchButtonName;

	private void resetStepDefaults() {
		stepName = "";
		StepType type = Step.parseStepType(null);
		stepIntro = "";
		stepSource = "";
		stepTestClass = null;
		stepLaunch = null;
		stepLaunchButtonName = "Run Tests";
	}

	// source state
	private String fileSource;
	private String fileBceo;
	private String fileBase64;

	private void resetEdurideFileDefaults() {
		fileSource = "";
		fileBase64 = "";
		fileBceo = "";
	}

	StringBuffer buffer = new StringBuffer();

	private void resetInput() {
		buffer = new StringBuffer();
	}

	// parse STATE
	private static final int OUTSIDE = 0;
	private static final int IN_ISA = 3;
	private static final int IN_STEP = 4;
	private static final int IN_ERFILE = 5;
	private int state = OUTSIDE;

	private boolean inStep() {
		return state == IN_STEP;
	}

	private boolean inIsa() {
		return (state == IN_ISA || state == IN_STEP);
	}

	private boolean inERFile() {
		return state == IN_ERFILE;
	}

	private boolean outside() {
		return state == OUTSIDE;
	}

	private void startStep() {
		state = IN_STEP;
	}

	private void startIsa() {
		state = IN_ISA;
	}

	private void startERFile() {
		state = IN_ERFILE;
	}

	private void endStep() {
		state = IN_ISA;
	}

	private void endIsa() {
		state = OUTSIDE;
	}

	private void endERFile() {
		state = OUTSIDE;
	}

	// //////////

	@Override
	public void startDocument() throws SAXException {
		
	}

	@Override
	public void endDocument() throws SAXException {

	}

	@Override
	public void startElement(String namespaceURI, String localName,
			String qName, Attributes atts) throws SAXException {
		if (outside()) {
			// not in any parse state yet
			if (qName.equalsIgnoreCase(isaTag)) {
				startIsa();
				resetIsaDefaults();
			} else if (qName.equalsIgnoreCase(sourceTag)) {
				startERFile();
				resetEdurideFileDefaults();
			}
		} else if (inIsa() && !(inStep())) {
			// in an ISA
			if (qName.equalsIgnoreCase(stepTag)) {
				startStep();
				resetStepDefaults();
			}
		} else {
			// an error -- catch it in endElement I guess?
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		String s = buffer.toString().trim();

		if (inIsa()) {
			// inside an ISA tag

			if (!inStep()) {
				// Not in a step
				if (qName.equalsIgnoreCase(introTag)) {
					isaIntro = s;
				} else if (qName.equalsIgnoreCase(nameTag)) {
					isaName = s;
				} else if (qName.equalsIgnoreCase(categoryTag)) {
					isaCategory = s;
				} else if (qName.equalsIgnoreCase(subcategoryTag)) {
					isaSubCategory = s;
				} else if (qName.equalsIgnoreCase(sortorderTag)) {
					isaSortOrder = s;
				} else if (qName.equalsIgnoreCase(isaTag)) {
					// closing the activity!
					makeNewActivity();
					endIsa();
				} else 
					// Note: stepTag here (endElement) is an error!
				{
					ISAUtil.createISAFormatProblemMarker(isafile, 1,
							"Bad tag in isa file, inside '"+isaTag+"' (name: '"
									+ isaName + "'): " + qName
									+ " ; Content is: " + s);

				}
			} else {
				// inside the step tag
				if (qName.equalsIgnoreCase(introTag)) {
					stepIntro = s;
				} else if (qName.equalsIgnoreCase(nameTag)) {
					stepName = s;
				} else if (qName.equalsIgnoreCase(typeTag)) {
					stepType = Step.parseStepType(s); // code is in Step
				} else if (qName.equalsIgnoreCase(sourceTag)) {
					stepSource = s;
				} else if (qName.equalsIgnoreCase(testclassTag)) {
					stepTestClass = s;
				} else if (qName.equalsIgnoreCase(launchTag)) {
					stepLaunch = s;
				} else if (qName.equalsIgnoreCase(launchButtonNameTag)) {
					stepLaunchButtonName = s;
				} else if (qName.equalsIgnoreCase(stepTag)) {
					makeNewStep();
					endStep();
				} else {
					ISAUtil.createISAFormatProblemMarker(isafile, 1,
							"Bad tag in isa file, inside '" + stepTag
									+ "' (name: '" + stepName + "'): " + qName
									+ " ; Content is: " + s);

				}
			}
		} else if (inERFile()) {
			// in a edurideFiletag
			// TODO
		} else {
			// not in isa or source... wha?
			ISAUtil.createISAFormatProblemMarker(isafile, 1,
					"Bad tag in isa file, outside of '" + isaTag + "' or '"
							+ sourceTag + "': " + qName + " ; Content is: " + s);
		}

		resetInput();

	}

	private void makeNewStep() {
		Step newstep = new Step(projectName, stepName, stepSource, stepType,
				stepIntro, stepTestClass, stepLaunch, stepLaunchButtonName);
		newstep.setIProject(iproj);
		isaSteps.add(newstep);
	}

	private void makeNewActivity() {
		Activity act = new Activity(projectName, isaIntro, isaName, isaSteps,
				isaCategory, isaSubCategory, isaSortOrder);
		act.setIsaFile(isafile);
		act.setIProject(iproj);
		acts.add(act);
	}

	@Override
	public void characters(char[] ch, int start, int length) {
		buffer.append(new String(ch, start, length));
	}

}