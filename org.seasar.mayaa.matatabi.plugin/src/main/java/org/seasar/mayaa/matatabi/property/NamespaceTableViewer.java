package org.seasar.mayaa.matatabi.property;

import java.io.Serializable;
import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class NamespaceTableViewer extends TableViewer {

	public NamespaceTableViewer(Composite parent, int style) {
		super(parent, style);
		createPartControl(parent);
	}

	public NamespaceTableViewer(Composite parent) {
		super(parent);
		createPartControl(parent);
	}

	public void createPartControl(Composite parent) {
		// 見た目の設定
		getTable().setLinesVisible(true);
		getTable().setHeaderVisible(true);

		// ヘッダの設定
		TableColumn col1 = new TableColumn(getTable(), SWT.LEFT);
		col1.setText("接頭辞");
		col1.setWidth(100);
		TableColumn col2 = new TableColumn(getTable(), SWT.LEFT);
		col2.setText("名前空間");
		col2.setWidth(400);

		// 編集設定
		String[] properties = new String[] { "prefix", "uri" };
		setColumnProperties(properties);
		CellEditor[] cellEditors = new CellEditor[] {
				new TextCellEditor(getTable()), new TextCellEditor(getTable()) };
		setCellEditors(cellEditors);
		setCellModifier(new NamespaceCellModifier(this));
		getTable().addMouseListener(new NamespaceMouseAdapter(this));

		// 値の設定
		setContentProvider(new ArrayContentProvider());
		setLabelProvider(new NamespaceLabelProvider());
	}

	public static class NamespaceLabelProvider extends LabelProvider implements
			ITableLabelProvider {

		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			Namespace namespace = (Namespace) element;
			switch (columnIndex) {
			case 0:
				return namespace.getPrefix();
			case 1:
				return namespace.getUri();
			}
			return "";
		}
	}

	public static final class Namespace implements Serializable {
		String prefix;

		String uri;

		public Namespace(String prefix, String uri) {
			this.prefix = prefix;
			this.uri = uri;
		}

		public Namespace(String string) {
			String[] namespace = string.split(":", 2);
			this.prefix = namespace[0];
			this.uri = namespace[1];
		}

		public String getPrefix() {
			return prefix;
		}

		public String getUri() {
			return uri;
		}

		public void setPrefix(String prefix) {
			this.prefix = prefix;
		}

		public void setUri(String uri) {
			this.uri = uri;
		}

		public String toString() {
			return prefix + ":" + uri;
		}

		public String getNamespaceAttribute() {
			if (prefix.equals("") || uri.equals("")) {
				return null;
			}
			return "xmlns:" + prefix + "=\"" + uri + "\"";
		}
	}

	public class NamespaceCellModifier implements ICellModifier {

		private TableViewer viewer;

		public NamespaceCellModifier(TableViewer viewer) {
			this.viewer = viewer;
		}

		public boolean canModify(Object element, String property) {
			return true;
		}

		public Object getValue(Object element, String property) {
			Namespace namespace = (Namespace) element;

			if (property.equals("prefix")) {
				return namespace.getPrefix();
			} else if (property.equals("uri")) {
				return namespace.getUri();
			}

			return null;
		}

		public void modify(Object element, String property, Object value) {
			TableItem tableItem = (TableItem) element;
			Namespace namespace = (Namespace) tableItem.getData();

			if (property.equals("prefix")) {
				namespace.setPrefix(value.toString());
			} else if (property.equals("uri")) {
				namespace.setUri(value.toString());
			}
			viewer.update(namespace, null);
		}
	}

	class NamespaceMouseAdapter extends MouseAdapter {

		private TableViewer viewer;

		public NamespaceMouseAdapter(TableViewer viewer) {
			this.viewer = viewer;
		}

		public void mouseDown(MouseEvent e) {
			int x = e.x;
			int y = e.y;

			Point clickPoint = new Point(x, y);

			Table table = viewer.getTable();
			int startRowNum = table.getTopIndex();
			int endRowNum = table.getItemCount();
			int columnCount = table.getColumnCount();

			TableItem targetItem = null;
			int columnIndex = 0;
			for (int rowCnt = startRowNum; rowCnt < endRowNum; rowCnt++) {
				TableItem item = table.getItem(rowCnt);
				for (int j = 0; j <= columnCount; j++) {
					Rectangle rect = item.getBounds(j);
					if (rect.contains(clickPoint)) {
						columnIndex = j;
						targetItem = item;
						break;
					}
				}
			}

			if (targetItem != null) {
				viewer.editElement(targetItem.getData(), columnIndex);
			} else {
				Namespace namespace = new Namespace("", "");
				((List) viewer.getInput()).add(namespace);
				add(namespace);
			}
		}
	}
}
