package edu.berkeley.eduride.base_plugin.isafile;

import edu.berkeley.eduride.base_plugin.model.Step;

public interface StepCreatedListener {

	// called after a code step has been created);
	public void codeStepCreated(Step step) throws ISAFormatException;
	
	public void htmlStepCreated(Step step) throws ISAFormatException;
}
