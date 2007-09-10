package org.seasar.mayaa.matatabi.editor.hyperlink;

import java.util.Iterator;
import java.util.List;

import org.seasar.mayaa.matatabi.util.EditorUtil;
import org.seasar.mayaa.matatabi.util.ParseUtil;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * HTML�t�@�C�������n�C�p�[�����N
 */
public class HtmlHyperlinkDetector extends IdAttributeHyperlinkDetector {
	/**
	 * �w�肵��node��id�������擾���܂��B
	 */
	protected Attr getIdAttribute(Node node) {
		if (node.getNodeType() != Node.ELEMENT_NODE) {
			return null;
		}
		if (!EditorUtil.hasMatatabiNature()) {
			return null;
		}

		List<String> namespaces = ParseUtil.getHtmlNamespaces(EditorUtil
				.getActiveFile().getProject());
		Attr attr = null;
		for (Iterator<String> iter = namespaces.iterator(); iter.hasNext()
				&& attr == null;) {
			String namespace = (String) iter.next();
			attr = ParseUtil.getAttributeNode((Element) node, namespace, "id");
			if (attr != null) {
				break;
			}
		}
		return attr;
	}
}
