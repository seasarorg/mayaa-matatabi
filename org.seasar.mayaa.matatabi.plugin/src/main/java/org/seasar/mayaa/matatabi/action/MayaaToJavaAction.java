package org.seasar.mayaa.matatabi.action;

import org.seasar.mayaa.matatabi.property.MatatabiPropertyPage;

public class MayaaToJavaAction extends OpenActionBase {
	public MayaaToJavaAction() {
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

	@Override
	protected String getSubDirectory(String packageName) {
		String packageSuffix = store
				.getString(MatatabiPropertyPage.DEFAULT_PACKAGE_SUFFIX);
		if (!"".equals(packageSuffix)) {
			packageName += "." + packageSuffix;
		}
		return super.getSubDirectory(packageName);
	}
}
