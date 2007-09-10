package org.seasar.mayaa.matatabi.action;

import org.eclipse.jface.action.IAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.seasar.mayaa.matatabi.property.MatatabiPropertyPage;
import org.seasar.mayaa.matatabi.util.GenerateUtil;

public class HTMLToMayaaAction extends OpenActionBase {

	/**
	 * Constructor for Action1.
	 */
	public HTMLToMayaaAction() {
		super("mayaa");
	}

	@Override
	protected void init() {
		baseDir = store.getString(MatatabiPropertyPage.WEB_ROOT_PATH);
		targetBaseDir = baseDir;
	}

	@Override
	public void run(IAction action) {
		super.run(action);
		if (!getTargetFile().exists()) {
			MessageBox messageBox = new MessageBox(targetPart.getSite()
					.getShell(), SWT.YES | SWT.NO | SWT.ICON_QUESTION);
			messageBox.setMessage("対応するMayaaファイルが存在しません。作成しますか？");
			messageBox.setText("エラー");
			switch (messageBox.open()) {
			case SWT.YES:
				GenerateUtil.generateMayaaFile();
				break;
			default:
				break;
			}
		}
		super.run(action);
	}

	@Override
	protected String getResourceName(String baseName) {
		if (baseName.indexOf("$") > 0) {
			baseName = baseName.substring(0, baseName.indexOf("$"));
		}
		return super.getResourceName(baseName);
	}
}
