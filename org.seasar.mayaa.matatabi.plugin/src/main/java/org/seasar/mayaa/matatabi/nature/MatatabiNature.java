package org.seasar.mayaa.matatabi.nature;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;

public class MatatabiNature implements IProjectNature {
	public static final String NATURE_ID = "org.seasar.mayaa.matatabi.MatatabiNature";

	private IProject project;

	public void configure() throws CoreException {
	}

	public void deconfigure() throws CoreException {
	}

	public IProject getProject() {
		return project;
	}

	public void setProject(IProject project) {
		this.project = project;
	}

}
