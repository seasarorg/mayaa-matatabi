package org.seasar.mayaa.matatabi.property;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.internal.core.search.JavaWorkspaceScope;
import org.eclipse.jdt.internal.ui.dialogs.OpenTypeSelectionDialog;
import org.eclipse.jdt.ui.text.IJavaColorConstants;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.PropertyPage;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.seasar.mayaa.matatabi.MatatabiPlugin;
import org.seasar.mayaa.matatabi.nature.MatatabiNature;
import org.seasar.mayaa.matatabi.property.NamespaceTableViewer.Namespace;
import org.seasar.mayaa.matatabi.property.ReplaceRuleTableViewer.ReplaceRule;
import org.seasar.mayaa.matatabi.util.PreferencesUtil;

/**
 * 設定ページ
 */
public class MatatabiPropertyPage extends PropertyPage {
	public static final String BUILDER_ID = "org.seasar.mayaa.matatabi.MatatabiBuilder";

	public static final String REPLACE_RULE = "replaceRule";

	public static final String NAMESPACES = "namespaces";

	public static final String UNDEFINE_ID_ATTRIBUTE = "undefineIdAttribute";

	public static final String DUPLICATE_ID_ATTRIBUTE = "duplicateIdAttribute";

	public static final String NOTEXIST_ID_ATTRIBUTE = "notexistIdAttribute";

	public static final String INVALID_ID_ATTRIBUTE = "invalidIdAttribute";

	public static final String MISSING_ID_ATTRIBUTE = "missingIdAttribute";

	public static final String ONLY_MAYAA_ID = "onlyMayaaId";

	public static final String JAVA_SOURCE_PATH = "javaSourcePath";

	public static final String WEB_ROOT_PATH = "webRootPath";

	public static final String DEFAULT_PACKAGE = "defaultPackage";

	public static final String DEFAULT_PACKAGE_SUFFIX = "defaultPackageSuffix";

	public static final String JAVA_CLASS_SUFFIX = "javaClassSuffix";

	public static final String JAVA_SUPER_CLASS = "javaSuperClass";

	public static final String JAVA_SUPER_INTERFACE = "javaSuperInterface";

	private Button useMatatabi;

	private Button useValidator;

	private Button onlyMayaaId;

	private TabFolder folder;

	private Combo missingIdAttribute;
	private Combo invalidIdAttribute;
	private Combo notexistIdAttribute;
	private Combo duplicateIdAttribute;
	private Combo undefineIdAttribute;

	private Text javaSourcePath;
	private Text webRootPath;
	private Text defaultPackage;
	private Text defaultPackageSuffix;
	private Text javaClassSuffix;
	private Text javaSuperClass;
	private Text javaSuperInterface1;
	private Text javaSuperInterface2;
	private Text javaSuperInterface3;

	private ReplaceRuleTableViewer replaceRuleTableViewer;

	private NamespaceTableViewer namespaceTableViewer;

	/**
	 * ページ初期化
	 */
	@Override
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

		useMatatabi = createCheckPart(panel, "matatabiを使用する");
		folder = createTabFolder(panel);

		Composite configMarkerPanel = createPanel(folder, 3);
		javaSourcePath = createFolderSelectionText(project, configMarkerPanel,
				"Javaソースパス");
		webRootPath = createFolderSelectionText(project, configMarkerPanel,
				"Webルートパス");
		defaultPackage = createJavaPackageSelectionText(project,
				configMarkerPanel, "Javaデフォルトパッケージ");
		defaultPackageSuffix = createJavaPackageSelectionText(project,
				configMarkerPanel, "Javaデフォルトパッケージ(サフィックス)");
		javaClassSuffix = createText(project, configMarkerPanel,
				"Javaクラスサフィックス");
		javaSuperClass = createJavaTypeSelectionText(project,
				configMarkerPanel, "Javaスーパークラス", IJavaSearchConstants.CLASS);
		javaSuperInterface1 = createJavaTypeSelectionText(project,
				configMarkerPanel, "Javaインターフェイス1",
				IJavaSearchConstants.INTERFACE);
		javaSuperInterface2 = createJavaTypeSelectionText(project,
				configMarkerPanel, "Javaインターフェイス2",
				IJavaSearchConstants.INTERFACE);
		javaSuperInterface3 = createJavaTypeSelectionText(project,
				configMarkerPanel, "Javaインターフェイス3",
				IJavaSearchConstants.INTERFACE);
		onlyMayaaId = createCheckPart(configMarkerPanel,
				"テンプレートファイルでm:idのみ処理対象とする");
		GridData data = new GridData();
		data.horizontalSpan = 2;
		onlyMayaaId.setLayoutData(data);

