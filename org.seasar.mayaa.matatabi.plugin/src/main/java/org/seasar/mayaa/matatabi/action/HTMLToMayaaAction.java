package org.seasar.mayaa.matatabi.action;

import org.seasar.mayaa.matatabi.property.MatatabiPropertyPage;

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
	protected String getResourceName(String baseName) {
		if (baseName.indexOf("$") > 0) {
			baseName = baseName.substring(0, baseName.indexOf("$"));
		}
		return super.getResourceName(baseName);
	}
}
