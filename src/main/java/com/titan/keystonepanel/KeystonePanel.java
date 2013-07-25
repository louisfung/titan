package com.titan.keystonepanel;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableCellRenderer;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.peterswing.CommonLib;
import com.peterswing.GenericTableModel;
import com.peterswing.advancedswing.jprogressbardialog.JProgressBarDialog;
import com.peterswing.advancedswing.jtable.SortableTableModel;
import com.peterswing.advancedswing.jtable.TableSorterColumnListener;
import com.titan.MainPanel;
import com.titan.TitanCommonLib;
import com.titan.communication.CommunicateLib;
import com.titanserver.Command;
import com.titanserver.ReturnCommand;

public class KeystonePanel extends JPanel implements MainPanel {
	JProgressBarDialog d;
	final JFrame frame;

	private JTable userTable;
	SortableTableModel userTableModel = new SortableTableModel(new GenericTableModel());
	TableSorterColumnListener userTableSorterColumnListener;

	private JTable roleTable;
	SortableTableModel roleTableModel = new SortableTableModel(new GenericTableModel());
	TableSorterColumnListener roleTableSorterColumnListener;

	private JTable tenantTable;
	SortableTableModel tenantTableModel = new SortableTableModel(new GenericTableModel());
	TableSorterColumnListener tenantTableSorterColumnListener;

	private JTable endpointTable;
	SortableTableModel endpointTableModel = new SortableTableModel(new GenericTableModel());
	TableSorterColumnListener endpointTableSorterColumnListener;
	private JTabbedPane tabbedPane;

