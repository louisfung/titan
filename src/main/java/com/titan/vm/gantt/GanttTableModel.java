package com.titan.vm.gantt;

import java.util.Date;
import java.util.HashSet;

import javax.swing.table.DefaultTableModel;

public class GanttTableModel extends DefaultTableModel {
	String columnNames[];
	HashSet<GanttData> data = new HashSet<GanttData>();

	public GanttTableModel() {
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
		} else {
			return data.size();
		}
	}

	public void setValueAt(Object aValue, int row, int column) {
		this.fireTableDataChanged();
	}

	public Object getValueAt(final int row, int column) {
		if (columnNames == null || data == null) {
			return null;
		} else {
			return null;
		}
	}

	public boolean isCellEditable(int row, int column) {
		return false;
	}

	public Class getColumnClass(int columnIndex) {
		return getValueAt(0, columnIndex).getClass();
	}

	public void init(Date date, Date date2) {

	}
}
