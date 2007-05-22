package org.seasar.mayaa.matatabi.nature;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.seasar.mayaa.matatabi.MatatabiPlugin;

public class MatatabiNature implements IProjectNature {
	public static final String NATURE_ID = "org.seasar.mayaa.matatabi.MatatabiNature";

	public static final String BUILDER_ID = "org.seasar.mayaa.matatabi.MatatabiBuilder";

	private IProject project;

	public void configure() throws CoreException {
		setBuilder();
	}

	public void deconfigure() throws CoreException {
		removeBuilder();
		project.deleteMarkers(MatatabiPlugin.MARKER_ID, false,
				IResource.DEPTH_INFINITE);
	}

	public IProject getProject() {
		return project;
	}

	public void setProject(IProject project) {
		this.project = project;
	}

	/**
	 * Builder‚Ì“o˜^
	 * 
	 * @throws CoreException
	 */
	private void setBuilder() throws CoreException {
		IProjectDescription description = project.getDescription();
		ICommand[] commands = description.getBuildSpec();
		for (int i = 0; i < commands.length; i++) {
			if (commands[i].getBuilderName().equals(BUILDER_ID)) {
				return; // ƒrƒ‹ƒ_[“o˜^‚ÌƒLƒƒƒ“ƒZƒ‹
			}
		}
		ICommand command = description.newCommand();
		command.setBuilderName(BUILDER_ID);
		ICommand[] newCommands = new ICommand[commands.length + 1];
		System.arraycopy(commands, 0, newCommands, 0, commands.length);
		newCommands[commands.length] = command;
		description.setBuildSpec(newCommands);
		project.setDescription(description, null);
	}

	/**
	 * Builder‚Ì“o˜^‰ðœ
	 * 
	 * @throws CoreException
	 */
	private void removeBuilder() throws CoreException {
		IProjectDescription description = project.getDescription();
		ICommand[] commands = description.getBuildSpec();
		for (int i = 0; i < commands.length; i++) {
			if (commands[i].getBuilderName().equals(BUILDER_ID)) {
				ICommand[] newCommands = new ICommand[commands.length - 1];
				System.arraycopy(commands, 0, newCommands, 0, i);
				System.arraycopy(commands, i + 1, newCommands, i,
						commands.length - i - 1);
				description.setBuildSpec(newCommands);
				project.setDescription(description, null);
				return; // or break;
			}
		}
	}
}