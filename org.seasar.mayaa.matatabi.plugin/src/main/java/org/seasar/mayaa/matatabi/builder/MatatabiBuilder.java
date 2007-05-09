package org.seasar.mayaa.matatabi.builder;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

public class MatatabiBuilder extends IncrementalProjectBuilder {

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

	private void fullBuild(IProgressMonitor monitor) throws CoreException {
		getProject().accept(new MatatabiValidator());
	}

	private void incrementalBuild(IProgressMonitor monitor)
			throws CoreException {
		IResourceDelta delta = getDelta(getProject());
		delta.accept(new MatatabiValidator());
	}
}