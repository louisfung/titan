package com.titan.instancepanel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.peterswing.CommonLib;
import com.peterswing.GenericTableModel;
import com.peterswing.advancedswing.jprogressbardialog.JProgressBarDialog;
import com.peterswing.advancedswing.jtable.SortableTableModel;
import com.peterswing.advancedswing.jtable.TableSorterColumnListener;
import com.peterswing.advancedswing.searchtextfield.JSearchTextField;
import com.titan.MainPanel;
import com.titan.SimpleTableDialog;
import com.titan.TitanCommonLib;
import com.titan.communication.CommunicateLib;
import com.titanserver.Command;
import com.titanserver.HttpResult;
import com.titanserver.ReturnCommand;

public class InstancePanel extends JPanel implements Runnable, MainPanel {
	JTable instanceTable;
	JFrame frame;
	JProgressBarDialog d;
	GenericTableModel instanceTableModel = new GenericTableModel();
	SortableTableModel instanceSortableTableModel = new SortableTableModel(instanceTableModel);
	TableSorterColumnListener instanceTableSorterColumnListener;
	final JSearchTextField searchTextField = new JSearchTextField();

	public InstancePanel(JFrame frame) {
		this.frame = frame;
		setBorder(new EmptyBorder(10, 10, 0, 10));

		JScrollPane scrollPane = new JScrollPane();

		instanceTable = new JTable();
		instanceTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					String instanceId = (String) instanceSortableTableModel.getValueAt(instanceTable.getSelectedRow(), instanceSortableTableModel.getColumnIndex("Id"));
					SimpleTableDialog dialog = new SimpleTableDialog(InstancePanel.this.frame, instanceId, true);
					dialog.tableModel.editables.put(1, true);

					dialog.tableModel.columnNames.clear();
					dialog.tableModel.columnNames.add("Name");
					dialog.tableModel.columnNames.add("Value");

					Vector<Object> col1 = new Vector<Object>();
					Vector<Object> col2 = new Vector<Object>();
					dialog.tableModel.values.clear();
					for (int x = 0; x < instanceTableModel.getColumnCount(); x++) {
						col1.add(instanceTableModel.getColumnName(x));
						col2.add(instanceTableModel.getValueAt(instanceTable.getSelectedRow(), x));
					}

					dialog.tableModel.values.add(col1);
					dialog.tableModel.values.add(col2);

					dialog.sortableableModel.fireTableStructureChanged();
					dialog.sortableableModel.fireTableDataChanged();

					CommonLib.centerDialog(dialog);
					dialog.setVisible(true);
				}
			}
		});
		instanceTable.getTableHeader().setReorderingAllowed(false);
		instanceTableSorterColumnListener = new TableSorterColumnListener(instanceTable, instanceSortableTableModel);
		instanceTable.setModel(instanceSortableTableModel);
		instanceTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		instanceTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		instanceTable.getTableHeader().addMouseListener(instanceTableSorterColumnListener);
		scrollPane.setViewportView(instanceTable);

		JPanel panel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);

		JButton btnLaunch = new JButton("Launch");
		btnLaunch.setIcon(new ImageIcon(InstancePanel.class.getResource("/com/titan/image/famfamfam/add.png")));
		btnLaunch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new LaunchInstanceDialog(InstancePanel.this.frame).setVisible(true);
				refresh();
			}
		});
		panel.add(btnLaunch);

		JButton btnStop = new JButton("Stop");
		btnStop.setIcon(new ImageIcon(InstancePanel.class.getResource("/com/titan/image/famfamfam/control_stop_blue.png")));
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (instanceTable.getSelectedRowCount() > 0) {
					String instanceId = (String) instanceSortableTableModel.getValueAt(instanceTable.getSelectedRow(), instanceSortableTableModel.getColumnIndex("Id"));
					int x = JOptionPane.showConfirmDialog(InstancePanel.this.frame, "Confirm to stop instance : " + instanceId + " ?", "Warning", JOptionPane.YES_NO_OPTION);

					if (x == JOptionPane.YES_OPTION) {
						Command command = new Command();
						command.command = "from titan: nova stop";
						HashMap<String, String> parameters = new HashMap<String, String>();
						parameters.put("$InstanceId", instanceId);
						command.parameters.add(parameters);
						ReturnCommand r = CommunicateLib.send(TitanCommonLib.getCurrentServerIP(), command);
						HttpResult httpResult = (HttpResult) r.map.get("result");
						String returnMessage = httpResult.content;
						if (!returnMessage.equals("")) {
							JOptionPane.showMessageDialog(InstancePanel.this.frame, returnMessage);
						}
						refresh();
					}
				}
			}
		});
		panel.add(btnStop);

		JButton btnRemote = new JButton("Remote");
		btnRemote.setIcon(new ImageIcon(InstancePanel.class.getResource("/com/titan/image/famfamfam/application_osx_terminal.png")));
		btnRemote.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (instanceTable.getSelectedRowCount() == 1) {
					String instanceName = (String) instanceTableModel.getValue("OS-EXT-SRV-ATTR:instance_name", instanceTable.getSelectedRow());
					MonitorDialog monitorDialog = new MonitorDialog(instanceName);
					monitorDialog.setVisible(true);
				}
			}
		});

		JButton btnDelete = new JButton("Delete");
		btnDelete.setIcon(new ImageIcon(InstancePanel.class.getResource("/com/titan/image/famfamfam/cross.png")));
		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (instanceTable.getSelectedRowCount() > 0) {
					final int rowCount = instanceTable.getRowCount();
					String instanceId = (String) instanceSortableTableModel.getValueAt(instanceTable.getSelectedRow(), instanceSortableTableModel.getColumnIndex("Id"));
					int x = JOptionPane.showConfirmDialog(InstancePanel.this.frame, "Confirm to delete instance : " + instanceId + " ?", "Warning", JOptionPane.YES_NO_OPTION);

					if (x == JOptionPane.YES_OPTION) {
						Command command = new Command();
						command.command = "from titan: nova delete";
						HashMap<String, String> parameters = new HashMap<String, String>();
						parameters.put("$InstanceId", instanceId);
						command.parameters.add(parameters);
						ReturnCommand r = CommunicateLib.send(TitanCommonLib.getCurrentServerIP(), command);
						HttpResult httpResult = (HttpResult) r.map.get("result");
						String returnMessage = httpResult.content;
						if (returnMessage != null) {
							JOptionPane.showMessageDialog(InstancePanel.this.frame, returnMessage);
						} else {
							final JProgressBarDialog d = new JProgressBarDialog(InstancePanel.this.frame, true);
							d.thread = new Thread() {
								public void run() {
									int newRowCount = instanceTableModel.getRowCount();
									long t1 = new Date().getTime();
									d.setTitle("Deleteing instance");
									d.jProgressBar.setString("Updating, please wait");
									d.jProgressBar.setStringPainted(true);
									d.jProgressBar.setIndeterminate(true);

									while (rowCount == newRowCount && new Date().getTime() - t1 < 10000) {
										Command command = new Command();
										command.command = "from titan: nova list";
										ReturnCommand r = CommunicateLib.send(TitanCommonLib.getCurrentServerIP(), command);
										HttpResult httpResult = (HttpResult) r.map.get("result");
										JSONArray servers = JSONObject.fromObject(httpResult.content).getJSONArray("servers");

										newRowCount = servers.size();
										try {
											Thread.sleep(100);
										} catch (InterruptedException e1) {
											e1.printStackTrace();
										}
										//										System.out.println(rowCount + "==" + newRowCount + " , " + new Date().getTime() + " > " + t1 + " === " + (long) (new Date().getTime() - t1));
									}
									refresh();
								}
							};
							d.setVisible(true);
						}
					}
				}
			}
		});
		panel.add(btnDelete);
		panel.add(btnRemote);

		JButton btnPause = new JButton("Pause");
		btnPause.setIcon(new ImageIcon(InstancePanel.class.getResource("/com/titan/image/famfamfam/control_pause_blue.png")));
		btnPause.setToolTipText("Stores the content of the VM in memory");
		btnPause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (instanceTable.getSelectedRowCount() > 0) {
					String instanceId = (String) instanceSortableTableModel.getValueAt(instanceTable.getSelectedRow(), instanceSortableTableModel.getColumnIndex("Id"));
					int x = JOptionPane.showConfirmDialog(InstancePanel.this.frame, "Confirm to pause instance : " + instanceId + " ?", "Warning", JOptionPane.YES_NO_OPTION);

					if (x == JOptionPane.YES_OPTION) {
						Command command = new Command();
						command.command = "from titan: nova pause";
						HashMap<String, String> parameters = new HashMap<String, String>();
						parameters.put("$InstanceId", instanceId);
						command.parameters.add(parameters);
						ReturnCommand r = CommunicateLib.send(TitanCommonLib.getCurrentServerIP(), command);
						HttpResult httpResult = (HttpResult) r.map.get("result");
						String returnMessage = httpResult.content;
						if (!returnMessage.equals("")) {
							JOptionPane.showMessageDialog(InstancePanel.this.frame, returnMessage);
						}
						refresh();
					}
				}
			}
		});
		panel.add(btnPause);

		JButton btnUnpause = new JButton("Unpause");
		btnUnpause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (instanceTable.getSelectedRowCount() > 0) {
					String instanceId = (String) instanceSortableTableModel.getValueAt(instanceTable.getSelectedRow(), instanceSortableTableModel.getColumnIndex("Id"));
					int x = JOptionPane.showConfirmDialog(InstancePanel.this.frame, "Confirm to unpause instance : " + instanceId + " ?", "Warning", JOptionPane.YES_NO_OPTION);

					if (x == JOptionPane.YES_OPTION) {
						Command command = new Command();
						command.command = "from titan: nova unpause";
						HashMap<String, String> parameters = new HashMap<String, String>();
						parameters.put("$InstanceId", instanceId);
						command.parameters.add(parameters);
						ReturnCommand r = CommunicateLib.send(TitanCommonLib.getCurrentServerIP(), command);
						HttpResult httpResult = (HttpResult) r.map.get("result");
						String returnMessage = httpResult.content;
						if (!returnMessage.equals("")) {
							JOptionPane.showMessageDialog(InstancePanel.this.frame, returnMessage);
						}
						refresh();
					}
				}
			}
		});
		btnUnpause.setIcon(new ImageIcon(InstancePanel.class.getResource("/com/titan/image/famfamfam/control_play_blue.png")));
		btnUnpause.setToolTipText("Unpause the content of the VM in memory");
		panel.add(btnUnpause);

		JButton btnSuspend = new JButton("Suspend");
		btnSuspend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (instanceTable.getSelectedRowCount() > 0) {
					String instanceId = (String) instanceSortableTableModel.getValueAt(instanceTable.getSelectedRow(), instanceSortableTableModel.getColumnIndex("Id"));
					int x = JOptionPane.showConfirmDialog(InstancePanel.this.frame, "Confirm to suspend instance : " + instanceId + " ?", "Warning", JOptionPane.YES_NO_OPTION);

					if (x == JOptionPane.YES_OPTION) {
						Command command = new Command();
						command.command = "from titan: nova suspend";
						HashMap<String, String> parameters = new HashMap<String, String>();
						parameters.put("$InstanceId", instanceId);
						command.parameters.add(parameters);
						ReturnCommand r = CommunicateLib.send(TitanCommonLib.getCurrentServerIP(), command);
						HttpResult httpResult = (HttpResult) r.map.get("result");
						String returnMessage = httpResult.content;
						if (!returnMessage.equals("")) {
							JOptionPane.showMessageDialog(InstancePanel.this.frame, returnMessage);
						}
						refresh();
					}
				}
			}
		});
		btnSuspend.setToolTipText("Suspend VM to disk");
		btnSuspend.setIcon(new ImageIcon(InstancePanel.class.getResource("/com/titan/image/famfamfam/disk.png")));
		panel.add(btnSuspend);

		JButton btnResume = new JButton("Resume");
		btnResume.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (instanceTable.getSelectedRowCount() > 0) {
					String instanceId = (String) instanceSortableTableModel.getValueAt(instanceTable.getSelectedRow(), instanceSortableTableModel.getColumnIndex("Id"));
					int x = JOptionPane.showConfirmDialog(InstancePanel.this.frame, "Confirm to resume instance : " + instanceId + " ?", "Warning", JOptionPane.YES_NO_OPTION);

					if (x == JOptionPane.YES_OPTION) {
						Command command = new Command();
						command.command = "from titan: nova resume";
						HashMap<String, String> parameters = new HashMap<String, String>();
						parameters.put("$InstanceId", instanceId);
						command.parameters.add(parameters);
						ReturnCommand r = CommunicateLib.send(TitanCommonLib.getCurrentServerIP(), command);
						HttpResult httpResult = (HttpResult) r.map.get("result");
						String returnMessage = httpResult.content;
						if (!returnMessage.equals("")) {
							JOptionPane.showMessageDialog(InstancePanel.this.frame, returnMessage);
						}
						refresh();
					}
				}
			}
		});
		btnResume.setToolTipText("Resume VM from disk");
		btnResume.setIcon(new ImageIcon(InstancePanel.class.getResource("/com/titan/image/famfamfam/disk.png")));
		panel.add(btnResume);

		JButton btnLog = new JButton("Log");
		btnLog.setIcon(new ImageIcon(InstancePanel.class.getResource("/com/titan/image/famfamfam/script.png")));
		btnLog.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (instanceTable.getSelectedRowCount() == 1) {
					String instanceId = (String) instanceSortableTableModel.getValueAt(instanceTable.getSelectedRow(), instanceSortableTableModel.getColumnIndex("Id"));
					ViewInstanceDialog dialog = new ViewInstanceDialog(InstancePanel.this.frame, instanceId);
					dialog.setVisible(true);
				}
			}
		});
		panel.add(btnLog);

		JButton btnCreateSnapshot = new JButton("Create snapshot");
		btnCreateSnapshot.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String name = JOptionPane.showInputDialog("Please input snapshot name?");
			}
		});
		panel.add(btnCreateSnapshot);

		JButton btnSoftReboot = new JButton("Soft reboot");
		btnSoftReboot.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String instanceId = (String) instanceSortableTableModel.getValueAt(instanceTable.getSelectedRow(), instanceSortableTableModel.getColumnIndex("Id"));
				int x = JOptionPane.showConfirmDialog(InstancePanel.this.frame, "Confirm to soft reboot instance : " + instanceId + " ?", "Warning", JOptionPane.YES_NO_OPTION);

				if (x == JOptionPane.YES_OPTION) {
					Command command = new Command();
					HashMap<String, String> parameters = new HashMap<String, String>();
					parameters.put("$InstanceId", instanceId);
					command.parameters.add(parameters);
					command.command = "from titan: nova soft-reboot";
					ReturnCommand r = CommunicateLib.send(TitanCommonLib.getCurrentServerIP(), command);
					JProgressBarDialog dialog = new JProgressBarDialog(InstancePanel.this.frame, true);
					dialog.jProgressBar.setIndeterminate(true);
					dialog.jProgressBar.setStringPainted(true);
					dialog.jProgressBar.setString("Waiting for instance reboot");
					dialog.thread = new Thread() {
						public void run() {
							try {
								Thread.sleep(2000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							refresh();
						}
					};
					dialog.setVisible(true);
				}
			}
		});
		panel.add(btnSoftReboot);

		JButton btnHardReboot = new JButton("Hard reboot");
		btnHardReboot.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String instanceId = (String) instanceSortableTableModel.getValueAt(instanceTable.getSelectedRow(), instanceSortableTableModel.getColumnIndex("Id"));
				int x = JOptionPane.showConfirmDialog(InstancePanel.this.frame, "Confirm to hard reboot instance : " + instanceId + " ?", "Warning", JOptionPane.YES_NO_OPTION);

				if (x == JOptionPane.YES_OPTION) {
					Command command = new Command();
					HashMap<String, String> parameters = new HashMap<String, String>();
					parameters.put("$InstanceId", instanceId);
					command.parameters.add(parameters);
					command.command = "from titan: nova hard-reboot";
					ReturnCommand r = CommunicateLib.send(TitanCommonLib.getCurrentServerIP(), command);
					JProgressBarDialog dialog = new JProgressBarDialog(InstancePanel.this.frame, true);
					dialog.jProgressBar.setIndeterminate(true);
					dialog.jProgressBar.setStringPainted(true);
					dialog.jProgressBar.setString("Waiting for instance reboot");
					dialog.thread = new Thread() {
						public void run() {
							try {
								Thread.sleep(2000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							refresh();
						}
					};
					dialog.setVisible(true);
				}
			}
		});
		panel.add(btnHardReboot);

		JButton btnAdvance = new JButton("Advance");
		btnAdvance.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (instanceTable.getSelectedRowCount() == 1) {
					String instanceName = (String) instanceTableModel.getValueAt(instanceTable.getSelectedRow(), instanceTableModel.getColumnIndex("OS-EXT-SRV-ATTR:instance_name"));
					new AdvanceInstanceDialog(InstancePanel.this.frame, instanceName).setVisible(true);
				}
			}
		});
		panel.add(btnAdvance);
		setLayout(new BorderLayout(0, 0));
		add(panel, BorderLayout.SOUTH);
		add(scrollPane);

		JPanel panel_1 = new JPanel();
		FlowLayout flowLayout_1 = (FlowLayout) panel_1.getLayout();
		flowLayout_1.setAlignment(FlowLayout.LEFT);
		add(panel_1, BorderLayout.NORTH);
		searchTextField.setPreferredSize(new Dimension(200, 25));
		panel_1.add(searchTextField);

		searchTextField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				instanceTableModel.searchPattern = searchTextField.getText();
				instanceTableModel.fireTableDataChanged();
				instanceTable.updateUI();
			}
		});

		refresh();
	}

	public void refresh() {
		d = new JProgressBarDialog(InstancePanel.this.frame, true);
		d.thread = new Thread(this);
		d.jProgressBar.setIndeterminate(true);
		d.jProgressBar.setStringPainted(true);
		d.setVisible(true);
	}

	public void run() {
		d.jProgressBar.setString("nova endpoints");
		Command command = new Command();
		command.command = "from titan: nova list";
		ReturnCommand r = CommunicateLib.send(TitanCommonLib.getCurrentServerIP(), command);
		HttpResult httpResult = (HttpResult) r.map.get("result");
		JSONArray servers = JSONObject.fromObject(httpResult.content).getJSONArray("servers");
		instanceTableModel.columnNames.clear();
		instanceTableModel.columnNames.add("Name");
		instanceTableModel.columnNames.add("status");
		instanceTableModel.columnNames.add("progress");
		instanceTableModel.columnNames.add("addresses");
		instanceTableModel.columnNames.add("flavor");
		instanceTableModel.columnNames.add("OS-EXT-SRV-ATTR:instance_name");
		instanceTableModel.columnNames.add("OS-EXT-SRV-ATTR:hypervisor_hostname");
		instanceTableModel.columnNames.add("OS-EXT-SRV-ATTR:host");
		instanceTableModel.columnNames.add("OS-EXT-STS:power_state");
		instanceTableModel.columnNames.add("OS-EXT-STS:vm_state");
		instanceTableModel.columnNames.add("OS-EXT-AZ:availability_zone");
		instanceTableModel.columnNames.add("OS-EXT-STS:task_state");
		instanceTableModel.columnNames.add("accessIPv4");
		instanceTableModel.columnNames.add("accessIPv6");
		instanceTableModel.columnNames.add("Id");
		instanceTableModel.columnNames.add("user_id");
		instanceTableModel.columnNames.add("config_drive");
		instanceTableModel.columnNames.add("created");
		instanceTableModel.columnNames.add("hostId");
		instanceTableModel.columnNames.add("key_name");
		instanceTableModel.columnNames.add("tenant_id");
		instanceTableModel.columnNames.add("updated");

		Vector<Object> col1 = new Vector<Object>();
		Vector<Object> col2 = new Vector<Object>();
		Vector<Object> col3 = new Vector<Object>();
		Vector<Object> col4 = new Vector<Object>();
		Vector<Object> col5 = new Vector<Object>();
		Vector<Object> col6 = new Vector<Object>();
		Vector<Object> col7 = new Vector<Object>();
		Vector<Object> col8 = new Vector<Object>();
		Vector<Object> col9 = new Vector<Object>();
		Vector<Object> col10 = new Vector<Object>();
		Vector<Object> col11 = new Vector<Object>();
		Vector<Object> col12 = new Vector<Object>();
		Vector<Object> col13 = new Vector<Object>();
		Vector<Object> col14 = new Vector<Object>();
		Vector<Object> col15 = new Vector<Object>();
		Vector<Object> col16 = new Vector<Object>();
		Vector<Object> col17 = new Vector<Object>();
		Vector<Object> col18 = new Vector<Object>();
		Vector<Object> col19 = new Vector<Object>();
		Vector<Object> col20 = new Vector<Object>();
		Vector<Object> col21 = new Vector<Object>();
		Vector<Object> col22 = new Vector<Object>();
		for (int x = 0; x < servers.size(); x++) {
			JSONObject obj = servers.getJSONObject(x);
			col1.add(TitanCommonLib.getJSONString(obj, "name", ""));
			col2.add(TitanCommonLib.getJSONString(obj, "status", ""));
			col3.add(TitanCommonLib.getJSONString(obj, "progress", ""));

			try {
				String address = "";
				for (int z = 0; z < obj.getJSONObject("addresses").getJSONArray("private").size(); z++) {
					JSONObject link = obj.getJSONObject("addresses").getJSONArray("private").getJSONObject(z);
					address += "type=" + link.getString("OS-EXT-IPS:type") + ", ";
					address += "addr=" + link.getString("addr") + ", ";
					address += "version=" + link.getString("version") + ", ";
				}
				col4.add(address);
			} catch (Exception ex) {
				col4.add("");
			}
			col5.add(TitanCommonLib.getJSONString(obj.getJSONObject("flavor"), "id", ""));
			col6.add(TitanCommonLib.getJSONString(obj, "OS-EXT-SRV-ATTR:instance_name", ""));
			col7.add(TitanCommonLib.getJSONString(obj, "OS-EXT-SRV-ATTR:hypervisor_hostname", ""));
			col8.add(TitanCommonLib.getJSONString(obj, "OS-EXT-SRV-ATTR:host", ""));
			col9.add(TitanCommonLib.getJSONString(obj, "OS-EXT-STS:power_state", ""));
			col10.add(TitanCommonLib.getJSONString(obj, "OS-EXT-STS:vm_state", ""));
			col11.add(TitanCommonLib.getJSONString(obj, "OS-EXT-AZ:availability_zone", ""));
			col12.add(TitanCommonLib.getJSONString(obj, "OS-EXT-STS:task_state", ""));
			col13.add(TitanCommonLib.getJSONString(obj, "accessIPv4", ""));
			col14.add(TitanCommonLib.getJSONString(obj, "accessIPv6", ""));
			col15.add(TitanCommonLib.getJSONString(obj, "id", ""));
			col16.add(TitanCommonLib.getJSONString(obj, "user_id", ""));
			col17.add(TitanCommonLib.getJSONString(obj, "config_drive", ""));
			col18.add(TitanCommonLib.getJSONString(obj, "created", ""));
			col19.add(TitanCommonLib.getJSONString(obj, "hostId", ""));
			col20.add(TitanCommonLib.getJSONString(obj, "key_name", ""));
			col21.add(TitanCommonLib.getJSONString(obj, "tenant_id", ""));
			col22.add(TitanCommonLib.getJSONString(obj, "updated", ""));
		}
		instanceTableModel.values.clear();
		instanceTableModel.values.add(col1);
		instanceTableModel.values.add(col2);
		instanceTableModel.values.add(col3);
		instanceTableModel.values.add(col4);
		instanceTableModel.values.add(col5);
		instanceTableModel.values.add(col6);
		instanceTableModel.values.add(col7);
		instanceTableModel.values.add(col8);
		instanceTableModel.values.add(col9);
		instanceTableModel.values.add(col10);
		instanceTableModel.values.add(col11);
		instanceTableModel.values.add(col12);
		instanceTableModel.values.add(col13);
		instanceTableModel.values.add(col14);
		instanceTableModel.values.add(col15);
		instanceTableModel.values.add(col16);
		instanceTableModel.values.add(col17);
		instanceTableModel.values.add(col18);
		instanceTableModel.values.add(col19);
		instanceTableModel.values.add(col20);
		instanceTableModel.values.add(col21);
		instanceTableModel.values.add(col22);

		instanceSortableTableModel.fireTableStructureChanged();
		instanceSortableTableModel.fireTableDataChanged();

		instanceTable.getColumnModel().getColumn(0).setPreferredWidth(350);
		instanceTable.getColumnModel().getColumn(1).setPreferredWidth(100);
		instanceTable.getColumnModel().getColumn(2).setPreferredWidth(100);
		instanceTable.getColumnModel().getColumn(3).setPreferredWidth(300);
		instanceTable.getColumnModel().getColumn(4).setPreferredWidth(100);
		instanceTable.getColumnModel().getColumn(5).setPreferredWidth(150);
		instanceTable.getColumnModel().getColumn(6).setPreferredWidth(150);
		instanceTable.getColumnModel().getColumn(7).setPreferredWidth(150);
		instanceTable.getColumnModel().getColumn(8).setPreferredWidth(150);
		instanceTable.getColumnModel().getColumn(9).setPreferredWidth(150);
		instanceTable.getColumnModel().getColumn(10).setPreferredWidth(150);
		instanceTable.getColumnModel().getColumn(11).setPreferredWidth(150);
		instanceTable.getColumnModel().getColumn(12).setPreferredWidth(150);
		instanceTable.getColumnModel().getColumn(13).setPreferredWidth(150);
		instanceTable.getColumnModel().getColumn(14).setPreferredWidth(150);
		instanceTable.getColumnModel().getColumn(15).setPreferredWidth(150);
		instanceTable.getColumnModel().getColumn(16).setPreferredWidth(150);
		instanceTable.getColumnModel().getColumn(17).setPreferredWidth(150);
		instanceTable.getColumnModel().getColumn(18).setPreferredWidth(150);
		instanceTable.getColumnModel().getColumn(19).setPreferredWidth(150);
		instanceTable.getColumnModel().getColumn(20).setPreferredWidth(300);
		instanceTable.getColumnModel().getColumn(21).setPreferredWidth(300);

		DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
		rightRenderer.setHorizontalAlignment(JTextField.RIGHT);
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JTextField.CENTER);

		for (int x = 0; x < 22; x++) {
			instanceTable.getColumnModel().getColumn(x).setCellRenderer(centerRenderer);
		}

		//		instanceTableModel.editables.clear();
		//		instanceTableModel.editables.add(false);
		//		instanceTableModel.editables.add(false);
		//		instanceTableModel.editables.add(false);
		//		instanceTableModel.editables.add(false);
		//		instanceTableModel.editables.add(false);
		//		instanceTableModel.editables.add(false);
		//		instanceTableModel.editables.add(false);
		//		instanceTableModel.editables.add(false);
		//		instanceTableModel.editables.add(false);
		//		instanceTableModel.editables.add(false);
		//		instanceTableModel.editables.add(false);
	}
}