		Composite errorMarkerPanel = createPanel(folder, 2);
		useValidator = createCheckPart(errorMarkerPanel,
				"MayaaファイルのValidatorを有効にする");
		missingIdAttribute = createErrorMarkerCombo(errorMarkerPanel,
				"ルート要素直下のid,xpath属性の必須チェック");
		invalidIdAttribute = createErrorMarkerCombo(errorMarkerPanel,
				"無効な位置のid,xpath属性のチェック");
		notexistIdAttribute = createErrorMarkerCombo(errorMarkerPanel,
				"テンプレートに存在しないid属性のチェック");
		duplicateIdAttribute = createErrorMarkerCombo(errorMarkerPanel,
				"重複するid属性のチェック");
		undefineIdAttribute = createErrorMarkerCombo(errorMarkerPanel,
				"未定義のid属性のチェック");

		Composite generatePanel = createPanel(folder, 1);
		Label namespaceLabel = new Label(generatePanel, SWT.BOLD);
		namespaceLabel.setText("Mayaaファイル名前空間");

		namespaceTableViewer = new NamespaceTableViewer(generatePanel,
				SWT.SINGLE | SWT.V_SCROLL);
		Label replaceRuleLabel = new Label(generatePanel, SWT.BOLD);
		replaceRuleLabel.setText("タグ変換ルール");
		replaceRuleTableViewer = new ReplaceRuleTableViewer(generatePanel,
				SWT.SINGLE | SWT.V_SCROLL);
		TabItem configTabItem = new TabItem(folder, SWT.NULL);
		configTabItem.setText("動作設定");
		configTabItem.setControl(configMarkerPanel);
		TabItem errorTabItem = new TabItem(folder, SWT.NULL);
		errorTabItem.setText("バリデーション");
		errorTabItem.setControl(errorMarkerPanel);
		TabItem generateTabItem = new TabItem(folder, SWT.NULL);
		generateTabItem.setText("自動生成");
		generateTabItem.setControl(generatePanel);

		loadStore(project);

