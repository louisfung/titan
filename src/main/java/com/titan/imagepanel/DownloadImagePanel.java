package com.titan.imagepanel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import net.miginfocom.swing.MigLayout;

public class DownloadImagePanel extends JPanel {
	public JLabel lblHeader;
	public JLabel logoLabel;
	public JLabel lblDate;
	public JTextArea textArea;
	public JLabel lblLicense;
	public JLabel lblAuthor;

	public DownloadImagePanel() {
		logoLabel = new JLabel("");
		logoLabel.setMaximumSize(new Dimension(150, 150));

		lblDate = new JLabel("Date");
		lblDate.setForeground(Color.GRAY);
		setLayout(new MigLayout("", "[148px][29px,grow][88px]", "[32px][99px,grow][16px][][]"));

		lblHeader = new JLabel("Header");
		lblHeader.setFont(new Font("Lucida Grande", Font.PLAIN, 18));
		add(lblHeader, "cell 1 0,grow");
		add(logoLabel, "cell 0 0 1 5");

		textArea = new JTextArea();
		textArea.setLineWrap(true);
		add(textArea, "cell 1 1 2 1,grow");
		add(lblDate, "cell 1 2,alignx left,aligny top");

		lblLicense = new JLabel("License");
		lblLicense.setForeground(Color.GRAY);
		add(lblLicense, "cell 1 3");

		lblAuthor = new JLabel("Author");
		lblAuthor.setForeground(Color.GRAY);
		add(lblAuthor, "cell 1 4");
	}
}
