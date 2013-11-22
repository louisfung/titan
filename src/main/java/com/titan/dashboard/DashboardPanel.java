package com.titan.dashboard;

import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import com.titan.mainframe.MainFrame;

public class DashboardPanel extends JPanel {
	MainFrame mainFrame;

	public DashboardPanel(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
		setLayout(new MigLayout("", "[]", "[]"));
	}

}
