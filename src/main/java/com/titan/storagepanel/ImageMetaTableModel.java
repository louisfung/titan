package com.titan.storagepanel;

import java.util.Vector;

import javax.swing.table.DefaultTableModel;

public class ImageMetaTableModel extends DefaultTableModel {
	String columnNames[] = { "Name", "Value" };
	Vector<String> names = new Vector<String>();
	Vector<String> values = new Vector<String>();

	public ImageMetaTableModel() {
	}

	public String getColumnName(int column) {
		if (column == 0) {
			return "";
		}
		return columnNames[column - 1];
	}

	public int getColumnCount() {
		return columnNames.length;
	}

	public int getRowCount() {
		if (names == null) {
			return 0;
		}
		return names.size();
	}

	public void setValueAt(Object aValue, int row, int column) {
		this.fireTableDataChanged();
	}

	public Object getValueAt(final int row, int column) {
		if (column == 0) {
			return names.get(row);
		} else {
			return values.get(row);
		}
	}

	public boolean isCellEditable(int row, int column) {
		return false;
	}

	public Class getColumnClass(int columnIndex) {
		try {
			return getValueAt(0, columnIndex).getClass();
		} catch (Exception ex) {
			return Object.class;
		}
	}

}
