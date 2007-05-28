package org.seasar.mayaa.matatabi.util;

import java.io.File;
import java.io.IOException;
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
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class ParseUtil {
	private static List htmlNamespaces = Arrays.asList(new String[] { "",
			"http://mayaa.seasar.org", "http://www.w3.org/TR/html4",
			"http://www.w3.org/1999/xhtml" });

	private static List mayaaNamespaces = Arrays
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
			if (container.getParent() instanceof IFolder
					|| container.getParent() instanceof IProject) {
				return getDefaultIdList(container.getParent());
			}
		} else {
			try {
				return getIdList(new InputSource(defaultMayaa.getContents()));
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
		return new LinkedHashMap<String, Element>();
	}

	public static Map getXmlIdList(IFile file) {
		try {
			return getXmlIdList(new InputSource(file.getContents()));
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return new LinkedHashMap();
	}

	public static Map getXmlIdList(InputSource input) {
		org.apache.xerces.parsers.DOMParser parser = new org.apache.xerces.parsers.DOMParser();
		Map<String, Element> idlist = new LinkedHashMap<String, Element>();
		try {

			parser.parse(input);
			Document document = parser.getDocument();
			traverse(idlist, document.getDocumentElement(), mayaaNamespaces);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return idlist;
	}

	public static Map<String, Element> getIdList(IFile file) {
		try {
			if (!file.exists()) {
				return new LinkedHashMap<String, Element>();
			}
			return getIdList(new InputSource(file.getContents()));
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return new LinkedHashMap<String, Element>();
	}

	public static Map<String, Element> getIdList(InputSource input) {
		DOMParser parser = new DOMParser();
		Map<String, Element> idlist = new LinkedHashMap<String, Element>();
		try {

			parser.parse(input);
			Document document = parser.getDocument();
			traverse(idlist, (Element) document.getElementsByTagName("html")
					.item(0), htmlNamespaces);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return idlist;
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
	private static void traverse(Map<String, Element> idlist, Element element,
			List namespaces) {
		for (Iterator iter = namespaces.iterator(); iter.hasNext();) {
			String namespace = (String) iter.next();
			traverse(idlist, element, namespace);
		}
	}

	private static void traverse(Map<String, Element> idlist, Element element,
			String namespace) {
		if (element.hasAttributeNS(namespace, "id")) {
			idlist.put(element.getAttributeNS(namespace, "id"), element);
		} else if (namespace.equals(element.getNamespaceURI())) {
			idlist.put(element.getAttribute("id"), element);
		} else {
			String prefix = element.lookupPrefix(namespace);
			if (prefix != null) {
				prefix = prefix.toLowerCase();
				if (element.hasAttribute(prefix + ":id")) {
					idlist.put(element.getAttribute(prefix + ":id"), element);
				}
			}
		}
		NodeList nodeList = element.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			if (nodeList.item(i) instanceof Element) {
				traverse(idlist, (Element) nodeList.item(i), namespace);
			}
		}
	}

	public static List getHtmlNamespaces() {
		return htmlNamespaces;
	}

	public static List getMayaaNamespaces() {
		return mayaaNamespaces;
	}
}
