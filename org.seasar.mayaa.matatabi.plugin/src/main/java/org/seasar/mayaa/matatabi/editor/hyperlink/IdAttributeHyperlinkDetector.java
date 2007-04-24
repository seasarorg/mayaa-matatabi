package org.seasar.mayaa.matatabi.editor.hyperlink;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.ui.IEditorPart;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.core.utils.StringUtils;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.seasar.mayaa.matatabi.util.EditorUtil;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;

/**
 * Mayaaファイル向けハイパーリンク
 * 
 * @author matoba
 */
public abstract class IdAttributeHyperlinkDetector implements
		IHyperlinkDetector {

	public IHyperlink[] detectHyperlinks(ITextViewer textViewer,
			IRegion region, boolean canShowMultipleHyperlinks) {
		if (region == null || textViewer == null) {
			return null;
		}

		IDocument document = textViewer.getDocument();
		Node currentNode = getCurrentNode(document, region.getOffset());
		if (currentNode != null) {
			// element nodes
			Attr attr = getIdAttribute(currentNode);

			if (attr != null) {
				IRegion hyperlinkRegion = getHyperlinkRegion(currentNode);
				IHyperlink hyperLink = createHyperlinkForClass(attr
						.getNodeValue(), hyperlinkRegion, document);
				if (hyperLink != null) {
					return new IHyperlink[] { hyperLink };
				}
			}
		}
		return null;
	}

	protected abstract Attr getIdAttribute(Node node);

	/**
	 * Returns the node the cursor is currently on in the document. null if no
	 * node is selected
	 * 
	 * @param offset
	 * @return Node either element, doctype, text, or null
	 */
	private Node getCurrentNode(IDocument document, int offset) {
		// get the current node at the offset (returns either: element,
		// doctype, text)
		IndexedRegion inode = null;
		IStructuredModel sModel = null;
		try {
			sModel = StructuredModelManager.getModelManager()
					.getExistingModelForRead(document);
			inode = sModel.getIndexedRegion(offset);
			if (inode == null)
				inode = sModel.getIndexedRegion(offset - 1);
		} finally {
			if (sModel != null)
				sModel.releaseFromRead();
		}

		if (inode instanceof Node) {
			return (Node) inode;
		}
		return null;
	}

	private IRegion getHyperlinkRegion(Node node) {
		IRegion hyperRegion = null;

		if (node != null) {
			short nodeType = node.getNodeType();
			if (nodeType == Node.DOCUMENT_TYPE_NODE
					|| nodeType == Node.ELEMENT_NODE
					|| nodeType == Node.TEXT_NODE) {
				// handle doc type node
				IDOMNode docNode = (IDOMNode) node;
				hyperRegion = new Region(docNode.getStartOffset(), docNode
						.getEndOffset()
						- docNode.getStartOffset());
			} else if (nodeType == Node.ATTRIBUTE_NODE) {
				// handle attribute nodes
				IDOMAttr att = (IDOMAttr) node;
				// do not include quotes in attribute value region
				int regOffset = att.getValueRegionStartOffset();
				int regLength = att.getValueRegionText().length();
				String attValue = att.getValueRegionText();
				if (StringUtils.isQuoted(attValue)) {
					regOffset = regOffset + 1;
					regLength = regLength - 2;
				}
				hyperRegion = new Region(regOffset, regLength);
			}
		}
		return hyperRegion;
	}

	/**
	 * Create the appropriate hyperlink.
	 */
	private IHyperlink createHyperlinkForClass(String target,
			IRegion hyperlinkRegion, IDocument document) {

		IHyperlink link = null;
		link = new AttributeHyperlink(hyperlinkRegion, target);

		return link;
	}

	/**
	 * IHyperlink implementation for the java class and other files.
	 */
	private class AttributeHyperlink implements IHyperlink {

		private final IRegion region;

		private final String name;

		/**
		 * Creates a new Java element hyperlink.
		 */
		public AttributeHyperlink(IRegion region, String name) {
			this.region = region;
			this.name = name;
		}

		public IRegion getHyperlinkRegion() {
			return this.region;
		}

		/**
		 * opens the standard Java Editor for the given IJavaElement
		 */
		public void open() {
			IEditorPart openEditorPart = EditorUtil.openFile();
			EditorUtil.selectText(name, openEditorPart);
		}

		public String getTypeLabel() {
			return null;
		}

		public String getHyperlinkText() {
			return null;
		}
	}
}
