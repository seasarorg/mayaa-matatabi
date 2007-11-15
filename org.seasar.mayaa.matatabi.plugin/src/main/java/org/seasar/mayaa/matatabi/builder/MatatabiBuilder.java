package org.seasar.mayaa.matatabi.builder;

import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.seasar.mayaa.matatabi.MatatabiPlugin;

/**
 * MayaaファイルのValidationを行うビルダー
 */
public class MatatabiBuilder extends IncrementalProjectBuilder {

	/**
	 * ビルドを行う。
	 * 
	 * @param kind
	 *            ビルドの種類
	 * @param args
	 *            引数
	 * @param monitor
	 *            モニター
	 */
	@Override
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor)
			throws CoreException {
		switch (kind) {
		case AUTO_BUILD:
		case INCREMENTAL_BUILD:
			incrementalBuild(monitor);
			break;
		case FULL_BUILD:
			fullBuild(monitor);
			break;
		default:
			break;
		}
		return null;
	}

	/**
	 * フルビルドを行う。
	 * 
	 * @param monitor
	 * @throws CoreException
	 */
	private void fullBuild(IProgressMonitor monitor) throws CoreException {
		getProject().deleteMarkers(MatatabiPlugin.MARKER_ID, false,
				IResource.DEPTH_INFINITE);
		getProject().accept(new MatatabiValidator());
	}

	/**
	 * インクリメンタルビルドを行う。
	 * 
	 * @param monitor
	 * @throws CoreException
	 */
	private void incrementalBuild(IProgressMonitor monitor)
			throws CoreException {
		IResourceDelta delta = getDelta(getProject());
		delta.accept(new MatatabiValidator());
	}
}