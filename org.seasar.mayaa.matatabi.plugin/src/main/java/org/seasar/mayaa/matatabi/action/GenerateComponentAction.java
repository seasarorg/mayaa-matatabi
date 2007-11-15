package org.seasar.mayaa.matatabi.action;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
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
import org.seasar.mayaa.matatabi.MatatabiPlugin;
import org.seasar.mayaa.matatabi.property.MatatabiPropertyPage;
import org.seasar.mayaa.matatabi.util.EditorUtil;
import org.seasar.mayaa.matatabi.util.GenerateUtil;
import org.seasar.mayaa.matatabi.util.ParseUtil;
import org.seasar.mayaa.matatabi.util.PreferencesUtil;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

/**
 * コンポーネントの一括生成を行う。
 */
public class GenerateComponentAction extends OpenActionBase {
	public GenerateComponentAction() {
		super("mayaa");
	}

	@Override
	protected void init() {
		baseDir = store.getString(MatatabiPropertyPage.WEB_ROOT_PATH);
		targetBaseDir = baseDir;
	}

	@Override
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
			Map<String, Element> sourceid = ParseUtil.getIdList(openFile);

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
					Map<String, Element> outid = new LinkedHashMap<String, Element>();
					try {
						outid = ParseUtil.getXmlIdList(new InputSource(
								new ByteArrayInputStream(document.get()
										.getBytes("UTF-8"))));
					} catch (UnsupportedEncodingException e) {
						MatatabiPlugin.errorLog(e);
					}
					for (String id : outid.keySet()) {
						sourceid.remove(id);
					}
					for (String id : ParseUtil.getDefaultIdList(
							file.getParent()).keySet()) {
						sourceid.remove(id);
					}

					InsertEdit insertEdit = new InsertEdit(offset, GenerateUtil
							.genereteTags(sourceid, PreferencesUtil
									.getPreference(file)));

					MultiTextEdit multiTextEdit = new MultiTextEdit();
					multiTextEdit.addChild(insertEdit);
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
