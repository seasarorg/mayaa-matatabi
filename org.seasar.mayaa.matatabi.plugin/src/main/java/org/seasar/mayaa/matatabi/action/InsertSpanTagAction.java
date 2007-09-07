package org.seasar.mayaa.matatabi.action;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.texteditor.ITextEditor;
import org.seasar.mayaa.matatabi.util.EditorUtil;

/**
 * HTMLファイルに<span m:id="xxxx"></span>を挿入するアクション
 */
public class InsertSpanTagAction implements IObjectActionDelegate,
		IEditorActionDelegate {
	protected IWorkbenchPart targetPart;

	protected IPath path;

	protected IProject project;

	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		this.targetPart = targetEditor;
	}

	public void run(IAction action) {
		if (EditorUtil.hasMatatabiNature()) {
			if (targetPart instanceof ITextEditor) {
				try {
					ITextEditor textEditor = (ITextEditor) targetPart;
					ISelectionProvider selectionProvider = textEditor
							.getSelectionProvider();
					ISelection selection = selectionProvider.getSelection();
					ITextSelection textSelection = (ITextSelection) selection;
					int offset = textSelection.getOffset();

					InputDialog dialog = new InputDialog(targetPart.getSite()
							.getShell(), "mayaa id入力", "mayaa idを入力してください", "",
							null);
					dialog.open();
					String id = dialog.getValue();

					IDocument document = textEditor.getDocumentProvider()
							.getDocument(textEditor.getEditorInput());
					ReplaceEdit replaceEdit = new ReplaceEdit(offset,
							textSelection.getLength(), "<span m:id=\"" + id
									+ "\">" + textSelection.getText()
									+ "</span>");

					MultiTextEdit multiTextEdit = new MultiTextEdit();
					multiTextEdit.addChild(replaceEdit);
					multiTextEdit.apply(document);
				} catch (MalformedTreeException e) {
					e.printStackTrace();
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
			}
		}
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
	}

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		this.targetPart = targetPart;
	}
}
