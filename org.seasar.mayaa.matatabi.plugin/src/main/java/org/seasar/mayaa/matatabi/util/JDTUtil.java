package org.seasar.mayaa.matatabi.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
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

	/**
	 * Javaプロジェクトからクラスローダーを作成する。
	 * 
	 * @param project
	 *            Javaプロジェクト
	 * @return Javaプロジェクトの設定を使用したクラスローダー
	 * @throws JavaModelException
	 */
	public static ClassLoader createProjectClassLoader(IProject project)
			throws JavaModelException {
		IJavaProject javaProject = JavaCore.create(project);
		return new URLClassLoader(getClasspathAsURLArray(javaProject));
	}

	public static URL[] getClasspathAsURLArray(IJavaProject javaProject)
			throws JavaModelException {
		if (javaProject == null)
			return null;
		Set<IJavaProject> visited = new HashSet<IJavaProject>();
		List<URL> urls = new ArrayList<URL>(20);
		collectClasspathURLs(javaProject, urls, visited, true);
		URL[] result = new URL[urls.size()];
		urls.toArray(result);
		return result;
	}

	private static void collectClasspathURLs(IJavaProject javaProject,
			List<URL> urls, Set<IJavaProject> visited, boolean isFirstProject)
			throws JavaModelException {
		if (visited.contains(javaProject))
			return;
		visited.add(javaProject);
		IPath outPath = javaProject.getOutputLocation().makeAbsolute();
		outPath = outPath.addTrailingSeparator();
		URL out = createFileURL(outPath);
		urls.add(out);
		IClasspathEntry[] entries = null;
		try {
			entries = javaProject.getResolvedClasspath(true);
		} catch (JavaModelException e) {
			return;
		}
		IClasspathEntry entry;
		for (int i = 0; i < entries.length; i++) {
			entry = entries[i];
			switch (entry.getEntryKind()) {
			case IClasspathEntry.CPE_LIBRARY:
			case IClasspathEntry.CPE_CONTAINER:
			case IClasspathEntry.CPE_VARIABLE:
				collectClasspathEntryURL(entry, urls);
				break;
			case IClasspathEntry.CPE_PROJECT: {
				if (isFirstProject || entry.isExported())

					collectClasspathURLs(getJavaProject(entry), urls, visited,
							false);

				break;
			}
			}
		}
	}

	private static URL createFileURL(IPath path) {
		URL url = null;
		try {
			url = path.toFile().toURI().toURL();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return url;
	}

	private static void collectClasspathEntryURL(IClasspathEntry entry,
			List<URL> urls) {
		URL url = createFileURL(entry.getPath());
		if (url != null)
			urls.add(url);
	}

	private static IJavaProject getJavaProject(IClasspathEntry entry) {
		IProject proj = ResourcesPlugin.getWorkspace().getRoot().getProject(
				entry.getPath().segment(0));
		if (proj != null)
			return JavaCore.create(proj);
		return null;
	}
}
