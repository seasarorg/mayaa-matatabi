package org.seasar.mayaa.matatabi.editor.contentsassist;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.xml.core.internal.document.AttrImpl;
import org.eclipse.wst.xml.ui.internal.contentassist.ContentAssistRequest;
import org.eclipse.wst.xml.ui.internal.contentassist.XMLContentAssistProcessor;
import org.seasar.mayaa.matatabi.MatatabiPlugin;
import org.seasar.mayaa.matatabi.util.EditorUtil;
import org.seasar.mayaa.matatabi.util.ParseUtil;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * コンテンツアシスト機能
 */
@SuppressWarnings("restriction")
public class HTMLContentAssistProcessor extends XMLContentAssistProcessor {
	/** アイコン */
	private Image icon;

	/** コンストラクタ */
	public HTMLContentAssistProcessor() {
		icon = MatatabiPlugin.getImageDescriptor("icons/mayaa_file_small.gif")
				.createImage();
	}

	/**
	 * 属性値の追加
	 */
	protected void addAttributeValueProposals(
			ContentAssistRequest contentAssistRequest) {
		// 属性を取得する(JBossのプラグインからいただいたコード)
		int beginPos = contentAssistRequest.getReplacementBeginPosition();
		NamedNodeMap map = contentAssistRequest.getNode().getAttributes();
		AttrImpl attribute = null;
		for (int i = 0; i < map.getLength(); i++) {
			Node tmp = map.item(i);
			if (tmp instanceof AttrImpl) {
				int start = ((AttrImpl) tmp).getStartOffset();
				int end = ((AttrImpl) tmp).getEndOffset();
				if (beginPos > start && beginPos < end) {
					attribute = (AttrImpl) tmp;
					break;
				}
			}
		}

		Node idAttribute = ParseUtil.getAttributeNode(
				(Element) contentAssistRequest.getNode(),
				MatatabiPlugin.XMLNS_MAYAA, "id");
		if (!attribute.equals(idAttribute)) {
			super.addAttributeValueProposals(contentAssistRequest);
			return;
		}

		IFile file = EditorUtil.getActiveFile();
		IProject project = file.getProject();
		IPath path = file.getProjectRelativePath();

		String fileName = path.toString();
		fileName = fileName.substring(0, fileName.length()
				- path.getFileExtension().length())
				+ "mayaa";
		IFile openFile = project.getFile(fileName);
		Map<String, Element> allSourceid = ParseUtil.getIdList(openFile);
		Map<String, Element> unusedSourceId = new TreeMap<String, Element>(
				allSourceid);
		for (Iterator<String> iter = ParseUtil.getIdList(file).keySet()
				.iterator(); iter.hasNext();) {
			unusedSourceId.remove(iter.next());
		}
		for (Iterator<String> iter = ParseUtil.getDefaultIdList(
				file.getParent()).keySet().iterator(); iter.hasNext();) {
			unusedSourceId.remove(iter.next());
		}

		Map<String, Element> usedSourceId = new TreeMap<String, Element>(
				allSourceid);
		for (Iterator<String> iter = unusedSourceId.keySet().iterator(); iter
				.hasNext();) {
			usedSourceId.remove(iter.next());
		}

		for (Iterator<String> iter = unusedSourceId.keySet().iterator(); iter
				.hasNext();) {
			String id = (String) iter.next();
			String matchString = contentAssistRequest.getMatchString()
					.substring(1);
			if (isMatch(id, matchString)) {
				contentAssistRequest.addProposal(new CompletionProposal("\""
						+ id + "\"", contentAssistRequest
						.getReplacementBeginPosition(), contentAssistRequest
						.getReplacementLength(), id.length() + 1, icon, id,
						null, ""));
			}
		}
		for (Iterator<String> iter = usedSourceId.keySet().iterator(); iter
				.hasNext();) {
			String id = (String) iter.next();
			String matchString = contentAssistRequest.getMatchString()
					.substring(1);
			if (isMatch(id, matchString)) {
				contentAssistRequest.addProposal(new CompletionProposal("\""
						+ id + "\"", contentAssistRequest
						.getReplacementBeginPosition(), contentAssistRequest
						.getReplacementLength(), id.length() + 1, null, id,
						null, ""));
			}
		}
	}

	/**
	 * 補完する文字列と入力中の文字列が先頭一致するかどうか。
	 * 
	 * @param contents
	 *            補完する文字列
	 * @param matchString
	 *            入力中の文字列
	 * @return
	 */
	private boolean isMatch(String contents, String matchString) {
		return (matchString.length() == 0 || contents.startsWith(matchString));
	}
}
