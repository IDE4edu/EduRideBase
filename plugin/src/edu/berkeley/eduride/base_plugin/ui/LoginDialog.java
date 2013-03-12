package edu.berkeley.eduride.base_plugin.ui;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

public class LoginDialog extends InputDialog {

	/*
	 * Only pops up if they aren't authenticated yet AND have said they want to remain a guest
	 * 
	 * check box in front of :
	 * - I wish to remain a guest
	 * - or maybe a BUTTON staying "Login as Guest"
	 * 
	 * two fields, greyed out if above is checked
	 *    username, password
	 * 
	 * one field never greyed out:
	 *     eduRideServer  (prepopulated with http://eduride.berkeley.edu)
	 * 
	 * OK button   
	 * 
	 * Make sure they don't enter EduRideBase.guestUserName as username
	 * It will push the workspaceID up to the server as well, so it can be linked
	 *   to username (if it isn't already)
	 * Give status of authentication report to user, let them try again if failed
	 */
	
	
	public LoginDialog(Shell parentShell, String dialogTitle,
			String dialogMessage, String initialValue, IInputValidator validator) {
		super(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
				"EduRide Login", dialogMessage, initialValue, validator);
	}

}
