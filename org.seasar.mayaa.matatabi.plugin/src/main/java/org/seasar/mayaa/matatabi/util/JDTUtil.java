package org.seasar.mayaa.matatabi.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IStorage;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

public class JDTUtil {
	/**
	 * プロジェクトのクラスパスからリソースを取得する。
	 * 
	 * @param project
	 *            プロジェクト
	 * @param packageName
	 *            パッケージ名
	 * @param name
	 *            リソース名
	 * @return 見つかったりソースのリスト
	 */
	public static List<Object> findResources(IProject project,
			String packageName, String name) {
		IJavaProject javaProject = JavaCore.create(project);
		List<Object> list = new ArrayList<Object>();
		IPackageFragmentRoot[] packageFragmentRoots;
		try {
			packageFragmentRoots = javaProject.getPackageFragmentRoots();
		} catch (JavaModelException e) {
			return list;
		}
		for (int i = 0; i < packageFragmentRoots.length; i++) {
			Object[] objects = getResources(packageFragmentRoots[i],
					packageName);
			if (objects == null) {
				continue;
			}

			for (int j = 0; j < objects.length; j++) {
				if (objects[j] instanceof IStorage) {
					if (((IStorage) objects[j]).getName().equals(name)) {
						list.add(objects[j]);
					}
				}
				if (objects[j] instanceof IResource) {
					if (((IResource) objects[j]).getName().equals(name)) {
						list.add(objects[j]);
					}
				}
			}
		}
		return list;
	}

	private static Object[] getResources(
			IPackageFragmentRoot packageFragmentRoot, String packageName) {
		IPackageFragment packageFragment = packageFragmentRoot
				.getPackageFragment(packageName);
		try {
			return packageFragment.getNonJavaResources();
		} catch (JavaModelException e) {
			return null;
		}
	}
}
