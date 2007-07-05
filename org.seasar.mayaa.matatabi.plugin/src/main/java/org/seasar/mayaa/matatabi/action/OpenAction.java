package org.seasar.mayaa.matatabi.action;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.seasar.mayaa.matatabi.util.EditorUtil;

/**
 * 同じディレクトリにある違う拡張子のファイルを開くアクション
 */
public class OpenAction implements IObjectActionDelegate, IEditorActionDelegate {
	protected IWorkbenchPart targetPart;

	protected IPath path;

	protected IProject project;

	protected String fileExtension;

	protected IEditorPart openEditorPart;

	/**
	 * Constructor for Action1.
	 */
	public OpenAction(String fileExtension) {
		super();
		this.fileExtension = fileExtension;
	}

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		this.targetPart = targetPart;
	}

	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		this.targetPart = targetEditor;
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
		}

		openEditorPart = EditorUtil.openFile(path, project);
		if (openEditorPart == null) {
			throw new RuntimeException();
		}
		EditorUtil.selectText(EditorUtil.getSelectText(targetPart),
				openEditorPart);
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		if (EditorUtil.hasMatatabiNature()) {
			action.setEnabled(true);
		} else {
			action.setEnabled(false);
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
