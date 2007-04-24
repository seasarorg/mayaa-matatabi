package org.seasar.mayaa.matatabi.editor;

import org.eclipse.swt.events.HelpEvent;
import org.eclipse.swt.events.HelpListener;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.help.WorkbenchHelp;
import org.eclipse.wst.sse.ui.StructuredTextEditor;

/**
 * Mayaaファイルエディター
 * 
 * @author matoba
 */
public class MayaaEditor extends StructuredTextEditor {
	protected void initializeKeyBindingScopes() {
		setKeyBindingScopes(new String[] { "org.seasar.mayaa.matatabi.editor.mayaaEditorScope" });
	}

	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		super.init(site, input);
	}
}
