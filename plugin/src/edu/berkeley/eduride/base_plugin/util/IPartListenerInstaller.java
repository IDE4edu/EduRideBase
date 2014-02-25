package edu.berkeley.eduride.base_plugin.util;

import java.util.ArrayList;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class IPartListenerInstaller {

	// returns an error string or null if aok
	public static String installOnWorkbench(IPartListener2 listener,
			String installer) {

		try {
			ArrayList<IWorkbenchPage> pages = getWorkbenchPages();
			IWorkbenchPage lastpage = null;
			for (IWorkbenchPage page : pages) {
				installOnPage(page, listener);
				lastpage = page;
			}
			if (lastpage != null) {
				Console.msg("IPartListener for " + installer
						+ " last installed on " + lastpage.getLabel());
			} else {
				Console.err("IPartListener didn't find any non-null workbench pages...");
			}
		} catch (EduRideException e) {
			return e.getMessage();
		} catch (Exception e) {
			Console.err("General exception in 'installOnWorkbench': " + e.getMessage());
			return e.getMessage();
		}
		return null;

	}

	private static ArrayList<IWorkbenchPage> getWorkbenchPages()
			throws EduRideException {

		ArrayList<IWorkbenchPage> output = new ArrayList<IWorkbenchPage>();
		String errString = "";
		try {
			IWorkbench workbench = PlatformUI.getWorkbench(); // might throw
																// exception
			IWorkbenchWindow windows[] = null;
			if (workbench == null) {
				errString += "Workbench is null!  ";

			} else {
				windows = workbench.getWorkbenchWindows();
				for (int i = 0; i < windows.length; i++) {
					IWorkbenchWindow window = windows[i];
					if (window == null) {
						errString += "Active workbench window " + i
								+ " is null.  ";
					} else {
						IWorkbenchPage[] pages = window.getPages();
						boolean installedOnAPage = false;
						for (IWorkbenchPage page : pages) {
							if (page != null) {
								output.add(page);
								installedOnAPage = true;
							}
							// window.getPartService().addPartListener(this);
							// more modernish
						}
						if (!installedOnAPage) {
							// darn, we got to try again here... arg.
							errString += "IPartListener: No non-null pages in any workbench window. ";
						}
					}
				}
			}
		} catch (IllegalStateException e) {
			errString += "getWorkbench() failed: " + e.getMessage() + ".  ";
		}
		if (errString != "") {
			throw new EduRideException(errString);
		}

		// return (errString == "" ? null : errString);
		return output;
	}

	// error check this?
	public static void installOnPage(IWorkbenchPage page,
			IPartListener2 listener) {
		page.addPartListener(listener);
	}

	// ///////////////////////////

	public static ArrayList<IEditorPart> getCurrentEditors() {
		try {
			ArrayList<IEditorPart> eds = new ArrayList<IEditorPart>();
			ArrayList<IWorkbenchPage> pages = getWorkbenchPages();
			for (IWorkbenchPage page : pages) {
				IEditorReference[] edRefs = page.getEditorReferences();
				for (IEditorReference edRef : edRefs) {
					IEditorPart ed = edRef.getEditor(false);
					if (ed != null) {
						eds.add(ed);
					}
				}
			}

			return eds;
		} catch (Exception e) {
			return null;
		}

	}

}
