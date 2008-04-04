package org.seasar.mayaa.matatabi.action;


public class MayaaToJavaAction extends OpenJavaActionBase {
	@Override
	protected String getResourceName(String baseName) {
		return baseName.substring(0, 1).toUpperCase() + baseName.substring(1)
				+ "Action";
	}
}
