package org.seasar.mayaa.matatabi.builder;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.seasar.mayaa.matatabi.util.EditorUtil;
import org.seasar.mayaa.matatabi.util.ParseUtil;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class MatatabiValidator implements IResourceVisitor,
		IResourceDeltaVisitor {

	public boolean visit(IResource resource) throws CoreException {

		resource.deleteMarkers(IMarker.PROBLEM, false, IResource.DEPTH_ZERO);
		if (resource instanceof IFile) {
			validate((IFile) resource);
		}
		return true;
	}

	public boolean visit(IResourceDelta delta) throws CoreException {
		switch (delta.getKind()) {
		case IResourceDelta.ADDED:
		case IResourceDelta.CHANGED:
			IResource resource = delta.getResource();
			if (resource instanceof IFile) {
				validate((IFile) resource);
			}
		}
		return true;
	}

	private void validate(IFile file) throws CoreException {
		if (file.getName().endsWith(
				EditorUtil.getTemplateFileExtension(file.getProject()))) {
			file.deleteMarkers(IMarker.PROBLEM, false, IResource.DEPTH_ZERO);
			try {
				String fileName = file.getProjectRelativePath().toString();
				fileName = fileName.substring(0, fileName.length()
						- file.getFileExtension().length())
						+ "html";
				IFile openFile = file.getProject().getFile(fileName);
				Set sourceid = ParseUtil.getIdList(openFile);
				Set defaultid = ParseUtil.getDefaultIdList((IFolder) file
						.getParent());

				SAXParserFactory parserFactory = SAXParserFactory.newInstance();
				parserFactory.setFeature(
						"http://xml.org/sax/features/namespaces", true);
				SAXParser parser = parserFactory.newSAXParser();
				parser.parse(file.getContents(), new ValidateHandler(file,
						sourceid, defaultid));
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}
	}

	private class ValidateHandler extends DefaultHandler {
		private static final String MAYAA_NAMESPACE = "http://mayaa.seasar.org";

		private int depth = 0;

		private boolean hasIdAttribute;

		private IFile file;

		private Locator locator;

		private Set sourceid;

		private Set defaultid;

		private Set idlist = new HashSet();

		public ValidateHandler(IFile file, Set sourceid, Set defaultid) {
			this.file = file;
			this.sourceid = sourceid;
			this.defaultid = defaultid;
		}

		public void setDocumentLocator(Locator locator) {
			this.locator = locator;
		}

		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			depth++;
			hasIdAttribute = false;
			String id = getId(attributes, uri);
			if (id == null) {
				if (requiredIdAttribute(uri, localName) && depth == 2) {
					setMarker("id属性が存在しません。", locator.getLineNumber());
				}
			} else {
				if (depth != 2) {
					setMarker("無効な位置にid属性が指定されています。", locator.getLineNumber());
				}
				if (!sourceid.contains(id)) {
					setMarker("テンプレートに存在しないid属性が指定されています。", locator
							.getLineNumber());
				}
				if (idlist.contains(id)) {
					setMarker("id属性が重複しています。", locator.getLineNumber());
				}
				idlist.add(id);
			}
		}

		/**
		 * id属性が必要な要素かどうか
		 * 
		 * @param uri
		 *            要素のNamespaceURI
		 * @param localName
		 *            要素の名前
		 * @return
		 */
		private boolean requiredIdAttribute(String uri, String localName) {
			return !(uri.equals(MAYAA_NAMESPACE) && (localName
					.equals("beforeRender") || localName.equals("afterRender")));
		}

		/**
		 * id属性を持っているかどうか
		 * 
		 * @param attributes
		 * @param elementUri
		 * @return
		 */
		private String getId(Attributes attributes, String elementUri) {
			String value = attributes.getValue(MAYAA_NAMESPACE, "id");
			if (value == null || elementUri.equals(MAYAA_NAMESPACE)) {
				value = attributes.getValue("id");
			}
			return value;
		}

		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			depth--;
		}

		public void endDocument() throws SAXException {
			sourceid.removeAll(idlist);
			sourceid.removeAll(defaultid);
			for (Iterator iter = sourceid.iterator(); iter.hasNext();) {
				String id = (String) iter.next();
				setMarker("未定義のid属性があります。(" + id + ")", 1);
			}

			super.endDocument();
		}

		private void setMarker(String message, int lineNumber) {
			try {
				IMarker marker = file.createMarker(IMarker.PROBLEM);
				Map attributeMap = new HashMap();
				attributeMap.put(IMarker.SEVERITY, Integer
						.valueOf(IMarker.SEVERITY_WARNING));
				attributeMap.put(IMarker.MESSAGE, message);
				attributeMap.put(IMarker.LINE_NUMBER, Integer
						.valueOf(lineNumber));
				marker.setAttributes(attributeMap);
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
	}
}