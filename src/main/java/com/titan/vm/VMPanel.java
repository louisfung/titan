package com.titan.vm;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class VMPanel extends JPanel {
	VMLabel iconLabel;
	JLabel label = new JLabel("");

	public VMPanel(VMLabel iconLabel) {
		this.iconLabel = iconLabel;
		setLayout(new BorderLayout(0, 0));

		add(iconLabel, BorderLayout.CENTER);
		label.setHorizontalAlignment(SwingConstants.CENTER);

		add(label, BorderLayout.SOUTH);

	}

}
