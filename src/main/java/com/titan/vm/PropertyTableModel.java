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
		this.fireTableDataChanged();
	}

	public Object getValueAt(final int row, int column) {
		//		if (data.get(row).isData) {
		//			if (column == 0) {
		//				return "";
		//			} else if (column == 1) {
		//				return data.get(row).name;
		//			} else {
		//				return data.get(row).value;
		//			}
		//		} else {
		//			if (column == 0) {
		//				return "+";
		//			} else {
		//				return data.get(row).type;
		//			}
		//		}
		if (row < getRowCount()) {
			return data.get(row);
		} else {
			return null;
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
