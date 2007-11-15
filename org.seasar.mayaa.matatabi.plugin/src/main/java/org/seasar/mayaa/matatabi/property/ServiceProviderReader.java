package org.seasar.mayaa.matatabi.property;

import org.eclipse.core.resources.IProject;
import org.seasar.mayaa.matatabi.MatatabiPlugin;
import org.seasar.mayaa.matatabi.util.JDTUtil;

/**
 * プロジェクトのServiceProviderの設定を読み込む。
 */
public class ServiceProviderReader {
	Class providerUtil;

	public ServiceProviderReader(IProject project) {
		ClassLoader classLoader;
		try {
			classLoader = JDTUtil.createProjectClassLoader(project);
			providerUtil = classLoader
					.loadClass("org.seasar.mayaa.impl.provider.ProviderUtil");
		} catch (Exception e) {
			MatatabiPlugin.errorLog(e);
		}
	}
}
