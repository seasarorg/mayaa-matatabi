package org.seasar.mayaa.matatabi.action;

import org.eclipse.jface.action.IAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;

public class OpenHTMLAction extends OpenAction {
	/**
	 * Constructor for Action1.
	 */
	public OpenHTMLAction() {
		super("html");
	}

	public void run(IAction action) {
		try {
			super.run(action);
		} catch (RuntimeException e) {
			MessageBox messageBox = new MessageBox(targetPart.getSite()
					.getShell(), SWT.OK | SWT.ICON_ERROR);
			messageBox.setMessage("対応するファイルが存在しません");
			messageBox.setText("エラー");
			messageBox.open();
		}
	}
}
