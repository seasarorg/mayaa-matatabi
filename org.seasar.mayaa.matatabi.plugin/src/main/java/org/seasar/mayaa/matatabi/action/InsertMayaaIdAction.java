package org.seasar.mayaa.matatabi.action;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.texteditor.ITextEditor;
import org.seasar.mayaa.matatabi.util.EditorUtil;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

/**
 * HTMLファイルの要素にm:idを挿入するアクション
 */
public class InsertMayaaIdAction implements IObjectActionDelegate,
		IEditorActionDelegate {
	protected IWorkbenchPart targetPart;

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
					IStructuredSelection structuredSelection = (IStructuredSelection) selection;

					IDocument document = textEditor.getDocumentProvider()
							.getDocument(textEditor.getEditorInput());
					Object object = structuredSelection.getFirstElement();
					if (object instanceof Element) {
						Element element = (Element) object;
						if (element.hasAttribute("m:id")) {
							return;
						}

						InputDialog dialog = new InputDialog(targetPart
								.getSite().getShell(), "id入力", "idを入力してください",
								"", null);
						if (dialog.open() == Dialog.CANCEL) {
							return;
						}
						String id = dialog.getValue();

						NamedNodeMap attributes = element.getAttributes();
						List<Attr> attributeList = new ArrayList<Attr>();
						for (int i = 0; i < attributes.getLength(); i++) {
							Attr attr = (Attr) attributes.item(i);
							attributeList.add((Attr) attr.cloneNode(false));
						}
						for (Attr attr : attributeList) {
							element.removeAttribute(attr.getName());
						}

						element.setAttribute("m:id", id);
						for (Attr attr : attributeList) {
							element.setAttribute(attr.getName(), attr
									.getValue());

						}

					}
				} catch (MalformedTreeException e) {
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
