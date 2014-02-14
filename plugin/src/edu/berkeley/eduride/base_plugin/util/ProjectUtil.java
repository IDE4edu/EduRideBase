package edu.berkeley.eduride.base_plugin.util;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;

public class ProjectUtil {
	
	
	public static QualifiedName isa_key = new QualifiedName("edu.berkeley.eduride","isAssignment");
	
	
	/**
	 * Determines whether a file is located within a project that contains an .isa file
	 */
	public static boolean withinISAProject(IFile file) {
		boolean containsISA = false;
		if (file != null) {
			// look at the files in the contained project, see if there
			// is an ISA file there.
			IProject proj = file.getProject();
			try {
				proj.setSessionProperty(isa_key, null);
				proj.accept(new IResourceVisitor() {			
					@Override
					public boolean visit(IResource resource) throws CoreException {
						if (!(resource.getType() == IResource.FILE)) return true;
						String extension = resource.getFileExtension();
						if (extension != null) {
							if (extension.equalsIgnoreCase("isa")) {
								resource.getProject().setSessionProperty(isa_key, new Boolean(true));
							}
						}
						return true;
					}
				});
				//TODO Fix null pointer exception when called on non-ISA project
				if (((Boolean)proj.getSessionProperty(isa_key)).booleanValue()) {
					containsISA = true;
				}
			} catch (CoreException e) {
				//e.printStackTrace();
			} catch (NullPointerException e) {
				// hm, called in a non-ISA project, ok.
			} catch (Exception e) {
				// whatever
			}
		}
		return containsISA;
	}

	
	
	
	

	// used in the feedback model to figure out which testclass to use for a
	// source file
	// What is the right thing here? Easy when navigator view is open, but what
	// about when it isn't?
	// - use the active project? Is this specific enough for situations where
	// source class doesn't uniquely identify test class?
	// - editor? No, this doesn't help anything, we already have the source
	// class...
	// - do this a different way and say which test classes can work for any
	// source class,
	// so they can all be shown?
	public static String getCurrentStep() {
		// TODO make this work for situations where a single source file has
		// multiple test classes (for different activities/steps)
		// this step key is passed to FeedbackModelProvider.setup as *.isa files
		// are parsed.
		return null;
	}
}
