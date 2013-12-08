package com.titan.vm;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class PropertyTableCellRenderer extends JPanel implements TableCellRenderer {
	JLabel jLabel = new JLabel();
	Icon collapse = new ImageIcon(getClass().getClassLoader().getResource("com/titan/image/famfamfam/collapse.png"));
	Icon expand = new ImageIcon(getClass().getClassLoader().getResource("com/titan/image/famfamfam/expand.png"));

	public PropertyTableCellRenderer() {
		this.setLayout(new BorderLayout());
		this.add(jLabel, BorderLayout.CENTER);
		jLabel.setOpaque(true);
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		Property property = (Property) value;
		if (property.isData) {
			if (column == 0) {
				jLabel.setText(null);
				jLabel.setIcon(null);
			} else if (column == 1) {
				jLabel.setText(property.name);
				jLabel.setIcon(null);
			} else {
				jLabel.setText(property.value);
				jLabel.setIcon(null);
			}
			jLabel.setFont(new Font("Arial", Font.PLAIN, 12));
		} else {
			jLabel.setFont(new Font("Arial", Font.BOLD, 12));
			if (column == 0) {
				jLabel.setText(null);
				jLabel.setIcon(collapse);
			} else if (column == 1) {
				jLabel.setText(property.type);
				jLabel.setIcon(null);
			} else {
				jLabel.setText(null);
				jLabel.setIcon(null);
			}
		}
		if (isSelected) {
			jLabel.setBackground(table.getSelectionBackground());
		} else {
			if (row % 2 == 0) {
				jLabel.setBackground(Color.white);
			} else {
				jLabel.setBackground(new Color(0xf4f4f4));
			}
		}
		return this;

	}

}