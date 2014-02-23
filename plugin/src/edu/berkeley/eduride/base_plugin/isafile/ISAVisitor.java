package edu.berkeley.eduride.base_plugin.isafile;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.JavaCore;


/*
 * Calls ISAParse on all isa files.
 * 
 */
public class ISAVisitor implements IResourceProxyVisitor {



	@Override
	public boolean visit(IResourceProxy proxy) throws CoreException {
		
		if (proxy.getType() == IResource.ROOT) {
			return true;
		} else if (proxy.getType() == IResource.PROJECT)  {
			// Project?  Keep visiting iff its a Java project
			IProject iproj = proxy.requestResource().getProject();
			return iproj.hasNature(JavaCore.NATURE_ID);
		} else if (proxy.getType() == IResource.FOLDER) {
			//Folder.  always visit.
			return true;
		} else if (proxy.getType() == IResource.FILE) {
			// FILE
			IFile ifile = (IFile) proxy.requestResource();
			if (ISAUtil.isISAFile(ifile)) {
				ISAUtil.setAsISA(ifile.getProject());
				ISAUtil.parseISA(ifile);
			}
		}
		return false;
	}

}
