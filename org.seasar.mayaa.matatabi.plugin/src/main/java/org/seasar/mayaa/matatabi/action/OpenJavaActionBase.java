package org.seasar.mayaa.matatabi.action;

import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.ui.wizards.NewClassCreationWizard;
import org.eclipse.jdt.ui.wizards.NewClassWizardPage;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.seasar.mayaa.matatabi.property.MatatabiPropertyPage;

public abstract class OpenJavaActionBase extends OpenActionBase {
	public OpenJavaActionBase() {
		super("java");
	}

	@Override
	protected void init() {
		baseDir = store.getString(MatatabiPropertyPage.WEB_ROOT_PATH);
		targetBaseDir = store.getString(MatatabiPropertyPage.JAVA_SOURCE_PATH)
				+ "/"
				+ store.getString(MatatabiPropertyPage.DEFAULT_PACKAGE)
						.replace('.', '/');
	}

	@Override
	public void run(IAction action) {
		super.run(action);
		if (!getTargetFile().exists()) {
			MessageBox messageBox = new MessageBox(targetPart.getSite()
					.getShell(), SWT.YES | SWT.NO | SWT.ICON_QUESTION);
			messageBox.setMessage("対応するJavaクラスが存在しません。作成しますか？");
			messageBox.setText("エラー");
			switch (messageBox.open()) {
			case SWT.YES:
				String sourcePath = store
						.getString(MatatabiPropertyPage.JAVA_SOURCE_PATH);
				IPackageFragmentRoot packageFragmentRoot = JavaCore.create(
						project).getPackageFragmentRoot(
						project.findMember(sourcePath));
				String packageName = store
						.getString(MatatabiPropertyPage.DEFAULT_PACKAGE)
						+ subDirectory.replaceAll("/", ".");
				IPackageFragment packageFragment = packageFragmentRoot
						.getPackageFragment(packageName);

				NewClassWizardPage page = new NewClassWizardPage();
				page.setTypeName(getResourceNames(baseName)[0], false);

				page.setPackageFragmentRoot(packageFragmentRoot, false);
				page.setPackageFragment(packageFragment, false);

				NewClassCreationWizard wizard = new NewClassCreationWizard(
						page, true);
				wizard.init(targetPart.getSite().getWorkbenchWindow()
						.getWorkbench(), selection);
				WizardDialog dialog = new WizardDialog(targetPart.getSite()
						.getShell(), wizard);
				dialog.open();

				break;
			default:
				break;
			}
		}
		super.run(action);
	}

	@Override
	protected String getSubDirectory(String packageName) {
		String packageSuffix = store
				.getString(MatatabiPropertyPage.DEFAULT_PACKAGE_SUFFIX);
		if (!"".equals(packageSuffix)) {
			packageName += "." + packageSuffix;
		}
		return super.getSubDirectory(packageName);
	}
}
