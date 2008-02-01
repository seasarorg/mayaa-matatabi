package org.seasar.mayaa.matatabi.action;

import org.seasar.mayaa.matatabi.property.MatatabiPropertyPage;

public class HTMLToJavaAction extends OpenActionBase {
	public HTMLToJavaAction() {
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
		if (baseName.indexOf("$") > 0) {
			baseName = baseName.substring(0, baseName.indexOf("$"));
		}
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
