package org.seasar.mayaa.matatabi.util;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.seasar.mayaa.matatabi.MatatabiPlugin;

/**
 * �ݒ�l�Ɋւ��鏈�����s���܂��B
 */
public class PreferencesUtil {
	/**
	 * �w�肵���t�@�C����������v���W�F�N�g�̐ݒ�l���擾���܂��B
	 * 
	 * @param file
	 *            �t�@�C��
	 * @return
	 */
	public static final ScopedPreferenceStore getPreference(IFile file) {
		return new ScopedPreferenceStore(new ProjectScope(file.getProject()),
				MatatabiPlugin.PLUGIN_ID);
	}
}
