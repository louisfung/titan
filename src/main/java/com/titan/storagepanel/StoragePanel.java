package com.titan.storagepanel;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
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
import com.peterswing.advancedswing.jtable.ComputerUnit;
import com.peterswing.advancedswing.jtable.SortableTableModel;
import com.peterswing.advancedswing.jtable.TableSorterColumnListener;
import com.titan.MainPanel;
import com.titan.TitanCommonLib;
import com.titan.communication.CommunicateLib;
import com.titanserver.Command;
import com.titanserver.ReturnCommand;

public class StoragePanel extends JPanel implements Runnable, MainPanel {
	private JTable imageTable;
	final JFrame frame;
	JProgressBarDialog d;
	GenericTableModel imageTableModel = new GenericTableModel();
	SortableTableModel sortableTableModel = new SortableTableModel(imageTableModel);
	private JTable uploadTable;

	GenericTableModel uploadTableModel = new GenericTableModel();
	SortableTableModel sortableUploadTableModel = new SortableTableModel(uploadTableModel);
	TableSorterColumnListener tableSorterColumnListener;

	GenericTableModel cinderTableModel = new GenericTableModel();
	SortableTableModel sortableCinderTableModel = new SortableTableModel(cinderTableModel);
	TableSorterColumnListener cinderTableSorterColumnListener;
	private JTable cinderTable;

	GenericTableModel volumeTypeTableModel = new GenericTableModel();
	SortableTableModel sortableVolumeTypeTableModel = new SortableTableModel(volumeTypeTableModel);
	TableSorterColumnListener volumeTypeTableSorterColumnListener;
	private JTable volumeTypeTable;

