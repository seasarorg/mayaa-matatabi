package org.seasar.mayaa.matatabi.builder;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Messages {
	private static final String BUNDLE_NAME = "org.seasar.mayaa.matatabi.builder.messages"; //$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(BUNDLE_NAME);

	private Messages() {
		// no-op
	}

	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}

	public static String getString(String key, String[] replace) {
		try {
			return MessageFormat.format(RESOURCE_BUNDLE.getString(key),
					(Object[]) replace);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
