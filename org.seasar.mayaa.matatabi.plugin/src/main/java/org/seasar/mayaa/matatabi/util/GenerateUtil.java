package org.seasar.mayaa.matatabi.util;

import java.io.ByteArrayInputStream;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.seasar.mayaa.matatabi.MatatabiPlugin;

public class GenerateUtil {

	public static String genereteTags(Set idlist) {
		StringBuffer buffer = new StringBuffer();
		for (Iterator iter = idlist.iterator(); iter.hasNext();) {
			String id = (String) iter.next();
			buffer.append("  <m:echo id=\"" + id + "\"></m:echo>\n");
		}
		return buffer.toString();
	}

	public static String genereteFile(String string) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		buffer.append("<m:mayaa xmlns:m=\"http://mayaa.seasar.org\">\n");
		buffer.append(string);
		buffer.append("</m:mayaa>\n");
		return buffer.toString();
	}

	public static final void generateMayaaFile() {
		IFile file = EditorUtil.getActiveFile();
		IProject project = file.getProject();
		IPath path = file.getProjectRelativePath();

		IFile mayaaFile = project.getFile(path.toString());
		Set idlist = ParseUtil.getIdList(mayaaFile);
		idlist.removeAll(ParseUtil.getDefaultIdList((IFolder) mayaaFile
				.getParent()));

		String fileContents = genereteFile(genereteTags(idlist));
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
				fileContents.getBytes());
		try {
			String fileName = path.toString();
			String fileExtension = MatatabiPlugin.getStoreValue(project,
					"fileExtension");
			if (fileExtension == null || fileExtension.equals("")) {
				fileExtension = "mayaa";
			}

			fileName = fileName.substring(0, fileName.length()
					- path.getFileExtension().length())
					+ fileExtension;
			IFile openFile = project.getFile(fileName);
			openFile.create(byteArrayInputStream, true, EditorUtil
					.getProgressMonitor());
		} catch (CoreException e1) {
			e1.printStackTrace();
		}
	}
}
