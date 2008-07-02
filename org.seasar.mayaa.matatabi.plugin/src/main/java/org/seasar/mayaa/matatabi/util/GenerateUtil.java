package org.seasar.mayaa.matatabi.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.XMLConstants;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.preference.IPreferenceStore;
import org.jaxen.Context;
import org.jaxen.ContextSupport;
import org.jaxen.JaxenException;
import org.jaxen.SimpleNamespaceContext;
import org.jaxen.SimpleVariableContext;
import org.jaxen.XPathFunctionContext;
import org.jaxen.dom.DocumentNavigator;
import org.jaxen.pattern.Pattern;
import org.jaxen.pattern.PatternParser;
import org.jaxen.saxpath.SAXPathException;
import org.seasar.mayaa.matatabi.MatatabiPlugin;
import org.seasar.mayaa.matatabi.property.NamespaceTableViewer.Namespace;
import org.seasar.mayaa.matatabi.property.ReplaceRuleTableViewer.ReplaceRule;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * コード生成に関する処理を行う
 * 
 * @author matoba
 */
public class GenerateUtil {

	/**
	 * idに対応するMayaaタグを生成します。
	 * 
	 * @param idlist
	 *            生成対象のidのリスト
	 * @return
	 */
	public static String genereteTags(Map<String, Element> idlist,
			IPreferenceStore store) {
		Map<String, ReplaceRule> replaceRules = PreferencesUtil
				.getReplaceRules(store);
		StringBuffer buffer = new StringBuffer();
		ReplaceRule defaultReplaceRule = replaceRules.remove("*");
		for (Iterator<Entry<String, Element>> iter = idlist.entrySet()
				.iterator(); iter.hasNext();) {
			Entry<String, Element> entry = iter.next();
			String templateString = null;

			for (Entry<String, ReplaceRule> replaceRuleEntry : replaceRules
					.entrySet()) {

				ReplaceRule replaceRule = replaceRuleEntry.getValue();
				if (!"".equals(replaceRule.getTag())
						&& isTargetNode(replaceRule.getTag(), entry.getValue())) {
					templateString = replaceRule.getReplace();
				}

			}
			if (templateString == null) {
				templateString = defaultReplaceRule.getReplace();
			}
			try {
				buffer.append("\t"
						+ parse(entry.getValue(), entry.getKey(),
								templateString) + "\n");
			} catch (IOException e) {
				MatatabiPlugin.errorLog(e);
			}
		}

		return buffer.toString();
	}

	private static String genereteFile(String string, IPreferenceStore store) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		buffer.append("<m:mayaa ");
		List<Namespace> namespaces = PreferencesUtil.getNamespaces(store);
		for (Iterator<Namespace> iter = namespaces.iterator(); iter.hasNext();) {
			Namespace namespace = iter.next();
			if (namespace.getNamespaceAttribute() != null) {
				buffer.append(namespace.getNamespaceAttribute() + "\n" + "\t");
			}
		}
		buffer.delete(buffer.length() - 2, buffer.length());
		buffer.append(">\n");
		buffer.append(string);
		buffer.append("</m:mayaa>\n");
		return buffer.toString();
	}

	/**
	 * 現在開いているHTMLファイルに対応するMayaaファイルを生成します。
	 */
	public static void generateMayaaFile() {
		IFile file = EditorUtil.getActiveFile();
		IProject project = file.getProject();
		IPath path = file.getProjectRelativePath();

		IFile htmlFile = project.getFile(path.toString());
		Map<String, Element> idlist = ParseUtil.getIdList(htmlFile);
		for (String id : ParseUtil.getDefaultIdList(htmlFile.getParent())
				.keySet()) {
			idlist.remove(id);
		}

		String fileContents = genereteFile(genereteTags(idlist, PreferencesUtil
				.getPreference(file)), PreferencesUtil.getPreference(file));
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
				fileContents.getBytes());
		try {
			String fileName = path.toString();
			if (fileName.indexOf("$") > 0) {
				fileName = fileName.substring(0, fileName.lastIndexOf("$"))
						+ ".mayaa";
			} else {
				fileName = fileName.substring(0, fileName.length()
						- path.getFileExtension().length())
						+ "mayaa";
			}
			IFile openFile = project.getFile(fileName);
			openFile.create(byteArrayInputStream, true, EditorUtil
					.getProgressMonitor());
		} catch (CoreException e) {
			MatatabiPlugin.errorLog(e);
		}
	}

	private static String parse(Element element, String id,
			String templateString) throws IOException {
		try {
			Velocity.init();
		} catch (Exception e) {
			MatatabiPlugin.errorLog(e);
		}
		VelocityContext context = new VelocityContext();
		Map<String, Attr> attributes = new HashMap<String, Attr>();
		for (int i = 0; i < element.getAttributes().getLength(); i++) {
			Attr attr = (Attr) element.getAttributes().item(i);
			attributes.put(attr.getName(), attr);
		}
		context.put("id", id);
		if (id.split("-").length > 1) {
			List<String> idWithCommand = new ArrayList<String>(Arrays.asList(id
					.split("-")));
			context.put("id_command", idWithCommand.remove(0));
			for (int i = 0; i < idWithCommand.size(); i++) {
				context.put("id_variable" + i, idWithCommand.get(i));
			}
		}
		context.put("name", element.getNodeName());
		context.put("hasBody", element.hasChildNodes());
		context.put("attributes", attributes);

		VelocityEngine engine = new VelocityEngine();
		StringWriter out = new StringWriter();
		engine.evaluate(context, out, "MATATABI", templateString);

		return out.getBuffer().toString();
	}

	public static boolean isTargetNode(String expression, Element element) {
		SimpleNamespaceContext nsContext = new SimpleNamespaceContext();
		Element root = element.getOwnerDocument().getDocumentElement();
		if (root.getLocalName() == null) {
			Node node = root.getFirstChild();
			do {
				if (node instanceof Element) {
					root = (Element) node;
					break;
				}
			} while ((node = root.getNextSibling()) != null);
		}
		NamedNodeMap attrs = root.getAttributes();
		String xmlns = "xmlns";
		for (int i = 0; i < attrs.getLength(); i++) {
			Node attr = attrs.item(i);
			String[] name = attr.getNodeName().split(":");
			if (xmlns.equals(name[0])) {
				if (name.length == 1) {
					nsContext.addNamespace(XMLConstants.DEFAULT_NS_PREFIX, attr
							.getNodeValue());
				} else {
					nsContext.addNamespace(name[1], attr.getNodeValue());
				}
			}
		}
		ContextSupport support = new ContextSupport(nsContext,
				XPathFunctionContext.getInstance(),
				new SimpleVariableContext(), new DocumentNavigator());
		Context context = new Context(support);
		try {
			Pattern pattern = PatternParser.parse(expression);
			return pattern.matches(element, context);
		} catch (JaxenException e) {
			throw new RuntimeException(e);
		} catch (SAXPathException e) {
			throw new RuntimeException(e);
		}
	}
}
