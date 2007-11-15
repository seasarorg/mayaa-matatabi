package org.seasar.mayaa.matatabi.editor.configuration;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.wst.html.ui.StructuredTextViewerConfigurationHTML;
import org.seasar.mayaa.matatabi.editor.contentsassist.HTMLContentAssistProcessor;
import org.seasar.mayaa.matatabi.editor.hyperlink.HtmlHyperlinkDetector;

/**
 * Htmlファイル向けのWTPエディタ設定
 */
public class MayaaHTMLTextViewerConfiguration extends
		StructuredTextViewerConfigurationHTML {

	@Override
	protected IContentAssistProcessor[] getContentAssistProcessors(
			ISourceViewer sourceViewer, String string) {
		List<IContentAssistProcessor> result = new ArrayList<IContentAssistProcessor>(
				0);
		result.add(new HTMLContentAssistProcessor());

		return result.toArray(new IContentAssistProcessor[0]);
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