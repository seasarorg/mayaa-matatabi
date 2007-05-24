package org.seasar.mayaa.matatabi.builder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.seasar.mayaa.matatabi.MatatabiPlugin;
import org.seasar.mayaa.matatabi.property.MatatabiPropertyPage;
import org.seasar.mayaa.matatabi.util.PreferencesUtil;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Mayaaファイルの解析し、Validationを行う。
 */
public class MatatabiValidateHandler extends DefaultHandler {
	private static final String MAYAA_NAMESPACE = "http://mayaa.seasar.org"; //$NON-NLS-1$

	private int depth = 0;

	private IFile file;

	private Locator locator;

	private Set sourceid;

	private Set defaultid;

	private Set idlist = new HashSet();

	private ScopedPreferenceStore preferenceStore;

	/**
	 * 
	 * @param file
	 * @param sourceid
	 * @param defaultid
	 */
	public MatatabiValidateHandler(IFile file, Set sourceid, Set defaultid) {
		this.file = file;
		this.sourceid = sourceid;
		this.defaultid = defaultid;
		this.preferenceStore = PreferencesUtil.getPreference(file);
	}

	/**
	 * 
	 */
	public void setDocumentLocator(Locator locator) {
		this.locator = locator;
	}

	/**
	 * 要素開始時の処理
	 * <ul>
	 * <li>id属性の存在チェック</li>
	 * <li>id属性の位置チェック</li>
	 * <li>テンプレートに存在しないid属性のチェック</li>
	 * <li>重複するid属性のチェック</li>
	 * </ul>
	 */
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		depth++;
		String id = getId(attributes, uri);
		if (id == null) {
			if (requiredIdAttribute(uri, localName) && depth == 2
					&& getXpath(attributes, uri) == null) {
				int severty = getSeverty(preferenceStore
						.getString(MatatabiPropertyPage.MISSING_ID_ATTRIBUTE));
				setMarker(
						Messages
								.getString("MatatabiValidator.MISSING_ID_ATTRIBUTE"), locator.getLineNumber(), severty); //$NON-NLS-1$
			}
		} else {
			if (depth != 2) {
				int severty = getSeverty(preferenceStore
						.getString(MatatabiPropertyPage.INVALID_ID_ATTRIBUTE));
				setMarker(
						Messages
								.getString("MatatabiValidator.INVALID_ID_ATTRIBUTE"), locator.getLineNumber(), //$NON-NLS-1$
						severty);
			}
			if (!sourceid.contains(id)
					&& !file.getName().equals("default.mayaa")) {
				int severty = getSeverty(preferenceStore
						.getString(MatatabiPropertyPage.NOTEXIST_ID_ATTRIBUTE));
				setMarker(
						Messages
								.getString("MatatabiValidator.NOTEXIST_ID_ATTRIBUTE"), locator //$NON-NLS-1$
								.getLineNumber(), severty);
			}
			if (idlist.contains(id)) {
				int severty = getSeverty(preferenceStore
						.getString(MatatabiPropertyPage.DUPLICATE_ID_ATTRIBUTE));
				setMarker(
						Messages
								.getString("MatatabiValidator.DUPLICATE_ID_ATTRIBUTE"), locator.getLineNumber(), severty); //$NON-NLS-1$
			}
			idlist.add(id);
		}
	}

	/**
	 * 要素終了時の処理
	 */
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		depth--;
	}

	/**
	 * 解析終了時の処理
	 * <ul>
	 * <li>テンプレートにあって、Mayaaファイルにないid属性のチェック</li>
	 * </ul>
	 */
	public void endDocument() throws SAXException {
		sourceid.removeAll(idlist);
		sourceid.removeAll(defaultid);
		for (Iterator iter = sourceid.iterator(); iter.hasNext();) {
			String id = (String) iter.next();
			int severty = getSeverty(preferenceStore
					.getString(MatatabiPropertyPage.UNDEFINE_ID_ATTRIBUTE));
			setMarker(
					Messages
							.getString(
									"MatatabiValidator.UNDEFINE_ID_ATTRIBUTE", new String[] { id }), 1, severty); //$NON-NLS-1$ //$NON-NLS-2$
		}

		super.endDocument();
	}

	/**
	 * id属性が必要な要素かどうか。
	 * 
	 * @param uri
	 *            要素のNamespaceURI
	 * @param localName
	 *            要素の名前
	 * @return
	 */
	private boolean requiredIdAttribute(String uri, String localName) {
		return !(uri.equals(MAYAA_NAMESPACE) && (localName
				.equals("beforeRender") || localName.equals("afterRender"))); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * id属性を取得する。
	 * 
	 * @param attributes
	 * @param elementUri
	 * @return
	 */
	private String getId(Attributes attributes, String elementUri) {
		return getAttributeValue(attributes, elementUri, "id");
	}

	/**
	 * xpath属性を取得する。
	 * 
	 * @param attributes
	 * @param elementUri
	 * @return
	 */
	private String getXpath(Attributes attributes, String elementUri) {
		return getAttributeValue(attributes, elementUri, "xpath");
	}

	private String getAttributeValue(Attributes attributes, String elementUri,
			String name) {
		String value = attributes.getValue(MAYAA_NAMESPACE, name); //$NON-NLS-1$
		if (value == null && elementUri.equals(MAYAA_NAMESPACE)) {
			value = attributes.getValue(name); //$NON-NLS-1$
		}
		return value;
	}

	/**
	 * マーカーを設定する。
	 * 
	 * @param message
	 * @param lineNumber
	 * @param severty
	 */
	private void setMarker(String message, int lineNumber, int severty) {
		if (severty == -1) {
			return;
		}
		try {
			IMarker marker = file.createMarker(MatatabiPlugin.MARKER_ID);
			Map attributeMap = new HashMap();
			attributeMap.put(IMarker.SEVERITY, Integer.valueOf(severty));
			attributeMap.put(IMarker.MESSAGE, message);
			attributeMap.put(IMarker.LINE_NUMBER, Integer.valueOf(lineNumber));
			marker.setAttributes(attributeMap);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 設定値からエラーレベルを取得する。
	 * 
	 * @param preferenceValue
	 * @return
	 */
	private int getSeverty(String preferenceValue) {
		if (preferenceValue.equals("0")) { //$NON-NLS-1$
			return IMarker.SEVERITY_ERROR;
		} else if (preferenceValue.equals("1")) { //$NON-NLS-1$
			return IMarker.SEVERITY_WARNING;
		} else if (preferenceValue.equals("2")) { //$NON-NLS-1$
			return IMarker.SEVERITY_INFO;
		} else {
			return -1;
		}
	}
}