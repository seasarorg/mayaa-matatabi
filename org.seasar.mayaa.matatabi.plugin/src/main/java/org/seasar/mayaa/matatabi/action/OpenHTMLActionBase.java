package org.seasar.mayaa.matatabi.action;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.dialogs.ListDialog;
import org.seasar.mayaa.matatabi.MatatabiPlugin;

public abstract class OpenHTMLActionBase extends OpenActionBase {
	/**
	 * Constructor for Action1.
	 */
	public OpenHTMLActionBase() {
		super("html");
	}

	@Override
	protected IFile getTargetFile() {
		IFile file = super.getTargetFile();
		List<IFile> targetFileList = new ArrayList<IFile>();
		if (file.exists()) {
			targetFileList.add(file);
		}

		String prefix = file.getName()
				.substring(0, file.getName().indexOf("."))
				+ "$";
		if (file.getParent() instanceof IFolder) {
			IFolder folder = (IFolder) file.getParent();
			try {
				for (IResource resource : folder.members()) {
					if (resource instanceof IFile
							&& resource.getName().startsWith(prefix)) {
						targetFileList.add((IFile) resource);
					}
				}
			} catch (CoreException e) {
				MatatabiPlugin.errorLog(e);
			}
		}

		if (targetFileList.size() == 1) {
			return targetFileList.get(0);
		}

		ListDialog dialog = new ListDialog(MatatabiPlugin.getShell());
		dialog.setMessage("複数のテンプレートファイルが見つかりました。開くファイルを選んでください。");
		dialog.setContentProvider(new IStructuredContentProvider() {
			public void dispose() {
				// no-op
			}

			public Object[] getElements(Object inputElement) {
				return ((List<IFile>) inputElement).toArray();
			}

			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {
				// no-op
			}
		});
		dialog.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return ((IFile) element).getName();
			}
		});
		dialog.setInput(targetFileList);
		dialog.setTitle("HTMLファイルを開く");
		if (dialog.open() == Window.OK) {
			Object[] result = dialog.getResult();
			if (result.length == 1) {
				return (IFile) result[0];
			}
		}

		return null;
	}
}