		return panel;
	}

	private Text createFolderSelectionText(IProject project,
			Composite configMarkerPanel, String labelText) {
		Label label = new Label(configMarkerPanel, SWT.NONE);
		label.setText(labelText);
		Text text = new Text(configMarkerPanel, SWT.SINGLE | SWT.BORDER);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		text.setLayoutData(data);
		Button srcpath = new Button(configMarkerPanel, SWT.PUSH);
		srcpath.setText("選択");

		srcpath.addSelectionListener(new FolderSelectionAdapter(getShell(),
				project, text));

		return text;
	}

	private Text createJavaTypeSelectionText(IProject project,
			Composite configMarkerPanel, String labelText, int type) {
		Label label = new Label(configMarkerPanel, SWT.NONE);
		label.setText(labelText);
		Text text = new Text(configMarkerPanel, SWT.SINGLE | SWT.BORDER);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		text.setLayoutData(data);
		Button srcpath = new Button(configMarkerPanel, SWT.PUSH);
		srcpath.setText("選択");

		srcpath.addSelectionListener(new JavaTypeSelectionAdapter(getShell(),
				text, type));

		return text;
	}

	private Text createJavaPackageSelectionText(IProject project,
			Composite configMarkerPanel, String labelText) {
		Label label = new Label(configMarkerPanel, SWT.NONE);
		label.setText(labelText);
		final Text text = new Text(configMarkerPanel, SWT.SINGLE | SWT.BORDER);
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		label = new Label(configMarkerPanel, SWT.NONE);

		return text;
	}

	private Text createText(IProject project, Composite configMarkerPanel,
			String labelText) {
		Label label = new Label(configMarkerPanel, SWT.NONE);
		label.setText(labelText);
		final Text text = new Text(configMarkerPanel, SWT.SINGLE | SWT.BORDER);
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		label = new Label(configMarkerPanel, SWT.NONE);

		return text;
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
		setPreferenceStore(new ScopedPreferenceStore(new ProjectScope(project),
				"org.seasar.mayaa.matatabi"));
		IPreferenceStore store = getPreferenceStore();

		if (store.getBoolean(ONLY_MAYAA_ID)) {
			onlyMayaaId.setSelection(true);
		}
		this.javaSourcePath.setText(store.getString(JAVA_SOURCE_PATH));
		this.webRootPath.setText(store.getString(WEB_ROOT_PATH));
		this.defaultPackage.setText(store.getString(DEFAULT_PACKAGE));
		this.defaultPackageSuffix.setText(store
				.getString(DEFAULT_PACKAGE_SUFFIX));
		this.javaClassSuffix.setText(store.getString(JAVA_CLASS_SUFFIX));
		this.javaSuperClass.setText(store.getString(JAVA_SUPER_CLASS));
		this.javaSuperInterface1.setText(store.getString(JAVA_SUPER_INTERFACE
				+ "1"));
		this.javaSuperInterface2.setText(store.getString(JAVA_SUPER_INTERFACE
				+ "2"));
		this.javaSuperInterface3.setText(store.getString(JAVA_SUPER_INTERFACE
				+ "3"));

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

		Collection<Namespace> namespaces = PreferencesUtil.getNamespaces(store);
		if (namespaces.size() == 0) {
			namespaces = getDefaultNamespace();
		}
		while (namespaces.size() < 5) {
			namespaces.add(new Namespace("", ""));
		}
		namespaceTableViewer.setInput(namespaces);

		Collection<ReplaceRule> replacerules = new ArrayList<ReplaceRule>(
				PreferencesUtil.getReplaceRules(store).values());
		if (replacerules.size() == 0) {
			replacerules = getDefaultReplaceRule();
		}
		while (replacerules.size() < 5) {
			replacerules.add(new ReplaceRule("", ""));
		}
		replaceRuleTableViewer.setInput(replacerules);

		try {
			if (project.hasNature(MatatabiNature.NATURE_ID)) {
				this.useMatatabi.setSelection(true);
			}
			for (ICommand command : project.getDescription().getBuildSpec()) {
				if (command.getBuilderName().equals(BUILDER_ID)) {
					this.useValidator.setSelection(true);
				}
			}
		} catch (CoreException e) {
			MatatabiPlugin.errorLog(e);
		}
	}

	@Override
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
	@Override
	protected void performDefaults() {
		this.javaSourcePath.setText("");
		this.webRootPath.setText("");
		this.defaultPackage.setText("");
		this.defaultPackageSuffix.setText("");
		this.javaClassSuffix.setText("Action");
		this.javaSuperClass.setText("");
		this.javaSuperInterface1.setText("");
		this.javaSuperInterface2.setText("");
		this.javaSuperInterface3.setText("");
		this.useMatatabi.setSelection(false);
		this.onlyMayaaId.setSelection(false);
		this.useValidator.setSelection(false);
		this.missingIdAttribute.select(1);
		this.invalidIdAttribute.select(1);
		this.notexistIdAttribute.select(1);
		this.duplicateIdAttribute.select(1);
		this.undefineIdAttribute.select(3);
		this.namespaceTableViewer.setInput(getDefaultNamespace());
		this.replaceRuleTableViewer.setInput(getDefaultReplaceRule());

		super.performDefaults();
	}

	@Override
	public boolean performOk() {
		boolean result = false;
		IProject project = getProject();
		if (project != null) {
			IPreferenceStore store = getPreferenceStore();

			store.setValue(ONLY_MAYAA_ID, onlyMayaaId.getSelection());
			store.setValue(JAVA_SOURCE_PATH, javaSourcePath.getText());
			store.setValue(WEB_ROOT_PATH, webRootPath.getText());
			store.setValue(DEFAULT_PACKAGE, defaultPackage.getText());
			store.setValue(DEFAULT_PACKAGE_SUFFIX, defaultPackageSuffix
					.getText());
			store.setValue(JAVA_CLASS_SUFFIX, javaClassSuffix.getText());
			store.setValue(JAVA_SUPER_CLASS, javaSuperClass.getText());
			store.setValue(JAVA_SUPER_INTERFACE + "1", javaSuperInterface1
					.getText());
			store.setValue(JAVA_SUPER_INTERFACE + "2", javaSuperInterface2
					.getText());
			store.setValue(JAVA_SUPER_INTERFACE + "3", javaSuperInterface3
					.getText());
			store.setValue(MISSING_ID_ATTRIBUTE, String
					.valueOf(missingIdAttribute.getSelectionIndex()));
			store.setValue(INVALID_ID_ATTRIBUTE, String
					.valueOf(invalidIdAttribute.getSelectionIndex()));
			store.setValue(NOTEXIST_ID_ATTRIBUTE, String
					.valueOf(notexistIdAttribute.getSelectionIndex()));
			store.setValue(DUPLICATE_ID_ATTRIBUTE, String
					.valueOf(duplicateIdAttribute.getSelectionIndex()));
			store.setValue(UNDEFINE_ID_ATTRIBUTE, String
					.valueOf(undefineIdAttribute.getSelectionIndex()));

			Collection<Namespace> namespaces = (Collection<Namespace>) namespaceTableViewer
					.getInput();
			int count = 0;
			for (Namespace namespace : namespaces) {
				if (namespace.getNamespaceAttribute() != null) {
					store.setValue(NAMESPACES + "." + count, namespace
							.toString());
					count++;
				}
			}

			Collection<ReplaceRule> replaceRules = (Collection<ReplaceRule>) replaceRuleTableViewer
					.getInput();
			count = 0;
			for (ReplaceRule replaceRule : replaceRules) {
				store.setValue(REPLACE_RULE + "." + count, replaceRule
						.toString());
				count++;
			}
		}

		result = true;

		try {
			// TODO projectがnullでない場合の処理をまとめる
			if (project != null) {
				if (useMatatabi.getSelection()) {
					if (!project.hasNature(MatatabiNature.NATURE_ID)) {
						IProjectDescription desc = project.getDescription();
						List<String> natureIds = new ArrayList<String>(Arrays
								.asList(desc.getNatureIds()));
						natureIds.add(MatatabiNature.NATURE_ID);
						desc.setNatureIds(natureIds
								.toArray(new String[natureIds.size()]));
						project.setDescription(desc, null);
					}
					if (useValidator.getSelection()) {
						setBuilder(project);
					} else {
						removeBuilder(project);
					}
				} else if (project.hasNature(MatatabiNature.NATURE_ID)) {
					IProjectDescription desc = project.getDescription();
					List<String> natureIds = new ArrayList<String>(Arrays
							.asList(desc.getNatureIds()));
					natureIds.remove(MatatabiNature.NATURE_ID);
					desc.setNatureIds(natureIds.toArray(new String[natureIds
							.size()]));
					project.setDescription(desc, null);

					removeBuilder(project);
				}
			}
		} catch (CoreException e) {
			MatatabiPlugin.errorLog(e);
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

	private Button createCheckPart(Composite composite, String label) {
		return createCheckPart(composite, label, SWT.SINGLE | SWT.BORDER);
	}

	private Button createCheckPart(Composite composite, String label, int style) {
		Button check = new Button(composite, SWT.CHECK);
		check.setText(label);
		new Label(composite, SWT.NONE);
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

	private List<Namespace> getDefaultNamespace() {
		List<Namespace> namespaces = new ArrayList<Namespace>();
		namespaces.add(new Namespace("m", "http://mayaa.seasar.org"));
		while (namespaces.size() < 5) {
			namespaces.add(new Namespace("", ""));
		}
		return namespaces;
	}

	private List<ReplaceRule> getDefaultReplaceRule() {
		List<ReplaceRule> replaceRules = new ArrayList<ReplaceRule>();

		replaceRules.add(new ReplaceRule("*", "<m:echo id=\"$id\"></m:echo>"));
		while (replaceRules.size() < 5) {
			replaceRules.add(new ReplaceRule("", ""));
		}

		return replaceRules;
	}

	/**
	 * Builderの登録
	 * 
	 * @throws CoreException
	 */
	private void setBuilder(IProject project) throws CoreException {
		IProjectDescription description = project.getDescription();
		ICommand[] commands = description.getBuildSpec();
		for (int i = 0; i < commands.length; i++) {
			if (commands[i].getBuilderName().equals(BUILDER_ID)) {
				return; // ビルダー登録のキャンセル
			}
		}
		ICommand command = description.newCommand();
		command.setBuilderName(BUILDER_ID);
		ICommand[] newCommands = new ICommand[commands.length + 1];
		System.arraycopy(commands, 0, newCommands, 0, commands.length);
		newCommands[commands.length] = command;
		description.setBuildSpec(newCommands);
		project.setDescription(description, null);
	}

	/**
	 * Builderの登録解除
	 * 
	 * @throws CoreException
	 */
	private void removeBuilder(IProject project) throws CoreException {
		project.deleteMarkers(MatatabiPlugin.MARKER_ID, false,
				IResource.DEPTH_INFINITE);

		IProjectDescription description = project.getDescription();
		ICommand[] commands = description.getBuildSpec();
		for (int i = 0; i < commands.length; i++) {
			if (commands[i].getBuilderName().equals(BUILDER_ID)) {
				ICommand[] newCommands = new ICommand[commands.length - 1];
				System.arraycopy(commands, 0, newCommands, 0, i);
				System.arraycopy(commands, i + 1, newCommands, i,
						commands.length - i - 1);
				description.setBuildSpec(newCommands);
				project.setDescription(description, null);
				return; // or break;
			}
		}
	}

	/**
	 * プロジェクト内のフォルダ選択
	 */
	private static class FolderSelectionAdapter extends SelectionAdapter {
		private Shell shell;
		private IProject project;
		private Text text;

		public FolderSelectionAdapter(Shell shell, IProject project, Text text) {
			this.shell = shell;
			this.project = project;
			this.text = text;
		}

		@Override
		public void widgetSelected(SelectionEvent e) {
			ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(
					shell, new WorkbenchLabelProvider(),
					new WorkbenchContentProvider());
			dialog.setInput(project);
			dialog.setAllowMultiple(false);
			dialog.addFilter(new ViewerFilter() {
				@Override
				public boolean select(Viewer viewer, Object parent,
						Object element) {
					return element instanceof IFolder;
				}
			});
			if (dialog.open() == Dialog.OK) {
				IResource result = (IResource) dialog.getFirstResult();
				if (result != null) {
					text.setText(result.getProjectRelativePath().toString());
				}
			}
		}
	}

	private static class JavaTypeSelectionAdapter extends SelectionAdapter {
		private Shell shell;
		private Text text;
		private int type;

		public JavaTypeSelectionAdapter(Shell shell, Text text, int type) {
			this.shell = shell;
			this.text = text;
			this.type = type;
		}

		@Override
		public void widgetSelected(SelectionEvent e) {
			OpenTypeSelectionDialog dialog = new OpenTypeSelectionDialog(shell,
					false, MatatabiPlugin.getDefault().getWorkbench()
							.getActiveWorkbenchWindow(),
					new JavaWorkspaceScope(), type);
			if (dialog.open() == Dialog.OK) {
				IType result = (IType) dialog.getFirstResult();
				if (result != null) {
					text.setText(result.getFullyQualifiedName());
				}
			}
		}
	}
}
