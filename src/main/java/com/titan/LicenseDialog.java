package com.titan;

import java.awt.BorderLayout;
import java.awt.Frame;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

public class LicenseDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();

	public LicenseDialog(Frame frame, String license) {
		super(frame, "License", true);
		setBounds(100, 100, 694, 531);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JScrollPane scrollPane = new JScrollPane();
			contentPanel.add(scrollPane);
			{
				JTextArea textArea = new JTextArea(license);
				scrollPane.setViewportView(textArea);
			}
		}
	}

}
