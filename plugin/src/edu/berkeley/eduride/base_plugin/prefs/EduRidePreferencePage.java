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
		label.setText(statusText());
		label.setLayoutData(gridData);
		
		final Button loginButton = new Button(parent, SWT.NULL);
		loginButton.setText(loginText());
		loginButton.pack();
		loginButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				super.mouseDown(e);
				EduRideBase.displayLoginPrompt();
				label.setText(statusText());
				loginButton.setText(loginText());
				loginButton.pack();
			}
		});
		
		return new Composite(parent, SWT.NULL);
	}
	
	private String statusText() {
		if (EduRideBase.getRemainGuestStatus()) {
			return "You have chosen to remain a guest.";
		} else if (EduRideBase.currentlyAuthenticated()) {
			return "You are logged in as " + EduRideBase.getUsernameStored() 
					+ " on domain " + EduRideBase.getDomain() + ".";
		} else {   
			return "You are not yet logged in.";
		}
	}
	
	private String loginText() {
		if (EduRideBase.currentlyAuthenticated()) {
			return "Change user/domain";
		} else {
			return "Login...";
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}
	
}