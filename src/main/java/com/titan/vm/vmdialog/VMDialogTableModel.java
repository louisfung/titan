package com.titan.vm.vmdialog;

import java.util.Vector;

import javax.swing.table.DefaultTableModel;

public class VMDialogTableModel extends DefaultTableModel {
	String columnNames[] = { "Id", "Name", "Type" };
	public Vector<Data> data = new Vector<Data>();

	public VMDialogTableModel() {
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
		if (column == 0) {
			return data.get(row).instanceId;
		} else if (column == 1) {
			return data.get(row).name;
		} else {
			return data.get(row).vmType;
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

	public void add(String instanceId, String name, String vmType) {
		Data d = new Data();
		d.instanceId = instanceId;
		d.name = name;
		d.vmType = vmType;
		data.add(d);
		fireTableDataChanged();
	}

	public class Data {
		public String instanceId;
		public String name;
		public String vmType;
	}
}
