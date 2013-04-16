package edu.berkeley.eduride.base_plugin.prefs;

import org.eclipse.jface.preference.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbench;
import edu.berkeley.eduride.base_plugin.EduRideBase;
import edu.berkeley.eduride.base_plugin.ui.LoginDialog;

/**
 * This class represents a preference page that
 * is contributed to the Preferences dialog. By 
 * subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows
 * us to create a page that is small and knows how to 
 * save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They
 * are stored in the preference store that belongs to
 * the main plug-in class. That way, preferences can
 * be accessed directly via the preference store.
 */

public class EduRidePreferencePage
	extends PreferencePage
	implements IWorkbenchPreferencePage {

	public EduRidePreferencePage() {
		setPreferenceStore(EduRideBase.getDefault().getPreferenceStore());
		setDescription("Preferences for EduRide");
	}
	
	@Override
	protected Control createContents(Composite parent) {
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		final Label label = new Label(parent, SWT.NULL);
		label.setText(loginText());
		label.setLayoutData(gridData);
		final Button dialogButton = new Button(parent, SWT.NULL);
		dialogButton.setText(buttonText());
		dialogButton.pack();
		dialogButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				super.mouseDown(e);
				if(EduRideBase.loginPrompt()) {
					label.setText(loginText());
					dialogButton.setText(buttonText());
					dialogButton.pack();
				}
			}
		});
		return new Composite(parent, SWT.NULL);
	}
	
	private String loginText() {
		String text = "";
		if (EduRideBase.whoami() == null) {
			text = "You are not yet logged in.";
		} else if (EduRideBase.whoami().equals(EduRideBase.guestUserName)){
			text = "You are a guest.";
		} else {
			text = "You are logged in as " + EduRideBase.whoami() 
					+ " on domain " + EduRideBase.whereami() + ".";
		}
		return text;
	}
	
	private String buttonText() {
		if (EduRideBase.whoami() == null) {
			return "Login...";
		} else {
			return "Change user/domain";
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}
	
}