	public StoragePanel(JFrame frame) {
		this.frame = frame;
		setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		add(panel, BorderLayout.NORTH);

		JLabel lblFlavor = new JLabel("Storage");
		lblFlavor.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
		panel.add(lblFlavor);

		JPanel panel_1 = new JPanel();
		add(panel_1, BorderLayout.CENTER);
		panel_1.setLayout(new BorderLayout(0, 0));

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		panel_1.add(tabbedPane, BorderLayout.CENTER);

		JPanel volumePanel = new JPanel();
		tabbedPane.addTab("Volume", null, volumePanel, null);
		volumePanel.setLayout(new BorderLayout(0, 0));

		JPanel panel_5 = new JPanel();
		FlowLayout flowLayout_2 = (FlowLayout) panel_5.getLayout();
		flowLayout_2.setAlignment(FlowLayout.LEFT);
		volumePanel.add(panel_5, BorderLayout.SOUTH);

		JButton btnCreateVolume = new JButton("Create volume");
		panel_5.add(btnCreateVolume);

		JButton btnDeleteVolume = new JButton("Delete volume");
		panel_5.add(btnDeleteVolume);

		JButton btnDetail = new JButton("Detail");
		panel_5.add(btnDetail);

		JButton attachVMButton = new JButton("Attach VM");
		panel_5.add(attachVMButton);

		JButton btnDetactVm = new JButton("Detact VM");
		panel_5.add(btnDetactVm);

		JScrollPane scrollPane_2 = new JScrollPane();
		volumePanel.add(scrollPane_2, BorderLayout.CENTER);

		cinderTable = new JTable();
		cinderTableSorterColumnListener = new TableSorterColumnListener(cinderTable, sortableCinderTableModel);
		cinderTable.getTableHeader().setReorderingAllowed(false);
		cinderTable.setModel(sortableCinderTableModel);
		cinderTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		cinderTable.getTableHeader().addMouseListener(cinderTableSorterColumnListener);
		cinderTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane_2.setViewportView(cinderTable);

		JPanel volumeTypePanel = new JPanel();
		tabbedPane.addTab("Volume type", null, volumeTypePanel, null);
		volumeTypePanel.setLayout(new BorderLayout(0, 0));

		JPanel panel_6 = new JPanel();
		FlowLayout flowLayout_3 = (FlowLayout) panel_6.getLayout();
		flowLayout_3.setAlignment(FlowLayout.LEFT);
		volumeTypePanel.add(panel_6, BorderLayout.SOUTH);

		JButton btnCreateType = new JButton("Create type");
		panel_6.add(btnCreateType);

		JButton btnDeleteType = new JButton("Delete type");
		panel_6.add(btnDeleteType);

		JScrollPane scrollPane_3 = new JScrollPane();
		volumeTypePanel.add(scrollPane_3, BorderLayout.CENTER);

		volumeTypeTable = new JTable();
		volumeTypeTableSorterColumnListener = new TableSorterColumnListener(volumeTypeTable, sortableVolumeTypeTableModel);
		volumeTypeTable.getTableHeader().setReorderingAllowed(false);
		volumeTypeTable.setModel(sortableVolumeTypeTableModel);
		volumeTypeTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		volumeTypeTable.getTableHeader().addMouseListener(volumeTypeTableSorterColumnListener);
		volumeTypeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane_3.setViewportView(volumeTypeTable);

		JPanel panel_3 = new JPanel();
		tabbedPane.addTab("Image", null, panel_3, null);
		panel_3.setLayout(new BorderLayout(0, 0));

		JPanel panel_2 = new JPanel();
		panel_3.add(panel_2, BorderLayout.SOUTH);
		FlowLayout flowLayout_1 = (FlowLayout) panel_2.getLayout();
		flowLayout_1.setAlignment(FlowLayout.LEFT);

		JButton uploadButton = new JButton("Upload");
		uploadButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				UploadImageDialog dialog = new UploadImageDialog(StoragePanel.this.frame);
				dialog.setVisible(true);
				if (dialog.refresh) {
					refresh();
				}
			}
		});
		panel_2.add(uploadButton);

		JButton btnDelete = new JButton("Delete");
		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (imageTable.getSelectedRowCount() == 1) {
					String imageId = (String) sortableTableModel.getValueAt(imageTable.getSelectedRow(), sortableTableModel.getColumnIndex("Id"));
					String imageName = (String) sortableTableModel.getValueAt(imageTable.getSelectedRow(), sortableTableModel.getColumnIndex("Name"));
					int temp = JOptionPane.showConfirmDialog(StoragePanel.this.frame, "Confirm to delete image " + imageName + " ?", "Warning", JOptionPane.YES_NO_OPTION);
					if (temp == JOptionPane.YES_OPTION) {
						Command command = new Command();
						command.command = "from titan: glance image-delete";
						HashMap<String, String> parameters = new HashMap<String, String>();
						parameters.put("$imageId", imageId);
						command.parameters.add(parameters);
						ReturnCommand r = CommunicateLib.send(TitanCommonLib.getCurrentServerIP(), command);
						refresh();
					}
				}
			}
		});
		panel_2.add(btnDelete);

		JButton btnViewMeta = new JButton("View meta");
		btnViewMeta.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (imageTable.getSelectedRow() == 1) {
					String imageId = (String) sortableTableModel.getValueAt(imageTable.getSelectedRow(), sortableTableModel.getColumnIndex("Id"));
					Command command = new Command();
					command.command = "from titan: nova delete-image";
					HashMap<String, String> parameters = new HashMap<String, String>();
					parameters.put("$imageId", imageId);
					command.parameters.add(parameters);
					ReturnCommand r = CommunicateLib.send(TitanCommonLib.getCurrentServerIP(), command);
				}
			}
		});
		panel_2.add(btnViewMeta);

		JScrollPane scrollPane = new JScrollPane();
		panel_3.add(scrollPane, BorderLayout.CENTER);

		imageTable = new JTable();
		tableSorterColumnListener = new TableSorterColumnListener(imageTable, sortableTableModel);
		imageTable.getTableHeader().setReorderingAllowed(false);
		imageTable.setModel(sortableTableModel);
		imageTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		imageTable.getTableHeader().addMouseListener(tableSorterColumnListener);
		imageTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane.setViewportView(imageTable);

		JPanel panel_4 = new JPanel();
		tabbedPane.addTab("Progress", null, panel_4, null);
		panel_4.setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane_1 = new JScrollPane();
		panel_4.add(scrollPane_1);

		uploadTableModel.columnNames.clear();
		uploadTableModel.columnNames.add("File");
		uploadTableModel.columnNames.add("Server");
		uploadTableModel.columnNames.add("Progress");
		uploadTableModel.columnNames.add("Message");
		uploadTableModel.fireTableStructureChanged();
		uploadTableModel.fireTableDataChanged();
		uploadTable = new JTable();
		uploadTable.setModel(sortableUploadTableModel);
		scrollPane_1.setViewportView(uploadTable);

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
		refreshCinder();
		refreshVolumeType();
		refreshImage();
	}

	private void refreshVolumeType() {
		d.jProgressBar.setString("cinder list");
		Command command = new Command();
		command.command = "from titan: cinder type-list";
		ReturnCommand r = CommunicateLib.send(TitanCommonLib.getCurrentServerIP(), command);
		JSONArray images = JSONObject.fromObject(r.map.get("result")).getJSONArray("volumes");
		volumeTypeTableModel.columnNames.clear();
		volumeTypeTableModel.columnNames.add("Id");
		volumeTypeTableModel.columnNames.add("Name");
		volumeTypeTableModel.columnNames.add("Size");
		volumeTypeTableModel.columnNames.add("Bootable");
		volumeTypeTableModel.columnNames.add("Status");
		volumeTypeTableModel.columnNames.add("Attachments");
		volumeTypeTableModel.columnNames.add("Zone");
		volumeTypeTableModel.columnNames.add("Created at");
		volumeTypeTableModel.columnNames.add("Tenant id");
		volumeTypeTableModel.columnNames.add("Description");
		volumeTypeTableModel.columnNames.add("Host");
		volumeTypeTableModel.columnNames.add("Volume type");
		volumeTypeTableModel.columnNames.add("Snapshot id");
		volumeTypeTableModel.columnNames.add("Source vol id");
		volumeTypeTableModel.columnNames.add("Meta");

		volumeTypeTableModel.columnTypes.clear();
		volumeTypeTableModel.columnTypes.put(2, ComputerUnit.class);

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

		for (int x = 0; x < images.size(); x++) {
			JSONObject obj = images.getJSONObject(x);
			col1.add(obj.getString("id"));
			col2.add(obj.getString("display_name"));
			col3.add(CommonLib.convertFilesize(Long.parseLong(obj.getString("size"))));
			col4.add(obj.getString("bootable"));
			col5.add(obj.getString("status"));
			col6.add(obj.getString("attachments"));
			col7.add(obj.getString("availability_zone"));
			col8.add(obj.getString("created_at"));
			col9.add(obj.getString("os-vol-tenant-attr:tenant_id"));
			col10.add(obj.getString("display_description"));
			col11.add(obj.getString("os-vol-host-attr:host"));
			col12.add(obj.getString("volume_type"));
			col13.add(obj.getString("snapshot_id"));
			col14.add(obj.getString("source_volid"));
			col15.add(obj.getString("metadata"));
		}
		volumeTypeTableModel.values.clear();
		volumeTypeTableModel.values.add(col1);
		volumeTypeTableModel.values.add(col2);
		volumeTypeTableModel.values.add(col3);
		volumeTypeTableModel.values.add(col4);
		volumeTypeTableModel.values.add(col5);
		volumeTypeTableModel.values.add(col6);
		volumeTypeTableModel.values.add(col7);
		volumeTypeTableModel.values.add(col8);
		volumeTypeTableModel.values.add(col9);
		volumeTypeTableModel.values.add(col10);
		volumeTypeTableModel.values.add(col11);
		volumeTypeTableModel.values.add(col12);
		volumeTypeTableModel.values.add(col13);
		volumeTypeTableModel.values.add(col14);
		volumeTypeTableModel.values.add(col15);

		DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
		leftRenderer.setHorizontalAlignment(JTextField.LEFT);
		DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
		rightRenderer.setHorizontalAlignment(JTextField.RIGHT);
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JTextField.CENTER);

		sortableVolumeTypeTableModel.fireTableStructureChanged();
		sortableVolumeTypeTableModel.fireTableDataChanged();
		sortableVolumeTypeTableModel.sortByColumn(tableSorterColumnListener.sortCol, volumeTypeTableSorterColumnListener.isSortAsc);

		volumeTypeTable.getColumnModel().getColumn(0).setPreferredWidth(300);
		volumeTypeTable.getColumnModel().getColumn(1).setPreferredWidth(300);
		volumeTypeTable.getColumnModel().getColumn(2).setPreferredWidth(120);
		volumeTypeTable.getColumnModel().getColumn(3).setPreferredWidth(80);
		volumeTypeTable.getColumnModel().getColumn(4).setPreferredWidth(80);
		volumeTypeTable.getColumnModel().getColumn(5).setPreferredWidth(200);
		volumeTypeTable.getColumnModel().getColumn(6).setPreferredWidth(200);

		volumeTypeTable.getColumnModel().getColumn(0).setCellRenderer(rightRenderer);
		volumeTypeTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
		volumeTypeTable.getColumnModel().getColumn(2).setCellRenderer(rightRenderer);
		volumeTypeTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
		volumeTypeTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
		volumeTypeTable.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
		volumeTypeTable.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);
		volumeTypeTable.getColumnModel().getColumn(7).setCellRenderer(centerRenderer);
		volumeTypeTable.getColumnModel().getColumn(8).setCellRenderer(centerRenderer);
		volumeTypeTable.getColumnModel().getColumn(9).setCellRenderer(centerRenderer);
		volumeTypeTable.getColumnModel().getColumn(10).setCellRenderer(centerRenderer);
		volumeTypeTable.getColumnModel().getColumn(11).setCellRenderer(centerRenderer);
		volumeTypeTable.getColumnModel().getColumn(12).setCellRenderer(centerRenderer);
		volumeTypeTable.getColumnModel().getColumn(13).setCellRenderer(centerRenderer);
	}

	private void refreshCinder() {
		d.jProgressBar.setString("cinder list");
		Command command = new Command();
		command.command = "from titan: cinder list";
		ReturnCommand r = CommunicateLib.send(TitanCommonLib.getCurrentServerIP(), command);
		JSONArray images = JSONObject.fromObject(r.map.get("result")).getJSONArray("volumes");
		cinderTableModel.columnNames.clear();
		cinderTableModel.columnNames.add("Id");
		cinderTableModel.columnNames.add("Name");
		cinderTableModel.columnNames.add("Size");
		cinderTableModel.columnNames.add("Bootable");
		cinderTableModel.columnNames.add("Status");
		cinderTableModel.columnNames.add("Attachments");
		cinderTableModel.columnNames.add("Zone");
		cinderTableModel.columnNames.add("Created at");
		cinderTableModel.columnNames.add("Tenant id");
		cinderTableModel.columnNames.add("Description");
		cinderTableModel.columnNames.add("Host");
		cinderTableModel.columnNames.add("Volume type");
		cinderTableModel.columnNames.add("Snapshot id");
		cinderTableModel.columnNames.add("Source vol id");
		cinderTableModel.columnNames.add("Meta");

		cinderTableModel.columnTypes.clear();
		cinderTableModel.columnTypes.put(2, ComputerUnit.class);

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

		for (int x = 0; x < images.size(); x++) {
			JSONObject obj = images.getJSONObject(x);
			col1.add(obj.getString("id"));
			col2.add(obj.getString("display_name"));
			col3.add(CommonLib.convertFilesize(Long.parseLong(obj.getString("size"))));
			col4.add(obj.getString("bootable"));
			col5.add(obj.getString("status"));
			col6.add(obj.getString("attachments"));
			col7.add(obj.getString("availability_zone"));
			col8.add(obj.getString("created_at"));
			col9.add(obj.getString("os-vol-tenant-attr:tenant_id"));
			col10.add(obj.getString("display_description"));
			col11.add(obj.getString("os-vol-host-attr:host"));
			col12.add(obj.getString("volume_type"));
			col13.add(obj.getString("snapshot_id"));
			col14.add(obj.getString("source_volid"));
			col15.add(obj.getString("metadata"));
		}
		cinderTableModel.values.clear();
		cinderTableModel.values.add(col1);
		cinderTableModel.values.add(col2);
		cinderTableModel.values.add(col3);
		cinderTableModel.values.add(col4);
		cinderTableModel.values.add(col5);
		cinderTableModel.values.add(col6);
		cinderTableModel.values.add(col7);
		cinderTableModel.values.add(col8);
		cinderTableModel.values.add(col9);
		cinderTableModel.values.add(col10);
		cinderTableModel.values.add(col11);
		cinderTableModel.values.add(col12);
		cinderTableModel.values.add(col13);
		cinderTableModel.values.add(col14);
		cinderTableModel.values.add(col15);

		DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
		leftRenderer.setHorizontalAlignment(JTextField.LEFT);
		DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
		rightRenderer.setHorizontalAlignment(JTextField.RIGHT);
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JTextField.CENTER);

		sortableCinderTableModel.fireTableStructureChanged();
		sortableCinderTableModel.fireTableDataChanged();
		sortableCinderTableModel.sortByColumn(tableSorterColumnListener.sortCol, cinderTableSorterColumnListener.isSortAsc);

		cinderTable.getColumnModel().getColumn(0).setPreferredWidth(300);
		cinderTable.getColumnModel().getColumn(1).setPreferredWidth(300);
		cinderTable.getColumnModel().getColumn(2).setPreferredWidth(120);
		cinderTable.getColumnModel().getColumn(3).setPreferredWidth(80);
		cinderTable.getColumnModel().getColumn(4).setPreferredWidth(80);
		cinderTable.getColumnModel().getColumn(5).setPreferredWidth(200);
		cinderTable.getColumnModel().getColumn(6).setPreferredWidth(200);

		cinderTable.getColumnModel().getColumn(0).setCellRenderer(rightRenderer);
		cinderTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
		cinderTable.getColumnModel().getColumn(2).setCellRenderer(rightRenderer);
		cinderTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
		cinderTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
		cinderTable.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
		cinderTable.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);
		cinderTable.getColumnModel().getColumn(7).setCellRenderer(centerRenderer);
		cinderTable.getColumnModel().getColumn(8).setCellRenderer(centerRenderer);
		cinderTable.getColumnModel().getColumn(9).setCellRenderer(centerRenderer);
		cinderTable.getColumnModel().getColumn(10).setCellRenderer(centerRenderer);
		cinderTable.getColumnModel().getColumn(11).setCellRenderer(centerRenderer);
		cinderTable.getColumnModel().getColumn(12).setCellRenderer(centerRenderer);
		cinderTable.getColumnModel().getColumn(13).setCellRenderer(centerRenderer);
	}

	private void refreshImage() {
		d.jProgressBar.setString("nova endpoints");
		Command command = new Command();
		command.command = "from titan: glance image-list";
		ReturnCommand r = CommunicateLib.send(TitanCommonLib.getCurrentServerIP(), command);
		JSONArray images = JSONObject.fromObject(r.map.get("result")).getJSONArray("images");
		imageTableModel.columnNames.clear();
		imageTableModel.columnNames.add("Id");
		imageTableModel.columnNames.add("Name");
		imageTableModel.columnNames.add("Size");
		imageTableModel.columnNames.add("Status");
		imageTableModel.columnNames.add("Type");
		imageTableModel.columnNames.add("Created");
		imageTableModel.columnNames.add("Updated");

		imageTableModel.columnTypes.clear();
		imageTableModel.columnTypes.put(2, ComputerUnit.class);

		Vector<Object> col1 = new Vector<Object>();
		Vector<Object> col2 = new Vector<Object>();
		Vector<Object> col3 = new Vector<Object>();
		Vector<Object> col4 = new Vector<Object>();
		Vector<Object> col5 = new Vector<Object>();
		Vector<Object> col6 = new Vector<Object>();
		Vector<Object> col7 = new Vector<Object>();

		for (int x = 0; x < images.size(); x++) {
			JSONObject obj = images.getJSONObject(x);
			col1.add(obj.getString("id"));
			col2.add(obj.getString("name"));
			col3.add(CommonLib.convertFilesize(Long.parseLong(obj.getString("size"))));
			col4.add(obj.getString("status"));
			col5.add(obj.getString("disk_format"));
			col6.add(obj.getString("created_at"));
			col7.add(obj.getString("updated_at"));
		}
		imageTableModel.values.clear();
		imageTableModel.values.add(col1);
		imageTableModel.values.add(col2);
		imageTableModel.values.add(col3);
		imageTableModel.values.add(col4);
		imageTableModel.values.add(col5);
		imageTableModel.values.add(col6);
		imageTableModel.values.add(col7);

		DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
		leftRenderer.setHorizontalAlignment(JTextField.LEFT);
		DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
		rightRenderer.setHorizontalAlignment(JTextField.RIGHT);
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JTextField.CENTER);

		sortableTableModel.fireTableStructureChanged();
		sortableTableModel.fireTableDataChanged();
		sortableTableModel.sortByColumn(tableSorterColumnListener.sortCol, tableSorterColumnListener.isSortAsc);

		imageTable.getColumnModel().getColumn(0).setPreferredWidth(300);
		imageTable.getColumnModel().getColumn(1).setPreferredWidth(300);
		imageTable.getColumnModel().getColumn(2).setPreferredWidth(120);
		imageTable.getColumnModel().getColumn(3).setPreferredWidth(80);
		imageTable.getColumnModel().getColumn(4).setPreferredWidth(80);
		imageTable.getColumnModel().getColumn(5).setPreferredWidth(200);
		imageTable.getColumnModel().getColumn(6).setPreferredWidth(200);

		imageTable.getColumnModel().getColumn(0).setCellRenderer(rightRenderer);
		imageTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
		imageTable.getColumnModel().getColumn(2).setCellRenderer(rightRenderer);
		imageTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
		imageTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
		imageTable.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
		imageTable.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);
	}

}
