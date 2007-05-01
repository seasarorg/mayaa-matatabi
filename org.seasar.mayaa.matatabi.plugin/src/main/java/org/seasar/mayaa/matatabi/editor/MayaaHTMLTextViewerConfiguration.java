package org.seasar.mayaa.matatabi.editor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.wst.html.ui.StructuredTextViewerConfigurationHTML;
import org.seasar.mayaa.matatabi.editor.hyperlink.HtmlHyperlinkDetector;

/**
 * Htmlファイル向けのWTPエディタ設定
 */
public class MayaaHTMLTextViewerConfiguration extends
		StructuredTextViewerConfigurationHTML {

	public IHyperlinkDetector[] getHyperlinkDetectors(ISourceViewer sourceViewer) {
		List result = new ArrayList(0);
		result.add(new HtmlHyperlinkDetector());

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