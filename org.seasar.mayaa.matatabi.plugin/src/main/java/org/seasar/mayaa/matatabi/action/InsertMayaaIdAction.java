package org.seasar.mayaa.matatabi.action;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.ui.texteditor.ITextEditor;
import org.seasar.mayaa.matatabi.MatatabiPlugin;
import org.seasar.mayaa.matatabi.util.EditorUtil;
import org.seasar.mayaa.matatabi.util.ParseUtil;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

/**
 * HTMLファイルの要素にm:idを挿入するアクション
 */
public class InsertMayaaIdAction extends ActionBase {
	public void run(IAction action) {
		if (EditorUtil.hasMatatabiNature()) {
			if (targetPart instanceof ITextEditor) {
				try {
					ITextEditor textEditor = (ITextEditor) targetPart;
					ISelectionProvider selectionProvider = textEditor
							.getSelectionProvider();
					ISelection selection = selectionProvider.getSelection();
					IStructuredSelection structuredSelection = (IStructuredSelection) selection;

					Object object = structuredSelection.getFirstElement();
					if (object instanceof Element) {
						Element element = (Element) object;
						if (ParseUtil.getAttributeValue(element,
								MatatabiPlugin.XMLNS_MAYAA, "id") != null) {
							return;
						}

						InputDialog dialog = new InputDialog(targetPart
								.getSite().getShell(), "mayaa id入力",
								"mayaa idを入力してください", "", null);
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
					MatatabiPlugin.errorLog(e);
				}
			}
		}
	}
}
