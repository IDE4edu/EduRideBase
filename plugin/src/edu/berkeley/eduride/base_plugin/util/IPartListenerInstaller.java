package edu.berkeley.eduride.base_plugin.util;

import java.util.ArrayList;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class IPartListenerInstaller {

	// returns an error string or null if aok
	public static String installOnAllExistingEditors (IPartListener2 listener) {

		String errString = "";
		try {
			IWorkbench workbench = PlatformUI.getWorkbench(); // might throw exception
			IWorkbenchWindow windows[] = null;
			if (workbench != null) {
				windows = workbench.getWorkbenchWindows();
			}
			for (int i = 0; i < windows.length; i++) {
				IWorkbenchWindow window = windows[i];
				if (window == null) {
					errString += "Active workbench window " + i + " is null.  ";
				} else {
					IWorkbenchPage[] pages = window.getPages();
					boolean installedOnAPage = false;
					for (IWorkbenchPage page : pages) {
						if (page != null) {
							installOnPage(page, listener);
							installedOnAPage = true;
						}
						// window.getPartService().addPartListener(this); 
						// more modernish
					}
					if (!installedOnAPage) {
						errString += "No non-null pages in any workbench window. ";
					}
				}
			}
		} catch (IllegalStateException e) {
			errString += "getWorkbench() failed: " + e.getMessage() + ".  ";
		}
		return (errString == "" ? null : errString);
	}
	
	
	// error check this?
	public static void installOnPage(IWorkbenchPage page, IPartListener2 listener) {
		page.addPartListener(listener);
		System.out.println("IPartListener installed on " + page.getLabel());
	}
}
