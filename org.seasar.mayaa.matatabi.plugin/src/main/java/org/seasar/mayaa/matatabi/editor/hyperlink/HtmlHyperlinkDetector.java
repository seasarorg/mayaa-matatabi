package org.seasar.mayaa.matatabi.editor.hyperlink;

import java.util.Iterator;
import java.util.List;

import org.seasar.mayaa.matatabi.util.EditorUtil;
import org.seasar.mayaa.matatabi.util.ParseUtil;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;

/**
 * HTMLファイル向けハイパーリンク
 */
public class HtmlHyperlinkDetector extends IdAttributeHyperlinkDetector {
	/**
	 * 指定したnodeのid属性を取得します。
	 */
	protected Attr getIdAttribute(Node node) {
		if (node.getNodeType() != Node.ELEMENT_NODE) {
			return null;
		}
		if (!EditorUtil.hasMatatabiNature()) {
			return null;
		}

		List<String> namespaces = ParseUtil.getHtmlNamespaces();
		Attr attr = null;
		for (Iterator<String> iter = namespaces.iterator(); iter.hasNext()
				&& attr == null;) {
			String namespace = (String) iter.next();
			if (namespace.length() > 0) {
				attr = (Attr) node.getAttributes().getNamedItemNS(namespace,
						"id");
			} else {
				attr = (Attr) node.getAttributes().getNamedItem("id");
			}
			// 要素の名前空間が対象の場合は、名前空間指定なしのid属性を取得
			if (attr == null && namespace.equals(node.getNamespaceURI())) {
				attr = (Attr) node.getAttributes().getNamedItemNS(null, "id");
			}
		}
		return attr;
	}
}
