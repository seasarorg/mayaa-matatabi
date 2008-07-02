package org.seasar.mayaa.matatabi.editor.configuration;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.wst.html.core.text.IHTMLPartitions;
import org.eclipse.wst.html.ui.StructuredTextViewerConfigurationHTML;
import org.seasar.mayaa.matatabi.editor.contentsassist.MayaaHTMLContentAssistProcessor;
import org.seasar.mayaa.matatabi.editor.hyperlink.HtmlHyperlinkDetector;

/**
 * Htmlファイル向けのWTPエディタ設定
 */
public class MayaaHTMLTextViewerConfiguration extends
		StructuredTextViewerConfigurationHTML {

	@Override
	protected IContentAssistProcessor[] getContentAssistProcessors(
			ISourceViewer sourceViewer, String partitionType) {

		if ((partitionType == IHTMLPartitions.HTML_DEFAULT)
				|| (partitionType == IHTMLPartitions.HTML_COMMENT)) {
			return new IContentAssistProcessor[] { new MayaaHTMLContentAssistProcessor() };
		} else {
			return super
					.getContentAssistProcessors(sourceViewer, partitionType);
		}
	}

	@Override
	public IHyperlinkDetector[] getHyperlinkDetectors(ISourceViewer sourceViewer) {
		List<IHyperlinkDetector> result = new ArrayList<IHyperlinkDetector>(0);
		result.add(new HtmlHyperlinkDetector());

		IHyperlinkDetector[] superDetectors = super
				.getHyperlinkDetectors(sourceViewer);
		for (int m = 0; m < superDetectors.length; m++) {
			IHyperlinkDetector detector = superDetectors[m];
			if (!result.contains(detector)) {
				result.add(detector);
			}
		}

		return result.toArray(new IHyperlinkDetector[0]);
	}

}