	public KeystonePanel(final JFrame frame) {
		this.frame = frame;
		setLayout(new BorderLayout(0, 0));

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (KeystonePanel.this.isVisible()) {
					String title = tabbedPane.getTitleAt(tabbedPane.getSelectedIndex());
					if (title.equals("User")) {
						initUserTable();
					} else if (title.equals("Role")) {
						initRoleTable();
					} else if (title.equals("Tenant")) {
						initTenantTable();
					} else if (title.equals("Endpoint")) {
						initEndpointTable();
					}
				}
			}
		});
		add(tabbedPane, BorderLayout.CENTER);

		JPanel userPanel = new JPanel();
		tabbedPane.addTab("User", null, userPanel, null);
		userPanel.setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane = new JScrollPane();
		userPanel.add(scrollPane, BorderLayout.CENTER);

		userTable = new JTable();
		userTableSorterColumnListener = new TableSorterColumnListener(userTable, userTableModel);
		userTable.getTableHeader().setReorderingAllowed(false);
		userTable.setModel(userTableModel);
		userTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		userTable.getTableHeader().addMouseListener(userTableSorterColumnListener);
		userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane.setViewportView(userTable);

		JPanel panel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		userPanel.add(panel, BorderLayout.SOUTH);

		JButton btnAddUser = new JButton("Add user");
		btnAddUser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AddUserDialog d = new AddUserDialog(frame, getAllTenants());
				d.setVisible(true);
			}
		});
		panel.add(btnAddUser);

		JButton btnDeleteUser = new JButton("Delete user");
		panel.add(btnDeleteUser);

		JButton btnDetail = new JButton("Detail");
		btnDetail.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (userTable.getSelectedRowCount() == 1) {
					GenericTableModel tenantModel = (GenericTableModel) tenantTableModel.model;
					if (tenantModel.getRowCount() == 0) {
						initTenantTable();
					}
					Hashtable<String, String> allTenants = getAllTenants();

					String userId = userTable.getValueAt(userTable.getSelectedRow(), 0).toString();
					String username = userTable.getValueAt(userTable.getSelectedRow(), 1).toString();
					String Tenant_Id = userTable.getValueAt(userTable.getSelectedRow(), 2).toString();
					UserDetailDialog d = new UserDetailDialog(frame, userId, username, Tenant_Id, allTenants);
					d.setVisible(true);
				}
			}
		});
		panel.add(btnDetail);

		JButton btnAssignRole = new JButton("Assign role");
		btnAssignRole.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (userTable.getSelectedRowCount() == 0) {
					JOptionPane.showMessageDialog(frame, "Please select a user", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				GenericTableModel tenantModel = (GenericTableModel) tenantTableModel.model;
				if (tenantModel.getRowCount() == 0) {
					initTenantTable();
				}
				if (tenantModel.getRowCount() == 0) {
					JOptionPane.showMessageDialog(frame, "No tenant in your openstack", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				GenericTableModel roleModel = (GenericTableModel) roleTableModel.model;
				if (roleModel.getRowCount() == 0) {
					initRoleTable();
				}
				if (roleModel.getRowCount() == 0) {
					JOptionPane.showMessageDialog(frame, "No role in your openstack", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				Hashtable<String, String> allTenants = getAllTenants();
				Hashtable<String, String> allRoles = getAllRoles();
				String userId = (String) userTable.getValueAt(userTable.getSelectedRow(), 0);
				String username = (String) userTable.getValueAt(userTable.getSelectedRow(), 1);
				String defaultTenant = (String) userTable.getValueAt(userTable.getSelectedRow(), 3);
				AssignUserRoleDialog d = new AssignUserRoleDialog(frame, userId, username, defaultTenant, allRoles, allTenants);
				d.setVisible(true);
			}
		});
		panel.add(btnAssignRole);

		JButton btnChangePassword = new JButton("Change password");
		panel.add(btnChangePassword);

		JPanel rolePanel = new JPanel();
		tabbedPane.addTab("Role", null, rolePanel, null);
		rolePanel.setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane_1 = new JScrollPane();
		rolePanel.add(scrollPane_1, BorderLayout.CENTER);

		roleTable = new JTable();
		roleTableSorterColumnListener = new TableSorterColumnListener(roleTable, roleTableModel);
		roleTable.getTableHeader().setReorderingAllowed(false);
		roleTable.setModel(roleTableModel);
		roleTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		roleTable.getTableHeader().addMouseListener(roleTableSorterColumnListener);
		roleTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane_1.setViewportView(roleTable);

		JPanel panel_1 = new JPanel();
		FlowLayout flowLayout_1 = (FlowLayout) panel_1.getLayout();
		flowLayout_1.setAlignment(FlowLayout.LEFT);
		rolePanel.add(panel_1, BorderLayout.SOUTH);

		JButton btnAddRole = new JButton("Add role");
		btnAddRole.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String roleName = JOptionPane.showInputDialog(frame, "Please input role name ?", "Question", JOptionPane.QUESTION_MESSAGE);
				if (roleName != null) {
					if (roleName.trim().equals("")) {
						JOptionPane.showMessageDialog(frame, "Role name cannot be empty !", "Error", JOptionPane.ERROR_MESSAGE);
					} else {
						Command command = new Command();
						command.command = "from titan: keystone role-create";
						HashMap<String, String> parameters = new HashMap<String, String>();
						parameters.put("$roleName", roleName);
						command.parameters.add(parameters);
						ReturnCommand r = CommunicateLib.send(TitanCommonLib.getCurrentServerIP(), command);
						String msg = (String) r.map.get("result");

						if (msg == null) {
							JOptionPane.showMessageDialog(frame, "Error, return value is null", "Error", JOptionPane.ERROR_MESSAGE);
							return;
						} else if (msg.contains("error")) {
							String errorMessage = JSONObject.fromObject(msg).getJSONObject("error").getString("message");
							if (errorMessage.length() > 80) {
								errorMessage = CommonLib.splitString(errorMessage, "\n", 80);
							}
							JOptionPane.showMessageDialog(frame, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
							return;
						} else {
							initRoleTable();
						}
					}
				}
			}
		});
		panel_1.add(btnAddRole);

		JButton btnDeleteRole = new JButton("Delete role");
		btnDeleteRole.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (roleTable.getSelectedRowCount() == 1) {
					String roleId = roleTable.getValueAt(roleTable.getSelectedRow(), 0).toString();
					String roleName = roleTable.getValueAt(roleTable.getSelectedRow(), 1).toString();
					int x = JOptionPane.showConfirmDialog(null, "Confirm to delete " + roleName + " ?", "Warning", JOptionPane.YES_NO_OPTION);

					if (x == JOptionPane.YES_OPTION) {
						Command command = new Command();
						command.command = "from titan: keystone role-delete";
						HashMap<String, String> parameters = new HashMap<String, String>();
						parameters.put("$roleId", roleId);
						command.parameters.add(parameters);
						ReturnCommand r = CommunicateLib.send(TitanCommonLib.getCurrentServerIP(), command);
						String msg = (String) r.map.get("result");
						initRoleTable();
					}
				}
			}
		});
		panel_1.add(btnDeleteRole);

		JPanel tenantPanel = new JPanel();
		tabbedPane.addTab("Tenant", null, tenantPanel, null);
		tenantPanel.setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane_2 = new JScrollPane();
		tenantPanel.add(scrollPane_2, BorderLayout.CENTER);

		tenantTable = new JTable();
		tenantTableSorterColumnListener = new TableSorterColumnListener(tenantTable, tenantTableModel);
		tenantTable.getTableHeader().setReorderingAllowed(false);
		tenantTable.setModel(tenantTableModel);
		tenantTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		tenantTable.getTableHeader().addMouseListener(tenantTableSorterColumnListener);
		tenantTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane_2.setViewportView(tenantTable);

		JPanel panel_2 = new JPanel();
		FlowLayout flowLayout_2 = (FlowLayout) panel_2.getLayout();
		flowLayout_2.setAlignment(FlowLayout.LEFT);
		tenantPanel.add(panel_2, BorderLayout.SOUTH);

		JButton btnCreateTenant = new JButton("Create tenant");
		btnCreateTenant.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AddTenantDialog d = new AddTenantDialog(frame);
				d.setVisible(true);
				initTenantTable();
			}
		});
		panel_2.add(btnCreateTenant);

		JButton btnDeleteTenant = new JButton("Delete tenant");
		btnDeleteTenant.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (tenantTable.getSelectedRowCount() == 1) {
					String tenantId = tenantTable.getValueAt(tenantTable.getSelectedRow(), 0).toString();
					String tenantName = tenantTable.getValueAt(tenantTable.getSelectedRow(), 1).toString();
					int x = JOptionPane.showConfirmDialog(null, "Confirm to delete " + tenantName + " ?", "Warning", JOptionPane.YES_NO_OPTION);

					if (x == JOptionPane.YES_OPTION) {
						Command command = new Command();
						command.command = "from titan: keystone tenant-delete";
						HashMap<String, String> parameters = new HashMap<String, String>();
						parameters.put("$tenantId", tenantId);
						command.parameters.add(parameters);
						ReturnCommand r = CommunicateLib.send(TitanCommonLib.getCurrentServerIP(), command);
						String msg = (String) r.map.get("result");

						initTenantTable();
					}
				}
			}
		});
		panel_2.add(btnDeleteTenant);

		JPanel endpointPanel = new JPanel();
		tabbedPane.addTab("Endpoint", null, endpointPanel, null);
		endpointPanel.setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane_3 = new JScrollPane();
		endpointPanel.add(scrollPane_3, BorderLayout.CENTER);

		endpointTable = new JTable();
		endpointTableSorterColumnListener = new TableSorterColumnListener(endpointTable, endpointTableModel);
		endpointTable.getTableHeader().setReorderingAllowed(false);
		endpointTable.setModel(endpointTableModel);
		endpointTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		endpointTable.getTableHeader().addMouseListener(endpointTableSorterColumnListener);
		endpointTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane_3.setViewportView(endpointTable);

		JPanel ec2Panel = new JPanel();
		tabbedPane.addTab("EC2 credentials", null, ec2Panel, null);

		refresh();
	}

	public void refresh() {
		String title = tabbedPane.getTitleAt(tabbedPane.getSelectedIndex());
		if (title == null) {
			return;
		}
		if (title.equals("User")) {
			initUserTable();
		} else if (title.equals("Role")) {
			initRoleTable();
		} else if (title.equals("Tenant")) {
			initTenantTable();
		} else if (title.equals("Endpoint")) {
			initEndpointTable();
		}
	}

	private void initUserTable() {
		d = new JProgressBarDialog(frame, true);
		d.thread = new Thread() {
			public void run() {
				try {
					d.jProgressBar.setString("keystone user-list");
					Command command = new Command();
					command.command = "from titan: keystone user-list";
					ReturnCommand r = CommunicateLib.send(TitanCommonLib.getCurrentServerIP(), command);
					String msg = (String) r.map.get("result");
					if (msg == null) {
						JOptionPane.showMessageDialog(frame, "Error, return value is null", "Error", JOptionPane.ERROR_MESSAGE);
						return;
					} else if (msg.contains("error")) {
						String errorMessage = JSONObject.fromObject(msg).getJSONObject("error").getString("message");
						if (errorMessage.length() > 80) {
							errorMessage = CommonLib.splitString(errorMessage, "\n", 80);
						}
						JOptionPane.showMessageDialog(frame, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
						return;
					}
					JSONArray flavors = JSONObject.fromObject(msg).getJSONArray("users");
					GenericTableModel model = (GenericTableModel) userTableModel.model;
					model.columnNames.clear();
					model.columnNames.add("Id");
					model.columnNames.add("Name");
					model.columnNames.add("Tenant Id");
					model.columnNames.add("Tenant");
					model.columnNames.add("Enabled");
					model.columnNames.add("Email");

					model.columnTypes.clear();

					Vector<Object> col1 = new Vector<Object>();
					Vector<Object> col2 = new Vector<Object>();
					Vector<Object> col3 = new Vector<Object>();
					Vector<Object> col4 = new Vector<Object>();
					Vector<Object> col5 = new Vector<Object>();
					Vector<Object> col6 = new Vector<Object>();
					GenericTableModel tenantModel = (GenericTableModel) tenantTableModel.model;
					if (tenantModel.getRowCount() == 0) {
						initTenantTable();
					}
					Hashtable<String, String> allTenants = getAllTenants();

					for (int x = 0; x < flavors.size(); x++) {
						JSONObject obj = flavors.getJSONObject(x);
						col1.add(obj.getString("id"));
						col2.add(obj.getString("name"));
						col3.add(obj.getString("tenantId"));
						col4.add(allTenants.get(obj.getString("tenantId")));
						col5.add(obj.getString("enabled"));
						col6.add(obj.getString("email"));
					}
					model.values.clear();
					model.values.add(col1);
					model.values.add(col2);
					model.values.add(col3);
					model.values.add(col4);
					model.values.add(col5);
					model.values.add(col6);

					DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
					leftRenderer.setHorizontalAlignment(JTextField.LEFT);
					DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
					rightRenderer.setHorizontalAlignment(JTextField.RIGHT);
					DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
					centerRenderer.setHorizontalAlignment(JTextField.CENTER);

					userTableModel.fireTableStructureChanged();
					userTableModel.fireTableDataChanged();
					userTableSorterColumnListener.sortCol = 1;
					userTableModel.sortByColumn(userTableSorterColumnListener.sortCol, userTableSorterColumnListener.isSortAsc);

					userTable.getColumnModel().getColumn(0).setPreferredWidth(350);
					userTable.getColumnModel().getColumn(1).setPreferredWidth(150);
					userTable.getColumnModel().getColumn(2).setPreferredWidth(250);
					userTable.getColumnModel().getColumn(3).setPreferredWidth(80);
					userTable.getColumnModel().getColumn(4).setPreferredWidth(150);

					userTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
					userTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
					userTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
					userTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
					userTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
				} catch (Exception ex) {
				}
			}
		};
		d.jProgressBar.setIndeterminate(true);
		d.jProgressBar.setStringPainted(true);
		d.setVisible(true);

	}

	private void initRoleTable() {
		d = new JProgressBarDialog(frame, true);
		d.thread = new Thread() {
			public void run() {
				try {
					d.jProgressBar.setString("keystone role-list");
					Command command = new Command();
					command.command = "from titan: keystone role-list";
					ReturnCommand r = CommunicateLib.send(TitanCommonLib.getCurrentServerIP(), command);
					String msg = (String) r.map.get("result");
					if (msg.contains("error")) {
						String errorMessage = JSONObject.fromObject(msg).getJSONObject("error").getString("message");
						if (errorMessage.length() > 40) {
							String ori = errorMessage;
							errorMessage = ori.substring(0, 30);
							errorMessage += " ... ";
							errorMessage += ori.substring(ori.length() - 10);
						}
						JOptionPane.showMessageDialog(frame, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
						return;
					}
					JSONArray flavors = JSONObject.fromObject(msg).getJSONArray("roles");
					GenericTableModel model = (GenericTableModel) roleTableModel.model;
					model.columnNames.clear();
					model.columnNames.add("Id");
					model.columnNames.add("Name");

					model.columnTypes.clear();

					Vector<Object> col1 = new Vector<Object>();
					Vector<Object> col2 = new Vector<Object>();

					for (int x = 0; x < flavors.size(); x++) {
						JSONObject obj = flavors.getJSONObject(x);
						col1.add(obj.getString("id"));
						col2.add(obj.getString("name"));
					}
					model.values.clear();
					model.values.add(col1);
					model.values.add(col2);

					DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
					leftRenderer.setHorizontalAlignment(JTextField.LEFT);
					DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
					rightRenderer.setHorizontalAlignment(JTextField.RIGHT);
					DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
					centerRenderer.setHorizontalAlignment(JTextField.CENTER);

					roleTableModel.fireTableStructureChanged();
					roleTableModel.fireTableDataChanged();
					roleTableModel.sortByColumn(roleTableSorterColumnListener.sortCol, roleTableSorterColumnListener.isSortAsc);

					roleTable.getColumnModel().getColumn(0).setPreferredWidth(350);
					roleTable.getColumnModel().getColumn(1).setPreferredWidth(350);

					roleTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
					roleTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
				} catch (Exception ex) {
				}
			}
		};
		d.jProgressBar.setIndeterminate(true);
		d.jProgressBar.setStringPainted(true);
		d.setVisible(true);
	}

	private void initTenantTable() {
		d = new JProgressBarDialog(frame, true);
		d.thread = new Thread() {
			public void run() {
				try {
					d.jProgressBar.setString("keystone tenant-list");
					Command command = new Command();
					command.command = "from titan: keystone tenant-list";
					ReturnCommand r = CommunicateLib.send(TitanCommonLib.getCurrentServerIP(), command);
					String msg = (String) r.map.get("result");
					if (msg.contains("error")) {
						String errorMessage = JSONObject.fromObject(msg).getJSONObject("error").getString("message");
						if (errorMessage.length() > 40) {
							String ori = errorMessage;
							errorMessage = ori.substring(0, 30);
							errorMessage += " ... ";
							errorMessage += ori.substring(ori.length() - 10);
						}
						JOptionPane.showMessageDialog(frame, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
						return;
					}
					JSONArray flavors = JSONObject.fromObject(msg).getJSONArray("tenants");
					GenericTableModel model = (GenericTableModel) tenantTableModel.model;
					model.columnNames.clear();
					model.columnNames.add("Id");
					model.columnNames.add("Name");
					model.columnNames.add("Description");
					model.columnNames.add("Enabled");

					model.columnTypes.clear();

					Vector<Object> col1 = new Vector<Object>();
					Vector<Object> col2 = new Vector<Object>();
					Vector<Object> col3 = new Vector<Object>();
					Vector<Object> col4 = new Vector<Object>();

					for (int x = 0; x < flavors.size(); x++) {
						JSONObject obj = flavors.getJSONObject(x);
						col1.add(obj.getString("id"));
						col2.add(obj.getString("name"));
						col3.add(obj.getString("description"));
						col4.add(obj.getString("enabled"));
					}
					model.values.clear();
					model.values.add(col1);
					model.values.add(col2);
					model.values.add(col3);
					model.values.add(col4);

					DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
					leftRenderer.setHorizontalAlignment(JTextField.LEFT);
					DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
					rightRenderer.setHorizontalAlignment(JTextField.RIGHT);
					DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
					centerRenderer.setHorizontalAlignment(JTextField.CENTER);

					tenantTableModel.fireTableStructureChanged();
					tenantTableModel.fireTableDataChanged();
					tenantTableModel.sortByColumn(tenantTableSorterColumnListener.sortCol, tenantTableSorterColumnListener.isSortAsc);

					tenantTable.getColumnModel().getColumn(0).setPreferredWidth(350);
					tenantTable.getColumnModel().getColumn(1).setPreferredWidth(120);
					tenantTable.getColumnModel().getColumn(2).setPreferredWidth(350);
					tenantTable.getColumnModel().getColumn(3).setPreferredWidth(80);

					tenantTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
					tenantTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
					tenantTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
					tenantTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
				} catch (Exception ex) {
				}
			}
		};
		d.jProgressBar.setIndeterminate(true);
		d.jProgressBar.setStringPainted(true);
		d.setVisible(true);
	}

	private void initEndpointTable() {
		d = new JProgressBarDialog(frame, true);
		d.thread = new Thread() {
			public void run() {
				try {
					d.jProgressBar.setString("keystone endpoint-list");
					Command command = new Command();
					command.command = "from titan: keystone endpoint-list";
					ReturnCommand r = CommunicateLib.send(TitanCommonLib.getCurrentServerIP(), command);
					String msg = (String) r.map.get("result");
					if (msg.contains("error")) {
						String errorMessage = JSONObject.fromObject(msg).getJSONObject("error").getString("message");
						if (errorMessage.length() > 40) {
							String ori = errorMessage;
							errorMessage = ori.substring(0, 30);
							errorMessage += " ... ";
							errorMessage += ori.substring(ori.length() - 10);
						}
						JOptionPane.showMessageDialog(frame, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
						return;
					}
					JSONArray flavors = JSONObject.fromObject(msg).getJSONArray("endpoints");
					GenericTableModel model = (GenericTableModel) endpointTableModel.model;
					model.columnNames.clear();
					model.columnNames.add("Id");
					model.columnNames.add("Service Id");
					model.columnNames.add("Region");
					model.columnNames.add("Admin URL");
					model.columnNames.add("Internal URL");
					model.columnNames.add("Public URL");

					model.columnTypes.clear();

					model.editables.put(3, true);
					model.editables.put(4, true);
					model.editables.put(5, true);

					Vector<Object> col1 = new Vector<Object>();
					Vector<Object> col2 = new Vector<Object>();
					Vector<Object> col3 = new Vector<Object>();
					Vector<Object> col4 = new Vector<Object>();
					Vector<Object> col5 = new Vector<Object>();
					Vector<Object> col6 = new Vector<Object>();

					for (int x = 0; x < flavors.size(); x++) {
						JSONObject obj = flavors.getJSONObject(x);
						col1.add(obj.getString("id"));
						col2.add(obj.getString("service_id"));
						col3.add(obj.getString("region"));
						col4.add(obj.getString("adminurl"));
						col5.add(obj.getString("internalurl"));
						col6.add(obj.getString("publicurl"));
					}
					model.values.clear();
					model.values.add(col1);
					model.values.add(col2);
					model.values.add(col3);
					model.values.add(col4);
					model.values.add(col5);
					model.values.add(col6);

					DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
					leftRenderer.setHorizontalAlignment(JTextField.LEFT);
					DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
					rightRenderer.setHorizontalAlignment(JTextField.RIGHT);
					DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
					centerRenderer.setHorizontalAlignment(JTextField.CENTER);

					endpointTableModel.fireTableStructureChanged();
					endpointTableModel.fireTableDataChanged();
					endpointTableModel.sortByColumn(endpointTableSorterColumnListener.sortCol, endpointTableSorterColumnListener.isSortAsc);

					endpointTable.getColumnModel().getColumn(0).setPreferredWidth(350);
					endpointTable.getColumnModel().getColumn(1).setPreferredWidth(250);
					endpointTable.getColumnModel().getColumn(2).setPreferredWidth(350);
					endpointTable.getColumnModel().getColumn(3).setPreferredWidth(350);
					endpointTable.getColumnModel().getColumn(4).setPreferredWidth(350);
					endpointTable.getColumnModel().getColumn(5).setPreferredWidth(350);

					endpointTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
					endpointTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
					endpointTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
					endpointTable.getColumnModel().getColumn(3).setCellRenderer(leftRenderer);
					endpointTable.getColumnModel().getColumn(4).setCellRenderer(leftRenderer);
					endpointTable.getColumnModel().getColumn(5).setCellRenderer(leftRenderer);
				} catch (Exception ex) {
				}
			}
		};
		d.jProgressBar.setIndeterminate(true);
		d.jProgressBar.setStringPainted(true);
		d.setVisible(true);
	}

	public Hashtable<String, String> getAllTenants() {
		GenericTableModel tenantModel = (GenericTableModel) tenantTableModel.model;
		if (tenantModel.getRowCount() == 0) {
			initTenantTable();
		}
		Hashtable<String, String> allTenants = new Hashtable<String, String>();
		for (int x = 0; x < tenantModel.getRowCount(); x++) {
			allTenants.put(tenantModel.getValueAt(x, 0).toString(), tenantModel.getValueAt(x, 1).toString());
		}
		return allTenants;
	}

	public Hashtable<String, String> getAllRoles() {
		GenericTableModel roleModel = (GenericTableModel) roleTableModel.model;
		Hashtable<String, String> allRoles = new Hashtable<String, String>();
		for (int x = 0; x < roleModel.getRowCount(); x++) {
			allRoles.put(roleModel.getValueAt(x, 0).toString(), roleModel.getValueAt(x, 1).toString());
		}
		return allRoles;
	}
}
