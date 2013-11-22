package com.titan.dashboard;

import javax.swing.JPanel;

import com.titan.mainframe.MainFrame;
import net.miginfocom.swing.MigLayout;

public class DashboardPanel extends JPanel {
	MainFrame mainFrame;

	public DashboardPanel(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
		setLayout(new MigLayout("", "[]", "[]"));
	}

}
