package edu.berkeley.eduride.base_plugin.ui;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import edu.berkeley.eduride.base_plugin.EduRideBase;

public class LoginDialog extends InputDialog {
	private Text userInput;
	private Text passwordInput;
	private Label errorLabel;
	private Button guestButton;

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
	
	
	public LoginDialog() {
		super(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
				"EduRide Login", "Domain:", EduRideBase.whereami(), null);
		setBlockOnOpen(true);
	}
	
	@Override
	protected Control createDialogArea(final Composite parent) {
		final Composite c = (Composite) super.createDialogArea(parent);
		final Button b = new Button(c, SWT.CHECK);
		Label label = new Label(c, SWT.NULL);
		label.setText("Log in:");
		final Text userInput = new Text(c, SWT.SINGLE | SWT.BORDER);
		this.userInput = userInput;
		label = new Label(c, SWT.NULL);
		label.setText("Password:");
		final Text passwordInput = new Text(c, SWT.PASSWORD | SWT.SINGLE | SWT.BORDER);
		this.passwordInput = passwordInput;
		if (EduRideBase.whoami() != null) {
			userInput.setText(EduRideBase.whoami());
		}
		this.guestButton = b;
		
		b.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				userInput.setEnabled(b.getSelection());
				passwordInput.setEnabled(b.getSelection());
			}
		});
		b.setText("I want to remain a guest");
		label = new Label(c, SWT.NULL);
		label.setText("hi");
		this.errorLabel = label;
		GridData g = new GridData();
		g.horizontalAlignment = SWT.FILL;
		g.grabExcessHorizontalSpace = true;
		userInput.setLayoutData(g);
		passwordInput.setLayoutData(g);
		label.setLayoutData(g);
		getText().setText(EduRideBase.whereami());
		return c;
	}
	
	@Override
	protected void okPressed() {
		boolean guest = false;
		if (guestButton.getSelection() ||
				userInput.getText().equals(EduRideBase.guestUserName)) {
			guest = true;
		}
		if (EduRideBase.authenticate(userInput.getText(), passwordInput.getText(), 
				getText().getText())) {
			super.okPressed();
		} else {
			errorLabel.setText("Invalid uesrname/password/domain");
			errorLabel.redraw();
			errorLabel.update();
		}
		
	}
}
