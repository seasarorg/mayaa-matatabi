package org.seasar.mayaa.matatabi.action;

import org.seasar.mayaa.matatabi.property.MatatabiPropertyPage;

public class JavaToHTMLAction extends OpenHTMLActionBase {
	@Override
	protected void init() {
		baseDir = store.getString(MatatabiPropertyPage.JAVA_SOURCE_PATH)
				+ "/"
				+ store.getString(MatatabiPropertyPage.DEFAULT_PACKAGE)
						.replace('.', '/');
		targetBaseDir = store.getString(MatatabiPropertyPage.WEB_ROOT_PATH);
	}

	@Override
	protected String getResourceName(String baseName) {
		return baseName.substring(0, 1).toLowerCase()
				+ baseName.substring(1, baseName.length() - 6);
	}

	@Override
	protected String getSubDirectory(String packageName) {
		String packageSuffix = store
				.getString(MatatabiPropertyPage.DEFAULT_PACKAGE_SUFFIX);
		if (!"".equals(packageSuffix)) {
			packageName = packageName.substring(0, packageName
					.lastIndexOf(packageSuffix));
		}
		return super.getSubDirectory(packageName);
	}
}
