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
	
	

	String isaIntro = "";
	String isaName = "";
	String isaCategory = "";
	String isaSubCategory = "";
	String isaSortOrder = "1";

	
	boolean inStep = false;

	String name;
	StepType type; // default comes from parseStepType()
	String intro;
	String source;
	String testclass;
	String launch;
	String launchButtonName;

	// Defaults
	private void resetDefaults() {
		name = "";
		StepType type = Step.parseStepType(null);
		intro = "";
		source = "";
		testclass = null;
		launch = null;
		launchButtonName = "Run Tests";
	}

	StringBuffer buffer = new StringBuffer();
	ArrayList<Step> steps = new ArrayList<Step>();

	////////////
	
	/* TODO
	 
(1) PROCESS <eduRideSource>, outside of <isa>
<eduRideSource>
   <file>
     ... a path ala <source>
   </file>
   <BCEO>
      ... send contents to BCEO...Util.importBCEOXML(xmlcontents, IResource)
   </BCEO>
   <base64>
      ... lots o crap
   </base64>
</eduRideSource>


(2) make it so one .isa file can have 0+ activities, 0+ eduRideSources


(3) persist eduRideSource stuff.


	 */
	
	
	
	
	@Override
	public void startDocument() throws SAXException {
	}

	@Override
	public void endDocument() throws SAXException {
		Activity act = new Activity(projectName, isaIntro, isaName, steps, isaCategory, isaSubCategory, isaSortOrder);
		act.setIsaFile(isafile);
		act.setIProject(iproj);
		acts.add( act );
	}

	
	
	@Override
	public void startElement(String namespaceURI, String localName,
			String qName, Attributes atts) throws SAXException {
		if (qName.equalsIgnoreCase(stepTag)) {
			inStep = true;
			resetDefaults();
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		String s = buffer.toString().trim();
		if (qName.equalsIgnoreCase(stepTag)) {
			Step newstep = new Step(projectName, name, source, type, intro, testclass,
					launch, launchButtonName);
			newstep.setIProject(iproj);
			steps.add(newstep);
			reset(false);
		} else if (!inStep) {
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
			}
			reset(false);
		} else {
			// in the step tag
			if (qName.equalsIgnoreCase(introTag)) {
				intro = s;
			} else if (qName.equalsIgnoreCase(nameTag)) {
				name = s;
			} else if (qName.equalsIgnoreCase(typeTag)) {
				type = Step.parseStepType(s); // code is in Step
			} else if (qName.equalsIgnoreCase(sourceTag)) {
				source = s;
			} else if (qName.equalsIgnoreCase(testclassTag)) {
				testclass = s;
			} else if (qName.equalsIgnoreCase(launchTag)) {
				launch = s;
			} else if (qName.equalsIgnoreCase(launchButtonNameTag)) {
				launchButtonName = s;
			} else if (qName.equalsIgnoreCase(isaTag)) {
				// do nothing
			} else {
				ISAUtil.createISAFormatProblemMarker(isafile, 1, "Bad tag in isa file: " + qName + " String is: " + s);
				// Console.isaErr("Bad tag in isa file: " + qName + " String is: " + s);
			}
			reset(inStep);
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) {
		buffer.append(new String(ch, start, length));
	}

	private void reset(boolean inEx) {
		buffer = new StringBuffer();
		inStep = inEx;
	}

}