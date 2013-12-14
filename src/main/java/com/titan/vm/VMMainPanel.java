package com.titan.vm;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.RowFilter;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.TableRowSorter;

import net.sf.json.JSONObject;

import com.peterswing.advancedswing.searchtextfield.JSearchTextField;
import com.titan.TitanCommonLib;
import com.titan.TitanSetting;
import com.titan.communication.CommunicateLib;
import com.titan.instancepanel.LaunchInstanceDialog;
import com.titan.instancepanel.MonitorDialog;
import com.titan.instancepanel.ViewInstanceDialog;
import com.titan.mainframe.MainFrame;
import com.titan.vm.vmdialog.VMDialog;
import com.titanserver.Command;
import com.titanserver.ReturnCommand;

public class VMMainPanel extends JPanel implements Runnable {
	MainFrame mainframe;
	JSlider slider = new JSlider();
	VMIconPanel iconPanel = new VMIconPanel(this);
	JLabel colLabel = new JLabel("");
	ButtonGroup group1 = new ButtonGroup();
	ButtonGroup chartButtonGroup = new ButtonGroup();
	private JToggleButton ganttViewButton;
	private JToggleButton deltailViewButton;
	JScrollPane scrollPane = new JScrollPane();
	VMGanttPanel vmGanttPanel;
	private JComboBox sortComboBox;
	JSONObject selectedVM = null;
	private JTable propertyTable;
	private JSplitPane splitPane;
	PropertyTableModel propertyTableModel = new PropertyTableModel();
	private JSearchTextField searchPropertyTextField;
	TableRowSorter<PropertyTableModel> propertyTableRowSorter;
	private JRadioButton chartRadioButton;
	private JRadioButton numberRadioButton;
	private JSearchTextField searchTextField;
	boolean isUpdatePropertyTableThreadRunning = false;
	boolean isUpdatePropertyTableThreadTrigger = false;

