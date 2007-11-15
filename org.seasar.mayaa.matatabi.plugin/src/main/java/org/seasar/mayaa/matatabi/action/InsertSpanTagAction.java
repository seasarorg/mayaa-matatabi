package org.seasar.mayaa.matatabi.action;

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
import org.eclipse.ui.texteditor.ITextEditor;
import org.seasar.mayaa.matatabi.MatatabiPlugin;
import org.seasar.mayaa.matatabi.util.EditorUtil;

/**
 * HTMLファイルに<span m:id="xxxx"></span>を挿入するアクション
 */
public class InsertSpanTagAction extends ActionBase {
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
					MatatabiPlugin.errorLog(e);
				} catch (BadLocationException e) {
					MatatabiPlugin.errorLog(e);
				}
			}
		}
	}
}
