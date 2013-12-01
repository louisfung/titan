package com.titan.vm;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sf.json.JSONObject;

import com.peterswing.advancedswing.searchtextfield.JSearchTextField;
import com.titan.TitanCommonLib;
import com.titan.TitanSetting;
import com.titan.communication.CommunicateLib;
import com.titan.instancepanel.InstancePanel;
import com.titan.instancepanel.LaunchInstanceDialog;
import com.titan.mainframe.MainFrame;
import com.titanserver.Command;
import com.titanserver.ReturnCommand;

public class VMMainPanel extends JPanel {
	MainFrame mainframe;
	JSlider slider = new JSlider();
	VMIconPanel iconPanel = new VMIconPanel(this);
	JLabel colLabel = new JLabel("");
	ButtonGroup group1 = new ButtonGroup();
	private JToggleButton ganttViewButton;
	private JToggleButton deltailViewButton;
	JScrollPane scrollPane = new JScrollPane();
	VMGanttPanel vmGanttPanel;
	private JComboBox sortComboBox;
	JSONObject json = null;

	public VMMainPanel(MainFrame mainFrame) {
		this.mainframe = mainFrame;
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
				refresh();
			}
		});
		toolBar.add(slider);

		slider.setValue(TitanSetting.getInstance().maxVMColumnCount);

		colLabel.setText(TitanSetting.getInstance().maxVMColumnCount + " columns");
		toolBar.add(colLabel);

		sortComboBox = new JComboBox(new String[] { "name", "cpu", "memory", "disk" });
		sortComboBox.setMaximumSize(new Dimension(100, 20));
		toolBar.add(sortComboBox);

		scrollPane.getVerticalScrollBar().setUnitIncrement(20);
		add(scrollPane, BorderLayout.CENTER);
		scrollPane.setViewportView(iconPanel);

		JPanel controlPanel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) controlPanel.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		add(controlPanel, BorderLayout.SOUTH);

		JButton btnLaunch = new JButton("Launch");
		btnLaunch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new LaunchInstanceDialog(VMMainPanel.this.mainframe).setVisible(true);
			}
		});
		btnLaunch.setIcon(new ImageIcon(VMMainPanel.class.getResource("/com/titan/image/famfamfam/add.png")));
		controlPanel.add(btnLaunch);

		JButton btnStop = new JButton("Stop");
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String instanceId = TitanCommonLib.getJSONString(json, "id", null);
				if (instanceId == null) {
					JOptionPane.showMessageDialog(VMMainPanel.this.mainframe, "Please select vm first", "Warning", JOptionPane.WARNING_MESSAGE);
					return;
				}
				int x = JOptionPane.showConfirmDialog(VMMainPanel.this.mainframe, "Confirm to stop instance : " + instanceId + " ?", "Warning", JOptionPane.YES_NO_OPTION);

				if (x == JOptionPane.YES_OPTION) {
					Command command = new Command();
					command.command = "from titan: nova stop";
					HashMap<String, String> parameters = new HashMap<String, String>();
					parameters.put("$InstanceId", instanceId);
					command.parameters.add(parameters);
					ReturnCommand r = CommunicateLib.send(TitanCommonLib.getCurrentServerIP(), command);
					String returnMessage = (String) r.map.get("result");
					if (!returnMessage.equals("")) {
						JOptionPane.showMessageDialog(VMMainPanel.this.mainframe, returnMessage);
					}
					refresh();
				}
			}
		});
		btnStop.setIcon(new ImageIcon(VMMainPanel.class.getResource("/com/titan/image/famfamfam/control_stop_blue.png")));
		controlPanel.add(btnStop);

		JButton btnDelete = new JButton("Delete");
		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnDelete.setIcon(new ImageIcon(VMMainPanel.class.getResource("/com/titan/image/famfamfam/cross.png")));
		controlPanel.add(btnDelete);

		JButton btnRemote = new JButton("Remote");
		btnRemote.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnRemote.setIcon(new ImageIcon(VMMainPanel.class.getResource("/com/titan/image/famfamfam/application_osx_terminal.png")));
		controlPanel.add(btnRemote);

		JButton btnPause = new JButton("Pause");
		btnPause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnPause.setIcon(new ImageIcon(VMMainPanel.class.getResource("/com/titan/image/famfamfam/control_pause_blue.png")));
		btnPause.setToolTipText("Stores the content of the VM in memory");
		controlPanel.add(btnPause);

		JButton btnUnpause = new JButton("Unpause");
		btnUnpause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnUnpause.setIcon(new ImageIcon(VMMainPanel.class.getResource("/com/titan/image/famfamfam/control_play_blue.png")));
		btnUnpause.setToolTipText("Unpause the content of the VM in memory");
		controlPanel.add(btnUnpause);

		JButton btnSuspend = new JButton("Suspend");
		btnSuspend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnSuspend.setToolTipText("Suspend VM to disk");
		btnSuspend.setIcon(new ImageIcon(VMMainPanel.class.getResource("/com/titan/image/famfamfam/disk.png")));
		controlPanel.add(btnSuspend);

		JButton btnResume = new JButton("Resume");
		btnResume.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnResume.setIcon(new ImageIcon(VMMainPanel.class.getResource("/com/titan/image/famfamfam/disk.png")));
		btnResume.setToolTipText("Resume VM from disk");
		controlPanel.add(btnResume);

		JButton btnLog = new JButton("Log");
		btnLog.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnLog.setIcon(new ImageIcon(VMMainPanel.class.getResource("/com/titan/image/famfamfam/script.png")));
		controlPanel.add(btnLog);

		JButton btnCreateSnapshot = new JButton("Create snapshot");
		btnCreateSnapshot.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		controlPanel.add(btnCreateSnapshot);

		JButton btnSoftReboot = new JButton("Soft reboot");
		btnSoftReboot.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		controlPanel.add(btnSoftReboot);

		JButton btnHardReboot = new JButton("Hard reboot");
		btnHardReboot.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		controlPanel.add(btnHardReboot);

		JButton btnAdvance = new JButton("Advance");
		btnAdvance.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		controlPanel.add(btnAdvance);

	}

	void refresh() {
		int maxVMColumnCount = (int) slider.getValue();
		iconPanel.init(maxVMColumnCount);
	}

}
