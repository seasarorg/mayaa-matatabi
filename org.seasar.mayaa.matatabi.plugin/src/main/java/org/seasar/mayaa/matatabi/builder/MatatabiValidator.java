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
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.seasar.mayaa.matatabi.MatatabiPlugin;
import org.seasar.mayaa.matatabi.property.MatatabiPropertyPage;
import org.seasar.mayaa.matatabi.util.EditorUtil;
import org.seasar.mayaa.matatabi.util.ParseUtil;
import org.seasar.mayaa.matatabi.util.PreferencesUtil;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Mayaa�t�@�C����Validation���s���B
 */
public class MatatabiValidator implements IResourceVisitor,
		IResourceDeltaVisitor {
	public boolean visit(IResource resource) throws CoreException {
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
			file.deleteMarkers(MatatabiPlugin.MARKER_ID, false,
					IResource.DEPTH_ZERO);
			try {
				String fileName = file.getProjectRelativePath().toString();
				fileName = fileName.substring(0, fileName.length()
						- file.getFileExtension().length())
						+ "html"; //$NON-NLS-1$
				IFile openFile = file.getProject().getFile(fileName);
				Set sourceid = ParseUtil.getIdList(openFile);
				Set defaultid = ParseUtil.getDefaultIdList((IFolder) file
						.getParent());

				SAXParserFactory parserFactory = SAXParserFactory.newInstance();
				parserFactory.setFeature(
						"http://xml.org/sax/features/namespaces", true); //$NON-NLS-1$
				SAXParser parser = parserFactory.newSAXParser();
				parser.parse(file.getContents(), new ValidateHandler(file,
						sourceid, defaultid));
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Mayaa�t�@�C���̉�͂��AValidation���s���B
	 */
	private class ValidateHandler extends DefaultHandler {
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
		public ValidateHandler(IFile file, Set sourceid, Set defaultid) {
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
		 * �v�f�J�n���̏���
		 * <ul>
		 * <li>id�����̑��݃`�F�b�N</li>
		 * <li>id�����̈ʒu�`�F�b�N</li>
		 * <li>�e���v���[�g�ɑ��݂��Ȃ�id�����̃`�F�b�N</li>
		 * <li>�d������id�����̃`�F�b�N</li>
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
		 * �v�f�I�����̏���
		 */
		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			depth--;
		}

		/**
		 * ��͏I�����̏���
		 * <ul>
		 * <li>�e���v���[�g�ɂ����āAMayaa�t�@�C���ɂȂ�id�����̃`�F�b�N</li>
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
		 * id�������K�v�ȗv�f���ǂ����B
		 * 
		 * @param uri
		 *            �v�f��NamespaceURI
		 * @param localName
		 *            �v�f�̖��O
		 * @return
		 */
		private boolean requiredIdAttribute(String uri, String localName) {
			return !(uri.equals(MAYAA_NAMESPACE) && (localName
					.equals("beforeRender") || localName.equals("afterRender"))); //$NON-NLS-1$ //$NON-NLS-2$
		}

		/**
		 * id�������擾����B
		 * 
		 * @param attributes
		 * @param elementUri
		 * @return
		 */
		private String getId(Attributes attributes, String elementUri) {
			return getAttributeValue(attributes, elementUri, "id");
		}

		/**
		 * xpath�������擾����B
		 * 
		 * @param attributes
		 * @param elementUri
		 * @return
		 */
		private String getXpath(Attributes attributes, String elementUri) {
			return getAttributeValue(attributes, elementUri, "xpath");
		}

		private String getAttributeValue(Attributes attributes,
				String elementUri, String name) {
			String value = attributes.getValue(MAYAA_NAMESPACE, name); //$NON-NLS-1$
			if (value == null && elementUri.equals(MAYAA_NAMESPACE)) {
				value = attributes.getValue(name); //$NON-NLS-1$
			}
			return value;
		}

		/**
		 * �}�[�J�[��ݒ肷��B
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
				attributeMap.put(IMarker.LINE_NUMBER, Integer
						.valueOf(lineNumber));
				marker.setAttributes(attributeMap);
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}

		/**
		 * �ݒ�l����G���[���x�����擾����B
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
}