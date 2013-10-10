package edu.berkeley.eduride.base_plugin;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;

public class UIHelper {
	
	public static QualifiedName isa_key = new QualifiedName("edu.berkeley.eduride","isAssignment");
	
	public static boolean containedInISA(IFile file) {
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
			}
		}
		return containsISA;
	}
}
