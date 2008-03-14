package org.seasar.mayaa.matatabi.editor.hyperlink;

import org.seasar.mayaa.matatabi.MatatabiPlugin;
import org.seasar.mayaa.matatabi.util.EditorUtil;
import org.seasar.mayaa.matatabi.util.ParseUtil;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Mayaa�t�@�C�������n�C�p�[�����N����
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

		// �g�b�v�v�f�̒����̗v�f�̂ݑΏۂƂ���
		if (!node.getParentNode().equals(
				node.getOwnerDocument().getDocumentElement())) {
			return null;
		}

		return ParseUtil.getAttributeNode((Element) node,
				MatatabiPlugin.XMLNS_MAYAA, "id");
	}
}
