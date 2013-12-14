package com.titan.vdipanel;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
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
import com.titan.TitanCommonLib;
import com.titan.communication.CommunicateLib;
import com.titanserver.Command;
import com.titanserver.HttpResult;
import com.titanserver.ReturnCommand;

public class VDIPanel extends JPanel implements Runnable, MainPanel {
	JTable vdiTable;
	JFrame frame;
	JProgressBarDialog d;
	GenericTableModel instanceTableModel = new GenericTableModel();
	SortableTableModel instanceSortableTableModel = new SortableTableModel(instanceTableModel);
	TableSorterColumnListener instanceTableSorterColumnListener;

	public VDIPanel(JFrame frame) {
		this.frame = frame;
		setBorder(new EmptyBorder(10, 10, 0, 10));

		JLabel lblInstances = new JLabel("Instances");
		lblInstances.setFont(new Font("Lucida Grande", Font.PLAIN, 20));

		JScrollPane scrollPane = new JScrollPane();

		vdiTable = new JTable();
		vdiTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					String instanceId = (String) instanceSortableTableModel.getValueAt(vdiTable.getSelectedRow(), instanceSortableTableModel.getColumnIndex("Id"));
					String instanceName = (String) instanceTableModel.getValueAt(vdiTable.getSelectedRow(), instanceTableModel.getColumnIndex("OS-EXT-SRV-ATTR:instance_name"));
					VDIUserDialog dialog = new VDIUserDialog(VDIPanel.this.frame, instanceName);
					CommonLib.centerDialog(dialog);
					dialog.setVisible(true);
				}
			}
		});
		vdiTable.getTableHeader().setReorderingAllowed(false);
		instanceTableSorterColumnListener = new TableSorterColumnListener(vdiTable, instanceSortableTableModel);
		vdiTable.setModel(instanceSortableTableModel);
		vdiTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		vdiTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		vdiTable.getTableHeader().addMouseListener(instanceTableSorterColumnListener);
		scrollPane.setViewportView(vdiTable);

		JSearchTextField searchTextField = new JSearchTextField();

		JPanel panel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(groupLayout
				.createParallelGroup(Alignment.LEADING)
				.addComponent(lblInstances, GroupLayout.PREFERRED_SIZE, 652, GroupLayout.PREFERRED_SIZE)
				.addGroup(
						groupLayout.createSequentialGroup().addGap(23).addComponent(searchTextField, GroupLayout.PREFERRED_SIZE, 214, GroupLayout.PREFERRED_SIZE)
								.addContainerGap(415, Short.MAX_VALUE))
				.addGroup(groupLayout.createSequentialGroup().addContainerGap().addComponent(panel, GroupLayout.DEFAULT_SIZE, 640, Short.MAX_VALUE).addContainerGap())
				.addGroup(groupLayout.createSequentialGroup().addContainerGap().addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 640, Short.MAX_VALUE).addContainerGap()));
		groupLayout.setVerticalGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(
				groupLayout.createSequentialGroup().addComponent(lblInstances).addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(searchTextField, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE).addGap(13)
						.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 345, Short.MAX_VALUE).addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(panel, GroupLayout.PREFERRED_SIZE, 33, GroupLayout.PREFERRED_SIZE)));

		JButton btnUser = new JButton("User");
		btnUser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String instanceId = (String) instanceSortableTableModel.getValueAt(vdiTable.getSelectedRow(), instanceSortableTableModel.getColumnIndex("Id"));
				String instanceName = (String) instanceTableModel.getValueAt(vdiTable.getSelectedRow(), instanceTableModel.getColumnIndex("OS-EXT-SRV-ATTR:instance_name"));
				VDIUserDialog dialog = new VDIUserDialog(VDIPanel.this.frame, instanceName);
				CommonLib.centerDialog(dialog);
				dialog.setVisible(true);
			}
		});
		panel.add(btnUser);

		JButton btnSetting = new JButton("Setting");
		btnSetting.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String instanceId = (String) instanceSortableTableModel.getValueAt(vdiTable.getSelectedRow(), instanceSortableTableModel.getColumnIndex("Id"));
				String instanceName = (String) instanceTableModel.getValueAt(vdiTable.getSelectedRow(), instanceTableModel.getColumnIndex("OS-EXT-SRV-ATTR:instance_name"));
				VDISettingDialog dialog = new VDISettingDialog(VDIPanel.this.frame, instanceName);
				CommonLib.centerDialog(dialog);
				dialog.setVisible(true);
			}
		});
		panel.add(btnSetting);
		setLayout(groupLayout);

		refresh();
	}

	public void refresh() {
		d = new JProgressBarDialog(frame, true);
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

		vdiTable.getColumnModel().getColumn(0).setPreferredWidth(350);
		vdiTable.getColumnModel().getColumn(1).setPreferredWidth(100);
		vdiTable.getColumnModel().getColumn(2).setPreferredWidth(100);
		vdiTable.getColumnModel().getColumn(3).setPreferredWidth(300);
		vdiTable.getColumnModel().getColumn(4).setPreferredWidth(100);
		vdiTable.getColumnModel().getColumn(5).setPreferredWidth(150);
		vdiTable.getColumnModel().getColumn(6).setPreferredWidth(150);
		vdiTable.getColumnModel().getColumn(7).setPreferredWidth(150);
		vdiTable.getColumnModel().getColumn(8).setPreferredWidth(150);
		vdiTable.getColumnModel().getColumn(9).setPreferredWidth(150);
		vdiTable.getColumnModel().getColumn(10).setPreferredWidth(150);
		vdiTable.getColumnModel().getColumn(11).setPreferredWidth(150);
		vdiTable.getColumnModel().getColumn(12).setPreferredWidth(150);
		vdiTable.getColumnModel().getColumn(13).setPreferredWidth(150);
		vdiTable.getColumnModel().getColumn(14).setPreferredWidth(150);
		vdiTable.getColumnModel().getColumn(15).setPreferredWidth(150);
		vdiTable.getColumnModel().getColumn(16).setPreferredWidth(150);
		vdiTable.getColumnModel().getColumn(17).setPreferredWidth(150);
		vdiTable.getColumnModel().getColumn(18).setPreferredWidth(150);
		vdiTable.getColumnModel().getColumn(19).setPreferredWidth(150);
		vdiTable.getColumnModel().getColumn(20).setPreferredWidth(300);
		vdiTable.getColumnModel().getColumn(21).setPreferredWidth(300);

		DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
		rightRenderer.setHorizontalAlignment(JTextField.RIGHT);
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JTextField.CENTER);

		for (int x = 0; x < 22; x++) {
			vdiTable.getColumnModel().getColumn(x).setCellRenderer(centerRenderer);
		}
	}
}
