package org.seasar.mayaa.matatabi.editor;

import org.eclipse.wst.sse.ui.StructuredTextEditor;

public class MayaaEditor extends StructuredTextEditor {
	public boolean isSaveAsAllowed() {
		return true;
	}

	protected void initializeKeyBindingScopes() {
		setKeyBindingScopes(new String[] { "org.seasar.mayaa.matatabi.editor.mayaaEditorScope" });
	}
}
