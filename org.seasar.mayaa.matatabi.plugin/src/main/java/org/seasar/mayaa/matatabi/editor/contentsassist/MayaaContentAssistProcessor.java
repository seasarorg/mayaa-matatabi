package org.seasar.mayaa.matatabi.editor.contentsassist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ContextInformation;
import org.eclipse.jst.jsp.core.internal.contentmodel.tld.CMDocumentFactoryTLD;
import org.eclipse.jst.jsp.core.internal.contentmodel.tld.CMElementDeclarationImpl;
import org.eclipse.jst.jsp.core.internal.contentmodel.tld.provisional.JSP11TLDNames;
import org.eclipse.jst.jsp.core.taglib.ITaglibRecord;
import org.eclipse.jst.jsp.core.taglib.TaglibIndex;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDocument;
import org.eclipse.wst.xml.core.internal.document.AttrImpl;
import org.eclipse.wst.xml.ui.internal.contentassist.ContentAssistRequest;
import org.eclipse.wst.xml.ui.internal.contentassist.XMLContentAssistProcessor;
import org.seasar.mayaa.matatabi.MatatabiPlugin;
import org.seasar.mayaa.matatabi.util.EditorUtil;
import org.seasar.mayaa.matatabi.util.ParseUtil;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * コンテンツアシスト機能
 */
@SuppressWarnings("restriction")
public class MayaaContentAssistProcessor extends XMLContentAssistProcessor {
	/** 名前空間 */
	private static final String XMLNS_URI = "http://www.w3.org/2000/xmlns/";
	private static final String XMLNS_MAYAA = "http://mayaa.seasar.org";

	/** ルートタグ */
	private static final String ROOT_TAG = "<m:mayaa>\n</m:mayaa>";

	/** タグ情報 */
	private List<String> tagList = new ArrayList<String>();
	private List<String> taglibList = new ArrayList<String>();

	/** タグの説明 */
	private List<ContextInformation> tagContextInformationList = new ArrayList<ContextInformation>();
	private List<ContextInformation> taglibContextInformationList = new ArrayList<ContextInformation>();

	/** 属性情報 */
	private Map<String, String[]> attributeMap = new HashMap<String, String[]>();
	private Map<String, String[]> taglibAttributeMap = new HashMap<String, String[]>();

	/** アイコン */
	private Image icon;

	/** コンストラクタ */
	public MayaaContentAssistProcessor() {
		icon = MatatabiPlugin.getImageDescriptor("icons/mayaa_file_small.gif")
				.createImage();

		tagList.add("<m:attribute />");
		tagList.add("<m:comment></m:comment>");
		tagList.add("<m:echo></m:echo>");
		tagList.add("<m:element></m:element>");
		tagList.add("<m:formatDate />");
		tagList.add("<m:formatNumber />");
		tagList.add("<m:write />");
		tagList.add("<m:for></m:for>");
		tagList.add("<m:forEach></m:forEach>");
		tagList.add("<m:if></m:if>");
		tagList.add("<m:with></m:with>");
		tagList.add("<m:doBase />");
		tagList.add("<m:doBody />");
		tagList.add("<m:doRender />");
		tagList.add("<m:beforeRender />");
		tagList.add("<m:afterRender />");
		tagList.add("<m:insert />");
		tagList.add("<m:exec />");
		tagList.add("<m:ignore />");
		tagList.add("<m:null />");

		tagContextInformationList.add(new ContextInformation("m:attribute",
				Messages.getString("description.attribute")));
		tagContextInformationList.add(new ContextInformation("m:comment",
				Messages.getString("description.comment")));
		tagContextInformationList.add(new ContextInformation("m:echo", Messages
				.getString("description.echo")));
		tagContextInformationList.add(new ContextInformation("m:element",
				Messages.getString("description.element")));
		tagContextInformationList.add(new ContextInformation("m:formatDate",
				Messages.getString("description.formatDate")));
		tagContextInformationList.add(new ContextInformation("m:formatNumber",
				Messages.getString("description.formatNumber")));
		tagContextInformationList.add(new ContextInformation("m:write",
				Messages.getString("description.write")));
		tagContextInformationList.add(new ContextInformation("m:for", Messages
				.getString("description.for")));
		tagContextInformationList.add(new ContextInformation("m:forEach",
				Messages.getString("description.forEach")));
		tagContextInformationList.add(new ContextInformation("m:if", Messages
				.getString("description.if")));

		tagContextInformationList.add(new ContextInformation("m:with", Messages
				.getString("description.with")));
		tagContextInformationList.add(new ContextInformation("m:doBase",
				Messages.getString("description.doBase")));
		tagContextInformationList.add(new ContextInformation("m:doBody",
				Messages.getString("description.doBody")));
		tagContextInformationList.add(new ContextInformation("m:doRender",
				Messages.getString("description.doRender")));
		tagContextInformationList.add(new ContextInformation("m:beforeRender",
				Messages.getString("description.beforeRender")));
		tagContextInformationList.add(new ContextInformation("m:afterRender",
				Messages.getString("description.afterRender")));
		tagContextInformationList.add(new ContextInformation("m:insert",
				Messages.getString("description.insert")));
		tagContextInformationList.add(new ContextInformation("m:exec", Messages
				.getString("description.exec")));
		tagContextInformationList.add(new ContextInformation("m:ignore",
				Messages.getString("description.ignore")));
		tagContextInformationList.add(new ContextInformation("m:null", Messages
				.getString("description.null")));

		attributeMap.put("m:attribute", new String[] { "name", "value" });
		attributeMap.put("m:comment", new String[] {});
		attributeMap.put("m:echo", new String[] { "name" });
		attributeMap.put("m:element", new String[] { "name" });
		attributeMap.put("m:formatDate", new String[] { "value", "pattern",
				"result" });
		attributeMap.put("m:formatNumber", new String[] { "value", "pattern",
				"result" });
		attributeMap.put("m:write", new String[] { "value", "default",
				"escapeXml", "escapeWhitespace", "escapeEol" });
		attributeMap.put("m:for",
				new String[] { "init", "test", "after", "max" });
		attributeMap.put("m:forEach", new String[] { "items", "var", "index" });
		attributeMap.put("m:if", new String[] { "test" });
		attributeMap.put("m:doRender", new String[] { "name" });
		attributeMap.put("m:insert", new String[] { "name", "path" });
		attributeMap
				.put("m:exec", new String[] { "script", "src", "encoding" });
	}

