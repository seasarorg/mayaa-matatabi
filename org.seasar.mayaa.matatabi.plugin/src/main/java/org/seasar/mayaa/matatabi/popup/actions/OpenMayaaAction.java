package org.seasar.mayaa.matatabi.popup.actions;

import org.eclipse.jface.action.IAction;
import org.seasar.mayaa.matatabi.util.GenerateUtil;

public class OpenMayaaAction extends OpenAction {

	/**
	 * Constructor for Action1.
	 */
	public OpenMayaaAction() {
		super("mayaa");
	}

	public void run(IAction action) {
		try {
			super.run(action);
		} catch (RuntimeException e) {
			GenerateUtil.generateMayaaFile();
			super.run(action);
		}
	}
}