	public VMMainPanel(final MainFrame mainFrame) {
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
		btnRefresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int maxVMColumnCount = (int) slider.getValue();
				iconPanel.init(searchTextField.getText(), maxVMColumnCount);
			}
		});
		btnRefresh.setIcon(new ImageIcon(VMMainPanel.class.getResource("/com/titan/image/famfamfam/arrow_rotate_clockwise.png")));
		toolBar.add(btnRefresh);

		searchTextField = new JSearchTextField();
		searchTextField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				refresh();
			}
		});
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

		chartRadioButton = new JRadioButton("Chart");
		chartButtonGroup.add(chartRadioButton);
		chartRadioButton.setSelected(true);
		toolBar.add(chartRadioButton);

		numberRadioButton = new JRadioButton("Number");
		chartButtonGroup.add(numberRadioButton);
		toolBar.add(numberRadioButton);

		scrollPane.getVerticalScrollBar().setUnitIncrement(20);
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
				action("from titan: nova stop");
			}
		});
		btnStop.setIcon(new ImageIcon(VMMainPanel.class.getResource("/com/titan/image/famfamfam/control_stop_blue.png")));
		controlPanel.add(btnStop);

		JButton btnDelete = new JButton("Delete");
		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				action("from titan: nova delete");
			}
		});
		btnDelete.setIcon(new ImageIcon(VMMainPanel.class.getResource("/com/titan/image/famfamfam/cross.png")));
		controlPanel.add(btnDelete);

		JButton btnRemote = new JButton("Remote");
		btnRemote.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String instanceName = TitanCommonLib.getJSONString(selectedVM, "OS-EXT-SRV-ATTR:instance_name", null);
				MonitorDialog monitorDialog = new MonitorDialog(instanceName);
				monitorDialog.setVisible(true);
			}
		});
		btnRemote.setIcon(new ImageIcon(VMMainPanel.class.getResource("/com/titan/image/famfamfam/application_osx_terminal.png")));
		controlPanel.add(btnRemote);

		JButton btnPause = new JButton("Pause");
		btnPause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				action("from titan: nova pause");
			}
		});
		btnPause.setIcon(new ImageIcon(VMMainPanel.class.getResource("/com/titan/image/famfamfam/control_pause_blue.png")));
		btnPause.setToolTipText("Stores the content of the VM in memory");
		controlPanel.add(btnPause);

		JButton btnUnpause = new JButton("Unpause");
		btnUnpause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				action("from titan: nova unpause");
			}
		});
		btnUnpause.setIcon(new ImageIcon(VMMainPanel.class.getResource("/com/titan/image/famfamfam/control_play_blue.png")));
		btnUnpause.setToolTipText("Unpause the content of the VM in memory");
		controlPanel.add(btnUnpause);

		JButton btnSuspend = new JButton("Suspend");
		btnSuspend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				action("from titan: nova suspend");
			}
		});
		btnSuspend.setToolTipText("Suspend VM to disk");
		btnSuspend.setIcon(new ImageIcon(VMMainPanel.class.getResource("/com/titan/image/famfamfam/disk.png")));
		controlPanel.add(btnSuspend);

		JButton btnResume = new JButton("Resume");
		btnResume.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				action("from titan: nova resume");
			}
		});
		btnResume.setIcon(new ImageIcon(VMMainPanel.class.getResource("/com/titan/image/famfamfam/disk.png")));
		btnResume.setToolTipText("Resume VM from disk");
		controlPanel.add(btnResume);

		JButton btnLog = new JButton("Log");
		btnLog.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String instanceId = TitanCommonLib.getJSONString(selectedVM, "id", null);
				ViewInstanceDialog dialog = new ViewInstanceDialog(mainframe, instanceId);
				dialog.setVisible(true);
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
				action("from titan: nova soft-reboot");
			}
		});
		controlPanel.add(btnSoftReboot);

		JButton btnHardReboot = new JButton("Hard reboot");
		btnHardReboot.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				action("from titan: nova hard-reboot");
			}
		});
		controlPanel.add(btnHardReboot);

		JButton btnAdvance = new JButton("Advance");
		btnAdvance.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		controlPanel.add(btnAdvance);

		JPanel propertyPanel = new JPanel();
		propertyPanel.setLayout(new BorderLayout(0, 0));

		JScrollPane propertyScrollPane = new JScrollPane();
		propertyPanel.add(propertyScrollPane, BorderLayout.CENTER);

		propertyTable = new JTable();
		propertyTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Property property = (Property) propertyTable.getValueAt(propertyTable.getSelectedRow(), 0);
				if (property.isData) {

				} else {
					property.expand = !property.expand;
					propertyTableModel.fireTableStructureChanged();
				}
			}
		});
		propertyTable.setModel(propertyTableModel);
		propertyTable.setDefaultRenderer(Property.class, new PropertyTableCellRenderer());
		propertyTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		propertyTableRowSorter = new TableRowSorter<PropertyTableModel>(propertyTableModel);
		propertyTable.setRowSorter(propertyTableRowSorter);
		RowFilter<PropertyTableModel, Integer> filter = new RowFilter<PropertyTableModel, Integer>() {
			@Override
			public boolean include(javax.swing.RowFilter.Entry<? extends PropertyTableModel, ? extends Integer> entry) {
				PropertyTableModel model = entry.getModel();
				Property property = (Property) model.getValueAt(entry.getIdentifier(), 0);
				if (searchPropertyTextField.getText().trim().equals("")) {
					return true;
				} else if (property.isData && property.name.toLowerCase().contains(searchPropertyTextField.getText().trim().toLowerCase())) {
					return true;
				} else if (!property.isData) {
					for (Property p : model.data) {
						if (p.isData && property.type.equals(p.type) && p.name.toLowerCase().contains(searchPropertyTextField.getText().trim().toLowerCase())) {
							return true;
						}
					}
					return false;
				} else {
					return false;
				}
			}
		};
		propertyTableRowSorter.setRowFilter(filter);
		propertyScrollPane.setViewportView(propertyTable);

		splitPane = new JSplitPane();
		splitPane.setOneTouchExpandable(true);
		add(splitPane, BorderLayout.CENTER);
		splitPane.add(scrollPane, JSplitPane.LEFT);
		splitPane.add(propertyPanel, JSplitPane.RIGHT);

		JPanel panel_1 = new JPanel();
		FlowLayout flowLayout_1 = (FlowLayout) panel_1.getLayout();
		flowLayout_1.setAlignment(FlowLayout.LEFT);
		propertyPanel.add(panel_1, BorderLayout.NORTH);

		searchPropertyTextField = new JSearchTextField();
		searchPropertyTextField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				//				propertyTableModel.filter(searchPropertyTextField.getText());
				propertyTableModel.fireTableStructureChanged();
			}
		});
		searchPropertyTextField.setPreferredSize(new Dimension(80, 20));
		panel_1.add(searchPropertyTextField);
		splitPane.setResizeWeight(0.7d);

		initPropertyTableModel();
		propertyTable.getColumnModel().getColumn(0).setPreferredWidth(20);
		propertyTable.getColumnModel().getColumn(1).setPreferredWidth(200);
		propertyTable.getColumnModel().getColumn(2).setPreferredWidth(200);
	}

	private void initPropertyTableModel() {
		propertyTableModel.data.add(new Property("instance", "", "", false));
		propertyTableModel.data.add(new Property("instance", "id", ""));
		propertyTableModel.data.add(new Property("instance", "status", ""));
		propertyTableModel.data.add(new Property("instance", "updated", ""));
		propertyTableModel.data.add(new Property("instance", "hostId", ""));
		propertyTableModel.data.add(new Property("instance", "OS-EXT-SRV-ATTR:host", ""));
		propertyTableModel.data.add(new Property("instance", "addresses", ""));
		propertyTableModel.data.add(new Property("instance", "links", ""));
		propertyTableModel.data.add(new Property("instance", "image", ""));
		propertyTableModel.data.add(new Property("instance", "OS-EXT-STS:vm_state", ""));
		propertyTableModel.data.add(new Property("instance", "OS-EXT-SRV-ATTR:instance_name", ""));
		propertyTableModel.data.add(new Property("instance", "OS-SRV-USG:launched_at", ""));
		propertyTableModel.data.add(new Property("instance", "OS-EXT-SRV-ATTR:hypervisor_hostname", ""));
		propertyTableModel.data.add(new Property("instance", "flavor", ""));
		propertyTableModel.data.add(new Property("instance", "OS-EXT-AZ:availability_zone", ""));
		propertyTableModel.data.add(new Property("instance", "user_id", ""));
		propertyTableModel.data.add(new Property("instance", "name", ""));
		propertyTableModel.data.add(new Property("instance", "created", ""));
		propertyTableModel.data.add(new Property("instance", "tenant_id", ""));
		propertyTableModel.data.add(new Property("instance", "OS-DCF:diskConfig", ""));
		propertyTableModel.data.add(new Property("instance", "os-extended-volumes:volumes_attached", ""));
		propertyTableModel.data.add(new Property("instance", "accessIPv4", ""));
		propertyTableModel.data.add(new Property("instance", "accessIPv6", ""));
		propertyTableModel.data.add(new Property("instance", "OS-EXT-STS:power_state", ""));
		propertyTableModel.data.add(new Property("instance", "config_drive", ""));
		propertyTableModel.data.add(new Property("instance", "metadata", ""));

		propertyTableModel.data.add(new Property("diagnostics", "", "", false));
		propertyTableModel.data.add(new Property("diagnostics", "cpu0_time", ""));
		propertyTableModel.data.add(new Property("diagnostics", "hdd_errors", ""));
		propertyTableModel.data.add(new Property("diagnostics", "hdd_read", ""));
		propertyTableModel.data.add(new Property("diagnostics", "hdd_read_req", ""));
		propertyTableModel.data.add(new Property("diagnostics", "hdd_write", ""));
		propertyTableModel.data.add(new Property("diagnostics", "hdd_write_req", ""));
		propertyTableModel.data.add(new Property("diagnostics", "memory", ""));
		propertyTableModel.data.add(new Property("diagnostics", "vda_errors", ""));
		propertyTableModel.data.add(new Property("diagnostics", "vda_read", ""));
		propertyTableModel.data.add(new Property("diagnostics", "vda_read_req", ""));
		propertyTableModel.data.add(new Property("diagnostics", "vda_write", ""));
		propertyTableModel.data.add(new Property("diagnostics", "vda_write_req", ""));
		propertyTableModel.data.add(new Property("diagnostics", "vnet1_rx", ""));
		propertyTableModel.data.add(new Property("diagnostics", "vnet1_rx_drop", ""));
		propertyTableModel.data.add(new Property("diagnostics", "vnet1_rx_errors", ""));
		propertyTableModel.data.add(new Property("diagnostics", "vnet1_rx_packets", ""));
		propertyTableModel.data.add(new Property("diagnostics", "vnet1_tx", ""));
		propertyTableModel.data.add(new Property("diagnostics", "vnet1_tx_drop", ""));
		propertyTableModel.data.add(new Property("diagnostics", "vnet1_tx_errors", ""));
		propertyTableModel.data.add(new Property("diagnostics", "vnet1_tx_packets", ""));

		propertyTableModel.fireTableStructureChanged();
	}

	void refresh() {
		int maxVMColumnCount = (int) slider.getValue();
		iconPanel.init(searchTextField.getText(), maxVMColumnCount);
	}

	void action(final String command) {
		VMPanel vmPanel = iconPanel;
		Vector<JSONObject> vms = vmPanel.getSelectedVM();

		if (vms.size() == 0) {
			JOptionPane.showMessageDialog(VMMainPanel.this.mainframe, "Please select vm first", "Warning", JOptionPane.WARNING_MESSAGE);
			return;
		}
		final VMDialog vmDialog = new VMDialog(mainframe);
		vmDialog.vmDialogTableModel.data.clear();
		for (JSONObject json : vms) {
			String instanceId = TitanCommonLib.getJSONString(json, "id", null);
			String name = TitanCommonLib.getJSONString(json, "name", null);
			String vmType = "unknown";
			vmDialog.vmDialogTableModel.add(instanceId, name, vmType);
		}
		vmDialog.thread = new Thread() {
			public void run() {
				for (int x = 0; x < vmDialog.vmDialogTableModel.getRowCount(); x++) {
					if (vmDialog.stopTrigger) {
						break;
					}
					Command cmd = new Command();
					cmd.command = command;
					HashMap<String, String> parameters = new HashMap<String, String>();
					String instanceId = (String) vmDialog.vmDialogTableModel.getValueAt(x, 0);
					parameters.put("$InstanceId", instanceId);
					vmDialog.vmDialogTableModel.updateStatus(instanceId, "running");
					cmd.parameters.add(parameters);
					ReturnCommand r = CommunicateLib.send(TitanCommonLib.getCurrentServerIP(), cmd);
					String returnMessage = (String) r.map.get("result");
					vmDialog.vmDialogTableModel.updateStatus(instanceId, "done");
				}
				refresh();
			}
		};
		vmDialog.setVisible(true);
	}

	@Override
	public void run() {
		try {
			isUpdatePropertyTableThreadTrigger = false;
			isUpdatePropertyTableThreadRunning = true;
			for (Property p : propertyTableModel.data) {
				propertyTableModel.changeValue(p.name, TitanCommonLib.getJSONString(selectedVM, p.name, null));
			}

			Command command = new Command();
			command.command = "from titan: nova diagnostics";
			HashMap<String, String> parameters = new HashMap<String, String>();
			parameters.put("$InstanceId", TitanCommonLib.getJSONString(selectedVM, "id", null));
			command.parameters.add(parameters);
			ReturnCommand r = CommunicateLib.send(TitanCommonLib.getCurrentServerIP(), command);
			String json = (String) r.map.get("result");
			JSONObject jsonObject = JSONObject.fromObject(json);

			for (Property p : propertyTableModel.data) {
				if (isUpdatePropertyTableThreadTrigger) {
					isUpdatePropertyTableThreadRunning = false;
					return;
				}
				String value = TitanCommonLib.getJSONString(jsonObject, p.name, null);
				if (value != null) {
					propertyTableModel.changeValue(p.name, value);
				}
			}
		} catch (Exception ex) {
		}
		isUpdatePropertyTableThreadRunning = false;
	}

}
