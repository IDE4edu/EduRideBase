package edu.berkeley.eduride.base_plugin.isafile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import edu.berkeley.eduride.base_plugin.isafile.ISABceoBoxSpec.BceoBoxType;
import edu.berkeley.eduride.base_plugin.model.Activity;
import edu.berkeley.eduride.base_plugin.model.EduRideFile;
import edu.berkeley.eduride.base_plugin.model.Step;
import edu.berkeley.eduride.base_plugin.model.Step.StepType;

/**
 * used by ISAParser
 * 
 * @author nate, andy
 * 
 */

class ISAParseHandler extends DefaultHandler {

	public static final String edurideTag = "eduride";

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
	public static final String base64Tag = "base64";
	public static final String boxTag = "box";
	// reuse type in box
	public static final String boxInlineValue = "inline";
	public static final String boxMultilineValue = "multiline";
	// reuse name in box
	public static final String startTag = "start";
	public static final String stopTag = "stop";

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

	// /////// tag state

	// isa state

	private ArrayList<Activity> acts = new ArrayList<Activity>();

	public ArrayList<Activity> getActivities() {
		return acts;
	}

	private String isaIntro;
	private String isaName;
	private String isaCategory;
	private String isaSubCategory;
	private String isaSortOrder;

	private void resetIsaDefaults() {
		isaIntro = "";
		isaName = "";
		isaCategory = "";
		isaSubCategory = "";
		isaSortOrder = "1";
		isaSteps = new ArrayList<Step>();
	}

	// step state

	ArrayList<Step> isaSteps;

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

	private ArrayList<EduRideFile> edurideFiles = new ArrayList<EduRideFile>();

	public ArrayList<EduRideFile> getEduRideFiles() {
		return edurideFiles;
	}

	private String fileSource;
	private String fileBase64;

	private void resetEdurideFileDefaults() {
		fileSource = "";
		fileBase64 = "";
		erfBoxes = new ArrayList<ISABceoBoxSpec>();
	}

	// // box state

	ArrayList<ISABceoBoxSpec> erfBoxes;

	private BceoBoxType boxType;
	private String boxName;
	private int boxStart;
	private int boxStop;

	private void resetBox() {
		boxType = BceoBoxType.UNKNOWN;
		boxName = null;
		boxStart = -1;
		boxStop = -1;
	}

	// //

	StringBuffer buffer = new StringBuffer();

	private void resetInput() {
		buffer = new StringBuffer();
	}

	// parse STATE
	private static final int OUTSIDE = 0;
	private static final int IN_EDURIDE = 1;
	private static final int IN_ISA = 3;
	private static final int IN_STEP = 4;
	private static final int IN_ERFILE = 5;
	private static final int IN_BOX = 6;
	private int state = OUTSIDE;

	private boolean outside() {
		return state == OUTSIDE;
	}

	private boolean inStep() {
		return state == IN_STEP;
	}

	private boolean inEduride() {
		// technically should be true if in any other state... whatever... SAX2
		// sax eggs
		return (state == IN_EDURIDE);
	}

	private boolean inIsa() {
		return (state == IN_ISA || inStep());
	}

	private boolean inERFile() {
		return (state == IN_ERFILE || inBox());
	}

	private boolean inBox() {
		return state == IN_BOX;
	}

