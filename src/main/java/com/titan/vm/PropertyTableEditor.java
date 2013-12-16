package com.titan.vm;

import java.awt.Component;
import java.util.EventObject;

import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.TableCellEditor;

public class PropertyTableEditor extends JTextField implements TableCellEditor {
	private CellEditorListener cellEditorListener = null;
	Property originalProperty;

	@Override
	public Component getTableCellEditorComponent(JTable table, Object obj, boolean isSelected, int row, int column) {
		System.out.println("getTableCellEditorComponent");
		Property property = (Property) obj;
		originalProperty = property;

		super.setText(property.value);

		return this;
	}

	@Override
	public Object getCellEditorValue() {
		originalProperty.value = getText();
		return originalProperty;
	}

	@Override
	public boolean isCellEditable(EventObject e) {
		return true;
	}

	@Override
	public boolean shouldSelectCell(EventObject e) {
		return true;
	}

	@Override
	public boolean stopCellEditing() {
		cellEditorListener.editingStopped(new ChangeEvent(this));
		return true;
	}

	@Override
	public void cancelCellEditing() {
		cellEditorListener.editingCanceled(new ChangeEvent(this));
	}

	@Override
	public void addCellEditorListener(CellEditorListener celleditorlistener) {
		cellEditorListener = celleditorlistener;
	}

	@Override
	public void removeCellEditorListener(CellEditorListener celleditorlistener) {
		if (this.cellEditorListener == celleditorlistener)
			cellEditorListener = null;
	}
}