package org.seasar.mayaa.matatabi.editor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.wst.xml.ui.StructuredTextViewerConfigurationXML;
import org.seasar.mayaa.matatabi.editor.contentsassist.MayaaContentAssistProcessor;
import org.seasar.mayaa.matatabi.editor.hyperlink.MayaaXMLHyperlinkDetector;

/**
 * Mayaa向けのWTPエディタ設定
 */
public class MayaaXMLTextViewerConfiguration extends
		StructuredTextViewerConfigurationXML {
	@Override
	protected IContentAssistProcessor[] getContentAssistProcessors(
			ISourceViewer sourceViewer, String string) {
		List result = new ArrayList(0);
		result.add(new MayaaContentAssistProcessor());

		return (IContentAssistProcessor[]) result
				.toArray(new IContentAssistProcessor[0]);
	}

	public IHyperlinkDetector[] getHyperlinkDetectors(ISourceViewer sourceViewer) {
		List result = new ArrayList(0);
		result.add(new MayaaXMLHyperlinkDetector());

		IHyperlinkDetector[] superDetectors = super
				.getHyperlinkDetectors(sourceViewer);
		for (int m = 0; m < superDetectors.length; m++) {
			IHyperlinkDetector detector = superDetectors[m];
			if (!result.contains(detector)) {
				result.add(detector);
			}
		}

		return (IHyperlinkDetector[]) result.toArray(new IHyperlinkDetector[0]);
	}
}