	private void startEduride() {
		state = IN_EDURIDE;
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

	private void startBox() {
		state = IN_BOX;
	}

	private void endStep() {
		state = IN_ISA;
	}

	private void endIsa() {
		state = IN_EDURIDE;
	}

	private void endERFile() {
		state = IN_EDURIDE;
	}

	private void endBox() {
		state = IN_ERFILE;
	}

	private void endEduride() {
		state = OUTSIDE;
	}

	// //////////

	@Override
	public void startDocument() throws SAXException {

	}

	@Override
	public void endDocument() throws SAXException {
		if (!outside()) {
			ISAUtil.createISAFormatProblemMarker(isafile, 1,
					"Document ended without properly closed '" + edurideTag
							+ "' tag.");
		}
	}

	@Override
	public void startElement(String namespaceURI, String localName,
			String qName, Attributes atts) throws SAXException {
		if (outside()) {
			if (qName.equalsIgnoreCase(edurideTag)) {
				startEduride();
			} else if (qName.equalsIgnoreCase(isaTag)) {
				// ok to have an <isa> outside of <eduride>, why not?
				startIsa();
				resetIsaDefaults();
			} else {
				ISAUtil.createISAFormatProblemMarker(isafile, 1,
						"Bad start tag in isa file, haven't seen '" + edurideTag + "' or '" + isaTag + "' yet): " + qName);

			}
		} else if (inEduride()) {

			if (qName.equalsIgnoreCase(isaTag)) {
				startIsa();
				resetIsaDefaults();
			} else if (qName.equalsIgnoreCase(edurideFileTag)) {
				startERFile();
				resetEdurideFileDefaults();
			} else {
				ISAUtil.createISAFormatProblemMarker(isafile, 1,
						"Bad start tag in isa file, inside '" + edurideTag + "): " + qName);
			}
		} else if (inIsa() && !(inStep())) {
			// in an ISA
			if (qName.equalsIgnoreCase(stepTag)) {
				startStep();
				resetStepDefaults();
			}
		} else if (inERFile() && !(inBox())) {
			if (qName.equalsIgnoreCase(boxTag)) {
				startBox();
				resetBox();
			}
		} else {
			// let it go, let it go, can't hold it back anymore
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
				} else {
					// Note: stepTag here (endElement) is an error!
					ISAUtil.createISAFormatProblemMarker(isafile, 1,
							"Bad tag in isa file, inside '" + isaTag
									+ "' (name: '" + isaName + "'): " + qName
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
			if (!(inBox())) {
				if (qName.equalsIgnoreCase(sourceTag)) {
					fileSource = s;
				} else if (qName.equalsIgnoreCase(base64Tag)) {
					fileBase64 = s;
				} else if (qName.equalsIgnoreCase(edurideFileTag)) {
					makeNewERFile();
					endERFile();
				} else {
					ISAUtil.createISAFormatProblemMarker(isafile, 1,
							"Bad tag in isa file, inside '" + edurideFileTag
									+ "' (source: '" + fileSource + "'): "
									+ qName);
				}

			} else {
				// in a box
				if (qName.equalsIgnoreCase(nameTag)) {
					boxName = s;
				} else if (qName.equalsIgnoreCase(typeTag)) {
					if (s.equalsIgnoreCase(boxMultilineValue)) {
						boxType = BceoBoxType.MULTILINE;
					} else if (s.equalsIgnoreCase(boxInlineValue)) {
						boxType = BceoBoxType.INLINE;
					} else {
						boxType = BceoBoxType.UNKNOWN;
					}
				} else if (qName.equalsIgnoreCase(startTag)) {
					// TODO -- catch NumberFormatExceptions here?
					boxStart = Integer.parseInt(s);
				} else if (qName.equalsIgnoreCase(stopTag)) {
					boxStop = Integer.parseInt(s);
				} else if (qName.equalsIgnoreCase(boxTag)) {
					makeNewBox();
					endBox();
				} else {
					ISAUtil.createISAFormatProblemMarker(isafile, 1,
							"Bad tag in isa file, inside '" + boxTag
									+ "' (name: '" + boxName + "'): " + qName);

				}
			}
		} else if (inEduride()) {
			// in eduride but not in isa or eduridefile
			if (qName.equalsIgnoreCase(edurideTag)) {
				endEduride();
				// TODO -- end all the processing??
			} else {
				ISAUtil.createISAFormatProblemMarker(isafile, 1,
						"Bad tag in isa file, inside '" + edurideTag + ": "
								+ qName);

			}
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

	private void makeNewERFile() {
		// TODO
		try {
			Path path = new Path(projectName + fileSource);
			IFile ifile = ResourcesPlugin.getWorkspace().getRoot()
					.getFile(path);
			if (!(ifile.exists())) {
				throw new IOException("File '" + fileSource + "' doesn't exist");
			}
//			File f = ifile.getFullPath().toFile();
//			if (!(f.exists())) {
//				throw new IOException("File '" + fileSource + "' doesn't exist");
//			}
			EduRideFile erf = EduRideFile.get(ifile, isafile, erfBoxes, fileBase64);
			edurideFiles.add(erf);

		} catch (IOException e) {
			ISAUtil.createISAFormatProblemMarker(isafile, 1, e.getMessage());

		}
	}

	private void makeNewBox() {
		ISABceoBoxSpec boxspec = new ISABceoBoxSpec(boxType, boxName, boxStart,
				boxStop);
		erfBoxes.add(boxspec);
	}

	@Override
	public void characters(char[] ch, int start, int length) {
		buffer.append(new String(ch, start, length));
	}

}