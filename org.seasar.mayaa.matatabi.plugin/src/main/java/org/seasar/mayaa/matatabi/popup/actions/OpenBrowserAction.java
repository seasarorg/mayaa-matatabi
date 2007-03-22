package org.seasar.mayaa.matatabi.popup.actions;

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
import org.eclipse.ui.internal.browser.DefaultBrowserSupport;

/**
 * 同じディレクトリにある違う拡張子のファイルを開くアクション
 */
public class OpenBrowserAction implements IEditorActionDelegate {
	protected IWorkbenchPart	targetPart;

	protected IFile				file;

	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		this.targetPart = targetEditor;
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		IFile file =
				((IFileEditorInput) ((IEditorPart) targetPart).getEditorInput())
						.getFile();
		try {
			System.out.println(file.getLocationURI().toURL());
			executeBrowser(file.getLocationURI().toURL());
		} catch (MalformedURLException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}

	private void executeBrowser(URL url) {
		try {
			(new DefaultBrowserSupport()).getExternalBrowser().openURL(url);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}
}
