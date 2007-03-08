package org.seasar.mayaa.matatabi.editor.contentsassist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.xml.ui.internal.contentassist.ContentAssistRequest;
import org.eclipse.wst.xml.ui.internal.contentassist.XMLContentAssistProcessor;
import org.seasar.mayaa.matatabi.MatatabiPlugin;
import org.seasar.mayaa.matatabi.util.EditorUtil;
import org.seasar.mayaa.matatabi.util.ParseUtil;
import org.w3c.dom.Document;

public class MayaaContentAssistProcessor extends XMLContentAssistProcessor {
	private String ROOT_TAG = "<m:mayaa>\n</m:mayaa>";

	private static List<String> tagList = new ArrayList<String>();

	private static List<IContextInformation> tagContextInformationList = new ArrayList<IContextInformation>();

	private static Map<String, String[]> attributeMap = new HashMap<String, String[]>();

	private Image icon;

	static {
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

	public MayaaContentAssistProcessor() {
		icon = MatatabiPlugin.getImageDescriptor("icons/mayaa_file_small.gif")
				.createImage();
	}

	@Override
	protected void addTagInsertionProposals(
			ContentAssistRequest contentAssistRequest, int childPosition) {
		if (contentAssistRequest.getNode().getParentNode() instanceof Document) {
			if (isMatch(ROOT_TAG, contentAssistRequest.getMatchString())) {
				contentAssistRequest.addProposal(new CompletionProposal(
						ROOT_TAG, contentAssistRequest
								.getReplacementBeginPosition(),
						contentAssistRequest.getReplacementLength(), ROOT_TAG
								.length(), icon, "<m:mayaa ...>", null, ""));
			}

		} else {
			for (int i = 0; i < tagList.size(); i++) {
				String tag = tagList.get(i);
				if (isMatch(tag, contentAssistRequest.getMatchString())) {
					contentAssistRequest.addProposal(new CompletionProposal(
							tag, contentAssistRequest
									.getReplacementBeginPosition(),
							contentAssistRequest.getReplacementLength(), tag
									.length(), icon, tag,
							tagContextInformationList.get(i),
							tagContextInformationList.get(i)
									.getInformationDisplayString()));
				}
			}
		}
		super.addTagInsertionProposals(contentAssistRequest, childPosition);
	}

	@Override
	protected void addTagNameProposals(
			ContentAssistRequest contentAssistRequest, int childPosition) {
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
			for (int i = 0; i < tagList.size(); i++) {
				String tag = tagList.get(i);
				String matchString = contentAssistRequest.getMatchString();
				if (isMatch(tag.substring(1), matchString)) {
					contentAssistRequest.addProposal(new CompletionProposal(tag
							.substring(1), contentAssistRequest
							.getReplacementBeginPosition(),
							contentAssistRequest.getReplacementLength(), tag
									.length() - 1, icon, tag,
							tagContextInformationList.get(i),
							tagContextInformationList.get(i)
									.getInformationDisplayString()));
				}
			}
		}
		super.addTagNameProposals(contentAssistRequest, childPosition);
	}

	@Override
	protected void addAttributeValueProposals(
			ContentAssistRequest contentAssistRequest) {

		IFile file = EditorUtil.getActiveFile();
		IProject project = file.getProject();
		IPath path = file.getProjectRelativePath();

		String fileName = path.toString();
		fileName = fileName.substring(0, fileName.length()
				- path.getFileExtension().length())
				+ "html";
		IFile openFile = project.getFile(fileName);
		Set sourceid = ParseUtil.getIdList(openFile);
		sourceid = new TreeSet(sourceid);

		for (Iterator iter = sourceid.iterator(); iter.hasNext();) {
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
		super.addAttributeValueProposals(contentAssistRequest);
	}

	@Override
	protected void addAttributeNameProposals(
			ContentAssistRequest contentAssistRequest) {
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

		String[] attributes = attributeMap.get(contentAssistRequest.getNode()
				.getNodeName());
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

	private boolean isMatch(String contents, String matchString) {
		return (matchString.length() == 0 || contents.startsWith(matchString));
	}
}
