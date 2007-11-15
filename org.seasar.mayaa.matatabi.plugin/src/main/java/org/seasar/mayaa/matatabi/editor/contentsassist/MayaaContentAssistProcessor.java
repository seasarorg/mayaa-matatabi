package org.seasar.mayaa.matatabi.editor.contentsassist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ContextInformation;
import org.eclipse.jst.jsp.core.internal.contentmodel.tld.CMAttributeDeclarationImpl;
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
import org.seasar.mayaa.matatabi.builder.MatatabiValidateHandler;
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
	/** ルートタグ */
	private static final String ROOT_TAG = "<m:mayaa>\n</m:mayaa>";
	private Map<String, AttributeInfo> rootTagAttributeList = new LinkedHashMap<String, AttributeInfo>();

	/** タグ情報 */
	private Map<String, TagInfo> tagList = new LinkedHashMap<String, TagInfo>();
	private Map<String, TagInfo> taglibList = new LinkedHashMap<String, TagInfo>();

	/** アイコン */
	private Image icon;

	/** コンストラクタ */
	public MayaaContentAssistProcessor() {
		icon = MatatabiPlugin.getImageDescriptor("icons/mayaa_file_small.gif")
				.createImage();

		rootTagAttributeList.put("m:contentType", new AttributeInfo(
				"contentType", MatatabiPlugin.XMLNS_MAYAA, false));
		rootTagAttributeList.put("m:noCache", new AttributeInfo("noCache",
				MatatabiPlugin.XMLNS_MAYAA, false));
		rootTagAttributeList.put("m:cacheControl", new AttributeInfo(
				"cacheControl", MatatabiPlugin.XMLNS_MAYAA, false));
		rootTagAttributeList.put("m:templateSuffix", new AttributeInfo(
				"templateSuffix", MatatabiPlugin.XMLNS_MAYAA, false));
		rootTagAttributeList.put("m:extends", new AttributeInfo("extends",
				MatatabiPlugin.XMLNS_MAYAA, false));

		tagList.put("m:attribute", new TagInfo("m", MatatabiPlugin.XMLNS_MAYAA,
				"attribute", new ContextInformation("m:attribute", Messages
						.getString("description.attribute")), false,
				new ArrayList<AttributeInfo>(Arrays.asList(new AttributeInfo[] {
						new AttributeInfo("name", MatatabiPlugin.XMLNS_MAYAA,
								true),
						new AttributeInfo("value", MatatabiPlugin.XMLNS_MAYAA,
								true) }))));
		tagList.put("m:with", new TagInfo("m", MatatabiPlugin.XMLNS_MAYAA,
				"with", new ContextInformation("m:with", Messages
						.getString("description.with")), true,
				new ArrayList<AttributeInfo>()));
		tagList.put("m:comment", new TagInfo("m", MatatabiPlugin.XMLNS_MAYAA,
				"comment", new ContextInformation("m:comment", Messages
						.getString("description.comment")), true,
				new ArrayList<AttributeInfo>()));
		tagList.put("m:echo", new TagInfo("m", MatatabiPlugin.XMLNS_MAYAA,
				"echo", new ContextInformation("m:echo", Messages
						.getString("description.echo")), true,
				new ArrayList<AttributeInfo>(Arrays
						.asList(new AttributeInfo[] { new AttributeInfo("name",
								MatatabiPlugin.XMLNS_MAYAA, false) }))));
		tagList.put("m:element", new TagInfo("m", MatatabiPlugin.XMLNS_MAYAA,
				"element", new ContextInformation("m:element", Messages
						.getString("description.element")), true,
				new ArrayList<AttributeInfo>(Arrays
						.asList(new AttributeInfo[] { new AttributeInfo("name",
								MatatabiPlugin.XMLNS_MAYAA, false) }))));
		tagList.put("m:formatDate", new TagInfo("m",
				MatatabiPlugin.XMLNS_MAYAA, "formatDate",
				new ContextInformation("m:formatDate", Messages
						.getString("description.formatDate")), false,
				new ArrayList<AttributeInfo>(Arrays.asList(new AttributeInfo[] {
						new AttributeInfo("value", MatatabiPlugin.XMLNS_MAYAA,
								true),
						new AttributeInfo("pattern",
								MatatabiPlugin.XMLNS_MAYAA, false),
						new AttributeInfo("result", MatatabiPlugin.XMLNS_MAYAA,
								false) }))));
		tagList.put("m:formatNumber", new TagInfo("m",
				MatatabiPlugin.XMLNS_MAYAA, "formatNumber",
				new ContextInformation("m:formatNumber", Messages
						.getString("description.formatNumber")), false,
				new ArrayList<AttributeInfo>(Arrays.asList(new AttributeInfo[] {
						new AttributeInfo("value", MatatabiPlugin.XMLNS_MAYAA,
								true),
						new AttributeInfo("pattern",
								MatatabiPlugin.XMLNS_MAYAA, false),
						new AttributeInfo("result", MatatabiPlugin.XMLNS_MAYAA,
								false) }))));
		tagList.put("m:write", new TagInfo("m", MatatabiPlugin.XMLNS_MAYAA,
				"write", new ContextInformation("m:write", Messages
						.getString("description.write")), false,
				new ArrayList<AttributeInfo>(Arrays.asList(new AttributeInfo[] {
						new AttributeInfo("value", MatatabiPlugin.XMLNS_MAYAA,
								true),
						new AttributeInfo("default",
								MatatabiPlugin.XMLNS_MAYAA, false),
						new AttributeInfo("escapeXml",
								MatatabiPlugin.XMLNS_MAYAA, false),
						new AttributeInfo("escapeWhitespace",
								MatatabiPlugin.XMLNS_MAYAA, false),
						new AttributeInfo("escapeEol",
								MatatabiPlugin.XMLNS_MAYAA, false) }))));
		tagList.put("m:for", new TagInfo("m", MatatabiPlugin.XMLNS_MAYAA,
				"for", new ContextInformation("m:for", Messages
						.getString("description.for")), true,
				new ArrayList<AttributeInfo>(Arrays.asList(new AttributeInfo[] {
						new AttributeInfo("test", MatatabiPlugin.XMLNS_MAYAA,
								true),
						new AttributeInfo("init", MatatabiPlugin.XMLNS_MAYAA,
								false),
						new AttributeInfo("after", MatatabiPlugin.XMLNS_MAYAA,
								false),
						new AttributeInfo("max", MatatabiPlugin.XMLNS_MAYAA,
								false) }))));
		tagList.put("m:forEach", new TagInfo("m", MatatabiPlugin.XMLNS_MAYAA,
				"forEach", new ContextInformation("m:forEach", Messages
						.getString("description.forEach")), true,
				new ArrayList<AttributeInfo>(Arrays.asList(new AttributeInfo[] {
						new AttributeInfo("items", MatatabiPlugin.XMLNS_MAYAA,
								true),
						new AttributeInfo("var", MatatabiPlugin.XMLNS_MAYAA,
								true),
						new AttributeInfo("index", MatatabiPlugin.XMLNS_MAYAA,
								false) }))));
		tagList.put("m:if", new TagInfo("m", MatatabiPlugin.XMLNS_MAYAA, "if",
				new ContextInformation("m:if", Messages
						.getString("description.if")), true,
				new ArrayList<AttributeInfo>(Arrays
						.asList(new AttributeInfo[] { new AttributeInfo("test",
								MatatabiPlugin.XMLNS_MAYAA, true) }))));
		tagList.put("m:doBase", new TagInfo("m", MatatabiPlugin.XMLNS_MAYAA,
				"doBase", new ContextInformation("m:doBase", Messages
						.getString("description.doBase")), false,
				new ArrayList<AttributeInfo>()));
		tagList.put("m:doBody", new TagInfo("m", MatatabiPlugin.XMLNS_MAYAA,
				"doBody", new ContextInformation("m:doBody", Messages
						.getString("description.doBody")), false,
				new ArrayList<AttributeInfo>()));
		tagList.put("m:doRender", new TagInfo("m", MatatabiPlugin.XMLNS_MAYAA,
				"doRender", new ContextInformation("m:doRender", Messages
						.getString("description.doRender")), false,
				new ArrayList<AttributeInfo>(Arrays
						.asList(new AttributeInfo[] { new AttributeInfo("name",
								MatatabiPlugin.XMLNS_MAYAA, false) }))));
		tagList.put("m:beforeRender", new TagInfo("m",
				MatatabiPlugin.XMLNS_MAYAA, "beforeRender",
				new ContextInformation("m:beforeRender", Messages
						.getString("description.beforeRender")), false,
				new ArrayList<AttributeInfo>()));
		tagList.put("m:afterRender", new TagInfo("m",
				MatatabiPlugin.XMLNS_MAYAA, "afterRender",
				new ContextInformation("m:afterRender", Messages
						.getString("description.afterRender")), false,
				new ArrayList<AttributeInfo>()));
		tagList.put("m:insert", new TagInfo("m", MatatabiPlugin.XMLNS_MAYAA,
				"insert", new ContextInformation("m:insert", Messages
						.getString("description.insert")), false,
				new ArrayList<AttributeInfo>(Arrays.asList(new AttributeInfo[] {
						new AttributeInfo("name", MatatabiPlugin.XMLNS_MAYAA,
								false),
						new AttributeInfo("path", MatatabiPlugin.XMLNS_MAYAA,
								true) }))));
		tagList.put("m:exec", new TagInfo("m", MatatabiPlugin.XMLNS_MAYAA,
				"exec", new ContextInformation("m:exec", Messages
						.getString("description.exec")), false,
				new ArrayList<AttributeInfo>(Arrays.asList(new AttributeInfo[] {
						new AttributeInfo("script", MatatabiPlugin.XMLNS_MAYAA,
								false),
						new AttributeInfo("src", MatatabiPlugin.XMLNS_MAYAA,
								false),
						new AttributeInfo("encoding",
								MatatabiPlugin.XMLNS_MAYAA, true) }))));
		tagList.put("m:ignore", new TagInfo("m", MatatabiPlugin.XMLNS_MAYAA,
				"ignore", new ContextInformation("m:ignore", Messages
						.getString("description.ignore")), false,
				new ArrayList<AttributeInfo>()));
		tagList.put("m:null", new TagInfo("m", MatatabiPlugin.XMLNS_MAYAA,
				"null", new ContextInformation("m:null", Messages
						.getString("description.null")), false,
				new ArrayList<AttributeInfo>()));
	}

	/**
	 * タグ追加
	 */
	protected void addTagInsertionProposals(
			ContentAssistRequest contentAssistRequest, int childPosition) {
		initTaglibInfo(contentAssistRequest);

		Node parentNode = contentAssistRequest.getNode().getParentNode();
		Node node = contentAssistRequest.getNode();
		if (parentNode instanceof Document) {
			// ルートノードの追加
			if (isMatch(ROOT_TAG, contentAssistRequest.getMatchString())) {
				contentAssistRequest.addProposal(new CompletionProposal(
						ROOT_TAG, contentAssistRequest
								.getReplacementBeginPosition(),
						contentAssistRequest.getReplacementLength(), ROOT_TAG
								.length(), icon, "<m:mayaa ...>", null, ""));
			}

		} else {
			for (Entry<String, TagInfo> entry : taglibList.entrySet()) {
				TagInfo tag = entry.getValue();
				if (isMatch(tag.getContent(node), contentAssistRequest
						.getMatchString())) {
					contentAssistRequest.addProposal(new CompletionProposal(tag
							.getContent(node), contentAssistRequest
							.getReplacementBeginPosition(),
							contentAssistRequest.getReplacementLength(), tag
									.getContent(node).length(), icon, tag
									.getFullName(), tag.getDescription(), tag
									.getDescription()
									.getInformationDisplayString()));
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

		Node parentNode = contentAssistRequest.getNode().getParentNode();
		Node node = contentAssistRequest.getNode();
		if (parentNode instanceof Document) {
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
			for (Entry<String, TagInfo> entry : taglibList.entrySet()) {
				TagInfo tag = (TagInfo) entry.getValue();
				String matchString = contentAssistRequest.getMatchString();
				if (isMatch(tag.getContent(node).substring(1), matchString)) {
					contentAssistRequest.addProposal(new CompletionProposal(tag
							.getContent(node).substring(1),
							contentAssistRequest.getReplacementBeginPosition(),
							contentAssistRequest.getReplacementLength(), tag
									.getContent(node).length() - 1, icon, tag
									.getFullName(), tag.getDescription(), tag
									.getDescription()
									.getInformationDisplayString()));
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
				+ "html";
		IFile openFile = project.getFile(fileName);
		Map<String, Element> allSourceid = ParseUtil.getIdList(openFile);
		Map<String, Element> unusedSourceId = new TreeMap<String, Element>(
				allSourceid);
		for (Iterator<String> iter = ParseUtil.getXmlIdList(file).keySet()
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

	protected void addAttributeNameProposals(
			ContentAssistRequest contentAssistRequest) {
		initTaglibInfo(contentAssistRequest);

		if (contentAssistRequest.getNode().getParentNode().getNodeName()
				.equals("m:mayaa")
				&& ParseUtil.getAttributeNode((Element) contentAssistRequest
						.getNode(), MatatabiPlugin.XMLNS_MAYAA, "id") == null
				&& MatatabiValidateHandler.requiredIdAttribute(
						contentAssistRequest.getNode().getNamespaceURI(),
						contentAssistRequest.getNode().getLocalName())) {
			String idAttribute = "m:id";
			if (isMatch(idAttribute, contentAssistRequest.getMatchString())) {
				contentAssistRequest.addProposal(new CompletionProposal(
						idAttribute + "=\"\"", contentAssistRequest
								.getReplacementBeginPosition(),
						contentAssistRequest.getReplacementLength(), 4, icon,
						idAttribute, null, ""));
			}
		}

		if (contentAssistRequest.getNode().getNodeName().equals("m:mayaa")) {
			ITaglibRecord[] taglibRecords = TaglibIndex
					.getAvailableTaglibRecords(EditorUtil.getActiveFile()
							.getFullPath());
			Map<String, String> namespaces = new TreeMap<String, String>();
			for (int i = 0; i < taglibRecords.length; i++) {
				String namespaceuri = taglibRecords[i].getDescriptor().getURI();
				String namespaceprefix = taglibRecords[i].getDescriptor()
						.getShortName();
				if (ParseUtil.getAttributeNode((Element) contentAssistRequest
						.getNode(), XMLNS_URI, namespaceprefix) == null) {
					namespaces.put(namespaceprefix, namespaceuri);
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

			for (Entry<String, AttributeInfo> entry : rootTagAttributeList
					.entrySet()) {
				if (ParseUtil.getAttributeValue((Element) contentAssistRequest
						.getNode(), entry.getValue().getNamespace(), entry
						.getValue().getName()) == null
						&& isMatch(entry.getKey(), contentAssistRequest
								.getMatchString())) {
					String contents = entry.getKey() + "=\"\"";
					contentAssistRequest.addProposal(new CompletionProposal(
							contents, contentAssistRequest
									.getReplacementBeginPosition(),
							contentAssistRequest.getReplacementLength(),
							contents.length() + 1, icon, contents, null, ""));
				}
			}

		}

		if (taglibList
				.containsKey(contentAssistRequest.getNode().getNodeName())) {
			List<AttributeInfo> attributes = taglibList.get(
					contentAssistRequest.getNode().getNodeName())
					.getAttributeInfos();
			for (AttributeInfo attributeInfo : attributes) {
				if (ParseUtil.getAttributeNode((Element) contentAssistRequest
						.getNode(), attributeInfo.getNamespace(), attributeInfo
						.getName()) == null
						&& isMatch(attributeInfo.getName(),
								contentAssistRequest.getMatchString())) {
					contentAssistRequest.addProposal(new CompletionProposal(
							attributeInfo.getName() + "=\"\"",
							contentAssistRequest.getReplacementBeginPosition(),
							contentAssistRequest.getReplacementLength(),
							attributeInfo.getName().length() + 2, icon,
							attributeInfo.getName(), null, ""));
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
		taglibList.putAll(tagList);

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
					TagInfo tag = new TagInfo(prefix, namespaceuri, node
							.getNodeName(), new ContextInformation(nodeName,
							node.getDescription() == null ? "" : node
									.getDescription()), !node.getBodycontent()
							.equals(JSP11TLDNames.CONTENT_EMPTY),
							new ArrayList<AttributeInfo>());
					if (!taglibList.containsKey(tag.getFullName())) {
						taglibList.put(tag.getFullName(), tag);
						for (int k = 0; k < node.getAttributes().getLength(); k++) {
							CMAttributeDeclarationImpl attr = (CMAttributeDeclarationImpl) node
									.getAttributes().item(k);
							tag.getAttributeInfos().add(
									new AttributeInfo(attr.getNodeName(),
											namespaceuri, attr.isRequired()));
						}
					}
				}
			}
		}
	}

	/**
	 * タグ情報
	 */
	public static class TagInfo {
		private String prefix;
		private String namespaceURI;
		private String name;
		private ContextInformation description;
		private boolean hasBody;
		private List<AttributeInfo> attributeInfos;

		public TagInfo(String prefix, String namespaceURI, String name,
				ContextInformation description, boolean hasBody,
				List<AttributeInfo> attributeInfos) {
			this.prefix = prefix;
			this.namespaceURI = namespaceURI;
			this.name = name;
			this.description = description;
			this.hasBody = hasBody;
			this.attributeInfos = attributeInfos;
		}

		public String getFullName() {
			return prefix + ":" + name;
		}

		public String getContent(Node node) {
			StringBuilder stringBuilder = new StringBuilder();
			if (node.getParentNode().getNodeName().equals("m:mayaa")
					&& MatatabiValidateHandler.requiredIdAttribute(
							namespaceURI, name)) {
				stringBuilder.append(" m:id=\"\"");
			}
			for (AttributeInfo attributeInfo : attributeInfos) {
				if (attributeInfo.isRequired()) {
					stringBuilder.append(" " + attributeInfo.name + "=\"\"");
				}
			}
			return hasBody ? "<" + getFullName() + stringBuilder + ">" + "</"
					+ getFullName() + ">" : "<" + getFullName() + stringBuilder
					+ " />";
		}

		public String getPrefix() {
			return prefix;
		}

		public String getName() {
			return name;
		}

		public ContextInformation getDescription() {
			return description;
		}

		public boolean isHasBody() {
			return hasBody;
		}

		public List<AttributeInfo> getAttributeInfos() {
			return attributeInfos;
		}
	}

	/**
	 * 属性情報
	 */
	public static class AttributeInfo {
		private String name;
		private String namespace;
		private boolean required;

		public AttributeInfo(String name, String namespace, boolean required) {
			this.name = name;
			this.namespace = namespace;
			this.required = required;
		}

		public String getName() {
			return name;
		}

		public boolean isRequired() {
			return required;
		}

		public String getNamespace() {
			return namespace;
		}

	}
}
