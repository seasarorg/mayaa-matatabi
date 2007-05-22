package org.seasar.mayaa.matatabi.util;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.seasar.mayaa.matatabi.MatatabiPlugin;

/**
 * 設定値に関する処理を行います。
 */
public class PreferencesUtil {
	/**
	 * 指定したファイルが属するプロジェクトの設定値を取得します。
	 * 
	 * @param file
	 *            ファイル
	 * @return
	 */
	public static final ScopedPreferenceStore getPreference(IFile file) {
		return new ScopedPreferenceStore(new ProjectScope(file.getProject()),
				MatatabiPlugin.PLUGIN_ID);
	}
}
