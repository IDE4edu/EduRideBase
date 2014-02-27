package edu.berkeley.eduride.base_plugin.util;

import java.util.ArrayList;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class IPartListenerInstaller {

	// returns an error string or null if a-ok
	public static String installOnWorkbench(IPartListener2 listener,
			String installer) {

		try {
			
			ArrayList<IWorkbenchWindow> nonnullwindows = getWorkbenchWindows();
			
			// will this ever fail?  
			for (IWorkbenchWindow window : nonnullwindows) {
				IPartService svc = window.getPartService();
				svc.addPartListener(listener);
			}
			
			

//////// OLD WAY OF DOING IT WITH PAGES...			
//			ArrayList<IWorkbenchPage> pages = getWorkbenchPages();
//			IWorkbenchPage lastpage = null;
//			for (IWorkbenchPage page : pages) {
//				installOnPage(page, listener);
//				lastpage = page;
//			}
//			if (lastpage != null) {
//				Console.msg("IPartListener for " + installer
//						+ " last installed on " + lastpage.getLabel());
//			} else {
//				Console.err("IPartListener didn't find any non-null workbench pages...");
//			}
			
			
		} catch (EduRideException e) {
			return e.getMessage();
		} catch (Exception e) {
			Console.err("General exception in 'installOnWorkbench': "
					+ e.getMessage());
			return e.getMessage();
		}
		return null;

	}

	
	public static ArrayList<IWorkbenchWindow> getWorkbenchWindows()
			throws EduRideException {
		ArrayList<IWorkbenchWindow> output = new ArrayList<IWorkbenchWindow>();

		try {
			IWorkbench workbench = PlatformUI.getWorkbench(); // might throw
																// exception
			if (workbench == null) {
				throw new EduRideException("Workbench is null -- when trying to install IPartListener.");
			} else if (workbench.getWorkbenchWindowCount() == 0){
				throw new EduRideException("Workbench has 0 windows! -- when trying to install IPartListener.");
			} else {
				IWorkbenchWindow windows[] = workbench.getWorkbenchWindows();
				boolean hasNonNullWindow = false;
				for (IWorkbenchWindow window : windows) {
					if (window != null) {
						output.add(window);
						hasNonNullWindow = true;
					}
				}
				if (!hasNonNullWindow) {
					throw new EduRideException("Workbench has "+ workbench.getWorkbenchWindowCount() +" windows, but all are null -- when trying to install IPartListener.");
				}
			}
		} catch (IllegalStateException e) {
			throw new EduRideException("getWorkbench() failed: " + e.getMessage() + ", when trying to install IPartListener. ");
		}
		return output;
	}
	
	
//	// TODO remove: older way of installing... 
//	private static ArrayList<IWorkbenchPage> getWorkbenchPages()
//			throws EduRideException {
//
//		ArrayList<IWorkbenchPage> output = new ArrayList<IWorkbenchPage>();
//		String errString = "";
//		try {
//			IWorkbench workbench = PlatformUI.getWorkbench(); // might throw
//																// exception
//			IWorkbenchWindow windows[] = null;
//			if (workbench == null) {
//				errString += "Workbench is null!  ";
//
//			} else {
//				windows = workbench.getWorkbenchWindows();
//				if (windows == null || windows.length == 0) {
//					errString += "There are no workbench windows yet!  ";
//				} else {
//					boolean installedOnAPage = false;
//					for (int i = 0; i < windows.length; i++) {
//						boolean windowHasNonNullPage = false;
//						IWorkbenchWindow window = windows[i];
//						if (window == null) {
//							errString += "Active workbench window " + i
//									+ " is null.  ";
//						} else {
//							IWorkbenchPage[] pages = window.getPages();
//
//							for (IWorkbenchPage page : pages) {
//								if (page != null) {
//									output.add(page);
//									windowHasNonNullPage = true;
//									installedOnAPage = true;
//								}
//								// window.getPartService().addPartListener(this);
//								// more modernish
//							}
//						}
//						if (!windowHasNonNullPage) {
//							errString += "Workbench window (" + window.toString() + ") has no non-null pages.  ";
//						}
//					}
//					if (!installedOnAPage) {
//						// darn, we got to try again here... arg.
//						errString += "IPartListener: No non-null pages in any workbench window. ";
//					}
//
//				}
//			}
//		} catch (IllegalStateException e) {
//			errString += "getWorkbench() failed: " + e.getMessage() + ".  ";
//		}
//		if (errString != "") {
//			throw new EduRideException(errString);
//		}
//
//		// return (errString == "" ? null : errString);
//		return output;
//	}
//
//	// TODO old way -- new way installs on windows.
//	// error check this?
//	public static void installOnPage(IWorkbenchPage page,
//			IPartListener2 listener) {
//		page.addPartListener(listener);
//	}
	
	
	
	
	
	

	// ///////////////////////////

	// returns all editors in any workbench
	public static ArrayList<IEditorPart> getCurrentEditors() {
		ArrayList<IEditorPart> eds = new ArrayList<IEditorPart>();
		try {
			ArrayList<IWorkbenchWindow> windows = getWorkbenchWindows();
			for (IWorkbenchWindow window : windows) {
				if (window != null) {
					IWorkbenchPage[] pages = window.getPages();
					for (IWorkbenchPage page : pages) {
						if (page != null) {
							IEditorReference[] edRefs = page
									.getEditorReferences();
							for (IEditorReference edRef : edRefs) {
								if (edRef != null) {
									IEditorPart ed = edRef.getEditor(false);
									if (ed != null) {
										eds.add(ed);
									}
								}
							}
						}
					}
				}
			}
		} catch (EduRideException e) {
			Console.err(e);
		} catch (Exception e) {
			Console.err(e);
		}
		return eds;
	}

}
