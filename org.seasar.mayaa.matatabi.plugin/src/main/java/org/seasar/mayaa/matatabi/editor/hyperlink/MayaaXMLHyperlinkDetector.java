package org.seasar.mayaa.matatabi.editor.hyperlink;

import org.seasar.mayaa.matatabi.util.EditorUtil;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;

/**
 * Mayaa�t�@�C�������n�C�p�[�����N����
 */
public class MayaaXMLHyperlinkDetector extends IdAttributeHyperlinkDetector {
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

		Attr attr = (Attr) node.getAttributes().getNamedItemNS(
				"http://mayaa.seasar.org", "id");
		// �v�f�̖��O��Ԃ�Mayaa�̏ꍇ�́A���O��Ԏw��Ȃ���id�������擾
		if (attr == null
				&& "http://mayaa.seasar.org".equals(node.getNamespaceURI())) {
			attr = (Attr) node.getAttributes().getNamedItemNS(null, "id");
		}
		return attr;
	}
}
