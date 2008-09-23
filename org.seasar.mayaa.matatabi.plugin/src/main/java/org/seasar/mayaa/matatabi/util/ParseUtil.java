package org.seasar.mayaa.matatabi.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.cyberneko.html.parsers.DOMParser;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.seasar.mayaa.matatabi.MatatabiPlugin;
import org.seasar.mayaa.matatabi.property.MatatabiPropertyPage;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class ParseUtil {
	private static List<String> htmlNamespaces = Arrays.asList(new String[] {
			"", "http://mayaa.seasar.org", "http://www.w3.org/TR/html4",
			"http://www.w3.org/1999/xhtml" });

	private static List<String> mayaaNamespaces = Arrays
			.asList(new String[] { "http://mayaa.seasar.org" });

	/**
	 * default.mayaaに定義してあるidを取得します。
	 * 
	 * @param folder
	 * @return
	 */
	public static Map<String, Element> getDefaultIdList(IContainer container) {
		IProject project = container.getProject();
		IPath path = container.getProjectRelativePath();
		IFile defaultMayaa = project.getFile(path.toString() + File.separator
				+ "default.mayaa");
		if (!defaultMayaa.exists()) {
			if (container.getParent() instanceof IFolder) {
				return getDefaultIdList(container.getParent());
			}
		} else {
			try {
				return getXmlIdList(new InputSource(defaultMayaa.getContents()));
			} catch (CoreException e) {
				MatatabiPlugin.errorLog(e);
			}
		}
		return new LinkedHashMap<String, Element>();
	}

	public static Map<String, Element> getXmlIdList(IFile file) {
		try {
			return getXmlIdList(new InputSource(file.getContents()));
		} catch (CoreException e) {
			MatatabiPlugin.errorLog(e);
		}
		return new LinkedHashMap<String, Element>();
	}

	public static Map<String, Element> getXmlIdList(InputSource inputSource) {
		org.apache.xerces.parsers.DOMParser parser = new org.apache.xerces.parsers.DOMParser();

		Map<String, Element> idlist = new LinkedHashMap<String, Element>();
		try {
			parser.parse(inputSource);
			Document document = parser.getDocument();
			traverse(idlist, document.getDocumentElement(),
					getMayaaNamespaces());
		} catch (SAXException e) {
			MatatabiPlugin.errorLog(e);
		} catch (IOException e) {
			MatatabiPlugin.errorLog(e);
		}
		return idlist;
	}

	public static Map<String, Element> getIdList(IFile file) {
		try {
			List<IFile> fileList = new ArrayList<IFile>();
			if (file.exists()) {
				fileList.add(file);
			}

			String prefix = file.getName().substring(0,
					file.getName().indexOf("."))
					+ "$";
			if (file.getParent() instanceof IFolder) {
				IFolder folder = (IFolder) file.getParent();
				for (IResource resource : folder.members()) {
					if (resource instanceof IFile
							&& resource.getName().startsWith(prefix)) {
						fileList.add((IFile) resource);
					}
				}
			}

			DOMParser parser = new DOMParser();
			try {
				parser.setProperty(
						"http://cyberneko.org/html/properties/names/attrs",
						"default");
				parser.setProperty(
						"http://cyberneko.org/html/properties/names/elems",
						"match");
			} catch (SAXException e) {
				MatatabiPlugin.errorLog(e);
			}

			Map<String, Element> idlist = new LinkedHashMap<String, Element>();
			for (IFile targetFile : fileList) {
				try {
					parser.parse(new InputSource(targetFile.getContents()));
					Document document = parser.getDocument();
					traverse(idlist, document, getHtmlNamespaces(file
							.getProject()));
				} catch (SAXException e) {
					MatatabiPlugin.errorLog(e);
				} catch (IOException e) {
					MatatabiPlugin.errorLog(e);
				}
			}

			return idlist;
		} catch (CoreException e) {
			MatatabiPlugin.errorLog(e);
		}
		return new LinkedHashMap<String, Element>();
	}

	/**
	 * 指定要素以下のID属性を探索する。
	 * 
	 * @param idlis
	 *            指定要素以下のID属性のリスト
	 * @param element
	 *            指定要素
	 * @param namespaces
	 *            対象となる名前空間
	 */
	private static void traverse(Map<String, Element> idlist, Node element,
			List<String> namespaces) {
		for (Iterator<String> iter = namespaces.iterator(); iter.hasNext();) {
			String namespace = iter.next();
			traverse(idlist, element, namespace);
		}
	}

	private static void traverse(Map<String, Element> idlist, Node element,
			String namespace) {
		if (element instanceof Element) {
			String value = getAttributeValue((Element) element, namespace, "id");
			if (value != null) {
				idlist.put(value, (Element) element);
			}
		}
		NodeList nodeList = element.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			if (nodeList.item(i) instanceof Element) {
				traverse(idlist, (Element) nodeList.item(i), namespace);
			}
		}
	}

	/**
	 * 考えられる方法を全て使って属性値を取得
	 * 
	 * @param element
	 * @param namespaceURI
	 * @param localName
	 * @return
	 */
	public static String getAttributeValue(Element element,
			String namespaceURI, String localName) {
		Attr attr = getAttributeNode(element, namespaceURI, localName);
		return attr == null ? null : attr.getValue();
	}

	public static Attr getAttributeNode(Element element, String namespaceURI,
			String localName) {
		// 正攻法
		if (element.hasAttributeNS(namespaceURI, localName)) {
			return element.getAttributeNodeNS(namespaceURI, localName);
		}
		// 属性の名前空間が省略されている場合、要素の名前空間をチェック
		else if (namespaceURI.equals(element.getNamespaceURI())
				&& element.hasAttribute(localName)) {
			return element.getAttributeNode(localName);
		}
		// 最後の手。名前空間のプレフィックスをとってきてローカル名にくっつけてチェック
		// でもデフォルト名前空間とか使われてるとまた変わるかも
		else {
			try {
				String prefix = element.lookupPrefix(namespaceURI);
				if (prefix != null) {
					prefix = prefix.toLowerCase();
					if (element.hasAttribute(prefix + ":" + localName)) {
						return element.getAttributeNode(prefix + ":"
								+ localName);
					}
				}
			} catch (DOMException e) {
			}
		}

		// namespaceなしに対応
		if ("".equals(namespaceURI) && element.hasAttribute(localName)) {
			return element.getAttributeNode(localName);
		}
		return null;
	}

	public static List<String> getHtmlNamespaces(IProject project) {
		return PreferencesUtil.getPreference(project).getBoolean(
				MatatabiPropertyPage.ONLY_MAYAA_ID) ? mayaaNamespaces
				: htmlNamespaces;
	}

	public static List<String> getMayaaNamespaces() {
		return mayaaNamespaces;
	}
}
