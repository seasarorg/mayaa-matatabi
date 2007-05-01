package org.seasar.mayaa.matatabi.editor.hyperlink;

import org.seasar.mayaa.matatabi.util.EditorUtil;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;

/**
 * id属性でのハイパーリンク処理を行う
 * 
 * @author matoba
 */
public class MayaaXMLHyperlinkDetector extends IdAttributeHyperlinkDetector {
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

		Attr attr = (Attr) node.getAttributes().getNamedItemNS(
				"http://mayaa.seasar.org", "id");
		// 要素の名前空間がMayaaの場合は、名前空間指定なしのid属性を取得
		if (attr == null
				&& "http://mayaa.seasar.org".equals(node.getNamespaceURI())) {
			attr = (Attr) node.getAttributes().getNamedItemNS(null, "id");
		}
		return attr;
	}
}
