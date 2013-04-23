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
	 * check box in front of :
	 * - I wish to remain a guest
	 * - or maybe a BUTTON staying "Login as Guest"
	 * 
	 * two fields, greyed out if above is checked
	 *    userInput, passwordInput
	 * 
	 * one field always greyed out:
	 *     domainInput  (prepopulated with http://eduride.berkeley.edu)
	 * 
	 * OK button   
	 * 
	 * Make sure they don't enter EduRideBase.guestUserName as username
	 * It will push the workspaceID up to the server as well, so it can be linked
	 *   to username (if it isn't already)
	 * Give status of authentication report to user, let them try again if failed
	 */
	
	
	public LoginDialog() {
		// TODO figure out why the "Domain:" label isn't showing
		super(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
				"EduRide Login", "Domain:", EduRideBase.getDomain(), null);
		setBlockOnOpen(true);
	}
	
	@Override
	protected Control createDialogArea(final Composite parent) {
		final Composite c = (Composite) super.createDialogArea(parent);
		
		final Button checkButton = new Button(c, SWT.CHECK);
		checkButton.setText("I want to remain a guest");
		this.guestButton = checkButton;
		checkButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				userInput.setEnabled(checkButton.getSelection());
				passwordInput.setEnabled(checkButton.getSelection());
			}
		});
		
		Label label = new Label(c, SWT.NULL);
		label.setText("Log in:");
		final Text userInput = new Text(c, SWT.SINGLE | SWT.BORDER);
		this.userInput = userInput;
		userInput.setText(EduRideBase.getUsernameStored());
		
		label = new Label(c, SWT.NULL);
		label.setText("Password:");
		final Text passwordInput = new Text(c, SWT.PASSWORD | SWT.SINGLE | SWT.BORDER);
		this.passwordInput = passwordInput;

		if (EduRideBase.getRemainGuestStatus()) {
			checkButton.setSelection(true);
			userInput.setEnabled(false);
			passwordInput.setEnabled(false);
		}

		label = new Label(c, SWT.NULL);
		this.errorLabel = label;
		
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		userInput.setLayoutData(gridData);
		passwordInput.setLayoutData(gridData);

		return c;
	}
	
	
	public boolean chosenGuest() {
		return guestButton.getSelection();
	}
	
	@Override
	protected void okPressed() {
		if (chosenGuest()) {
			return;
		} else if (EduRideBase.authenticate(userInput.getText(),
				passwordInput.getText(), getText().getText())) {
			super.okPressed();
		} else {
			errorLabel.setText("Invalid username/password for domain");
			passwordInput.clearSelection();
			errorLabel.pack();
			errorLabel.redraw();
			errorLabel.update();
		}

	}
}
