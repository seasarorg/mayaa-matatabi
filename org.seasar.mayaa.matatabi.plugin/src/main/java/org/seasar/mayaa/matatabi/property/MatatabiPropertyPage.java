package org.seasar.mayaa.matatabi.property;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PropertyPage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.seasar.mayaa.matatabi.nature.MatatabiNature;

/**
 * 設定ページ
 * 
 * @author matoba
 */
public class MatatabiPropertyPage extends PropertyPage {
	private Text fileExtension;

	private Button useMatatabi;

	/**
	 * ページ初期化
	 */
	protected Control createContents(Composite parent) {
		IProject project = getProject();

		this.useMatatabi = createCheckPart(parent, "matatabiを使用する");

		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);
		GridData data = new GridData(GridData.FILL);
		data.grabExcessHorizontalSpace = true;
		composite.setLayoutData(data);

		this.fileExtension = createTextPart(composite, "Mayaaファイル拡張子");

		setPreferenceStore(new ScopedPreferenceStore(new ProjectScope(
				(IProject) getElement()), "org.seasar.mayaa.matatabi"));
		IPreferenceStore store = getPreferenceStore();
		this.fileExtension.setText(store.getString("fileExtension"));
		try {
			if (project.hasNature(MatatabiNature.NATURE_ID)) {
				this.useMatatabi.setSelection(true);
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}

		return composite;
	}

	protected IPreferenceStore doGetPreferenceStore() {
		IProject project = getProject();
		ScopedPreferenceStore store = null;
		if (project != null) {
			setPreferenceStore(new ScopedPreferenceStore(new ProjectScope(
					(IProject) getElement()), "org.seasar.mayaa.matatabi"));
		}
		return store;
	}

	/**
	 * デフォルトに戻す
	 */
	protected void performDefaults() {
		this.fileExtension.setText("");
		this.useMatatabi.setSelection(false);
		super.performDefaults();
	}

	public boolean performOk() {
		boolean result = false;
		IProject project = getProject();
		if (project != null) {
			IPreferenceStore store = getPreferenceStore();

			store.setValue("fileExtension", fileExtension.getText());
		}
		result = true;

		try {
			if (useMatatabi.getSelection()
					&& !project.hasNature(MatatabiNature.NATURE_ID)) {
				IProjectDescription desc = project.getDescription();
				List natureIds = new ArrayList(Arrays.asList(desc
						.getNatureIds()));
				natureIds.add(MatatabiNature.NATURE_ID);
				desc.setNatureIds((String[]) natureIds
						.toArray(new String[natureIds.size()]));
				project.setDescription(desc, null);
			} else if (project.hasNature(MatatabiNature.NATURE_ID)) {
				IProjectDescription desc = project.getDescription();
				List natureIds = new ArrayList(Arrays.asList(desc
						.getNatureIds()));
				natureIds.remove(MatatabiNature.NATURE_ID);
				desc.setNatureIds((String[]) natureIds
						.toArray(new String[natureIds.size()]));
				project.setDescription(desc, null);
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}

		return result;
	}

	private IProject getProject() {
		IProject result = null;
		IAdaptable adaptable = getElement();
		if (adaptable != null) {
			result = (IProject) adaptable.getAdapter(IProject.class);
		}
		return result;
	}

	private Text createTextPart(Composite composite, String label) {
		return createTextPart(composite, label, SWT.SINGLE | SWT.BORDER);
	}

	private Text createTextPart(Composite composite, String label, int style) {
		Label l = new Label(composite, SWT.NONE);
		l.setText(label);
		Text txt = new Text(composite, style);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		txt.setLayoutData(data);
		return txt;
	}

	private Button createCheckPart(Composite composite, String label) {
		return createCheckPart(composite, label, SWT.SINGLE | SWT.BORDER);
	}

	private Button createCheckPart(Composite composite, String label, int style) {
		Button check = new Button(composite, SWT.CHECK);
		check.setText(label);
		return check;
	}
}
