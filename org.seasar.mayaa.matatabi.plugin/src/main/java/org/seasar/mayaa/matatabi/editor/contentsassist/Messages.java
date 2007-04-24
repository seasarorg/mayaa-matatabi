package org.seasar.mayaa.matatabi.editor.contentsassist;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * ���b�Z�[�W�Ǘ�
 * 
 * @author matoba
 */
public class Messages {
	private static final String BUNDLE_NAME = "org.seasar.mayaa.matatabi.editor.contentsassist.messages"; //$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(BUNDLE_NAME);

	private Messages() {
	}

	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
