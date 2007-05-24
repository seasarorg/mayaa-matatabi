package org.seasar.mayaa.matatabi.util;

import java.io.ByteArrayInputStream;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

/**
 * コード生成に関する処理を行う
 * 
 * @author matoba
 */
public class GenerateUtil {

	/**
	 * idに対応するMayaaタグを生成します。
	 * 
	 * @param idlist
	 *            生成対象のidのリスト
	 * @return
	 */
	public static String genereteTags(Set idlist) {
		StringBuffer buffer = new StringBuffer();
		for (Iterator iter = idlist.iterator(); iter.hasNext();) {
			String id = (String) iter.next();
			buffer.append("  <m:echo id=\"" + id + "\"></m:echo>\n");
		}

		return buffer.toString();
	}

	private static String genereteFile(String string) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		buffer.append("<m:mayaa xmlns:m=\"http://mayaa.seasar.org\">\n");
		buffer.append(string);
		buffer.append("</m:mayaa>\n");
		return buffer.toString();
	}

	/**
	 * 現在開いているHTMLファイルに対応するMayaaファイルを生成します。
	 */
	public static void generateMayaaFile() {
		IFile file = EditorUtil.getActiveFile();
		IProject project = file.getProject();
		IPath path = file.getProjectRelativePath();

		IFile mayaaFile = project.getFile(path.toString());
		Set idlist = ParseUtil.getIdList(mayaaFile);
		idlist.removeAll(ParseUtil.getDefaultIdList(mayaaFile.getParent()));

		String fileContents = genereteFile(genereteTags(idlist));
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
				fileContents.getBytes());
		try {
			String fileName = path.toString();
			fileName = fileName.substring(0, fileName.length()
					- path.getFileExtension().length())
					+ "mayaa";
			IFile openFile = project.getFile(fileName);
			openFile.create(byteArrayInputStream, true, EditorUtil
					.getProgressMonitor());
		} catch (CoreException e1) {
			e1.printStackTrace();
		}
	}
}