	/**
	 * タグ追加
	 */
	protected void addTagInsertionProposals(
			ContentAssistRequest contentAssistRequest, int childPosition) {
		initTaglibInfo(contentAssistRequest);

		if (contentAssistRequest.getNode().getParentNode() instanceof Document) {
			// ルートノードの追加
			if (isMatch(ROOT_TAG, contentAssistRequest.getMatchString())) {
				contentAssistRequest.addProposal(new CompletionProposal(
						ROOT_TAG, contentAssistRequest
								.getReplacementBeginPosition(),
						contentAssistRequest.getReplacementLength(), ROOT_TAG
								.length(), icon, "<m:mayaa ...>", null, ""));
			}

		} else {
			for (int i = 0; i < taglibList.size(); i++) {
				String tag = (String) taglibList.get(i);
				if (isMatch(tag, contentAssistRequest.getMatchString())) {
					contentAssistRequest.addProposal(new CompletionProposal(
							tag, contentAssistRequest
									.getReplacementBeginPosition(),
							contentAssistRequest.getReplacementLength(), tag
									.length(), icon, tag,
							(ContextInformation) taglibContextInformationList
									.get(i),
							((ContextInformation) taglibContextInformationList
									.get(i)).getInformationDisplayString()));
				}
			}
		}
		super.addTagInsertionProposals(contentAssistRequest, childPosition);
	}

	/**
	 * タグ名追加
	 */
	protected void addTagNameProposals(
			ContentAssistRequest contentAssistRequest, int childPosition) {
		initTaglibInfo(contentAssistRequest);

		if (contentAssistRequest.getNode().getParentNode() instanceof Document) {
			if (isMatch(ROOT_TAG.substring(1), contentAssistRequest
					.getMatchString())) {
				contentAssistRequest
						.addProposal(new CompletionProposal(ROOT_TAG
								.substring(1), contentAssistRequest
								.getReplacementBeginPosition(),
								contentAssistRequest.getReplacementLength(),
								ROOT_TAG.length() - 1, icon, "<m:mayaa ...>",
								null, ""));
			}
		} else {
			for (int i = 0; i < taglibList.size(); i++) {
				String tag = (String) taglibList.get(i);
				String matchString = contentAssistRequest.getMatchString();
				if (isMatch(tag.substring(1), matchString)) {
					contentAssistRequest.addProposal(new CompletionProposal(tag
							.substring(1), contentAssistRequest
							.getReplacementBeginPosition(),
							contentAssistRequest.getReplacementLength(), tag
									.length() - 1, icon, tag,
							(ContextInformation) taglibContextInformationList
									.get(i),
							((ContextInformation) taglibContextInformationList
									.get(i)).getInformationDisplayString()));
				}
			}
		}
		super.addTagNameProposals(contentAssistRequest, childPosition);
	}

