package org.seasar.mayaa.matatabi.action;

import java.util.ArrayList;
import java.util.List;

import org.seasar.mayaa.matatabi.property.MatatabiPropertyPage;
import org.seasar.mayaa.matatabi.util.FileUtil;

public class JavaToMayaaAction extends OpenMayaaActionBase {
	@Override
	protected void init() {
		baseDir = store.getString(MatatabiPropertyPage.JAVA_SOURCE_PATH)
				+ "/"
				+ store.getString(MatatabiPropertyPage.DEFAULT_PACKAGE)
						.replace('.', '/');
		targetBaseDir = store.getString(MatatabiPropertyPage.WEB_ROOT_PATH);
	}

	@Override
	protected String[] getResourceNames(String baseName) {
		List<String> resourceNames = new ArrayList<String>();
		String resurceName = baseName.substring(0, 1).toLowerCase()
				+ baseName.substring(1, baseName.length() - 6);
		resourceNames.add(resurceName);

		resourceNames.add(FileUtil.toSeparated(resurceName, '_'));
		resourceNames.add(FileUtil.toSeparated(resurceName, '-'));

		return resourceNames.toArray(new String[0]);
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
