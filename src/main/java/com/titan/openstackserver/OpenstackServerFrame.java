package com.titan.openstackserver;

import java.awt.BorderLayout;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.peterswing.GenericTableModel;
import com.peterswing.advancedswing.jprogressbardialog.JProgressBarDialog;
import com.titan.communication.CommunicateLib;
import com.titanserver.Command;
import com.titanserver.HttpResult;
import com.titanserver.ReturnCommand;

public class OpenstackServerFrame extends JDialog implements Runnable {

	private final JPanel contentPanel = new JPanel();
	JProgressBarDialog d = new JProgressBarDialog(this, true);
	private JTable flavorTable;
	private JTable tokenTable;
	GenericTableModel tokenTableModel = new GenericTableModel();
	GenericTableModel endpointTableModel = new GenericTableModel();
	GenericTableModel flavorTableModel = new GenericTableModel();
	GenericTableModel hypervisorTableModel = new GenericTableModel();
	GenericTableModel imageTableModel = new GenericTableModel();
	private JTable endpointTable;
	private JTable hypervisorTable;
	private JTable imageTable;

	public OpenstackServerFrame(JFrame frame) {
		super(frame, true);
		setBounds(100, 100, 679, 440);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
			contentPanel.add(tabbedPane, BorderLayout.CENTER);
			JPanel panel_1 = new JPanel();
			tabbedPane.addTab("Token", null, panel_1, null);
			panel_1.setLayout(new BorderLayout(0, 0));
			JScrollPane scrollPane_1 = new JScrollPane();
			panel_1.add(scrollPane_1, BorderLayout.CENTER);
			tokenTable = new JTable();
			tokenTable.getTableHeader().setReorderingAllowed(false);
			tokenTable.setModel(tokenTableModel);
			tokenTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			tokenTableModel.columnNames.clear();
			tokenTableModel.columnNames.add("Name");
			tokenTableModel.columnNames.add("Value");
			scrollPane_1.setViewportView(tokenTable);
			{
				JPanel panel = new JPanel();
				tabbedPane.addTab("Endpoint", null, panel, null);
				panel.setLayout(new BorderLayout(0, 0));
				{
					JScrollPane scrollPane = new JScrollPane();
					panel.add(scrollPane, BorderLayout.CENTER);
					{
						endpointTable = new JTable();
						endpointTable.getTableHeader().setReorderingAllowed(false);
						endpointTable.setModel(endpointTableModel);
						endpointTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
						endpointTableModel.columnNames.clear();
						endpointTableModel.columnNames.add("Name");
						endpointTableModel.columnNames.add("Type");
						endpointTableModel.columnNames.add("Admin URL");
						endpointTableModel.columnNames.add("Id");
						endpointTableModel.columnNames.add("Internal URL");
						endpointTableModel.columnNames.add("Public URL");
						endpointTableModel.columnNames.add("Region");
						endpointTableModel.columnNames.add("Endpoints links");
						scrollPane.setViewportView(endpointTable);
					}
				}
			}
			{
				JPanel panel = new JPanel();
				tabbedPane.addTab("Flavor", null, panel, null);
				panel.setLayout(new BorderLayout(0, 0));
				{
					JScrollPane scrollPane = new JScrollPane();
					panel.add(scrollPane, BorderLayout.CENTER);
					{
						flavorTable = new JTable();
						flavorTable.getTableHeader().setReorderingAllowed(false);
						flavorTable.setModel(flavorTableModel);
						flavorTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
						flavorTableModel.columnNames.clear();
						flavorTableModel.columnNames.add("id");
						flavorTableModel.columnNames.add("name");
						flavorTableModel.columnNames.add("ram");
						flavorTableModel.columnNames.add("disable");
						flavorTableModel.columnNames.add("vcpus");
						flavorTableModel.columnNames.add("swap");
						flavorTableModel.columnNames.add("public");
						flavorTableModel.columnNames.add("factor");
						flavorTableModel.columnNames.add("ephemeral");
						flavorTableModel.columnNames.add("disk");
						flavorTableModel.columnNames.add("links");
						scrollPane.setViewportView(flavorTable);
					}
				}
			}
			{
				JPanel panel = new JPanel();
				tabbedPane.addTab("Hypervisor", null, panel, null);
				panel.setLayout(new BorderLayout(0, 0));
				{
					JScrollPane scrollPane = new JScrollPane();
					panel.add(scrollPane, BorderLayout.CENTER);
					{
						hypervisorTable = new JTable();
						hypervisorTable.getTableHeader().setReorderingAllowed(false);
						hypervisorTable.setModel(hypervisorTableModel);
						hypervisorTableModel.columnNames.clear();
						hypervisorTableModel.columnNames.add("Id");
						hypervisorTableModel.columnNames.add("Hypervisor hostname");
						hypervisorTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
						scrollPane.setViewportView(hypervisorTable);
					}
				}
			}
			{
				JPanel panel = new JPanel();
				tabbedPane.addTab("Image", null, panel, null);
				panel.setLayout(new BorderLayout(0, 0));
				{
					JScrollPane scrollPane = new JScrollPane();
					panel.add(scrollPane, BorderLayout.CENTER);
					{
						imageTable = new JTable();
						imageTable.getTableHeader().setReorderingAllowed(false);
						imageTableModel.columnNames.clear();
						imageTableModel.columnNames.add("Id");
						imageTableModel.columnNames.add("Name");
						imageTableModel.columnNames.add("Progress");
						imageTableModel.columnNames.add("Status");
						imageTableModel.columnNames.add("Updated");
						imageTableModel.columnNames.add("Created");
						imageTableModel.columnNames.add("Size");
						imageTableModel.columnNames.add("minDisk");
						imageTableModel.columnNames.add("minRam");
						imageTableModel.columnNames.add("base_image_ref");
						imageTableModel.columnNames.add("image_location");
						imageTableModel.columnNames.add("image_state");
						imageTableModel.columnNames.add("image_type");
						imageTableModel.columnNames.add("instance_type_ephemeral_gb");
						imageTableModel.columnNames.add("instance_type_flavorid");
						imageTableModel.columnNames.add("instance_type_id");
						imageTableModel.columnNames.add("instance_type_memory_mb");
						imageTableModel.columnNames.add("instance_type_name");
						imageTableModel.columnNames.add("instance_type_root_gb");
						imageTableModel.columnNames.add("instance_type_rxtx_factor");
						imageTableModel.columnNames.add("instance_type_swap");
						imageTableModel.columnNames.add("instance_type_vcpu_weight");
						imageTableModel.columnNames.add("instance_type_vcpus");
						imageTableModel.columnNames.add("instance_uuid");
						imageTableModel.columnNames.add("kernel_id");
						imageTableModel.columnNames.add("owner_id");
						imageTableModel.columnNames.add("ramdisk_id");
						imageTableModel.columnNames.add("user_id");
						imageTableModel.columnNames.add("links");
						imageTable.setModel(imageTableModel);
						imageTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
						scrollPane.setViewportView(imageTable);
					}
				}
			}
		}
		setLocationRelativeTo(null);

