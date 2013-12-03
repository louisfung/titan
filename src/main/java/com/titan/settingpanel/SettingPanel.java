package com.titan.settingpanel;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
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
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.peterswing.GenericTableModel;
import com.peterswing.advancedswing.jprogressbardialog.JProgressBarDialog;
import com.peterswing.advancedswing.jtable.SortableTableModel;
import com.peterswing.advancedswing.jtable.TableSorterColumnListener;
import com.titan.MainPanel;
import com.titan.TitanCommonLib;
import com.titan.communication.CommunicateLib;
import com.titan.settingpanel.screenpermission.AddScreenPermissionGroupDialog;
import com.titanserver.Command;
import com.titanserver.ReturnCommand;
import com.titanserver.table.InstancePermission;
import com.titanserver.table.InstancePermissionGroup;
import com.titanserver.table.ScreenPermission;
import com.titanserver.table.ScreenPermissionGroup;
import com.titanserver.table.User;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;

public class SettingPanel extends JPanel implements MainPanel, Runnable {
	JFrame frame;
	JProgressBarDialog d;

	SortableTableModel screenPermissionGroupTableModel = new SortableTableModel(new GenericTableModel());
	TableSorterColumnListener screenPermissionGroupColumnListener;
	private JTable screenGroupTable;

	SortableTableModel instancePermissionTableModel = new SortableTableModel(new GenericTableModel());
	TableSorterColumnListener instancePermissionColumnListener;
	private JTable instancePermissionTable;

	SortableTableModel screenPermissionTableModel = new SortableTableModel(new GenericTableModel());
	TableSorterColumnListener screenPermissionColumnListener;
	private JTable screenPermissionTable;

	SortableTableModel userTableModel = new SortableTableModel(new GenericTableModel());
	TableSorterColumnListener userColumnListener;
	private JTable userTable;

	SortableTableModel instancePermissionGroupTableModel = new SortableTableModel(new GenericTableModel());
	TableSorterColumnListener instancePermissionGroupColumnListener;
	private JTable instancePermissionGroupTable;

	GenericTableModel quotaDefaultsTableModel = new GenericTableModel();
	private JTable quotaDefaultsTable;

	GenericTableModel quotaTableModel = new GenericTableModel();
	private JTable quotaTable;

