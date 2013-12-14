package com.titan.storagepanel;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.peterswing.CommonLib;
import com.titan.mainframe.MainFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class ImageMetaDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	public JTable table;

	public ImageMetaDialog(MainFrame mainframe) {
		super(mainframe, "Image meta", true);
		setBounds(100, 100, 600, 500);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JScrollPane scrollPane = new JScrollPane();
			contentPanel.add(scrollPane, BorderLayout.CENTER);
			{
				table = new JTable();
				scrollPane.setViewportView(table);
			}
		}
		CommonLib.centerDialog(this);
	}

}
