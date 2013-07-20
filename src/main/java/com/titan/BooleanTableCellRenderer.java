package com.titan;

import java.awt.Color;
import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellRenderer;

public class BooleanTableCellRenderer extends JLabel implements TableCellRenderer {
	ImageIcon tick = new ImageIcon(getClass().getClassLoader().getResource("com/c2/titan/image/famfamfam/tick.png"));
	ImageIcon cross = new ImageIcon(getClass().getClassLoader().getResource("com/c2/titan/image/famfamfam/cross.png"));

	public BooleanTableCellRenderer() {
		this.setHorizontalAlignment(JTextField.CENTER);
		this.setOpaque(true);
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		try {
			if (Boolean.parseBoolean(String.valueOf(value))) {
				setIcon(tick);
			} else {
				setIcon(cross);
			}
			if (isSelected) {
				this.setBackground(table.getSelectionBackground());
			} else {
				this.setBackground(Color.white);
			}
		} catch (Exception e) {
			setIcon(cross);
		}
		return this;
	}
}