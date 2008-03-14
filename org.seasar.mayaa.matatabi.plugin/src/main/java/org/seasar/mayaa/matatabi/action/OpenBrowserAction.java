package org.seasar.mayaa.matatabi.action;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.seasar.mayaa.matatabi.MatatabiPlugin;

/**
 * 外部ブラウザでファイルを開く
 */
public class OpenBrowserAction implements IEditorActionDelegate {
	protected IWorkbenchPart targetPart;

	protected IFile file;

	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		this.targetPart = targetEditor;
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		IFile targetFile = ((IFileEditorInput) ((IEditorPart) targetPart)
				.getEditorInput()).getFile();
		try {
			System.out.println(targetFile.getLocationURI().toURL());
			executeBrowser(targetFile.getLocationURI().toURL());
		} catch (MalformedURLException e) {
			MatatabiPlugin.errorLog(e);
		}
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		// no-op
	}

	private void executeBrowser(URL url) {
		try {
			targetPart.getSite().getWorkbenchWindow().getWorkbench()
					.getBrowserSupport().getExternalBrowser().openURL(url);
		} catch (PartInitException e) {
			MatatabiPlugin.errorLog(e);
		}
	}
}
