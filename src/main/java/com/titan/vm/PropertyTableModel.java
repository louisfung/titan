package com.titan.vm;

import java.util.Vector;

import javax.swing.table.DefaultTableModel;

public class PropertyTableModel extends DefaultTableModel {
	String columnNames[] = { "Property", "Value" };
	Vector<Property> data = new Vector<Property>();

	public PropertyTableModel() {
	}

	public String getColumnName(int column) {
		return columnNames[column];
	}

	public int getColumnCount() {
		if (columnNames == null) {
			return 0;
		}
		return columnNames.length;
	}

	public int getRowCount() {
		if (data == null) {
			return 0;
		}
		return data.size();
	}

	public void setValueAt(Object aValue, int row, int column) {
		this.fireTableDataChanged();
	}

	public Object getValueAt(final int row, int column) {
		if (data.get(row).isData) {
			if (column == 0) {
				return data.get(row).name;
			} else {
				return data.get(row).value;
			}
		} else {
			return data.get(row).type;
		}
	}

	public boolean isCellEditable(int row, int column) {
		return false;
	}

	public Class getColumnClass(int columnIndex) {
		return getValueAt(0, columnIndex).getClass();
	}

}
