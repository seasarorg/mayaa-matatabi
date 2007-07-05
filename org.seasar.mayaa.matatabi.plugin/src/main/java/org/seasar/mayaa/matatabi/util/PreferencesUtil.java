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
		return getPreference(file.getProject());
	}

	public static final ScopedPreferenceStore getPreference(IProject project) {
		return new ScopedPreferenceStore(new ProjectScope(project),
				MatatabiPlugin.PLUGIN_ID);
	}

	/**
	 * �^�O�ϊ����[���̎擾
	 * 
	 * @param store
	 *            IPreferenceStore
	 * @return �^�O�ϊ����[��
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
	 * Mayaa�t�@�C�����O��Ԃ̎擾
	 * 
	 * @param store
	 *            IPreferenceStore
	 * @return Mayaa�t�@�C�����O���
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