	public SettingPanel(final JFrame frame) {
		this.frame = frame;
		setBorder(new EmptyBorder(10, 10, 0, 10));
		setLayout(new BorderLayout(0, 0));

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		add(tabbedPane, BorderLayout.CENTER);

		JPanel userPanel = new JPanel();
		tabbedPane.addTab("User", null, userPanel, null);
		userPanel.setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane_3 = new JScrollPane();
		userPanel.add(scrollPane_3, BorderLayout.CENTER);

		userTable = new JTable();
		userColumnListener = new TableSorterColumnListener(userTable, userTableModel);
		userTable.getTableHeader().setReorderingAllowed(false);
		userTable.setModel(userTableModel);
		userTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		userTable.getTableHeader().addMouseListener(userColumnListener);
		userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane_3.setViewportView(userTable);

		JPanel screenGoupPanel = new JPanel();
		tabbedPane.addTab("Screen group", null, screenGoupPanel, null);
		screenGoupPanel.setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		screenGoupPanel.add(panel, BorderLayout.SOUTH);

		JButton btnAddGroup = new JButton("Add group");
		btnAddGroup.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new AddScreenPermissionGroupDialog(frame, SettingPanel.this).setVisible(true);
			}
		});
		panel.add(btnAddGroup);

		JButton btnEditGroup = new JButton("Edit group");
		panel.add(btnEditGroup);

		JButton btnDeleteGroup = new JButton("Delete group");
		panel.add(btnDeleteGroup);

		JScrollPane scrollPane = new JScrollPane();
		screenGoupPanel.add(scrollPane, BorderLayout.CENTER);

		screenGroupTable = new JTable();
		screenPermissionGroupColumnListener = new TableSorterColumnListener(screenGroupTable, screenPermissionGroupTableModel);
		screenGroupTable.getTableHeader().setReorderingAllowed(false);
		screenGroupTable.setModel(screenPermissionGroupTableModel);
		screenGroupTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		screenGroupTable.getTableHeader().addMouseListener(screenPermissionGroupColumnListener);
		screenGroupTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane.setViewportView(screenGroupTable);

		JPanel screenPermissionPanel = new JPanel();
		tabbedPane.addTab("Screen permission", null, screenPermissionPanel, null);
		screenPermissionPanel.setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane_2 = new JScrollPane();
		screenPermissionPanel.add(scrollPane_2, BorderLayout.CENTER);

		screenPermissionTable = new JTable();
		screenPermissionColumnListener = new TableSorterColumnListener(screenPermissionTable, screenPermissionTableModel);
		screenPermissionTable.getTableHeader().setReorderingAllowed(false);
		screenPermissionTable.setModel(screenPermissionTableModel);
		screenPermissionTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		screenPermissionTable.getTableHeader().addMouseListener(screenPermissionColumnListener);
		screenPermissionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane_2.setViewportView(screenPermissionTable);

		JPanel instancePermissionGroupPanel = new JPanel();
		tabbedPane.addTab("Instance permission group", null, instancePermissionGroupPanel, null);
		instancePermissionGroupPanel.setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane_4 = new JScrollPane();
		instancePermissionGroupPanel.add(scrollPane_4, BorderLayout.CENTER);

		instancePermissionGroupTable = new JTable();
		instancePermissionGroupColumnListener = new TableSorterColumnListener(instancePermissionGroupTable, instancePermissionGroupTableModel);
		instancePermissionGroupTable.getTableHeader().setReorderingAllowed(false);
		instancePermissionGroupTable.setModel(instancePermissionGroupTableModel);
		instancePermissionGroupTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		instancePermissionGroupTable.getTableHeader().addMouseListener(instancePermissionGroupColumnListener);
		instancePermissionGroupTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane_4.setViewportView(instancePermissionGroupTable);

		JPanel instancePermissionPanel = new JPanel();
		tabbedPane.addTab("Instance permission", null, instancePermissionPanel, null);
		instancePermissionPanel.setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane_1 = new JScrollPane();
		instancePermissionPanel.add(scrollPane_1, BorderLayout.CENTER);

		instancePermissionTable = new JTable();
		instancePermissionColumnListener = new TableSorterColumnListener(instancePermissionTable, instancePermissionTableModel);
		instancePermissionTable.getTableHeader().setReorderingAllowed(false);
		instancePermissionTable.setModel(instancePermissionTableModel);
		instancePermissionTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		instancePermissionTable.getTableHeader().addMouseListener(instancePermissionColumnListener);
		instancePermissionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane_1.setViewportView(instancePermissionTable);

		JPanel quotaDefaultsPanel = new JPanel();
		tabbedPane.addTab("Quota defaults", null, quotaDefaultsPanel, null);
		quotaDefaultsPanel.setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane_5 = new JScrollPane();
		quotaDefaultsPanel.add(scrollPane_5, BorderLayout.CENTER);

		quotaDefaultsTable = new JTable();
		quotaDefaultsTable.setModel(quotaDefaultsTableModel);
		scrollPane_5.setViewportView(quotaDefaultsTable);

		JPanel quotaTenantPanel = new JPanel();
		tabbedPane.addTab("Quota for a tenant/user", null, quotaTenantPanel, null);
		quotaTenantPanel.setLayout(new BorderLayout(0, 0));

		JPanel panel_1 = new JPanel();
		FlowLayout flowLayout_1 = (FlowLayout) panel_1.getLayout();
		flowLayout_1.setAlignment(FlowLayout.LEFT);
		quotaTenantPanel.add(panel_1, BorderLayout.SOUTH);

		JButton changeButton = new JButton("Change");
		changeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		panel_1.add(changeButton);

		JButton refreshButton = new JButton("Refresh");
		panel_1.add(refreshButton);
		refreshButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				initQuotaTable();
			}
		});
		refreshButton.setIcon(new ImageIcon(SettingPanel.class.getResource("/com/titan/image/famfamfam/arrow_refresh.png")));

		JScrollPane scrollPane_6 = new JScrollPane();
		quotaTenantPanel.add(scrollPane_6, BorderLayout.CENTER);

		quotaTable = new JTable();
		quotaTable.setModel(quotaTableModel);
		scrollPane_6.setViewportView(quotaTable);

		refresh();
	}

	@Override
	public void refresh() {
		d = new JProgressBarDialog(SettingPanel.this.frame, true);
		d.thread = new Thread(this);
		d.jProgressBar.setIndeterminate(true);
		d.jProgressBar.setStringPainted(true);
		d.setVisible(true);
	}

	public void run() {
		d.jProgressBar.setString("get users");
		initUserTable();

		d.jProgressBar.setString("get screen permission group");
		initScreenPermissionGroupTable();

		d.jProgressBar.setString("get screen permissions");
		initScreenPermissionTable();

		d.jProgressBar.setString("get instance permission group");
		initInstancePermissionGroupTable();

		d.jProgressBar.setString("get instance permissions");
		initInstancePermissionTable();

		d.jProgressBar.setString("get quota defaults");
		initQuotaDefaultsTable();

		d.jProgressBar.setString("get quota");
		initQuotaTable();
	}

	private void initUserTable() {
		Command command = new Command();
		command.command = "get users";
		ReturnCommand r = CommunicateLib.send(TitanCommonLib.getCurrentServerIP(), command);
		List<User> users = (List<User>) r.map.get("result");
		GenericTableModel model = (GenericTableModel) userTableModel.model;
		model.columnNames.clear();
		model.columnNames.add("User Id");
		model.columnNames.add("Username");
		model.columnNames.add("Email");
		model.columnNames.add("Enable");
		model.columnNames.add("Screen group");
		model.columnNames.add("Instance group");

		Vector<Object> col1 = new Vector<Object>();
		Vector<Object> col2 = new Vector<Object>();
		Vector<Object> col3 = new Vector<Object>();
		Vector<Object> col4 = new Vector<Object>();
		Vector<Object> col5 = new Vector<Object>();
		Vector<Object> col6 = new Vector<Object>();
		for (int x = 0; x < users.size(); x++) {
			col1.add(users.get(x).getUserId());
			col2.add(users.get(x).getUsername());
			col3.add(users.get(x).getEmail());
			col4.add(users.get(x).isEnable());
			Set<ScreenPermissionGroup> screenPermissionGroups = (Set<ScreenPermissionGroup>) users.get(x).getScreenPermissionGroups();
			if (screenPermissionGroups == null || screenPermissionGroups.size() < 1) {
				col5.add(null);
			} else {
				ScreenPermissionGroup s = (ScreenPermissionGroup) screenPermissionGroups.toArray()[0];
				col5.add(s.getName());
			}
			Set<InstancePermissionGroup> instancePermissionGroups = (Set<InstancePermissionGroup>) users.get(x).getInstancePermissionGroups();
			if (instancePermissionGroups == null || instancePermissionGroups.size() < 1) {
				col6.add(null);
			} else {
				InstancePermissionGroup s = (InstancePermissionGroup) instancePermissionGroups.toArray()[0];
				col6.add(s.getName());
			}
		}
		model.values.clear();
		model.values.add(col1);
		model.values.add(col2);
		model.values.add(col3);
		model.values.add(col4);
		model.values.add(col5);
		model.values.add(col6);

		userTableModel.fireTableStructureChanged();
		userTableModel.fireTableDataChanged();

		userTable.getColumnModel().getColumn(0).setPreferredWidth(80);
		userTable.getColumnModel().getColumn(1).setPreferredWidth(120);
		userTable.getColumnModel().getColumn(2).setPreferredWidth(300);
		userTable.getColumnModel().getColumn(3).setPreferredWidth(50);
		userTable.getColumnModel().getColumn(4).setPreferredWidth(200);
		userTable.getColumnModel().getColumn(5).setPreferredWidth(200);

		DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
		rightRenderer.setHorizontalAlignment(JTextField.RIGHT);
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JTextField.CENTER);

		userTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
		userTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
		userTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
		userTable.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
	}

	private void initScreenPermissionGroupTable() {
		Command command = new Command();
		command.command = "get screen permission groups";
		ReturnCommand r = CommunicateLib.send(TitanCommonLib.getCurrentServerIP(), command);
		List<ScreenPermissionGroup> screenPermissionsGroup = (List<ScreenPermissionGroup>) r.map.get("result");
		GenericTableModel model = (GenericTableModel) screenPermissionGroupTableModel.model;
		model.columnNames.clear();
		model.columnNames.add("Name");

		Vector<Object> col1 = new Vector<Object>();

		model.values.clear();
		model.values.add(col1);

		command = new Command();
		command.command = "get screen permissions";
		r = CommunicateLib.send(TitanCommonLib.getCurrentServerIP(), command);
		List<ScreenPermission> allScreenPermissions = (List<ScreenPermission>) r.map.get("result");

		Hashtable<String, Vector<Object>> cols = new Hashtable<String, Vector<Object>>();
		for (ScreenPermission screenPermission : allScreenPermissions) {
			Vector<Object> v = new Vector<Object>();
			cols.put(screenPermission.getName(), v);
			model.columnNames.add(screenPermission.getName());
			model.values.add(v);
		}

		for (int x = 0; x < screenPermissionsGroup.size(); x++) {
			col1.add(screenPermissionsGroup.get(x).getName());
			for (ScreenPermission screenPermission : allScreenPermissions) {
				boolean bingo = false;
				loop1: for (ScreenPermission s : screenPermissionsGroup.get(x).getScreenPermissions()) {
					if (screenPermission.getName().equals(s.getName())) {
						cols.get(screenPermission.getName()).add(true);
						bingo = true;
						break loop1;
					}
				}
				if (!bingo) {
					cols.get(screenPermission.getName()).add(false);
				}
			}
		}

		screenPermissionGroupTableModel.fireTableStructureChanged();
		screenPermissionGroupTableModel.fireTableDataChanged();

		screenGroupTable.getColumnModel().getColumn(0).setPreferredWidth(200);

		DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
		rightRenderer.setHorizontalAlignment(JTextField.RIGHT);
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JTextField.CENTER);
	}

	private void initInstancePermissionGroupTable() {
		Command command = new Command();
		command.command = "get instance permission groups";
		ReturnCommand r = CommunicateLib.send(TitanCommonLib.getCurrentServerIP(), command);
		List<InstancePermissionGroup> InstancePermissionsGroup = (List<InstancePermissionGroup>) r.map.get("result");
		GenericTableModel model = (GenericTableModel) instancePermissionGroupTableModel.model;
		model.columnNames.clear();
		model.columnNames.add("Name");

		Vector<Object> col1 = new Vector<Object>();

		model.values.clear();
		model.values.add(col1);

		command = new Command();
		command.command = "get instance permissions";
		r = CommunicateLib.send(TitanCommonLib.getCurrentServerIP(), command);
		List<InstancePermission> addInstancePermissions = (List<InstancePermission>) r.map.get("result");

		Hashtable<String, Vector<Object>> cols = new Hashtable<String, Vector<Object>>();
		for (InstancePermission InstancePermission : addInstancePermissions) {
			Vector<Object> v = new Vector<Object>();
			cols.put(InstancePermission.getName(), v);
			model.columnNames.add(InstancePermission.getName());
			model.values.add(v);
		}

		for (int x = 0; x < InstancePermissionsGroup.size(); x++) {
			col1.add(InstancePermissionsGroup.get(x).getName());
			for (InstancePermission InstancePermission : addInstancePermissions) {
				boolean bingo = false;
				loop1: for (InstancePermission s : InstancePermissionsGroup.get(x).getInstancePermissions()) {
					if (InstancePermission.getName().equals(s.getName())) {
						cols.get(InstancePermission.getName()).add(true);
						bingo = true;
						break loop1;
					}
				}
				if (!bingo) {
					cols.get(InstancePermission.getName()).add(false);
				}
			}
		}

		instancePermissionGroupTableModel.fireTableStructureChanged();
		instancePermissionGroupTableModel.fireTableDataChanged();

		instancePermissionGroupTable.getColumnModel().getColumn(0).setPreferredWidth(200);

		DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
		rightRenderer.setHorizontalAlignment(JTextField.RIGHT);
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JTextField.CENTER);
	}

	private void initScreenPermissionTable() {
		Command command = new Command();
		command.command = "get screen permissions";
		ReturnCommand r = CommunicateLib.send(TitanCommonLib.getCurrentServerIP(), command);
		List<ScreenPermission> screenPermissions = (List<ScreenPermission>) r.map.get("result");
		GenericTableModel model = (GenericTableModel) screenPermissionTableModel.model;
		model.columnNames.clear();
		model.columnNames.add("Name");
		model.columnNames.add("Description");

		Vector<Object> col1 = new Vector<Object>();
		Vector<Object> col2 = new Vector<Object>();
		for (int x = 0; x < screenPermissions.size(); x++) {
			col1.add(screenPermissions.get(x).getName());
			col2.add(screenPermissions.get(x).getDescription());
		}
		model.values.clear();
		model.values.add(col1);
		model.values.add(col2);

		screenPermissionTableModel.fireTableStructureChanged();
		screenPermissionTableModel.fireTableDataChanged();

		screenPermissionTable.getColumnModel().getColumn(0).setPreferredWidth(200);
		screenPermissionTable.getColumnModel().getColumn(1).setPreferredWidth(400);

		DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
		rightRenderer.setHorizontalAlignment(JTextField.RIGHT);
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JTextField.CENTER);
	}

	private void initInstancePermissionTable() {
		Command command = new Command();
		command.command = "get instance permissions";
		ReturnCommand r = CommunicateLib.send(TitanCommonLib.getCurrentServerIP(), command);
		List<InstancePermission> InstancePermissions = (List<InstancePermission>) r.map.get("result");
		GenericTableModel model = (GenericTableModel) instancePermissionTableModel.model;
		model.columnNames.clear();
		model.columnNames.add("Name");
		model.columnNames.add("Description");

		Vector<Object> col1 = new Vector<Object>();
		Vector<Object> col2 = new Vector<Object>();
		for (int x = 0; x < InstancePermissions.size(); x++) {
			col1.add(InstancePermissions.get(x).getName());
			col2.add(InstancePermissions.get(x).getDescription());
		}
		model.values.clear();
		model.values.add(col1);
		model.values.add(col2);

		instancePermissionTableModel.fireTableStructureChanged();
		instancePermissionTableModel.fireTableDataChanged();

		instancePermissionTable.getColumnModel().getColumn(0).setPreferredWidth(200);
		instancePermissionTable.getColumnModel().getColumn(1).setPreferredWidth(400);

		DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
		rightRenderer.setHorizontalAlignment(JTextField.RIGHT);
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JTextField.CENTER);
	}

	private void initQuotaDefaultsTable() {
		Command command = new Command();
		command.command = "from titan: nova quota-defaults";
		ReturnCommand r = CommunicateLib.send(TitanCommonLib.getCurrentServerIP(), command);
		JSONObject quotas = ((JSONObject) JSONObject.fromObject(r.map.get("result")).get("quota_set"));

		quotaDefaultsTableModel.columnNames.clear();
		quotaDefaultsTableModel.columnNames.add("Name");
		quotaDefaultsTableModel.columnNames.add("Description");

		Vector<Object> col1 = new Vector<Object>();
		Vector<Object> col2 = new Vector<Object>();
		Iterator<String> keys = quotas.keys();
		while (keys.hasNext()) {
			String key = keys.next();
			col1.add(key);
			col2.add(quotas.getString(key));
		}

		quotaDefaultsTableModel.values.clear();
		quotaDefaultsTableModel.values.add(col1);
		quotaDefaultsTableModel.values.add(col2);

		quotaDefaultsTableModel.fireTableStructureChanged();
		quotaDefaultsTableModel.fireTableDataChanged();

		quotaDefaultsTable.getColumnModel().getColumn(0).setPreferredWidth(200);
		quotaDefaultsTable.getColumnModel().getColumn(1).setPreferredWidth(400);

		DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
		rightRenderer.setHorizontalAlignment(JTextField.RIGHT);
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JTextField.CENTER);
	}

	private void initQuotaTable() {
		Command command = new Command();
		command.command = "from titan: nova quota-show";
		ReturnCommand r = CommunicateLib.send(TitanCommonLib.getCurrentServerIP(), command);
		JSONObject quotas = ((JSONObject) JSONObject.fromObject(r.map.get("result")).get("quota_set"));

		quotaTableModel.columnNames.clear();
		quotaTableModel.columnNames.add("Name");
		quotaTableModel.columnNames.add("Description");

		Vector<Object> col1 = new Vector<Object>();
		Vector<Object> col2 = new Vector<Object>();
		Iterator<String> keys = quotas.keys();
		while (keys.hasNext()) {
			String key = keys.next();
			col1.add(key);
			col2.add(quotas.getString(key));
		}

		quotaTableModel.values.clear();
		quotaTableModel.values.add(col1);
		quotaTableModel.values.add(col2);

		quotaTableModel.fireTableStructureChanged();
		quotaTableModel.fireTableDataChanged();

		quotaTable.getColumnModel().getColumn(0).setPreferredWidth(200);
		quotaTable.getColumnModel().getColumn(1).setPreferredWidth(400);

		DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
		rightRenderer.setHorizontalAlignment(JTextField.RIGHT);
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JTextField.CENTER);
	}
}
