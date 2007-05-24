package org.seasar.mayaa.matatabi.popup.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
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
			MessageBox messageBox = new MessageBox(targetPart.getSite()
					.getShell(), SWT.YES | SWT.NO | SWT.ICON_QUESTION);
			messageBox.setMessage("�Ή�����Mayaa�t�@�C�������݂��܂���B�쐬���܂����H");
			messageBox.setText("�G���[");
			switch (messageBox.open()) {
			case SWT.YES:
				GenerateUtil.generateMayaaFile();
				break;
			default:
				break;
			}
			try {
				super.run(action);
			} catch (RuntimeException e2) {
			}
		}
	}
}
