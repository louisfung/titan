package com.titan.dashboard;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import net.miginfocom.swing.MigLayout;

import com.peterswing.advancedswing.searchtextfield.JSearchTextField;
import com.titan.mainframe.MainFrame;
import com.titan.OS;
import javax.swing.JSlider;

public class DashboardPanel extends JPanel {
	MainFrame mainFrame;

	public DashboardPanel(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
		setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		add(panel, BorderLayout.NORTH);
		panel.setLayout(new BorderLayout(0, 0));

		JToolBar toolBar = new JToolBar();
		panel.add(toolBar);

		JToggleButton tglbtnNewToggleButton_2 = new JToggleButton("");
		tglbtnNewToggleButton_2.setIcon(new ImageIcon(DashboardPanel.class.getResource("/com/titan/image/famfamfam/color_swatch.png")));
		toolBar.add(tglbtnNewToggleButton_2);

		JToggleButton tglbtnNewToggleButton = new JToggleButton("");
		tglbtnNewToggleButton.setIcon(new ImageIcon(DashboardPanel.class.getResource("/com/titan/image/famfamfam/text_list_bullets.png")));
		toolBar.add(tglbtnNewToggleButton);

		JToggleButton tglbtnNewToggleButton_1 = new JToggleButton("");
		tglbtnNewToggleButton_1.setIcon(new ImageIcon(DashboardPanel.class.getResource("/com/titan/image/famfamfam/text_align_justify.png")));
		toolBar.add(tglbtnNewToggleButton_1);

		JButton btnRefresh = new JButton("Refresh");
		btnRefresh.setIcon(new ImageIcon(DashboardPanel.class.getResource("/com/titan/image/famfamfam/arrow_rotate_clockwise.png")));
		toolBar.add(btnRefresh);

		JSearchTextField searchTextField = new JSearchTextField();
		searchTextField.setMaximumSize(new Dimension(100,20));
		toolBar.add(searchTextField);
		
		JSlider slider = new JSlider();
		slider.setMaximumSize(new Dimension(200,20));
		toolBar.add(slider);

		JPanel mainPanel = new JPanel();
		add(mainPanel, BorderLayout.CENTER);
		mainPanel.setLayout(new MigLayout("", "[][][][][][]", "[][]"));

		VMLabel label = new VMLabel();
		label.setOS(OS.redhat);
		mainPanel.add(label, "cell 0 0");

		VMLabel label_1 = new VMLabel();
		label_1.setOS(OS.suse);
		mainPanel.add(label_1, "cell 1 0");

		VMLabel label_2 = new VMLabel();
		label_2.setOS(OS.centos);
		mainPanel.add(label_2, "cell 2 0");

		VMLabel label_3 = new VMLabel();
		label_3.setOS(OS.windows);
		mainPanel.add(label_3, "cell 3 0");

		VMLabel label_4 = new VMLabel();
		mainPanel.add(label_4, "cell 4 0");

		VMLabel label_5 = new VMLabel();
		mainPanel.add(label_5, "cell 5 0");

		VMLabel label_6 = new VMLabel();
		label_6.setOS(OS.suse);
		mainPanel.add(label_6, "cell 0 1");

		VMLabel label_7 = new VMLabel();
		label_7.setOS(OS.ubuntu);
		mainPanel.add(label_7, "cell 1 1");

		VMLabel label_8 = new VMLabel();
		mainPanel.add(label_8, "cell 2 1");

		VMLabel label_9 = new VMLabel();
		mainPanel.add(label_9, "cell 3 1");

		VMLabel label_10 = new VMLabel();
		mainPanel.add(label_10, "cell 4 1");

		VMLabel label_11 = new VMLabel();
		mainPanel.add(label_11, "cell 5 1");
	}

}
