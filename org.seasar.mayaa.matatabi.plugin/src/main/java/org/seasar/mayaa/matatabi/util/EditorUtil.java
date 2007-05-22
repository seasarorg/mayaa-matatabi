package org.seasar.mayaa.matatabi.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.WorkbenchWindow;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.texteditor.ITextEditor;
import org.seasar.mayaa.matatabi.MatatabiPlugin;
import org.seasar.mayaa.matatabi.nature.MatatabiNature;

/**
 * エディタに関する処理を行う
 * 
 * @author matoba
 */
public class EditorUtil {

	public static IEditorPart openFile(IPath path, IProject project) {

		String fileExtension = getTemplateFileExtension(project);

		String extension = path.getFileExtension().equals(fileExtension) ? "html"
				: fileExtension;

		String fileName = path.toString();
		fileName = fileName.substring(0, fileName.length()
				- path.getFileExtension().length())
				+ extension;
		IFile openFile = project.getFile(fileName);
		if (!openFile.exists()) {
			return null;
		}

		try {
			IEditorDescriptor descriptor = IDE.getEditorDescriptor(openFile);
			return IDE.openEditor(PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getActivePage(), openFile,
					descriptor.getId());
		} catch (PartInitException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static final String getSelectText(IWorkbenchPart targetPart) {
		String id = null;
		if (targetPart instanceof ITextEditor) {
			ITextEditor textEditor = (ITextEditor) targetPart;
			ITextSelection textSelection = (ITextSelection) textEditor
					.getSelectionProvider().getSelection();
			if (textSelection.getLength() > 0) {
				id = textSelection.getText();
			}
		}
		return id;
	}

	public static final void selectText(String id, IEditorPart openEditorPart) {
		if (openEditorPart instanceof MultiPageEditorPart) {
			openEditorPart = EditorUtil
					.getSourceEditor((MultiPageEditorPart) openEditorPart);
		}
		if (openEditorPart instanceof ITextEditor) {
			if (id != null) {
				ITextEditor openTextEditor = (ITextEditor) openEditorPart;
				IDocument document = openTextEditor.getDocumentProvider()
						.getDocument(openTextEditor.getEditorInput());
				int offset = document.get().indexOf("id=\"" + id + "\"");
				if (offset > 0) {
					openTextEditor.getSelectionProvider().setSelection(
							new TextSelection(offset + 4, id.length()));
				}
			}
		}

	}

	public static IEditorPart getSourceEditor(
			final MultiPageEditorPart multiPageEditorPart) {
		try {
			final Method method = MultiPageEditorPart.class.getDeclaredMethod(
					"getPageCount", new Class[] {});
			method.setAccessible(true);
			Object pageCountObject = AccessController
					.doPrivileged(new PrivilegedAction() {
						public Object run() {
							try {
								return method.invoke(multiPageEditorPart,
										new Object[] {});
							} catch (IllegalArgumentException e) {
								e.printStackTrace();
							} catch (IllegalAccessException e) {
								e.printStackTrace();
							} catch (InvocationTargetException e) {
								e.printStackTrace();
							}
							return null;
						}
					});
			Method getEditorMethod = MultiPageEditorPart.class
					.getDeclaredMethod("getEditor", new Class[] { int.class });
			getEditorMethod.setAccessible(true);
			int pageCount = ((Integer) pageCountObject).intValue();
			for (int i = 0; i <= pageCount; i++) {
				Object editor = getEditorMethod.invoke(multiPageEditorPart,
						new Object[] { new Integer(i) });
				if (editor instanceof ITextEditor) {
					return (IEditorPart) editor;
				}
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static IEditorPart openFile() {
		IFile file = getActiveFile();
		IPath path = file.getProjectRelativePath();
		IProject project = file.getProject();

		return openFile(path, project);
	}

	public static IFile getActiveFile() {
		IEditorInput editorInput = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().getActiveEditor()
				.getEditorInput();
		if (editorInput instanceof IFileEditorInput) {
			return ((IFileEditorInput) (editorInput)).getFile();
		}
		return null;
	}

	public static IProgressMonitor getProgressMonitor() {
		WorkbenchWindow workbenchWindow = (WorkbenchWindow) PlatformUI
				.getWorkbench().getActiveWorkbenchWindow();
		IActionBars bars = workbenchWindow.getActionBars();
		IStatusLineManager lineManager = bars.getStatusLineManager();
		return lineManager.getProgressMonitor();
	}

	public static String getTemplateFileExtension(IProject project) {
		String fileExtension = MatatabiPlugin.getStoreValue(project,
				"fileExtension");
		if (fileExtension == null || fileExtension.equals("")) {
			fileExtension = "mayaa";
		}
		return fileExtension;
	}

	/**
	 * 開いているファイルが属するプロジェクトにMatatabiNatureが設定されているかどうか。
	 * 
	 * @return
	 */
	public static boolean hasMatatabiNature() {
		IProject project = getActiveFile().getProject();
		if (project == null) {
			return false;
		}

		try {
			return project.hasNature(MatatabiNature.NATURE_ID);
		} catch (CoreException e) {
			return false;
		}
	}
}
