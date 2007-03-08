package org.seasar.mayaa.matatabi.util;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashSet;
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
			parser.setFeature("http://xml.org/sax/features/namespaces", true);
			parser.setFeature("http://xml.org/sax/features/validation", false);

			parser.parse(input);
			Document document = parser.getDocument();
			traverse(idlist, (Element) document.getElementsByTagName("html")
					.item(0));
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return idlist;
	}

	private static void traverse(Set idlist, Element element) {
		if (element.hasAttribute("m:id")) {
			idlist.add(element.getAttribute("m:id"));
		} else if (element.hasAttribute("id")) {
			idlist.add(element.getAttribute("id"));
		}
		NodeList nodeList = element.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			if (nodeList.item(i) instanceof Element) {
				traverse(idlist, (Element) nodeList.item(i));
			}
		}
	}
}
