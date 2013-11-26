package com.titan.vm;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class VMPanel extends JPanel {
	VMLabel iconLabel;
	JLabel label = new JLabel("");
	public boolean isSelected;
	LineBorder border = new LineBorder(new Color(0, 0, 0), 1);
	EmptyBorder emptyBorder = new EmptyBorder(1, 1, 1, 1);

	public VMPanel(VMLabel iconLabel) {
		this.iconLabel = iconLabel;
		setLayout(new BorderLayout(0, 0));

		add(iconLabel, BorderLayout.CENTER);
		label.setHorizontalAlignment(SwingConstants.CENTER);

		add(label, BorderLayout.SOUTH);
		setBorder(emptyBorder);
	}

	public void setSelected(boolean selected) {
		isSelected = selected;
		if (isSelected) {
			setBorder(border);
		} else {
			setBorder(emptyBorder);
		}
	}

}
