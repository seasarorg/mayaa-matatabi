package org.seasar.mayaa.matatabi.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.seasar.mayaa.matatabi.MatatabiPlugin;
import org.seasar.mayaa.matatabi.property.MatatabiPropertyPage;
import org.seasar.mayaa.matatabi.property.NamespaceTableViewer.Namespace;
import org.seasar.mayaa.matatabi.property.ReplaceRuleTableViewer.ReplaceRule;

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
		return getPreference(file.getProject());
	}

	public static final ScopedPreferenceStore getPreference(IProject project) {
		return new ScopedPreferenceStore(new ProjectScope(project),
				MatatabiPlugin.PLUGIN_ID);
	}

	/**
	 * タグ変換ルールの取得
	 * 
	 * @param store
	 *            IPreferenceStore
	 * @return タグ変換ルール
	 */
	public static Map<String, ReplaceRule> getReplaceRules(
			IPreferenceStore store) {
		Map<String, ReplaceRule> replacerules = new LinkedHashMap<String, ReplaceRule>();
		for (int i = 0;; i++) {
			String replaceRuleString = store
					.getString(MatatabiPropertyPage.REPLACE_RULE + "." + i);
			if (!replaceRuleString.equals("")) {
				ReplaceRule replaceRule = new ReplaceRule(replaceRuleString);
				replacerules.put(replaceRule.getTag().toLowerCase(),
						replaceRule);
			} else {
				break;
			}
		}
		return replacerules;
	}

	/**
	 * Mayaaファイル名前空間の取得
	 * 
	 * @param store
	 *            IPreferenceStore
	 * @return Mayaaファイル名前空間
	 */
	public static List<Namespace> getNamespaces(IPreferenceStore store) {
		List<Namespace> namespaces = new ArrayList<Namespace>();
		for (int i = 0;; i++) {
			String namespaceString = store
					.getString(MatatabiPropertyPage.NAMESPACES + "." + i);
			if (!namespaceString.equals("")) {
				namespaces.add(new Namespace(namespaceString));
			} else {
				break;
			}
		}
		return namespaces;
	}

}
