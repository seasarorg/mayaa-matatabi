package org.seasar.mayaa.matatabi.util;

public class FileUtil {
	public static String toCamelCase(String name, char separator) {
		StringBuilder stringBuilder = new StringBuilder();

		boolean toUppserCase = false;
		for (char charactor : name.toCharArray()) {
			if (toUppserCase) {
				stringBuilder.append(Character.toUpperCase(charactor));
				toUppserCase = false;
			} else if (charactor == separator) {
				toUppserCase = true;
			} else {
				stringBuilder.append(charactor);
			}
		}
		return stringBuilder.toString();
	}

	public static String toSeparated(String name, char separator) {
		StringBuilder stringBuilder = new StringBuilder();
		for (char charactor : name.toCharArray()) {
			if (charactor >= 'A' && charactor <= 'Z') {
				stringBuilder.append(separator);
				stringBuilder.append(Character.toLowerCase(charactor));
			} else {
				stringBuilder.append(charactor);
			}
		}
		return stringBuilder.toString();
	}
}
