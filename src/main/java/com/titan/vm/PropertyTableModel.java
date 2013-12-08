package com.titan.vm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class PropertyTableModel extends DefaultTableModel {
	String columnNames[] = { "Peoperty", "Value" };
	Vector<String> id = new Vector<String>();
	Vector<String> server = new Vector<String>();
	Vector<String> project = new Vector<String>();
	Vector<String> host = new Vector<String>();
	Vector<String> ip = new Vector<String>();
	Vector<String> size = new Vector<String>();
	Vector<String> status = new Vector<String>();
	Vector<String> task = new Vector<String>();
	Vector<String> power = new Vector<String>();
	JButton button = new JButton("Add");

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
		if (project == null) {
			return 0;
		}
		return project.size();
	}

	public void setValueAt(Object aValue, int row, int column) {
		this.fireTableDataChanged();
	}

	public Object getValueAt(final int row, int column) {
		if (row < getRowCount()) {
			if (column == 0) {
				return id.get(row);
			} else if (column == 1) {
				return server.get(row);
			} else if (column == 2) {
				return project.get(row);
			} else if (column == 3) {
				return host.get(row);
			} else if (column == 4) {
				return ip.get(row);
			} else if (column == 5) {
				return size.get(row);
			} else if (column == 6) {
				return status.get(row);
			} else if (column == 7) {
				return task.get(row);
			} else if (column == 8) {
				return power.get(row);
			} else {
				return "";
			}
		} else {
			return button;
		}
	}

	public boolean isCellEditable(int row, int column) {
		return false;
	}

	public Class getColumnClass(int columnIndex) {
		return getValueAt(0, columnIndex).getClass();
	}
}
