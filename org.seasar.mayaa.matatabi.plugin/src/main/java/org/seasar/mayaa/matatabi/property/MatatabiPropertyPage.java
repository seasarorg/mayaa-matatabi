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
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
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
	public static final String UNDEFINE_ID_ATTRIBUTE = "undefineIdAttribute";

	public static final String DUPLICATE_ID_ATTRIBUTE = "duplicateIdAttribute";

	public static final String NOTEXIST_ID_ATTRIBUTE = "notexistIdAttribute";

	public static final String INVALID_ID_ATTRIBUTE = "invalidIdAttribute";

	public static final String MISSING_ID_ATTRIBUTE = "missingIdAttribute";

	private Text fileExtension;

	private Button useMatatabi;

	private TabFolder folder;

	private Combo missingIdAttribute;

	private Combo invalidIdAttribute;

	private Combo notexistIdAttribute;

	private Combo duplicateIdAttribute;

	private Combo undefineIdAttribute;

	/**
	 * ページ初期化
	 */
	protected Control createContents(Composite parent) {
		IProject project = getProject();
		Composite composite = drawPage(parent, project);
		return composite;
	}

	private Composite drawPage(Composite parent, IProject project) {
		Composite panel = new Composite(parent, SWT.NULL);
		GridLayout g = new GridLayout(1, true);
		g.marginHeight = 0;
		g.marginWidth = 0;
		panel.setLayout(g);
		panel.setFont(parent.getFont());

		this.useMatatabi = createCheckPart(panel, "matatabiを使用する");
		folder = createTabFolder(panel);

		Composite generalPanel = createPanel(folder, 2);
		Composite errorMarkerPanel = createPanel(folder, 2);

		this.fileExtension = createTextPart(generalPanel, "Mayaaファイル拡張子");
		missingIdAttribute = createErrorMarkerCombo(errorMarkerPanel,
				"ルート要素直下のid属性");
		invalidIdAttribute = createErrorMarkerCombo(errorMarkerPanel,
				"無効な位置のid属性");
		notexistIdAttribute = createErrorMarkerCombo(errorMarkerPanel,
				"テンプレートに存在しないid属性");
		duplicateIdAttribute = createErrorMarkerCombo(errorMarkerPanel,
				"重複するid属性");
		undefineIdAttribute = createErrorMarkerCombo(errorMarkerPanel,
				"未定義のid属性");

		TabItem generalTabItem = new TabItem(folder, SWT.NULL);
		generalTabItem.setText("基本設定");
		generalTabItem.setControl(generalPanel);
		TabItem errorTabItem = new TabItem(folder, SWT.NULL);
		errorTabItem.setText("エラーマーカー");
		errorTabItem.setControl(errorMarkerPanel);

		loadStore(project);

		return panel;
	}

	private Combo createErrorMarkerCombo(Composite composite, String label) {
		Combo combo = createComboPart(composite, label);
		combo.add("エラー");
		combo.add("警告");
		combo.add("情報");
		combo.add("無視");

		combo.select(1);

		return combo;
	}

	private TabFolder createTabFolder(Composite panel) {
		TabFolder tabFolder = new TabFolder(panel, SWT.NONE);
		tabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));
		tabFolder.setFont(panel.getFont());
		return tabFolder;
	}

	private Composite createPanel(Composite parent, int numColumns) {
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout(numColumns, false);
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		return composite;
	}

	private void loadStore(IProject project) {
		setPreferenceStore(new ScopedPreferenceStore(new ProjectScope(
				(IProject) getElement()), "org.seasar.mayaa.matatabi"));
		IPreferenceStore store = getPreferenceStore();
		this.fileExtension.setText(store.getString("fileExtension"));
		if (!store.getString(MISSING_ID_ATTRIBUTE).equals("")) {
			this.missingIdAttribute.select(store.getInt(MISSING_ID_ATTRIBUTE));
		}
		if (!store.getString(INVALID_ID_ATTRIBUTE).equals("")) {
			this.invalidIdAttribute.select(store.getInt(INVALID_ID_ATTRIBUTE));
		}
		if (!store.getString(NOTEXIST_ID_ATTRIBUTE).equals("")) {
			this.notexistIdAttribute
					.select(store.getInt(NOTEXIST_ID_ATTRIBUTE));
		}
		if (!store.getString(DUPLICATE_ID_ATTRIBUTE).equals("")) {
			this.duplicateIdAttribute.select(store
					.getInt(DUPLICATE_ID_ATTRIBUTE));
		}
		if (!store.getString(UNDEFINE_ID_ATTRIBUTE).equals("")) {
			this.undefineIdAttribute
					.select(store.getInt(UNDEFINE_ID_ATTRIBUTE));
		}
		try {
			if (project.hasNature(MatatabiNature.NATURE_ID)) {
				this.useMatatabi.setSelection(true);
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
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
		this.missingIdAttribute.select(1);
		this.invalidIdAttribute.select(1);
		this.notexistIdAttribute.select(1);
		this.duplicateIdAttribute.select(1);
		this.undefineIdAttribute.select(1);

		super.performDefaults();
	}

	public boolean performOk() {
		boolean result = false;
		IProject project = getProject();
		if (project != null) {
			IPreferenceStore store = getPreferenceStore();

			store.setValue("fileExtension", fileExtension.getText());
			store.setValue(MISSING_ID_ATTRIBUTE, missingIdAttribute
					.getSelectionIndex());
			store.setValue(INVALID_ID_ATTRIBUTE, invalidIdAttribute
					.getSelectionIndex());
			store.setValue(NOTEXIST_ID_ATTRIBUTE, notexistIdAttribute
					.getSelectionIndex());
			store.setValue(DUPLICATE_ID_ATTRIBUTE, duplicateIdAttribute
					.getSelectionIndex());
			store.setValue(UNDEFINE_ID_ATTRIBUTE, undefineIdAttribute
					.getSelectionIndex());
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

	private Combo createComboPart(Composite composite, String label) {
		return createComboPart(composite, label, SWT.SINGLE | SWT.BORDER);
	}

	private Combo createComboPart(Composite composite, String label, int style) {
		Label l = new Label(composite, SWT.NONE);
		l.setText(label);
		Combo combo = new Combo(composite, SWT.READ_ONLY);

		return combo;
	}
}
