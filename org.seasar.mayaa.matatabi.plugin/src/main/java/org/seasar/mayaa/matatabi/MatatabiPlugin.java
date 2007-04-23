package org.seasar.mayaa.matatabi;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.osgi.framework.BundleContext;

/**
 * プラグインクラス
 */
public class MatatabiPlugin extends AbstractUIPlugin {

	// The shared instance.
	private static MatatabiPlugin plugin;

	/**
	 * The constructor.
	 */
	public MatatabiPlugin() {
		plugin = this;
	}

	/**
	 * This method is called upon plug-in activation
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
	}

	/**
	 * Returns the shared instance.
	 */
	public static MatatabiPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path.
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin(
				"org.seasar.mayaa.matatabi", path);
	}

	public static String getStoreValue(IProject project, String key) {
		return (new ScopedPreferenceStore(new ProjectScope(project),
				"org.seasar.mayaa.matatabi")).getString(key);
	}
}
