package org.seasar.mayaa.matatabi.editor.hyperlink;

import org.seasar.mayaa.matatabi.MatatabiPlugin;
import org.seasar.mayaa.matatabi.util.EditorUtil;
import org.seasar.mayaa.matatabi.util.ParseUtil;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Mayaaファイル向けハイパーリンク処理
 */
public class MayaaXMLHyperlinkDetector extends IdAttributeHyperlinkDetector {
	@Override
	protected Attr getIdAttribute(Node node) {
		if (node.getNodeType() != Node.ELEMENT_NODE) {
			return null;
		}
		if (!EditorUtil.hasMatatabiNature()) {
			return null;
		}

		// トップ要素の直下の要素のみ対象とする
		if (!node.getParentNode().equals(
				node.getOwnerDocument().getDocumentElement())) {
			return null;
		}

		return ParseUtil.getAttributeNode((Element) node,
				MatatabiPlugin.XMLNS_MAYAA, "id");
	}
}
