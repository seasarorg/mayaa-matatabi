package org.seasar.mayaa.matatabi.popup.actions;

import java.io.ByteArrayInputStream;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.ui.texteditor.ITextEditor;
import org.seasar.mayaa.matatabi.util.EditorUtil;
import org.seasar.mayaa.matatabi.util.GenerateUtil;
import org.seasar.mayaa.matatabi.util.ParseUtil;
import org.w3c.dom.NamedNodeMap;
import org.xml.sax.InputSource;

/**
 * コンポーネントの一括生成を行う。
 * 
 * @author matoba
 * 
 */
public class GenerateComponentAction extends OpenAction {
	public GenerateComponentAction() {
		super("mayaa");
	}

	public void run(IAction action) {
		if (EditorUtil.hasMatatabiNature()) {
			IFile file = EditorUtil.getActiveFile();
			path = file.getProjectRelativePath();
			project = file.getProject();

			String fileName = path.toString();
			fileName = fileName.substring(0, fileName.length()
					- path.getFileExtension().length())
					+ "html";
			IFile openFile = project.getFile(fileName);
			Set sourceid = ParseUtil.getIdList(openFile);

			if (targetPart instanceof ITextEditor) {
				try {
					ITextEditor textEditor = (ITextEditor) targetPart;

					ISelectionProvider selectionProvider = textEditor
							.getSelectionProvider();
					ISelection selection = selectionProvider.getSelection();
					ITextSelection textSelection = (ITextSelection) selection;
					int offset = textSelection.getOffset();

					IDocument document = textEditor.getDocumentProvider()
							.getDocument(textEditor.getEditorInput());
					Set outid = ParseUtil
							.getIdList(new InputSource(
									new ByteArrayInputStream(document.get()
											.getBytes())));
					sourceid.removeAll(outid);
					sourceid.removeAll(ParseUtil
							.getDefaultIdList((IFolder) file.getParent()));

					InsertEdit insertEdit = new InsertEdit(offset, GenerateUtil
							.genereteTags(sourceid));

					MultiTextEdit multiTextEdit = new MultiTextEdit();
					multiTextEdit.addChild(insertEdit);
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
