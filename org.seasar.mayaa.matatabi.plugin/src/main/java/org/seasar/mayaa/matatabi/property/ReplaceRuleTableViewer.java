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

public class ReplaceRuleTableViewer extends TableViewer {

	public ReplaceRuleTableViewer(Composite parent, int style) {
		super(parent, style);
		createPartControl(parent);
	}

	public ReplaceRuleTableViewer(Composite parent) {
		super(parent);
		createPartControl(parent);
	}

	public void createPartControl(Composite parent) {
		// 見た目の設定
		getTable().setLinesVisible(true);
		getTable().setHeaderVisible(true);

		// ヘッダの設定
		TableColumn col1 = new TableColumn(getTable(), SWT.LEFT);
		col1.setText("タグ名");
		col1.setWidth(100);
		TableColumn col2 = new TableColumn(getTable(), SWT.LEFT);
		col2.setText("テンプレート");
		col2.setWidth(400);

		// 編集設定
		String[] properties = new String[] { "tag", "replace" };
		setColumnProperties(properties);
		CellEditor[] cellEditors = new CellEditor[] {
				new TextCellEditor(getTable()), new TextCellEditor(getTable()) };
		setCellEditors(cellEditors);
		setCellModifier(new RelpaceCellModifier(this));
		getTable().addMouseListener(new RelpaceMouseAdapter(this));

		// 値の設定
		setContentProvider(new ArrayContentProvider());
		setLabelProvider(new RelpaceLabelProvider());
	}

	public static class RelpaceLabelProvider extends LabelProvider implements
			ITableLabelProvider {

		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			ReplaceRule relpace = (ReplaceRule) element;
			switch (columnIndex) {
			case 0:
				return relpace.getTag();
			case 1:
				return relpace.getReplace();
			}
			return "";
		}
	}

	public static final class ReplaceRule implements Serializable {
		private static final long serialVersionUID = -3332275212133101838L;

		private String tag;

		private String replace;

		public ReplaceRule(String prefix, String uri) {
			this.tag = prefix;
			this.replace = uri;
		}

		public ReplaceRule(String string) {
			String[] namespace = string.split(":", 2);
			this.tag = namespace[0];
			this.replace = namespace[1];
		}

		public String getTag() {
			return tag;
		}

		public String getReplace() {
			return replace;
		}

		public void setTag(String tag) {
			this.tag = tag;
		}

		public void setReplace(String replace) {
			this.replace = replace;
		}

		public String toString() {
			return tag + ":" + replace;
		}
	}

	public class RelpaceCellModifier implements ICellModifier {

		private TableViewer viewer;

		public RelpaceCellModifier(TableViewer viewer) {
			this.viewer = viewer;
		}

		public boolean canModify(Object element, String property) {
			return true;
		}

		public Object getValue(Object element, String property) {
			ReplaceRule Relpace = (ReplaceRule) element;

			if (property.equals("tag")) {
				return Relpace.getTag();
			} else if (property.equals("replace")) {
				return Relpace.getReplace();
			}

			return null;
		}

		public void modify(Object element, String property, Object value) {
			TableItem tableItem = (TableItem) element;
			ReplaceRule Relpace = (ReplaceRule) tableItem.getData();

			if (property.equals("tag")) {
				Relpace.setTag(value.toString());
			} else if (property.equals("replace")) {
				Relpace.setReplace(value.toString());
			}
			viewer.update(Relpace, null);
		}
	}

	class RelpaceMouseAdapter extends MouseAdapter {

		private TableViewer viewer;

		public RelpaceMouseAdapter(TableViewer viewer) {
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
				ReplaceRule relpace = new ReplaceRule("", "");
				((List<ReplaceRule>) viewer.getInput()).add(relpace);
				add(relpace);
			}
		}
	}
}
