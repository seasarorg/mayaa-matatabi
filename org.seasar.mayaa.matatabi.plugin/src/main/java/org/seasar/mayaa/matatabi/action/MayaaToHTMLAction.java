package org.seasar.mayaa.matatabi.action;

import org.seasar.mayaa.matatabi.property.MatatabiPropertyPage;

public class MayaaToHTMLAction extends OpenHTMLActionBase {
	@Override
	protected void init() {
		baseDir = store.getString(MatatabiPropertyPage.WEB_ROOT_PATH);
		targetBaseDir = baseDir;
	}
}
