package edu.berkeley.eduride.base_plugin.ui;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

public class LoginDialog extends InputDialog {

	public LoginDialog(Shell parentShell, String dialogTitle,
			String dialogMessage, String initialValue, IInputValidator validator) {
		super(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
				"EduRide Login", dialogMessage, initialValue, validator);
	}

}
