package org.seasar.mayaa.matatabi.action;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.seasar.mayaa.matatabi.MatatabiPlugin;
import org.seasar.mayaa.matatabi.util.EditorUtil;
import org.seasar.mayaa.matatabi.util.PreferencesUtil;

public abstract class OpenActionBase extends ActionBase {
	protected IPath path;

	protected IProject project;

	protected IStructuredSelection selection;

	protected IEditorPart openEditorPart;
	protected ScopedPreferenceStore store = null;

	protected String baseDir;
	protected String targetBaseDir;
	protected String baseName;
	protected String subDirectory;
	private String targetExtension;

	/**
	 * Constructor for Action1.
	 */
	public OpenActionBase(String targetExtension) {
		super();
		this.targetExtension = targetExtension;
	}

	protected abstract void init();

	protected String getResourceName(String baseName) {
		return baseName;
	}

	protected String getSubDirectory(String packageName) {
		return packageName.replace('.', '/');
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		if (path == null && targetPart instanceof IEditorPart) {
			IFile file = ((IFileEditorInput) ((IEditorPart) targetPart)
					.getEditorInput()).getFile();
			path = file.getProjectRelativePath();
			project = file.getProject();
			store = PreferencesUtil.getPreference(file);
		} else {
			store = PreferencesUtil.getPreference(project);
		}

		init();
		if (path.toString().startsWith(baseDir)) {
			IFile openFile = getTargetFile();
			if (openFile == null || !openFile.exists()) {
				return;
			}
			try {
				openEditorPart = EditorUtil.openFile(openFile);
			} catch (PartInitException e) {
				MatatabiPlugin.errorLog(e);
			}
		}
	}

	protected IFile getTargetFile() {
		String[] filePath = path.toString().substring(baseDir.length()).split(
				"/");
		StringBuilder className = new StringBuilder("");
		for (int i = 0; i < filePath.length - 1; i++) {
			if (!filePath[i].equals("")) {
				className.append("." + filePath[i]);
			}
		}

		baseName = filePath[filePath.length - 1].substring(0,
				filePath[filePath.length - 1].indexOf("."));
		subDirectory = getSubDirectory(className.toString());
		return project.getFile(targetBaseDir + "/" + subDirectory + "/"
				+ getResourceName(baseName) + "." + targetExtension);
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		super.selectionChanged(action, selection);
		if (selection instanceof IStructuredSelection) {
			this.selection = (IStructuredSelection) selection;
		}
		if (selection instanceof StructuredSelection) {
			StructuredSelection ss = (StructuredSelection) selection;
			Object obj = ss.getFirstElement();
			if (obj instanceof IFile) {
				path = ((IFile) obj).getProjectRelativePath();
				project = ((IFile) obj).getProject();
			}
		} else {
			path = null;
			project = null;
		}
	}
}
