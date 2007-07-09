package org.seasar.mayaa.matatabi.builder;

import java.io.IOException;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.seasar.mayaa.matatabi.MatatabiPlugin;
import org.seasar.mayaa.matatabi.util.ParseUtil;
import org.xml.sax.SAXException;

/**
 * MayaaファイルのValidationを行う。
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
		if (file.getName().endsWith("mayaa")) {
			file.deleteMarkers(MatatabiPlugin.MARKER_ID, false,
					IResource.DEPTH_ZERO);
			try {
				String fileName = file.getProjectRelativePath().toString();
				fileName = fileName.substring(0, fileName.length()
						- file.getFileExtension().length())
						+ "html"; //$NON-NLS-1$
				IFile openFile = file.getProject().getFile(fileName);
				Set<String> sourceid = ParseUtil.getIdList(openFile).keySet();
				Set<String> defaultid = ParseUtil.getDefaultIdList(
						file.getParent()).keySet();

				SAXParserFactory parserFactory = SAXParserFactory.newInstance();
				parserFactory.setFeature(
						"http://xml.org/sax/features/namespaces", true); //$NON-NLS-1$
				SAXParser parser = parserFactory.newSAXParser();
				parser.parse(file.getContents(), new MatatabiValidateHandler(
						file, sourceid, defaultid));
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}