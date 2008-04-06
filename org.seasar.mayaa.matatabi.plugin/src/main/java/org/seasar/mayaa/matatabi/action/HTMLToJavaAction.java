package org.seasar.mayaa.matatabi.action;

import org.seasar.mayaa.matatabi.util.FileUtil;

public class HTMLToJavaAction extends OpenJavaActionBase {
	@Override
	protected String[] getResourceNames(String baseName) {
		if (baseName.indexOf("$") > 0) {
			baseName = baseName.substring(0, baseName.indexOf("$"));
		}

		if (baseName.indexOf('-') > 0) {
			baseName = FileUtil.toCamelCase(baseName, '-');
		} else if (baseName.indexOf('_') > 0) {
			baseName = FileUtil.toCamelCase(baseName, '_');
		}
		String resourceName = baseName.substring(0, 1).toUpperCase()
				+ baseName.substring(1) + javaClassSuffix;

		return new String[] { resourceName };
	}
}