	/**
	 * 属性値の追加
	 */
	protected void addAttributeValueProposals(
			ContentAssistRequest contentAssistRequest) {
		// トップ要素またはトップ要素の直下の要素でない場合は処理しない
		if (!contentAssistRequest.getNode().getParentNode().equals(
				contentAssistRequest.getNode().getOwnerDocument()
						.getDocumentElement())
				&& !contentAssistRequest.getNode().equals(
						contentAssistRequest.getNode().getOwnerDocument()
								.getDocumentElement())) {
			super.addAttributeValueProposals(contentAssistRequest);
			return;
		}

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

		// タグライブラリの名前空間の補完
		if (contentAssistRequest.getNode().equals(
				contentAssistRequest.getNode().getOwnerDocument()
						.getDocumentElement())) {
			if (attribute != null
					&& XMLNS_URI.equals(attribute.getNamespaceURI())) {
				ITaglibRecord[] taglibRecords = TaglibIndex
						.getAvailableTaglibRecords(EditorUtil.getActiveFile()
								.getFullPath());
				Set<String> usedNamespaces = new HashSet<String>();
				Set<String> namespaces = new TreeSet<String>();
				for (int i = 0; i < map.getLength(); i++) {
					if (map.item(i).getNamespaceURI().equals(XMLNS_URI)) {
						usedNamespaces.add(map.item(i).getNodeValue());
					}
				}
				for (int i = 0; i < taglibRecords.length; i++) {
					String namespaceuri = taglibRecords[i].getDescriptor()
							.getURI();
					if (!usedNamespaces.contains(namespaceuri)) {
						namespaces.add(namespaceuri);
					}
				}
				for (String namespaceuri : namespaces) {
					String value = "\"" + namespaceuri + "\"";
					if (isMatch(value, contentAssistRequest.getMatchString())) {
						contentAssistRequest
								.addProposal(new CompletionProposal(value,
										contentAssistRequest
												.getReplacementBeginPosition(),
										contentAssistRequest
												.getReplacementLength(), value
												.length() + 1, icon,
										namespaceuri, null, ""));
					}

				}
			}

			super.addAttributeValueProposals(contentAssistRequest);
			return;
		}

		Node idAttribute = contentAssistRequest.getNode().getAttributes()
				.getNamedItemNS(XMLNS_MAYAA, "id");
		// 要素の名前空間がMayaaの場合は、名前空間指定なしのid属性を取得
		if (idAttribute == null
				&& XMLNS_MAYAA.equals(contentAssistRequest.getNode()
						.getNamespaceURI())) {
			idAttribute = contentAssistRequest.getNode().getAttributes()
					.getNamedItemNS(null, "id");
		}

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
				+ "html";
		IFile openFile = project.getFile(fileName);
		Map<String, Element> allSourceid = ParseUtil.getIdList(openFile);
		Map<String, Element> unusedSourceId = new TreeMap<String, Element>(
				allSourceid);
		for (Iterator iter = ParseUtil.getXmlIdList(file).keySet().iterator(); iter
				.hasNext();) {
			unusedSourceId.remove(iter.next());
		}
		for (Iterator iter = ParseUtil.getDefaultIdList(
				(IFolder) file.getParent()).keySet().iterator(); iter.hasNext();) {
			unusedSourceId.remove(iter.next());
		}

		Map<String, Element> usedSourceId = new TreeMap<String, Element>(
				allSourceid);
		for (Iterator iter = unusedSourceId.keySet().iterator(); iter.hasNext();) {
			usedSourceId.remove(iter.next());
		}

		for (Iterator iter = unusedSourceId.keySet().iterator(); iter.hasNext();) {
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
		for (Iterator iter = usedSourceId.keySet().iterator(); iter.hasNext();) {
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

	protected void addAttributeNameProposals(
			ContentAssistRequest contentAssistRequest) {
		initTaglibInfo(contentAssistRequest);

		if (contentAssistRequest.getNode().getParentNode().getNodeName()
				.equals("m:mayaa")
				&& contentAssistRequest.getNode().getAttributes().getNamedItem(
						"id") == null
				&& isMatch("id", contentAssistRequest.getMatchString())) {
			contentAssistRequest.addProposal(new CompletionProposal("id=\"\"",
					contentAssistRequest.getReplacementBeginPosition(),
					contentAssistRequest.getReplacementLength(), 4, icon, "id",
					null, ""));
		}

		if (contentAssistRequest.getNode().getNodeName().equals("m:mayaa")) {
			ITaglibRecord[] taglibRecords = TaglibIndex
					.getAvailableTaglibRecords(EditorUtil.getActiveFile()
							.getFullPath());
			NamedNodeMap map = contentAssistRequest.getNode().getAttributes();
			Set<String> usedNamespaces = new HashSet<String>();
			Map<String, String> namespaces = new TreeMap<String, String>();
			for (int i = 0; i < map.getLength(); i++) {
				if (XMLNS_URI.equals(map.item(i).getNamespaceURI())) {
					usedNamespaces.add(map.item(i).getNodeValue());
				}
			}
			for (int i = 0; i < taglibRecords.length; i++) {
				String namespaceuri = taglibRecords[i].getDescriptor().getURI();
				if (!usedNamespaces.contains(namespaceuri)) {
					namespaces.put(taglibRecords[i].getDescriptor()
							.getShortName(), namespaceuri);
				}
			}
			for (Entry<String, String> entry : namespaces.entrySet()) {
				String namespace = "xmlns:" + entry.getKey() + "=\""
						+ entry.getValue() + "\"";
				if (isMatch(namespace, contentAssistRequest.getMatchString())) {
					contentAssistRequest.addProposal(new CompletionProposal(
							namespace, contentAssistRequest
									.getReplacementBeginPosition(),
							contentAssistRequest.getReplacementLength(),
							namespace.length() + 1, icon, namespace, null, ""));
				}
			}

		}

		String[] attributes = (String[]) taglibAttributeMap
				.get(contentAssistRequest.getNode().getNodeName());
		if (attributes != null) {
			for (int i = 0; i < attributes.length; i++) {
				if (contentAssistRequest.getNode().getAttributes()
						.getNamedItem(attributes[i]) == null
						&& isMatch(attributes[i], contentAssistRequest
								.getMatchString())) {
					contentAssistRequest.addProposal(new CompletionProposal(
							attributes[i] + "=\"\"", contentAssistRequest
									.getReplacementBeginPosition(),
							contentAssistRequest.getReplacementLength(),
							attributes[i].length() + 2, icon, attributes[i],
							null, ""));
				}
			}
		}
		super.addAttributeNameProposals(contentAssistRequest);
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

	/**
	 * タグライブラリの情報を読み込む
	 * 
	 * @param contentAssistRequest
	 */
	private void initTaglibInfo(ContentAssistRequest contentAssistRequest) {
		taglibList.clear();
		taglibContextInformationList.clear();
		taglibAttributeMap.clear();

		taglibList.addAll(tagList);
		taglibContextInformationList.addAll(tagContextInformationList);
		taglibAttributeMap.putAll(attributeMap);

		ITaglibRecord[] taglibRecords = TaglibIndex
				.getAvailableTaglibRecords(EditorUtil.getActiveFile()
						.getFullPath());
		Map<String, String> namespaceMap = new HashMap<String, String>();
		Element root = contentAssistRequest.getNode().getOwnerDocument()
				.getDocumentElement();
		for (int i = 0; i < root.getAttributes().getLength(); i++) {
			Attr attr = (Attr) root.getAttributes().item(i);
			if (XMLNS_URI.equals(attr.getNamespaceURI())) {
				namespaceMap.put(attr.getValue(), attr.getLocalName());
			}
		}
		for (int i = 0; i < taglibRecords.length; i++) {
			String namespaceuri = taglibRecords[i].getDescriptor().getURI();
			if (namespaceMap.keySet().contains(namespaceuri)) {
				String prefix = namespaceMap.get(namespaceuri);
				CMDocument document = (new CMDocumentFactoryTLD())
						.createCMDocument(taglibRecords[i]);
				for (int j = 0; j < document.getElements().getLength(); j++) {
					CMElementDeclarationImpl node = (CMElementDeclarationImpl) document
							.getElements().item(j);

					String nodeName = prefix + ":" + node.getNodeName();
					String tag = null;
					// Bodyを持たない場合は閉じタグ省略
					if (node.getBodycontent().equals(
							JSP11TLDNames.CONTENT_EMPTY)) {
						tag = "<" + nodeName + " />";
					} else {
						tag = "<" + nodeName + "></" + nodeName + ">";
					}
					if (!taglibList.contains(tag)) {
						taglibList.add(tag);
						taglibContextInformationList
								.add(new ContextInformation(nodeName, node
										.getDescription() == null ? "" : node
										.getDescription()));
						List<String> attributes = new ArrayList<String>();
						for (int k = 0; k < node.getAttributes().getLength(); k++) {
							attributes.add(node.getAttributes().item(k)
									.getNodeName());
						}
						taglibAttributeMap.put(nodeName, attributes
								.toArray(new String[] {}));
					}
				}
			}
		}
	}
}
