package org.seasar.mayaa.matatabi.editor;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.wst.sse.ui.StructuredTextEditor;

/**
 * Mayaaファイルエディター
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
