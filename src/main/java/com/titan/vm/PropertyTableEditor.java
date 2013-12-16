package com.titan.vm;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.util.EventObject;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;

public class PropertyTableEditor extends JPanel implements TableCellEditor {
	JLabel jLabel = new JLabel();
	Icon collapse = new ImageIcon(getClass().getClassLoader().getResource("com/titan/image/famfamfam/collapse.png"));
	Icon expand = new ImageIcon(getClass().getClassLoader().getResource("com/titan/image/famfamfam/expand.png"));
	Color backgrounColor = new Color(253, 253, 253);

	public PropertyTableEditor() {
		this.setLayout(new BorderLayout());
		this.add(jLabel, BorderLayout.CENTER);
		jLabel.setOpaque(true);
	}

	@Override
	public Object getCellEditorValue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isCellEditable(EventObject anEvent) {
		return true;
	}

	@Override
	public boolean shouldSelectCell(EventObject anEvent) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean stopCellEditing() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void cancelCellEditing() {
		// TODO Auto-generated method stub

	}

	@Override
	public void addCellEditorListener(CellEditorListener l) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeCellEditorListener(CellEditorListener l) {
		// TODO Auto-generated method stub

	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		return new JTextField(value.toString());
	}

}