		d.thread = new Thread(this);
		d.jProgressBar.setIndeterminate(true);
		d.jProgressBar.setStringPainted(true);
		d.setVisible(true);
	}

	public void run() {
		d.jProgressBar.setString("nova endpoints");
		Command command = new Command();
		command.command = "from titan: nova endpoints";
		ReturnCommand r = CommunicateLib.send(command);
		System.out.println("-------------------------------------------------------------");
		HttpResult httpResult = (HttpResult) r.map.get("result");
		JSONObject endpoints = JSONObject.fromObject(httpResult.content);
		Vector<Object> col1 = new Vector<Object>();
		Vector<Object> col2 = new Vector<Object>();
		Vector<Object> col3 = new Vector<Object>();
		Vector<Object> col4 = new Vector<Object>();
		Vector<Object> col5 = new Vector<Object>();
		Vector<Object> col6 = new Vector<Object>();
		Vector<Object> col7 = new Vector<Object>();
		Vector<Object> col8 = new Vector<Object>();
		JSONObject access = endpoints.getJSONObject("access");
		JSONObject metaData = access.getJSONObject("metadata");
		JSONObject token = access.getJSONObject("token");
		JSONObject tenant = token.getJSONObject("tenant");
		col1.add("meta, is_admin");
		col2.add(metaData.getString("is_admin"));
		col1.add("meta, roles");
		col2.add(metaData.getJSONArray("roles"));
		col1.add("token, expires");
		col2.add(token.getString("expires"));
		col1.add("token, id");
		col2.add(token.getString("id"));
		col1.add("token, issued_at");
		col2.add(token.getString("issued_at"));
		col1.add("tenant, description");
		col2.add(tenant.getString("description"));
		col1.add("tenant, enabled");
		col2.add(tenant.getString("enabled"));
		col1.add("tenant, id");
		col2.add(tenant.getString("id"));
		col1.add("tenant, name");
		col2.add(tenant.getString("name"));
		tokenTableModel.values.add(col1);
		tokenTableModel.values.add(col2);
		tokenTableModel.fireTableStructureChanged();
		tokenTableModel.fireTableDataChanged();
		tokenTable.getColumnModel().getColumn(0).setPreferredWidth(200);
		tokenTable.getColumnModel().getColumn(1).setPreferredWidth(800);

		col1 = new Vector<Object>();
		col2 = new Vector<Object>();
		col3 = new Vector<Object>();
		col4 = new Vector<Object>();
		col5 = new Vector<Object>();
		col6 = new Vector<Object>();
		col7 = new Vector<Object>();
		col8 = new Vector<Object>();
		JSONArray serviceCatalogies = access.getJSONArray("serviceCatalog");
		for (int x = 0; x < serviceCatalogies.size(); x++) {
			JSONObject obj = serviceCatalogies.getJSONObject(x);
			JSONObject enpoint = (JSONObject) obj.getJSONArray("endpoints").get(0);
			col1.add(obj.getString("name"));
			col2.add(obj.getString("type"));
			col3.add(enpoint.getString("adminURL"));
			col4.add(enpoint.getString("id"));
			col5.add(enpoint.getString("internalURL"));
			col6.add(enpoint.getString("publicURL"));
			col7.add(enpoint.getString("region"));
			col8.add(obj.getString("endpoints_links"));
		}
		endpointTableModel.values.add(col1);
		endpointTableModel.values.add(col2);
		endpointTableModel.values.add(col3);
		endpointTableModel.values.add(col4);
		endpointTableModel.values.add(col5);
		endpointTableModel.values.add(col6);
		endpointTableModel.values.add(col7);
		endpointTableModel.values.add(col8);
		endpointTableModel.fireTableStructureChanged();
		endpointTableModel.fireTableDataChanged();
		endpointTable.getColumnModel().getColumn(2).setPreferredWidth(400);
		endpointTable.getColumnModel().getColumn(3).setPreferredWidth(200);
		endpointTable.getColumnModel().getColumn(4).setPreferredWidth(400);
		endpointTable.getColumnModel().getColumn(5).setPreferredWidth(400);

		d.jProgressBar.setString("nova flavor-list");
		command = new Command();
		command.command = "from titan: nova flavor-list";
		r = CommunicateLib.send(command);
		System.out.println("-------------------------------------------------------------");
		JSONObject flavorList = JSONObject.fromObject(httpResult.content);
		JSONArray arr = flavorList.getJSONArray("flavors");
		Vector<Object> id = new Vector<Object>();
		Vector<Object> names = new Vector<Object>();
		Vector<Object> ram = new Vector<Object>();
		Vector<Object> disable = new Vector<Object>();
		Vector<Object> vcups = new Vector<Object>();
		Vector<Object> swap = new Vector<Object>();
		Vector<Object> isPublic = new Vector<Object>();
		Vector<Object> factor = new Vector<Object>();
		Vector<Object> ephemeral = new Vector<Object>();
		Vector<Object> disk = new Vector<Object>();
		Vector<Object> links = new Vector<Object>();
		for (int x = 0; x < arr.size(); x++) {
			id.add(((JSONObject) arr.get(x)).getString("id"));
			names.add(((JSONObject) arr.get(x)).getString("name"));
			ram.add(((JSONObject) arr.get(x)).getString("ram"));
			disable.add(((JSONObject) arr.get(x)).getString("OS-FLV-DISABLED:disabled"));
			vcups.add(((JSONObject) arr.get(x)).getString("vcpus"));
			swap.add(((JSONObject) arr.get(x)).getString("swap"));
			isPublic.add(((JSONObject) arr.get(x)).getString("os-flavor-access:is_public"));
			factor.add(((JSONObject) arr.get(x)).getString("rxtx_factor"));
			ephemeral.add(((JSONObject) arr.get(x)).getString("OS-FLV-EXT-DATA:ephemeral"));
			disk.add(((JSONObject) arr.get(x)).getString("disk"));
			links.add(((JSONObject) arr.get(x)).getString("links"));
		}
		flavorTableModel.values.add(id);
		flavorTableModel.values.add(names);
		flavorTableModel.values.add(ram);
		flavorTableModel.values.add(disable);
		flavorTableModel.values.add(vcups);
		flavorTableModel.values.add(swap);
		flavorTableModel.values.add(isPublic);
		flavorTableModel.values.add(factor);
		flavorTableModel.values.add(ephemeral);
		flavorTableModel.values.add(disk);
		flavorTableModel.values.add(links);
		flavorTableModel.fireTableStructureChanged();
		flavorTableModel.fireTableDataChanged();
		flavorTable.getColumnModel().getColumn(10).setPreferredWidth(1000);

		d.jProgressBar.setString("nova agent-list");
		command = new Command();
		command.command = "from titan: nova agent-list";
		r = CommunicateLib.send(command);
		System.out.println(r.map.get("result"));
		System.out.println("-------------------------------------------------------------");
		JSONObject agentList = JSONObject.fromObject(httpResult.content);

		d.jProgressBar.setString("nova aggregate-list");
		command = new Command();
		command.command = "from titan: nova aggregate-list";
		r = CommunicateLib.send(command);
		System.out.println(r.map.get("result"));
		System.out.println("-------------------------------------------------------------");
		JSONObject aggregateList = JSONObject.fromObject(httpResult.content);

		d.jProgressBar.setString("nova cloudpipe-list");
		command = new Command();
		command.command = "from titan: nova cloudpipe-list";
		r = CommunicateLib.send(command);
		System.out.println(r.map.get("result"));
		System.out.println("-------------------------------------------------------------");
		JSONObject cloudpipeList = JSONObject.fromObject(httpResult.content);

		d.jProgressBar.setString("nova host-list");
		command = new Command();
		command.command = "from titan: nova host-list";
		r = CommunicateLib.send(command);
		System.out.println(r.map.get("result"));
		System.out.println("-------------------------------------------------------------");
		JSONObject hostList = JSONObject.fromObject(httpResult.content);

		d.jProgressBar.setString("nova hypervisor-list");
		command = new Command();
		command.command = "from titan: nova hypervisor-list";
		r = CommunicateLib.send(command);
		System.out.println(r.map.get("result"));
		System.out.println("-------------------------------------------------------------");
		JSONObject hypervisorList = JSONObject.fromObject(httpResult.content);
		arr = hypervisorList.getJSONArray("hypervisors");
		Vector<Object> hypervisorId = new Vector<Object>();
		Vector<Object> hypervisorHost = new Vector<Object>();
		for (int x = 0; x < arr.size(); x++) {
			hypervisorId.add(((JSONObject) arr.get(x)).getString("id"));
			hypervisorHost.add(((JSONObject) arr.get(x)).getString("hypervisor_hostname"));
		}
		hypervisorTableModel.values.add(hypervisorId);
		hypervisorTableModel.values.add(hypervisorHost);
		hypervisorTableModel.fireTableStructureChanged();
		hypervisorTableModel.fireTableDataChanged();
		hypervisorTable.getColumnModel().getColumn(1).setPreferredWidth(400);

		d.jProgressBar.setString("nova image-list");
		command = new Command();
		command.command = "from titan: nova image-list";
		r = CommunicateLib.send(command);
		System.out.println(r.map.get("result"));
		System.out.println("-------------------------------------------------------------");
		JSONObject imageList = JSONObject.fromObject(httpResult.content);
		arr = imageList.getJSONArray("images");
		col1 = new Vector<Object>();
		col2 = new Vector<Object>();
		col3 = new Vector<Object>();
		col4 = new Vector<Object>();
		col5 = new Vector<Object>();
		col6 = new Vector<Object>();
		col7 = new Vector<Object>();
		col8 = new Vector<Object>();
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
		Vector<Object> col23 = new Vector<Object>();
		Vector<Object> col24 = new Vector<Object>();
		Vector<Object> col25 = new Vector<Object>();
		Vector<Object> col26 = new Vector<Object>();
		Vector<Object> col27 = new Vector<Object>();
		Vector<Object> col28 = new Vector<Object>();
		Vector<Object> col29 = new Vector<Object>();
		for (int x = 0; x < arr.size(); x++) {
			col1.add(((JSONObject) arr.get(x)).getString("id"));
			col2.add(((JSONObject) arr.get(x)).getString("name"));
			col3.add(((JSONObject) arr.get(x)).getString("progress"));
			col4.add(((JSONObject) arr.get(x)).getString("status"));
			col5.add(((JSONObject) arr.get(x)).getString("updated"));
			col6.add(((JSONObject) arr.get(x)).getString("created"));
			col7.add(((JSONObject) arr.get(x)).getString("OS-EXT-IMG-SIZE:size"));
			col8.add(((JSONObject) arr.get(x)).getString("minDisk"));
			col9.add(((JSONObject) arr.get(x)).getString("minRam"));
			JSONObject imageMeta = ((JSONObject) arr.get(x)).getJSONObject("metadata");
			try {
				col10.add(imageMeta.getString("base_image_ref"));
			} catch (Exception ex) {
				col10.add("");
			}
			try {
				col11.add(imageMeta.getString("image_location"));
			} catch (Exception ex) {
				col11.add("");
			}
			try {
				col12.add(imageMeta.getString("image_state"));
			} catch (Exception ex) {
				col12.add("");
			}
			try {
				col13.add(imageMeta.getString("image_type"));
			} catch (Exception ex) {
				col13.add("");
			}
			try {
				col14.add(imageMeta.getString("instance_type_ephemeral_gb"));
			} catch (Exception ex) {
				col14.add("");
			}
			try {
				col15.add(imageMeta.getString("instance_type_flavorid"));
			} catch (Exception ex) {
				col15.add("");
			}
			try {
				col16.add(imageMeta.getString("instance_type_id"));
			} catch (Exception ex) {
				col16.add("");
			}
			try {
				col17.add(imageMeta.getString("instance_type_memory_mb"));
			} catch (Exception ex) {
				col17.add("");
			}
			try {
				col18.add(imageMeta.getString("instance_type_name"));
			} catch (Exception ex) {
				col18.add("");
			}
			try {
				col19.add(imageMeta.getString("instance_type_root_gb"));
			} catch (Exception ex) {
				col19.add("");
			}
			try {
				col20.add(imageMeta.getString("instance_type_rxtx_factor"));
			} catch (Exception ex) {
				col20.add("");
			}
			try {
				col21.add(imageMeta.getString("instance_type_swap"));
			} catch (Exception ex) {
				col21.add("");
			}
			try {
				col22.add(imageMeta.getString("instance_type_vcpu_weight"));
			} catch (Exception ex) {
				col22.add("");
			}
			try {
				col23.add(imageMeta.getString("instance_type_vcpus"));
			} catch (Exception ex) {
				col23.add("");
			}
			try {
				col24.add(imageMeta.getString("instance_uuid"));
			} catch (Exception ex) {
				col24.add("");
			}
			try {
				col25.add(imageMeta.getString("kernel_id"));
			} catch (Exception ex) {
				col25.add("");
			}
			try {
				col26.add(imageMeta.getString("owner_id"));
			} catch (Exception ex) {
				col26.add("");
			}
			try {
				col27.add(imageMeta.getString("ramdisk_id"));
			} catch (Exception ex) {
				col27.add("");
			}
			try {
				col28.add(imageMeta.getString("user_id"));
			} catch (Exception ex) {
				col28.add("");
			}
			col29.add(""/*((JSONObject) arr.get(x)).getString("links")*/);
		}

		imageTableModel.values.add(col1);
		imageTableModel.values.add(col2);
		imageTableModel.values.add(col3);
		imageTableModel.values.add(col4);
		imageTableModel.values.add(col5);
		imageTableModel.values.add(col6);
		imageTableModel.values.add(col7);
		imageTableModel.values.add(col8);
		imageTableModel.values.add(col9);
		imageTableModel.values.add(col10);
		imageTableModel.values.add(col11);
		imageTableModel.values.add(col12);
		imageTableModel.values.add(col13);
		imageTableModel.values.add(col14);
		imageTableModel.values.add(col15);
		imageTableModel.values.add(col16);
		imageTableModel.values.add(col17);
		imageTableModel.values.add(col18);
		imageTableModel.values.add(col19);
		imageTableModel.values.add(col20);
		imageTableModel.values.add(col21);
		imageTableModel.values.add(col22);
		imageTableModel.values.add(col23);
		imageTableModel.values.add(col24);
		imageTableModel.values.add(col25);
		imageTableModel.values.add(col26);
		imageTableModel.values.add(col27);
		imageTableModel.values.add(col28);
		imageTableModel.values.add(col29);
		imageTableModel.fireTableStructureChanged();
		imageTableModel.fireTableDataChanged();
		//		imageTable.getColumnModel().getColumn(1).setPreferredWidth(400);
	}
}
