package org.seasar.mayaa.matatabi.action;

import org.seasar.mayaa.matatabi.property.MatatabiPropertyPage;

public class OpenActionPairAction extends OpenActionBase {
	public OpenActionPairAction() {
		super("java");
	}

	@Override
	protected void init() {
		baseDir = store.getString(MatatabiPropertyPage.WEB_ROOT_PATH);
		targetBaseDir = store.getString(MatatabiPropertyPage.JAVA_SOURCE_PATH)
				+ "/"
				+ store.getString(MatatabiPropertyPage.DEFAULT_PACKAGE)
						.replace('.', '/');
	}

	@Override
	protected String getResourceName(String baseName) {
		return baseName.substring(0, 1).toUpperCase() + baseName.substring(1)
				+ "Action";
	}
}
