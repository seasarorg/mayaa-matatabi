package org.seasar.mayaa.matatabi.action;


public class HTMLToJavaAction extends OpenJavaActionBase {
	@Override
	protected String getResourceName(String baseName) {
		if (baseName.indexOf("$") > 0) {
			baseName = baseName.substring(0, baseName.indexOf("$"));
		}
		return baseName.substring(0, 1).toUpperCase() + baseName.substring(1)
				+ "Action";
	}
}
