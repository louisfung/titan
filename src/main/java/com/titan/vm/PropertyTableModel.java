package com.titan.vm;

import java.util.Vector;

import javax.swing.table.DefaultTableModel;

public class PropertyTableModel extends DefaultTableModel {
	String columnNames[] = { "Property", "Value" };
	Vector<Property> data = new Vector<Property>();

	public PropertyTableModel() {
	}

	public String getColumnName(int column) {
		if (column == 0) {
			return "";
		}
		return columnNames[column - 1];
	}

	public int getColumnCount() {
		return 3;
	}

	public int getRowCount() {
		if (data == null) {
			return 0;
		}
		return data.size();
	}

	public void setValueAt(Object aValue, int row, int column) {
		Property property = (Property) aValue;
		data.set(row, property);
		fireTableCellUpdated(row, column);
	}

	public Object getValueAt(final int row, int column) {
		if (row >= 0 && row < getRowCount()) {
			return data.get(row);
		} else {
			return null;
		}
	}

	public boolean isCellEditable(int row, int column) {
		if (column == 2) {
			if (((Property) getValueAt(row, column)).isEditable) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public Class getColumnClass(int columnIndex) {
		return Property.class;
	}

	//	public void filter(String text) {
	//		for (Property property : data) {
	//			if (property.isData) {
	//				if (property.name.toLowerCase().contains(text.trim().toLowerCase())) {
	//					property.isVisible = true;
	//				} else {
	//					property.isVisible = false;
	//				}
	//			} else {
	//				property.isVisible = false;
	//			}
	//		}
	//	}

	public void changeValue(String property, String value) {
		for (Property p : data) {
			if (p.name.equals(property)) {
				p.value = value;
			}
		}
		fireTableDataChanged();
	}

}
