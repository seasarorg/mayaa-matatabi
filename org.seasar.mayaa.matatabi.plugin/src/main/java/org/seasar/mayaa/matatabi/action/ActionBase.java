package org.seasar.mayaa.matatabi.action;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.seasar.mayaa.matatabi.util.EditorUtil;

/**
 * �����f�B���N�g���ɂ���Ⴄ�g���q�̃t�@�C�����J���A�N�V����
 */
public abstract class ActionBase implements IObjectActionDelegate,
		IEditorActionDelegate {
	protected IWorkbenchPart targetPart;

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		this.targetPart = targetPart;
	}

	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		this.targetPart = targetEditor;
	}

	public void selectionChanged(IAction action, ISelection selection) {
		if (EditorUtil.hasMatatabiNature()) {
			action.setEnabled(true);
		} else {
			action.setEnabled(false);
		}
	}
}
