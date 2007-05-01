package org.seasar.mayaa.matatabi.util;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.cyberneko.html.parsers.DOMParser;
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

	public static Set getDefaultIdList(IFolder folder) {
		IProject project = folder.getProject();
		IPath path = folder.getProjectRelativePath();
		IFile defaultMayaa = project.getFile(path.toString() + File.separator
				+ "default.mayaa");
		if (!defaultMayaa.exists()) {
			if (folder.getParent() instanceof IFolder) {
				return getDefaultIdList((IFolder) folder.getParent());
			}
		} else {
			try {
				return getIdList(new InputSource(defaultMayaa.getContents()));
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
		return new LinkedHashSet();
	}

	public static Set getXmlIdList(IFile file) {
		try {
			return getXmlIdList(new InputSource(file.getContents()));
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return new LinkedHashSet();
	}

	public static Set getXmlIdList(InputSource input) {
		org.apache.xerces.parsers.DOMParser parser = new org.apache.xerces.parsers.DOMParser();
		Set idlist = new LinkedHashSet();
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

	public static Set getIdList(IFile file) {
		try {
			return getIdList(new InputSource(file.getContents()));
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return new LinkedHashSet();
	}

	public static Set getIdList(InputSource input) {
		DOMParser parser = new DOMParser();
		Set idlist = new LinkedHashSet();
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
	private static void traverse(Set idlist, Element element, List namespaces) {
		for (Iterator iter = namespaces.iterator(); iter.hasNext();) {
			String namespace = (String) iter.next();
			if (element.hasAttributeNS(namespace, "id")) {
				idlist.add(element.getAttributeNS(namespace, "id"));
			} else if (namespace.equals(element.getNamespaceURI())) {
				idlist.add(element.getAttribute("id"));
			} else {
				String prefix = element.lookupPrefix(namespace);
				if (prefix != null) {
					prefix = prefix.toLowerCase();
					if (element.hasAttribute(prefix + ":id")) {
						idlist.add(element.getAttribute(prefix + ":id"));
					}
				}
			}
			NodeList nodeList = element.getChildNodes();
			for (int i = 0; i < nodeList.getLength(); i++) {
				if (nodeList.item(i) instanceof Element) {
					traverse(idlist, (Element) nodeList.item(i), namespaces);
				}
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
