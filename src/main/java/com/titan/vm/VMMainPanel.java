package com.titan.vm;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.peterswing.advancedswing.searchtextfield.JSearchTextField;
import com.titan.TitanSetting;
import com.titan.mainframe.MainFrame;

public class VMMainPanel extends JPanel {
	MainFrame mainFrame;
	JSlider slider = new JSlider();
	VMIconPanel iconPanel = new VMIconPanel(this);
	JLabel colLabel = new JLabel("");
	ButtonGroup group1 = new ButtonGroup();
	private JToggleButton ganttViewButton;
	private JToggleButton deltailViewButton;
	JScrollPane scrollPane = new JScrollPane();
	VMGanttPanel vmGanttPanel;

	public VMMainPanel(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
		setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		add(panel, BorderLayout.NORTH);
		panel.setLayout(new BorderLayout(0, 0));

		JToolBar toolBar = new JToolBar();
		panel.add(toolBar);

		JToggleButton iconViewButton = new JToggleButton("");
		iconViewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				scrollPane.setViewportView(iconPanel);
			}
		});
		iconViewButton.setSelected(true);
		iconViewButton.setIcon(new ImageIcon(VMMainPanel.class.getResource("/com/titan/image/famfamfam/color_swatch.png")));
		toolBar.add(iconViewButton);
		group1.add(iconViewButton);

		ganttViewButton = new JToggleButton("");
		ganttViewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (vmGanttPanel == null) {
					vmGanttPanel = new VMGanttPanel();
				}
				scrollPane.setViewportView(vmGanttPanel);
			}
		});
		ganttViewButton.setIcon(new ImageIcon(VMMainPanel.class.getResource("/com/titan/image/famfamfam/text_list_bullets.png")));
		toolBar.add(ganttViewButton);
		group1.add(ganttViewButton);

		deltailViewButton = new JToggleButton("");
		deltailViewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		deltailViewButton.setIcon(new ImageIcon(VMMainPanel.class.getResource("/com/titan/image/famfamfam/text_align_justify.png")));
		toolBar.add(deltailViewButton);
		group1.add(deltailViewButton);

		JButton btnRefresh = new JButton("Refresh");
		btnRefresh.setIcon(new ImageIcon(VMMainPanel.class.getResource("/com/titan/image/famfamfam/arrow_rotate_clockwise.png")));
		toolBar.add(btnRefresh);

		JSearchTextField searchTextField = new JSearchTextField();
		searchTextField.setMaximumSize(new Dimension(100, 20));
		toolBar.add(searchTextField);

		slider.setMaximumSize(new Dimension(120, 20));
		slider.setMinimum(5);
		slider.setMaximum(20);
		slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				int maxVMColumnCount = (int) slider.getValue();
				iconPanel.init(maxVMColumnCount);
			}
		});
		toolBar.add(slider);

		slider.setValue(TitanSetting.getInstance().maxVMColumnCount);

		colLabel.setText(TitanSetting.getInstance().maxVMColumnCount + " columns");
		toolBar.add(colLabel);

		add(scrollPane, BorderLayout.CENTER);
		scrollPane.setViewportView(iconPanel);

	}

}
