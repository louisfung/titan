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

	public StoragePanel(JFrame frame) {
		this.frame = frame;
		setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		add(panel, BorderLayout.NORTH);

		JLabel lblFlavor = new JLabel("Image");
		lblFlavor.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
		panel.add(lblFlavor);

		JPanel panel_1 = new JPanel();
		add(panel_1, BorderLayout.CENTER);
		panel_1.setLayout(new BorderLayout(0, 0));

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		panel_1.add(tabbedPane, BorderLayout.CENTER);

		JPanel panel_3 = new JPanel();
		tabbedPane.addTab("List", null, panel_3, null);
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
