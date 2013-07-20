package com.titan.keystonepanel;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.peterswing.CommonLib;
import com.peterswing.GenericTableModel;
import com.peterswing.advancedswing.jprogressbardialog.JProgressBarDialog;
import com.peterswing.advancedswing.jtable.SortableTableModel;
import com.peterswing.advancedswing.jtable.TableSorterColumnListener;
import com.titan.TitanCommonLib;
import com.titan.communication.CommunicateLib;
import com.titanserver.Command;
import com.titanserver.ReturnCommand;

public class UserDetailDialog extends JDialog {
	private JTable roleTable;

	SortableTableModel roleTableModel = new SortableTableModel(new GenericTableModel());
	TableSorterColumnListener roleTableSorterColumnListener;
	Frame frame;
	String userId;
	String username;
	String defaultTenantId;
	Hashtable<String, String> allTenants;

	public UserDetailDialog(Frame frame, String userId, String username, String defaultTenantId, Hashtable<String, String> allTenants) {
		super(frame, true);
		this.frame = frame;
		this.userId = userId;
		this.username = username;
		this.defaultTenantId = defaultTenantId;
		this.allTenants = allTenants;
		setTitle("User detail : " + username);
		setBounds(100, 100, 650, 450);
		getContentPane().setLayout(new BorderLayout());
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton closeButton = new JButton("Close");
				closeButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						setVisible(false);
					}
				});
				closeButton.setActionCommand("OK");
				buttonPane.add(closeButton);
				getRootPane().setDefaultButton(closeButton);
			}
		}

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		getContentPane().add(tabbedPane, BorderLayout.CENTER);

		JPanel panel = new JPanel();
		tabbedPane.addTab("Role", null, panel, null);
		panel.setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane = new JScrollPane();
		panel.add(scrollPane, BorderLayout.CENTER);

		roleTable = new JTable();
		roleTableSorterColumnListener = new TableSorterColumnListener(roleTable, roleTableModel);
		roleTable.getTableHeader().setReorderingAllowed(false);
		roleTable.setModel(roleTableModel);
		roleTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		roleTable.getTableHeader().addMouseListener(roleTableSorterColumnListener);
		roleTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		initRoleTable();

		scrollPane.setViewportView(roleTable);
		CommonLib.centerDialog(this);
	}

	private void initRoleTable() {
		final JProgressBarDialog d = new JProgressBarDialog(frame, true);
		d.thread = new Thread() {
			public void run() {
				try {
					Enumeration ee = allTenants.keys();
					String tenantId = null;

					GenericTableModel model = (GenericTableModel) roleTableModel.model;
					Vector<Object> col1 = new Vector<Object>();
					Vector<Object> col2 = new Vector<Object>();
					Vector<Object> col3 = new Vector<Object>();

					while (ee.hasMoreElements()) {
						tenantId = (String) ee.nextElement();
						String tenantName = allTenants.get(tenantId);

						Command command = new Command();
						command.command = "from titan: keystone user-role-list";
						d.jProgressBar.setString("getting role list for tenant " + tenantId);
						HashMap<String, String> parameters = new HashMap<String, String>();
						parameters.put("$userId", userId);
						parameters.put("$Tenant_Id", tenantId);
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
						}
						JSONArray roles = JSONObject.fromObject(msg).getJSONArray("roles");

						model.columnNames.clear();
						model.columnNames.add("Id");
						model.columnNames.add("Tenant");
						model.columnNames.add("Role");

						model.columnTypes.clear();

						for (int x = 0; x < roles.size(); x++) {
							JSONObject obj = roles.getJSONObject(x);
							col1.add(obj.getString("id"));
							if (tenantId.equals(defaultTenantId)) {
								col2.add("(*) " + tenantName);
							} else {
								col2.add(tenantName);
							}
							col3.add(obj.getString("name"));
						}

					}
					model.values.clear();
					model.values.add(col1);
					model.values.add(col2);
					model.values.add(col3);

					DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
					leftRenderer.setHorizontalAlignment(JTextField.LEFT);
					DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
					rightRenderer.setHorizontalAlignment(JTextField.RIGHT);
					DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
					centerRenderer.setHorizontalAlignment(JTextField.CENTER);

					roleTableModel.fireTableStructureChanged();
					roleTableModel.fireTableDataChanged();
					roleTableModel.sortByColumn(roleTableSorterColumnListener.sortCol, roleTableSorterColumnListener.isSortAsc);

					roleTable.getColumnModel().getColumn(0).setPreferredWidth(300);
					roleTable.getColumnModel().getColumn(1).setPreferredWidth(150);
					roleTable.getColumnModel().getColumn(2).setPreferredWidth(150);

					roleTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
					roleTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
					roleTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
				} catch (Exception ex) {
				}

			}
		};
		d.jProgressBar.setIndeterminate(true);
		d.jProgressBar.setStringPainted(true);
		d.setVisible(true);
	}
}
