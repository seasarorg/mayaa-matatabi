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
import org.seasar.mayaa.matatabi.util.EditorUtil;

/**
 * HTML�t�@�C����<span m:id="xxxx"></span>��}������A�N�V����
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
							.getShell(), "mayaa id����", "mayaa id����͂��Ă�������", "",
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
}
